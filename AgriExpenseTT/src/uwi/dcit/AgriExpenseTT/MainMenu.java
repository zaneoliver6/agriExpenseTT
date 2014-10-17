package uwi.dcit.AgriExpenseTT;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.FlyOutContainer;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.agriexpensett.upaccendpoint.model.UpAcc;

public class MainMenu extends ActionBarActivity {
	
	protected FlyOutContainer root;
	protected SignIn signInObject;
	
	public final static String APP_NAME = "AgriExpenseTT";
	
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.root=(FlyOutContainer) this.getLayoutInflater().inflate(R.layout.activity_main_menu, null);
		this.setContentView(root);
		signInObject = new SignIn(MainMenu.this,MainMenu.this);
		setupButtons();
		
		//Place Up Button Support
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	/*
	 * Dealing with Button Configurations
	 */

	private void setupButtons() {
		//Change the text of the sign-in button if user has signed in before
		Button btn_SignIn=(Button)findViewById(R.id.btn_SignIn);		
		UpAcc acc= signInObject.isExisting();
		if(acc!=null && acc.getSignedIn()== 1){
			btn_SignIn.setText("Sign Out");
		}
	}
	
	public void openNewCycleFragment(View view){		
		startActivity(new Intent(MainMenu.this,NewCycle.class));
	}
	
	public void openNewPurchaseFragment(View view){
		startActivity(new Intent(MainMenu.this,NewPurchase.class));
	}
	
	public void openManageLabourFragment(View view){
		startActivity(new Intent(MainMenu.this,HireLabour.class));
	}
	
	public void openManageResourceFragment(View view){
		Log.d(MainMenu.APP_NAME, "Launching Open Resource Fragment");
		startActivity(new Intent(MainMenu.this,ManageResources.class));
	}
	
	public void openManageDataFragment(View view){
		startActivity(new Intent(MainMenu.this,ManageData.class));
	}
	
	public void openManageExports(View view){
		startActivity(new Intent(MainMenu.this, ManageReport.class));		
	}
	
	public void openAboutFragment(View view){
		startActivity(new Intent(MainMenu.this, AboutScreen.class ));
	}

	public void openHelpFragment(View view){
		startActivity(new Intent(MainMenu.this, HelpScreen.class));
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
			signInObject.signIn();
		}
	}
	
	public void signInStart(View view){
		//TODO Open A new Activity
		
		DbHelper dbh=new DbHelper(MainMenu.this);
		SQLiteDatabase db=dbh.getReadableDatabase();
		UpAcc acc=DbQuery.getUpAcc(db);
		db.close();
		if(!isNetworkAvailable()){
			Toast.makeText(MainMenu.this, "No internet connection, Unable to sign-in at the moment.", Toast.LENGTH_SHORT).show();
			return;
		}
		if(acc.getSignedIn()==0){//not signed in
			if(acc.getCounty()==null||acc.getCounty().equals("")){//location was never set
				Intent i=new Intent(MainMenu.this,SelectLocation.class);
				startActivityForResult(i, 1);
			}else{
				signInObject.signIn();
			}
		}else{
			signInObject.signIn();
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
