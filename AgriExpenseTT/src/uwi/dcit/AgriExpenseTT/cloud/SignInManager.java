package uwi.dcit.AgriExpenseTT.cloud;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.PrefUtils;
import uwi.dcit.AgriExpenseTT.models.UpdateAccountContract;
import uwi.dcit.agriexpensesvr.accountApi.AccountApi;
import uwi.dcit.agriexpensesvr.accountApi.model.Account;


public class SignInManager implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
	public static final int RC_SIGN_IN = 412;
	private final String TAG_NAME = "SignInManager";
	private Activity context;
	private SQLiteDatabase db;
	private DbHelper dbh;
	private Activity activity;
	private String county,country;
	private ProgressDialog syncDialog;

	public SignInManager(Activity activity, Activity ctx) {
		this.context = ctx;
		this.activity = activity;
        dbh = new DbHelper(context);
        db = dbh.getWritableDatabase();
    }

	public void signIn(String country, String county){
		this.country = country;
		this.county = county;

		// Using Google recommended way to sign in https://developers.google.com/identity/sign-in/android/sign-in?configured=true
		Log.d(TAG_NAME, "Running new version of the sign in activity with country");
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.build();

		GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		context.startActivityForResult(signInIntent, RC_SIGN_IN); // NB The activity callback is handled in the Base Activity
	}


	public void handleSignInResult(GoogleSignInResult result) {
		String namespace;
		if (result != null && result.getSignInAccount() != null) {
			namespace = result.getSignInAccount().getEmail();
			if (namespace != null) {
				Log.d(TAG_NAME, "Handle Sign in received: " + namespace);
				// Store User Email to Local storage
				PrefUtils.setUserEmail(context, namespace);
				// Display a Dialog so the user is aware a background task is in operation
				syncDialog = ProgressDialog.show(context, "Backup Information", "Uploading Information for " + namespace + " to the cloud to prevent data loss", true);
				syncDialog.show();
				// Run the Synchronization process in the background using Async Task
				new SetupSignInTask(namespace).execute();
			}
		}else{
			Log.d(TAG_NAME, "Received Null From the Google Signed in Request");

		}
	}

	public void signIn(){
		Log.d(TAG_NAME, "Running new version of the sign in activity");
	}

	public void signInOld() {
		Log.d(TAG_NAME, "Attempting to Log in");
		Account acc = isExistingOld(); 								// Check if Account is already created
		if(acc == null) {
//			accountSetUpOld(); // Account doesn't exist so we've to setup a new one (means we never signed in)
		}
		else {
			this.country=acc.getCountry();
			this.county=acc.getCounty();
			startSyncOld(acc.getAccount());
		}// Account exists already so we can Initiate Sign-in process
		//Due to the fact that start sync is threaded, it will finish on its own timing...
		//need to update the sign in method later down.
	}

	private Account isExistingOld() {
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

		ArrayList<String> deviceAccounts = new ArrayList<>(); //TODO Check if account from a shared preference instead
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

	private SignInManager getSigninObject() {
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

	/*
		This method relates to handleing the google sign in connection
		https://developers.google.com/android/guides/api-client#manually_managed_connections
	 */
	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	private void reportFailures(String message, String type) {
		//TODO Develop System to send messages to
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
			boolean isSuccess = false;
			try {
				Account localAccount = isExistingOld();
				DbQuery.signInAccount(db, localAccount);
				if (localAccount != null && cloudAcc != null) {
					Sync sync = new Sync(db, dbh, context, getSigninObject());
					sync.start(namespace, cloudAcc);
				}
				isSuccess = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.onPostExecute(cloudAcc);

			syncDialog.cancel();
			if (isSuccess) {
				new AlertDialog.Builder(context) // Use Dialog to provide better feedback to ensure... toast are not easily seen
						.setIcon(android.R.drawable.ic_dialog_alert) //TODO Change to OK icon from material library
						.setTitle("Backup")
						.setMessage("Records was successfully backed up in the cloud")
						.setPositiveButton("Dismiss", null)
						.show();
			} else {
				new AlertDialog.Builder(context) // Use Dialog to provide better feedback to ensure... toast are not easily seen
						.setIcon(android.R.drawable.ic_dialog_alert) //TODO Change to OK icon from material library
						.setTitle("Backup")
						.setMessage("Error Occurred While attempting to backup. Do you wish to report a message to the administrators of the app to help resolve the issue?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								reportFailures("Backup Data", "Unable to backup information to server after first signing");
							}
						})
						.setNegativeButton("No", null)
						.show();
			}
		}

	}
}
