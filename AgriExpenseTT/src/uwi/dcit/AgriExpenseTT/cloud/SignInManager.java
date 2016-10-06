package uwi.dcit.AgriExpenseTT.cloud;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

//import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.Main;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.models.UpdateAccountContract;
import uwi.dcit.agriexpensesvr.accountApi.AccountApi;
import uwi.dcit.agriexpensesvr.accountApi.model.Account;



public class SignInManager {
	private final String TAG_NAME = "SignInManager";
	Context context;
	SQLiteDatabase db;
	DbHelper dbh;
	Activity activity;
	private String county,country;

    public SignInManager(Activity activity, Context ctx){
        this.context = ctx;
        this.activity = activity;
        dbh = new DbHelper(context);
        db = dbh.getWritableDatabase();

    }

//	public void signIn(String country, String county) {                // TODO Change why do we ask for the country and county before we check for user
//		Log.d(TAG_NAME, "Attempting to Log in");
//		Account acc = isExistingOld(); 								// Check if Account is already created
//		if(acc == null) {
//			accountSetUpOld();                                        // Account doesn't exist so we've to setup a new one (means we never signed in)
//			this.country=country;
//			this.county=county;
//		} else {                                                        // Account exists already so we can Initiate Sign-in process
//			startSyncOld(acc.getAccount());
//		}
//	}

	public void signIn(String country, String county){
		Log.d(TAG_NAME, "Running new version of the sign in activity");

	}

	public void signIn(){
		Log.d(TAG_NAME, "Attempting to Log in");
		Account acc = isExistingOld(); 								// Check if Account is already created
		if(acc == null) {
			accountSetUpOld(); // Account doesn't exist so we've to setup a new one (means we never signed in)
		}
		else {
			this.country=acc.getCountry();
			this.county=acc.getCounty();
			startSyncOld(acc.getAccount());
		}// Account exists already so we can Initiate Sign-in process
		//Due to the fact that start sync is threaded, it will finish on its own timing...
		//need to update the sign in method later down.
	}

    public Account isExistingOld(){
        Account acc = DbQuery.getUpAcc(db);// Attempts to retrieve the Account from the database Record in the app!
		if (acc != null) Log.i("SignInManager", "Account Previously Created");
		else Log.i("SignInManager", "No Account Previous Exists");
		return acc;                         // Return the Account if received (will be null if none exists).
	}


	public boolean localAccountExists(){
		Account acc = DbQuery.getUpAcc(db);
		return acc != null;
	}

	//TODO This function doesn't appear to work well ()
	public void cloudAccountCheck(){
		AccountApi.Builder accountBuilder = new AccountApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
		accountBuilder = CloudEndpointUtils.updateBuilder(accountBuilder);
		AccountApi accountApi = accountBuilder.build();

		final CloudInterface cloudIF = new CloudInterface(context, db, dbh);
		ArrayList<String> deviceAccounts = getAccounts(); //TODO Check if account from a shared preference instead
		//TODO Check why we only accessing the first from the list of accounts accessed
		final String username = convertString2Namespace(deviceAccounts.get(0));
		Log.i("SingInManager", "Selected: " + username);

//		Log.i(">>>><<",username);
		if(username!=null){
			try {
				new Thread(new Runnable() {
					public void run() {
						Account cloudAccount = cloudIF.getAccount(username);
						if (cloudAccount != null) {
							long time = 0;
							cloudAccount.setLastUpdated(time);
							DbQuery.insertAccountTask(db,dbh,cloudAccount);
						}
					}
				}).start();
				Thread.sleep(1000L);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public boolean signOut(){
		Log.d(TAG_NAME, "Account is logged in, attempting to sign out");
		ContentValues cv = new ContentValues();
		cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_SIGNEDIN, 0);
		db.update(UpdateAccountContract.UpdateAccountEntry.TABLE_NAME, cv, UpdateAccountContract.UpdateAccountEntry._ID + "=1",null);
		return true;
	}

	private void startSyncOld(final String namespace){
		new SetupSignInTask(namespace).execute(); //The SetupSignInTask will handle the process of signing in within a new task/thread
	}


	public void accountSetUpOld(){
        Log.d(TAG_NAME, "");
		ArrayList<String> deviceAccounts = getAccounts();
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		if(deviceAccounts.isEmpty()){
			handleNoAccounts();
			//return;
		}

		final CharSequence[] items = new CharSequence[deviceAccounts.size()];
		int i=0;

		for(String account : deviceAccounts){
			items[i++] = account;
		}

		//Display List to user for choosing account
	    (new AlertDialog.Builder(context))
	    	.setTitle("Select Account")
	    	.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
                    String namespace = convertString2Namespace(items[item].toString());
		            if (namespace != null)
						startSyncOld(namespace); //convert choice to name space and attempt to upload
                    else
						signInReturn(false, "Unable to create Account with the backup system");
		        }
		    }).show();
	}
//
	public void signInReturn(boolean success,String message){
		if(success){
			Toast.makeText(activity.getBaseContext(), "Sign-in Successfully Completed", Toast.LENGTH_SHORT).show();
		}else{
			if(message == null) Toast.makeText(activity, "The sign-in process was not successful. Please try again", Toast.LENGTH_SHORT).show();
			else Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
		}
	}

	//--------------------------------------------------Helper stuff
	private void handleNoAccounts(){
		(new AlertDialog.Builder(context))
			.setTitle("No Accounts Available")
			.setMessage("A Google Account is required to backup the application data. Create an account before attempting to backup")
			.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					context.startActivity(new Intent(context, Main.class));
				}
			})
			.setNeutralButton("Create Account", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					context.startActivity(new Intent(android.provider.Settings.ACTION_ADD_ACCOUNT));
				}
			})
			.show();
	}

	private ArrayList<String> getAccounts(){
		ArrayList<String> accountList = new ArrayList<>();
//		android.accounts.Account[] accounts = AccountManager.get(context).getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);

		android.accounts.Account[] accounts = new android.accounts.Account[0];

		for(android.accounts.Account a:accounts){
		  accountList.add(a.name);
		}
		return accountList;
	}

	private String convertString2Namespace(String str){
        // Using the Android built in SimpleSplitter to delimit string
        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter('@');
        splitter.setString(str);
        if (splitter.hasNext())     //If it contains an @ then its a valid email address
             return "_" + splitter.next(); //concatenate and return the address (address highly likely to be unique)
        else return null;           // Return null if not valid
	}



	public boolean isSignedIn(){
		Account account = this.isExistingOld();
        return account != null && (account.getSignedIn() == 1);
    }

	public SignInManager getSigninObject(){
		return this;
	}

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

	//Create an Asynchronous Task to create new thread to handle this process
	private class SetupSignInTask extends AsyncTask<String, Void, Account> {

		private String namespace;

		public SetupSignInTask(String namespace) {
			this.namespace = namespace;
		}

		@Override
		protected Account doInBackground(String... params) {

			CloudInterface cloudIF = new CloudInterface(context, db, dbh);
			Account cloudAccount = cloudIF.getAccount(namespace);    //returns  Account if there is any to the onPostExecute
			Account localAccount = isExistingOld();
			//It doesn't matter what is not present within the system, the insertAccount method wil lendure that both a
			//cloud and local account is created.
			if (cloudAccount == null || localAccount == null) {
				Log.d(TAG_NAME, "No Account Exists locally on  app or in the cloud ... Creating a new Account");
				//Should be able to obtain the country and area selection from the user.
				cloudAccount = cloudIF.insertAccount(namespace, 0, country, county);
			} else
				cloudAccount = cloudIF.getAccount(namespace);
			return cloudAccount;
		}

		@Override
		protected void onPostExecute(Account cloudAcc) {
			Account localAccount = isExistingOld();
			DbQuery.signInAccount(db, localAccount);
			if (localAccount != null && cloudAcc != null) {
				Sync sync = new Sync(db, dbh, context, getSigninObject());
				sync.start(namespace, cloudAcc);
			}
			super.onPostExecute(cloudAcc);

		}

	}
}
