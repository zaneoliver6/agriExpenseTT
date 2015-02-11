package uwi.dcit.AgriExpenseTT;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.fragments.help.FragmentSlidingTabsEdit;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.helpers.NavigationControl;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;
import uwi.dcit.AgriExpenseTT.models.LocalResourcePurchase;

public class EditData extends ActionBarActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,NavigationControl {
	ArrayList<LocalCycle> li;
	ArrayList<LocalResourcePurchase> pli;
//	public final int req_cycle=1; //TODO Remove unused fields
//	final int req_purchase=2;
	DbHelper dbh;
	SQLiteDatabase db;
    Fragment leftFrag,rightFrag;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_navigation);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Fragment fragment=new FragmentSlidingTabsEdit();
        FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.navContentLeft,fragment);
        ft.commit();

        // Google Analytics
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Edit Data Screen");
	}

    @Override
    public void navigate(Fragment oldFrag, Fragment newFrag) {

    }

    @Override
    public Fragment getLeftFrag() {
        return leftFrag;
    }

    @Override
    public Fragment getRightFrag() {
        return rightFrag;
    }

    @Override
    public String[] getMenuOptions() {
        return new String[0];
    }

    @Override
    public int[] getMenuImages() {
        return new int[0];
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

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
