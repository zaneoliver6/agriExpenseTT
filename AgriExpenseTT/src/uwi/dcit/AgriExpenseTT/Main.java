package uwi.dcit.AgriExpenseTT;


import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import uwi.dcit.AgriExpenseTT.fragments.FragmentEmpty;
import uwi.dcit.AgriExpenseTT.fragments.FragmentSlidingMain;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.helpers.NavigationControl;
import uwi.dcit.AgriExpenseTT.helpers.NetworkHelper;
import uwi.dcit.AgriExpenseTT.helpers.SignInManager;


public class Main extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,NavigationControl {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    Fragment leftFrag,rightFrag;
    protected SignInManager signInManager;

    //if portrait will use leftfrag
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_navigation);
        signInManager = new SignInManager(Main.this,Main.this);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp( R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        //Check for orientation to determine which interface to load
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setupLand();
        }else {
            setupPort();
        }
        // Added Google Analytics
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Main Screen");
    }

    private void setupPort() {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.navContentLeft,new FragmentSlidingMain())
            .commit();
    }
    private void setupLand() {
        Fragment fragment=new FragmentSlidingMain();

        Fragment emptyFrag=new FragmentEmpty();
        Bundle arguments=new Bundle();
        arguments.putString("type","select");
        emptyFrag.setArguments(arguments);
        FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
        leftFrag=fragment;
        rightFrag=emptyFrag;
        ft.replace(R.id.navContentLeft,fragment);
        ft.replace(R.id.navContentRight,emptyFrag);
        ft.commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
//        Toast.makeText(getApplicationContext(),"something",Toast.LENGTH_SHORT).show();
        switch (position){

            case 0:
                //new cycle
                startActivity(new Intent(getApplicationContext(), NewCycle.class));
                break;
            case 1:
                //new purchase
                startActivity(new Intent(getApplicationContext(), NewPurchase.class));
                break;
            case 2:
                startActivity(new Intent(getApplicationContext(), HireLabour.class));
                //hire labour
                break;
            case 3:
                //something else
                startActivity(new Intent(getApplicationContext(),ManageReport.class));
                break;
            case 4:
                startActivity(new Intent(getApplicationContext(),ManageData.class));
                break;
            case 5:
                backUpData();
                break;

        }

    }

    public void restoreActionBar() {
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        return new String[]{getString(R.string.menu_item_newCycle),
                getString(R.string.menu_item_newPurchase),getString(R.string.menu_item_hireLabour),getString(R.string.menu_item_genFile)
                ,getString(R.string.menu_item_manageData)
        ,getString(R.string.menu_item_signIn)};
    }

    @Override
    public int[] getMenuImages() {
        return new int[]{R.drawable.mainmenu_cycle_triangle
                ,R.drawable.mainmenu_shopping_cart,R.drawable.mainmenu_shovel_single,
                R.drawable.mainmenu_reports,R.drawable.mainmenu_data_settings,
                R.drawable.mainmenu_signin};
    }

    @Override
    public void navigate(Fragment oldFrag,Fragment newFrag) {
        FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
        if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
            Class reflectClass = oldFrag.getClass();
            Bundle arguments=oldFrag.getArguments();
            try {
                oldFrag = (Fragment)reflectClass.newInstance();
            } catch (Exception e){e.printStackTrace();}
            oldFrag.setArguments(arguments);
            ft.replace(R.id.navContentLeft, oldFrag);
            leftFrag=oldFrag;
            leftFrag.setRetainInstance(true);
            ft.replace(R.id.navContentRight,newFrag).addToBackStack("left");
            rightFrag=newFrag;
            rightFrag.setRetainInstance(false);
        }else{
            ft.replace(R.id.navContentLeft,newFrag).addToBackStack("right");
        }
        ft.commit();
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
    public void openNewCycle(View view){
        startActivity(new Intent(getApplicationContext(),NewCycle.class));
    }
    public void openNewPurchase(View view){
        startActivity(new Intent(getApplicationContext(),NewPurchase.class));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(findViewById(R.id.navContentRight)!=null){
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.remove(rightFrag).commit();
            //have to put in someting here to purge transaction to ensure its still not running
            getSupportFragmentManager().executePendingTransactions();
        }
        super.onSaveInstanceState(outState);
    }
    public void backUpData(){
        Intent i = new Intent(getApplicationContext(), Backup.class);
        if (this.signInManager.isExisting() == null){ 			// User does not exist => check Internet and then create user
            if (!NetworkHelper.isNetworkAvailable(this)){ 		// No network available so display appropriate message
                Toast.makeText(getApplicationContext(), "No internet connection, Unable to sign-in at the moment.", Toast.LENGTH_LONG).show();
                return;
            }
            i.putExtra("ACTION",  Backup.SIGN_UP); 				// Launch the Backup activity with the sign-up action passed
        }else if (!this.signInManager.isSignedIn()){ 			// If not signed attempt to login with existing account
            i.putExtra("ACTION",  Backup.SIGN_IN); 				// Launch the Backup activity with the sign-in action passed
        }else i.putExtra("ACTION", Backup.VIEW);				// Launch the Backup activity to simply view the data because user is existing and signed in
        startActivity(i);
    }
}
