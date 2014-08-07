package uwi.dcit.AgriExpenseTT;

import com.example.agriexpensett.upaccendpoint.model.UpAcc;

import helper.CSVHelper;
import helper.DbHelper;
import helper.DbQuery;
import helper.FlyOutContainer;
import android.support.v7.app.ActionBarActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainMenu extends ActionBarActivity {
	FlyOutContainer root;
	SignIn s;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.root=(FlyOutContainer) this.getLayoutInflater().inflate(R.layout.activity_main_menu, null);
		this.setContentView(root);
		s=new SignIn(MainMenu.this,MainMenu.this);
		//setContentView(R.layout.activity_main_menu);

		setupButtons();
	}

	private void setupButtons() {
		Button btn_newCycle=(Button)findViewById(R.id.newCycle);
		Button btn_purchase=(Button)findViewById(R.id.NewPurchase);
		
		Button btn_CycleDet=(Button)findViewById(R.id.ResDetail);
		Button btn_SignIn=(Button)findViewById(R.id.btn_SignIn);
		Button btn_HireLabour=(Button)findViewById(R.id.HireLabour);
		UpAcc acc=s.isExisting();
		if(acc!=null && acc.getSignedIn()==1){
			btn_SignIn.setText("Sign Out");
		}
		click c=new click();
		btn_newCycle.setOnClickListener(c);
		btn_purchase.setOnClickListener(c);
		
		btn_CycleDet.setOnClickListener(c);
		btn_SignIn.setOnClickListener(c);
		btn_HireLabour.setOnClickListener(c);
		
		Button btn_manageD=(Button)findViewById(R.id.manageData);
		btn_manageD.setOnClickListener(c);
		Button btn_gen=(Button)findViewById(R.id.generateFile);
		btn_gen.setOnClickListener(c);
	}
	public class click implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent nextActivity = null;
			if(v.getId()==R.id.newCycle){
				System.out.println("Select new Cycle");
				nextActivity=new Intent(MainMenu.this,NewCycleRedesigned.class);
			}else if(v.getId()==R.id.NewPurchase){
				System.out.println("New Purchase");
				nextActivity=new Intent(MainMenu.this,NewPurchaseRedesign.class);
			}else if(v.getId()==R.id.ResDetail){
				nextActivity=new Intent(MainMenu.this,ViewNavigation.class);
			}else if(v.getId()==R.id.btn_SignIn){
				signInStart();
				//testing shit
				return;
			}else if(v.getId()==R.id.HireLabour){
				nextActivity=new Intent(MainMenu.this,HireLabour.class);
			}else if(v.getId()==R.id.manageData){
				nextActivity=new Intent(MainMenu.this,ManageData.class);
			}else if(v.getId()==R.id.generateFile){
				CSVHelper cvh=new CSVHelper(MainMenu.this);
				cvh.stuff(MainMenu.this);
				return;
			}
			startActivity(nextActivity);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
			s.signIn();//TODO
		}
	}
	
	public void signInStart(){
		DbHelper dbh=new DbHelper(MainMenu.this);
		SQLiteDatabase db=dbh.getReadableDatabase();
		UpAcc acc=DbQuery.getUpAcc(db);
		db.close();
		if(!isNetworkAvailable()){
			Toast.makeText(MainMenu.this, "No internet connection", Toast.LENGTH_SHORT).show();
			return;
		}
		if(acc.getSignedIn()==0){//not signed in
			if(acc.getCounty()==null||acc.getCounty().equals("")){//location was never set
				Intent i=new Intent(MainMenu.this,SelectLocation.class);
				startActivityForResult(i, 1);
			}else{
				s.signIn();
			}
		}else{
			s.signIn();
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
