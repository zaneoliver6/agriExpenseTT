package uwi.dcit.AgriExpenseTT.cloud;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;

import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.Main;
import uwi.dcit.AgriExpenseTT.dbstruct.structs.UpAccount;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.models.UpdateAccountContract;
import uwi.dcit.agriexpensesvr.upAccApi.model.UpAcc;

//import com.dcit.agriexpensett.upAccApi.model.UpAcc;


public class SignInManager {
	Context context;
	SQLiteDatabase db;
	DbHelper dbh;
	Activity activity;
	private String county,country;
    private final String TAG_NAME = "SignInManager";

    public SignInManager(Activity activity, Context ctx){
        this.context = ctx;
        this.activity = activity;
        dbh = new DbHelper(context);
        db = dbh.getWritableDatabase();
    }

	public void signIn(){
        Log.d(TAG_NAME, "Attempting to Log in");
		UpAcc acc = isExisting(); 								// Check if Account is already created
		if(acc == null) accountSetUp();							// Account doesn't exist so we've to setup a new one (means we never signed in)
		else startSync(acc.getAcc());						    // Account exists already so we can Initiate Sign-in process
	}
	
	public boolean signOut(){
		Log.d(TAG_NAME, "Account is logged in, attempting to sign out");
		ContentValues cv = new ContentValues();	
		cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_SIGNEDIN, 0);
		db.update(UpdateAccountContract.UpdateAccountEntry.TABLE_NAME, cv, UpdateAccountContract.UpdateAccountEntry._ID + "=1",null);
		return true;
	}
	
	private void startSync(final String namespace){
		new SetupSignInTask(namespace).execute(); //The SetupSignInTask will handle the process of signing in within a new task/thread
	}
	
	public void accountSetUp(){
        Log.d(TAG_NAME, "");
		ArrayList<String> deviceAccounts = getAccounts();
		if(deviceAccounts.isEmpty()){
			handleNoAccounts();
			return;
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
		            if (namespace != null)startSync(namespace); //convert choice to name space and attempt to upload
                    else signInReturn(false, "Unable to create Account with the backup system");
		        }
		    }).show();
	}
	
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
		Account[] accounts = AccountManager.get(context).getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);

		for(Account a:accounts){
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

	public UpAcc isExisting(){
		UpAcc acc = UpAccount.getUpAcc(db);   // Attempts to retrieve the Account from the database Record
		if(acc.getAcc() == null || acc.getAcc().equals(""))
            return null;                    // The information returned will be null if no record exists
		return acc;                         // Return the Account if received.
	}
	
	public boolean isSignedIn(){
		UpAcc account = this.isExisting();
        return account != null && (account.getSignedIn() == 1);
    }
	
	public SignInManager getSigninObject(){
		return this;
	}
	
	//Create an Asynchronous Task to create new thread to handle this process
	private class SetupSignInTask extends AsyncTask<String, Void, UpAcc> {

		private String namespace;
		
		public SetupSignInTask(String namespace){
			this.namespace=namespace;
		}

		@Override
		protected UpAcc doInBackground(String... params) {
			CloudInterface cloudIF = new CloudInterface(context, db, dbh);
            UpAcc account = cloudIF.getUpAcc(namespace);//returns  UpAcc if there is any to the onPostExecute
            if (account == null){
                Log.d(TAG_NAME, "No Account Exists Creating a new Account");
                cloudIF.insertUpAccC(namespace, 0, country, county);
            }
            return null;
		}

		@Override
		protected void onPostExecute(UpAcc cloudAcc) {
			if (cloudAcc != null) {
                Sync sync = new Sync(db, dbh, context, getSigninObject());
                sync.start(namespace, cloudAcc);
            }
            super.onPostExecute(cloudAcc);
		}
		
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
}
