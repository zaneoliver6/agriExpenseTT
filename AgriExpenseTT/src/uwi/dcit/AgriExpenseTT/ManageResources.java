package uwi.dcit.AgriExpenseTT;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.fragments.FragmentSlidingTabsManageRes;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;
import uwi.dcit.AgriExpenseTT.models.LocalResourcePurchase;

public class ManageResources extends ActionBarActivity {
	
	private ArrayList<LocalCycle> li;
	private ArrayList<LocalResourcePurchase> pli;
	private DbHelper dbh;
	private SQLiteDatabase db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_navigation);

		Log.d(MainMenu.APP_NAME, "OnCreate Method was ran");
        Fragment fragment=new FragmentSlidingTabsManageRes();
        FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.navContent,fragment);
        ft.commit();
		/*

        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Manage Resources");
		
>>>>>>> 5e9d2318b9214de3ccb86720f6fe26c21d577e50
		//for empty lists
		dbh	= new DbHelper(ManageResources.this);
		db	= dbh.getReadableDatabase();
		
		if (db == null)Log.d(MainMenu.APP_NAME, "Database is null");
		else Log.d(MainMenu.APP_NAME, "Database is not null");
		
		//Retrieve Cycles
		li = new ArrayList<LocalCycle>();
		DbQuery.getCycles(db, dbh, li);
		
		Log.d(MainMenu.APP_NAME, "Found Cycles: "+li.size());
		
		//Retrieve Purchases
		pli = new ArrayList<LocalResourcePurchase>();
		DbQuery.getPurchases(db, dbh, pli, null, null,true);
		
		Log.d(MainMenu.APP_NAME, "Found Purchases: " + pli.size());
		
		// Initialize and Set the name of the tabs
		TabListener tL=new TabListener();
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


		ActionBar.Tab t1 = getActionBar().newTab();
		t1.setText("Purchases").setTabListener(tL);
		
    	ActionBar.Tab t2 = getActionBar().newTab();
    	t2.setText("Resources").setTabListener(tL);

    	ActionBar.Tab t3 = getActionBar().newTab();
    	t3.setText("Cycles").setTabListener(tL);
    	
    	//Add the tabs in order of [cycle, purchases, resources]
    	getActionBar().addTab(t3);
    	getActionBar().addTab(t1);
    	getActionBar().addTab(t2);
    	
    	Log.d(MainMenu.APP_NAME, "Completed the OnCreate Method");
    	*/
	}
	
	/*
	public class TabListener implements ActionBar.TabListener{
		ActionBarActivity mActivity;
		Fragment currFragment;
		
		@Override
		public void onTabReselected(Tab arg0, FragmentTransaction ft) {
			
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Fragment fragment	= null;
			Bundle arguments	= new Bundle();
			
			if(currFragment == null){
				if(li.isEmpty()){
					fragment = new FragmentEmpty();
					arguments.putString("type", "cycle");
					fragment.setArguments(arguments);
				}else{
					fragment=new FragmentViewCycles();
				}
				ft.replace(R.id.navContent, fragment);
				currFragment = fragment;
				return;
			}
			
			if(tab.getText().toString().equals("Purchases")){
				if(pli.isEmpty()){
					fragment=new FragmentEmpty();
					arguments.putString("type", "purchase");
				}else{
					fragment=new ChoosePurchaseFragment();
				}
			}else if(tab.getText().toString().equals("Cycles")){
				if(li.isEmpty()){
					fragment=new FragmentEmpty();
					arguments.putString("type", "cycle");
				}else{
					fragment=new FragmentViewCycles();
				}
			}else if(tab.getText().toString().equals("Resources")){
				fragment=new FragmentViewResources();
			}
			currFragment=fragment;
			fragment.setArguments(arguments);
			ft.replace(R.id.navContent, fragment);
		}


		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_navigation, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */

}
