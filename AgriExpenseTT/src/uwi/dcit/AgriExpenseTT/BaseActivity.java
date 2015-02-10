package uwi.dcit.AgriExpenseTT;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import uwi.dcit.AgriExpenseTT.helpers.MenuHelper;
import uwi.dcit.AgriExpenseTT.helpers.NavigationControl;
import uwi.dcit.AgriExpenseTT.helpers.SignInManager;


public abstract class BaseActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks , NavigationControl {

    protected SignInManager signInManager;
    protected Fragment leftFrag,rightFrag;
    protected NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR); // Request Feature must be called before adding content
        super.onCreate(savedInstanceState);
        signInManager = new SignInManager(BaseActivity.this,BaseActivity.this);
    }

    public void setupNavDrawer(){
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (mNavigationDrawerFragment != null)
            mNavigationDrawerFragment.setUp( R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position){
            case 0:
                // Home
                startActivity(new Intent(getApplicationContext(), Main.class));
                break;
            case 1:
                //new cycle
                startActivity(new Intent(getApplicationContext(), NewCycle.class));
                break;
            case 2:
                //new purchase
                startActivity(new Intent(getApplicationContext(), NewPurchase.class));
                break;
            case 3:
                //hire labour
                startActivity(new Intent(getApplicationContext(), HireLabour.class));
                break;
            case 4:
                //report manager
                startActivity(new Intent(getApplicationContext(),ManageReport.class));
                break;
            case 5:
                // manage data
                startActivity(new Intent(getApplicationContext(),ManageData.class));
                break;
            case 6:
                backUpData();
                break;

        }
    }

    public void backUpData(){
    }

    @Override
    public void navigate(Fragment oldFrag,Fragment newFrag) {

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
        return new String[]{
                getString(R.string.menu_item_home),
                getString(R.string.menu_item_newCycle),
                getString(R.string.menu_item_newPurchase),
                getString(R.string.menu_item_hireLabour),
                getString(R.string.menu_item_genFile),
                getString(R.string.menu_item_manageData),
                getString(R.string.menu_item_signIn)
        };
    }

    @Override
    public int[] getMenuImages() {
        return new int[]{
                R.drawable.ic_home_black_36dp,
                R.drawable.mainmenu_cycle_triangle,
                R.drawable.mainmenu_shopping_cart,
                R.drawable.mainmenu_shovel_single,
                R.drawable.mainmenu_reports,
                R.drawable.mainmenu_data_settings,
                R.drawable.mainmenu_signin
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            // Respond to the action bar's Up/Home button
//            case android.R.id.home:
//                MenuHelper.handleClick(getResources().getString(R.string.menu_item_home), this);
//                return true;
//        }
        if (item.getTitle() != null)
            Log.d("Base Activity", item.getTitle().toString());
        else
            Log.d("Base Activity", "No Title Received");
        return super.onOptionsItemSelected(item);
    }
}
