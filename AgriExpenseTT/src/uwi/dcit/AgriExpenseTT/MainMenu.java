package uwi.dcit.AgriExpenseTT;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.FlyOutContainer;
import uwi.dcit.AgriExpenseTT.helpers.NetworkHelper;
import uwi.dcit.AgriExpenseTT.helpers.SignInManager;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.agriexpensett.upaccendpoint.model.UpAcc;

public class MainMenu extends ActionBarActivity {
	protected FlyOutContainer root;
	protected SignInManager signInManager;
	public final static String APP_NAME = "AgriExpenseTT";
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.root=(FlyOutContainer) this.getLayoutInflater().inflate(R.layout.activity_main_menu, null);
		this.setContentView(root);
		signInManager = new SignInManager(MainMenu.this,MainMenu.this);
		setupButtons();
	}
	
	/*
	 * Dealing with Button Configurations
	 */

	private void setupButtons() {
		//Change the text of the sign-in button if user has signed in before
		Button btn_SignIn=(Button)findViewById(R.id.btn_SignIn);		
		UpAcc acc= signInManager.isExisting();
		if(acc!=null && acc.getSignedIn()== 1){
			btn_SignIn.setText("Sign Out");
		}
	}
	
	public void openNewCycleFragment(View view){		
		startActivity(new Intent(getApplicationContext(),NewCycle.class));
	}
	
	public void openNewPurchaseFragment(View view){
		startActivity(new Intent(getApplicationContext(),NewPurchase.class));
	}
	
	public void openManageLabourFragment(View view){
		startActivity(new Intent(getApplicationContext(),HireLabour.class));
	}
	
	public void openManageResourceFragment(View view){
		startActivity(new Intent(getApplicationContext(),ManageResources.class));
	}
	
	public void openManageDataFragment(View view){
		startActivity(new Intent(getApplicationContext(),ManageData.class));
	}
	
	public void openManageExports(View view){
		startActivity(new Intent(getApplicationContext(), ManageReport.class));		
	}
	
	public void openAboutFragment(View view){
		startActivity(new Intent(getApplicationContext(), AboutScreen.class ));
	}

	public void openHelpFragment(View view){
		startActivity(new Intent(getApplicationContext(), HelpScreen.class));
	}
	
	public void openBackupDataFragment(View view){
		Intent i = new Intent(getApplicationContext(), Backup.class);
		if (this.signInManager.isExisting() == null){ 			// User does not exist => check internet and then create user
			if (!NetworkHelper.isNetworkAvailable(this)){ 		// No network available so display appropriate message
				Toast.makeText(getApplicationContext(), "No internet connection, Unable to sign-in at the moment.", Toast.LENGTH_LONG).show();
				return;
			}
			i.putExtra("ACTION",  Backup.SIGN_UP); 				// Launch the Backup activity with the sign-up action passed
		}else if (!this.signInManager.isSignedIn()){ 			// If not signed attempt to login with existing account
			i.putExtra("ACTION",  Backup.SIGN_IN); 				// Launch the Backup activity with the sign-in action passed
		}else i.putExtra("ACTION", Backup.VIEW);				// Launch the Backup activity to simply view the data
		startActivity(i);										// Launch the Backup activity
	}
	
	/*
	 * Dealing with Menu Operations
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_item_about:
				this.openAboutFragment(null);
				return true;
			case R.id.menu_item_help:
				this.openHelpFragment(null);
				return true;
			case R.id.menu_item_settings:
				this.openManageDataFragment(null);
				return true;	
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	public void toggleMenu(View v){
		this.root.toggleMenu();
	}
	public void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_CANCELED){
			return;
		}
		if(requestCode==1){
			String county=data.getExtras().getString("county");
			ContentValues cv=new ContentValues();
			cv.put(DbHelper.UPDATE_ACCOUNT_COUNTY, county);
			DbHelper dbh=new DbHelper(MainMenu.this);
			SQLiteDatabase db=dbh.getReadableDatabase();
			db.update(DbHelper.TABLE_UPDATE_ACCOUNT, cv, DbHelper.UPDATE_ACCOUNT_ID+"=1", null);
			System.out.println("result String"+county);
			signInManager.signIn();
		}
	}
	
	public void signInStart(View view){
		//TODO Open A new Activity
		
		DbHelper dbh=new DbHelper(MainMenu.this);
		SQLiteDatabase db=dbh.getReadableDatabase();
		UpAcc acc=DbQuery.getUpAcc(db);
		db.close();
		if(!isNetworkAvailable()){
			Toast.makeText(getApplicationContext(), "No internet connection, Unable to sign-in at the moment.", Toast.LENGTH_SHORT).show();
			return;
		}
		if(acc.getSignedIn()==0){//not signed in
			if(acc.getCounty()==null||acc.getCounty().equals("")){//location was never set
				Intent i=new Intent(getApplicationContext(),SelectLocation.class);
				startActivityForResult(i, 1);
			}else{
				signInManager.signIn();
			}
		}else{
			signInManager.signIn();
		}
	}
	public void toggleSignIn(){
		Button btnSignIn=(Button)findViewById(R.id.btn_SignIn);
		if(btnSignIn.getText().toString().equals("Sign In")){
			btnSignIn.setText("Sign Out");
		}else{
			btnSignIn.setText("Sign In");
		}
	}
	/**
	 * A placeholder fragment containing a simple view.
	 */
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	

}
