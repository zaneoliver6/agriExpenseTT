package uwi.dcit.AgriExpenseTT;

<<<<<<< HEAD
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
=======
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
>>>>>>> 5e9d2318b9214de3ccb86720f6fe26c21d577e50

import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.fragments.help.FragmentSlidingTabsEdit;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
<<<<<<< HEAD
=======
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
>>>>>>> 5e9d2318b9214de3ccb86720f6fe26c21d577e50
import uwi.dcit.AgriExpenseTT.models.LocalCycle;
import uwi.dcit.AgriExpenseTT.models.LocalResourcePurchase;

public class EditData extends ActionBarActivity {
	ArrayList<LocalCycle> li;
	ArrayList<LocalResourcePurchase> pli;
//	public final int req_cycle=1; //TODO Remove unused fields
//	final int req_purchase=2;
	DbHelper dbh;
	SQLiteDatabase db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_navigation);
        Fragment fragment=new FragmentSlidingTabsEdit();
        FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.navContent,fragment);
        ft.commit();
        /*
		//ActionBar.NavigationMode = ActionBarNavigationMode.Tabs;
<<<<<<< HEAD

=======
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_navigation);
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Edit Data");
>>>>>>> 5e9d2318b9214de3ccb86720f6fe26c21d577e50
		//for empty lists
		dbh=new DbHelper(EditData.this);
		db=dbh.getReadableDatabase();
		li=new ArrayList<LocalCycle>();
		DbQuery.getCycles(db, dbh, li);
		pli=new ArrayList<LocalResourcePurchase>();
		DbQuery.getPurchases(db, dbh, pli, null, null,true);
		
		TabListener tL=new TabListener();
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		ActionBar.Tab t1 = getActionBar().newTab();
		t1.setText("Purchases");
    	t1.setTabListener(tL);
    	
    	ActionBar.Tab t3 = getActionBar().newTab();
    	t3.setText("Cycles");
    	t3.setTabListener(tL);
    	
    	getActionBar().addTab(t3);
    	getActionBar().addTab(t1);
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
			Fragment n2=null;
			Bundle data=new Bundle();
			if(currFragment==null){
				if(li.isEmpty()){
					n2=new FragmentEmpty();
					data.putString("type", "cycle");
					n2.setArguments(data);
					ft.add(R.id.navContent, n2);
				}else{
					n2=new FragmentViewCycles();
					data.putString("type", "edit");
					n2.setArguments(data);
					ft.add(R.id.navContent, n2);
				}
				currFragment=n2;
				return;
			}
			if(tab.getText().toString().equals("Purchases")){
				if(pli.isEmpty()){
					n2=new FragmentEmpty();
					data.putString("type", "purchase");
				}else{
					n2=new ChoosePurchaseFragment();
					data.putString("det","edit");
				}
			}else if(tab.getText().toString().equals("Cycles")){
				if(li.isEmpty()){
					n2=new FragmentEmpty();
					data.putString("type", "cycle");
				}else{
					n2=new FragmentViewCycles();
					data.putString("type","edit");
				}
			}
			currFragment=n2;
			n2.setArguments(data);
			ft.replace(R.id.navContent, n2);
		}

		@Override
		public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
			
		}
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_data, menu);
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
	*/
}
