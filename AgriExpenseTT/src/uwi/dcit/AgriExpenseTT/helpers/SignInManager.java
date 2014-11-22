package uwi.dcit.AgriExpenseTT.helpers;

import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.MainMenu;

import com.example.agriexpensett.upaccendpoint.model.UpAcc;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

public class SignInManager {
	Context context;
	SQLiteDatabase db;
	DbHelper dbh;
	Activity activity;
	
	public SignInManager(Activity activity, Context ctx){
		this.context = ctx;
		this.activity = activity;
		dbh = new DbHelper(context);
		db = dbh.getReadableDatabase();
	}
	
	public SignInManager(SQLiteDatabase db,DbHelper dbh,Activity activity,Context ctx){
		this.context = ctx;
		this.db = db;
		this.dbh = dbh;
		this.activity = activity;
	}
	
	public void signIn(){
		UpAcc acc = isExisting(); 								// Check if Account is already created
		if(acc == null)accSetUp();								// Account doesn't exist so we've to setup a new one (means we never signed in)
		else{
			ContentValues cv = new ContentValues();				// account exists already so we can sign in OR out
			if(acc.getSignedIn() == 1){							// we're already signed in so lets sign out 
				cv.put(DbHelper.UPDATE_ACCOUNT_SIGNEDIN, 0);	// updates the database that we signed out
				((MainMenu)activity).toggleSignIn();			// Changes the text on the button to sign out
				db.update(DbHelper.TABLE_UPDATE_ACCOUNT, cv, DbHelper.UPDATE_ACCOUNT_ID+"=1",null); //TODO Determine why this is necessary
			}else if(acc.getSignedIn() == 0){					// if we're signed out then we need to sign in 
				initialSignIn(acc.getAcc());					// Initiate Sign-in process
			}
		}
	}
	
	private void initialSignIn(final String namespace){
		//The SetupSignInTask will handle the process of signing in within a new task/thread
		new SetupSignInTask(namespace).execute();
	}
	
	public void accSetUp(){
		ArrayList<String> deviceAccounts = new ArrayList<String>();
		populateAccounts(deviceAccounts);
		
		if(deviceAccounts.isEmpty()){
			Toast.makeText(context, "No accounts available for sign-in. Create a google Account first and try again.", Toast.LENGTH_LONG).show();
			noAccs();
			return;
		}
			
		final CharSequence[] items= new CharSequence[deviceAccounts.size()]; int i=0;
		for(@SuppressWarnings("unused") String k:deviceAccounts)
			items[i]=deviceAccounts.get(i++);
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    builder.setTitle("Select Account");
	    builder.setItems(items, new DialogInterface.OnClickListener() {
	        @Override
			public void onClick(DialogInterface dialog, int item) {
	            String namespace=convertString((items[item].toString()));
	            initialSignIn(namespace);
	        }
	    }).show();
	}
	
	public void signInReturn(boolean success,String message){
		if(success){
			((MainMenu)activity).toggleSignIn();
			Toast.makeText(activity.getBaseContext(), "Sign-in Successfully Completed", Toast.LENGTH_SHORT).show();
		}else{
			if(message==null||message.equals(""))
				Toast.makeText(activity, "The sign-in process was not successful. Please try again", Toast.LENGTH_SHORT).show();
			else 
				Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
		}
	}
	//--------------------------------------------------Helper stuff
	private void noAccs(){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("No Accounts Available");
		builder.setMessage("There are no accounts available to sign in with, please go to your phone's settings and create and account");
		builder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { }
		});
		builder.show();
	}
	
	private void populateAccounts(ArrayList<String> accountLists){
		Account[] accounts = AccountManager.get(context).getAccounts();
		for(Account a:accounts){
		  accountLists.add(a.name);
		}
	}
	
	private String convertString(String str){
		int len = str.length();
		String newStr = "_";
		for(int i = 0; i < len; i++){
			if(isChar(str.charAt(i)))
				newStr += str.charAt(i);
			else if(str.charAt(i) == '@')
				break;
		}
		return newStr;
	}
	
	private boolean isChar(char c){
		if((c>='a'&&c<='z')||(c>='A'&&c<='Z'))
			return true;
		return false;
	}
	
	public UpAcc isExisting(){
		UpAcc acc = DbQuery.getUpAcc(db); //Attemtp to retrieve the Account from the database Record		
		if(acc.getAcc()==null || acc.getAcc().equals(""))return null; //The information returned will be null if no record exists
		
		System.out.println("Account already Created ... Returning existing Account");
		return acc; //Return the Account if received.
	}
	public SignInManager getSignin(){
		return this;
	}
	
	//Create an Asynchronous Task to create new thread to handle this process
	class SetupSignInTask extends AsyncTask<Void, Void, UpAcc>{
		private String namespace;
		
		public SetupSignInTask(String namespace){
			this.namespace=namespace;
		}
		@Override
		protected UpAcc doInBackground(Void... params) {
			CloudInterface cloudIF = new CloudInterface(context, db, dbh);
			UpAcc cloudAcc=cloudIF.getUpAcc(namespace);//getting a the cloud upAcc if there's any >.<
			return cloudAcc;//this is passed to onPostExecute
		}

		@Override
		protected void onPostExecute(UpAcc cloudAcc) {
			Sync sync=new Sync(db, dbh, context,getSignin());
			sync.start(namespace, cloudAcc);
			super.onPostExecute(cloudAcc);
		}
		
	}
}	
