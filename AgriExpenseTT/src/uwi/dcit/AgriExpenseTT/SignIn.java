package uwi.dcit.AgriExpenseTT;

import java.util.ArrayList;

import helper.CloudInterface;
import helper.DbHelper;
import helper.DbQuery;
import helper.Sync;

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

public class SignIn {
	Context context;
	SQLiteDatabase db;
	DbHelper dbh;
	Activity activity;
	public SignIn(Activity activity,Context ctx){
		this.context=ctx;
		dbh=new DbHelper(context);
		db=dbh.getReadableDatabase();
		this.activity=activity;
	}
	public SignIn(SQLiteDatabase db,DbHelper dbh,Activity activity,Context ctx){
		this.context=ctx;
		this.db=db;
		this.dbh=dbh;
		this.activity=activity;
	}
	public void signIn(){
		UpAcc acc=isExisting();//returns the account obj or null, for existing or not, (have we ever signed in ?)
		if(acc==null){
			accSetUp();//account doesnt exist so we've to setup a new one (means we never signed in)
		}else{
			ContentValues cv=new ContentValues();//account exists already so we can sign in OR out
			
			if(acc.getSignedIn()==1){//we're already signed in so lets sign out 
				cv.put(DbHelper.UPDATE_ACCOUNT_SIGNEDIN, 0);//updates the database that we signed out
				((MainMenu)activity).toggleSignIn();
				db.update(DbHelper.TABLE_UPDATE_ACCOUNT, cv, DbHelper.UPDATE_ACCOUNT_ID+"=1",null);
			}else if(acc.getSignedIn()==0){//if we're signed out 
				initialSignIn(acc.getAcc());//we gotta sign in
			}
			
		}
	}
	private void initialSignIn(final String namespace){
		
		class setup extends AsyncTask<Void, Void, UpAcc>{
			String namespace;
			public setup(String namespace){
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

		new setup(namespace).execute();
	}
	
	public void accSetUp(){
		ArrayList<String> accs=new ArrayList<String>();
		populateAcc(accs);
		if(accs.isEmpty()){
			Toast.makeText(context, "no accounts", Toast.LENGTH_SHORT).show();
			noAccs();
			return;
		}
			
		final CharSequence[] items= new CharSequence[accs.size()]; int i=0;
		for(@SuppressWarnings("unused") String k:accs)
			items[i]=accs.get(i++);
		
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
			Toast.makeText(activity.getBaseContext(), "Signed in", Toast.LENGTH_SHORT).show();
		}else{
			if(message==null||message.equals(""))
				Toast.makeText(activity, "Cannot Sign In Try again", Toast.LENGTH_SHORT).show();
			else 
				Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
		}
	}
	//--------------------------------------------------Helper stuff
	private void noAccs(){
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		builder.setTitle("No accounts available");
		builder.setMessage("There are no accounts available to sign in with,"
				+ " please go to your phone's settings and create and account");
		builder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				
			}
		});
		builder.show();
	}
	
	private void populateAcc(ArrayList<String> accs){
		Account[] accounts = AccountManager.get(context).getAccounts();
		for(Account a:accounts){
		  accs.add(a.name);
		  System.out.println(a.name);
		}
	}
	private String convertString(String str){
		int len=str.length();
		String newStr="_";
		for(int i=0;i<len;i++){
			if(isChar(str.charAt(i)))
					newStr+=str.charAt(i);
			else if(str.charAt(i)=='@')
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
		UpAcc acc=DbQuery.getUpAcc(db);
		if(acc.getAcc()==null || acc.getAcc().equals(""))
			return null;
		System.out.println("account exists !!!!!!!!");
		return acc;
	}
	public SignIn getSignin(){
		return this;
	}
}	
