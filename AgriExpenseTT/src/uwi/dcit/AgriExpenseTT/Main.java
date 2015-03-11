package uwi.dcit.AgriExpenseTT;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.cocosw.undobar.UndoBarController;
import com.cocosw.undobar.UndoBarController.UndoBar;

import uwi.dcit.AgriExpenseTT.fragments.FragmentEmpty;
import uwi.dcit.AgriExpenseTT.fragments.FragmentSlidingMain;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;


public class Main extends BaseActivity implements UndoBarController.UndoListener {

    private CharSequence mTitle;
    public final static String APP_NAME = "AgriExpenseTT";
    private UndoBar undobar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_navigation);
        // Needed after setContentView to refer to the appropriate XML View
        setupNavDrawer();

        mTitle = getTitle();

        // Check for orientation to determine which interface to load => if portrait will use leftfrag
        if(this.isTablet && this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setupLand();
        }else {
            setupPort();
        }
        // Added Google Analytics
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Main Screen");

//        final Bundle b = new Bundle();
//        b.putInt("index", 1);
//        undobar = new UndoBarController.UndoBar(this).listener(this);
//        undobar.message("Message Bar Created")
//                .noicon(true)
//                .token(b)
//                .show();
    }

    private void setupPort() {
        Fragment frag = new FragmentSlidingMain();

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.navContentLeft,frag)
            .commit();
    }

    private void setupLand() {
        leftFrag = new FragmentSlidingMain();
        rightFrag = new FragmentEmpty();

        Bundle arguments=new Bundle();
        arguments.putString("type","select");
        rightFrag.setArguments(arguments);

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.navContentLeft, leftFrag)
            .replace(R.id.navContentRight, rightFrag)
            .commit();
    }

    public void restoreActionBar() {
//        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
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
    public void onBackPressed(){

        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    @Override
    public void navigate(Fragment oldFrag,Fragment newFrag) {
        FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
        if(this.isTablet && this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){

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
    protected void onSaveInstanceState(Bundle outState) {
        if(findViewById(R.id.navContentRight)!=null){
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.remove(rightFrag).commit();
            //have to put in someting here to purge transaction to ensure its still not running
            getSupportFragmentManager().executePendingTransactions();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == RequestCode_backup) {
            // Make sure the request was successful
            if (resultCode == 1) {
                String country=data.getStringExtra("country");
                String county=data.getStringExtra("county");
                Log.d("Main Activity","returned with "+country+" "+county);
                signInManager.signIn();
            }
        }
    }

    public void openNewCycle(View view){
        startActivity(new Intent(getApplicationContext(), NewCycle.class));
    }

    public void openNewPurchase(View view){
        startActivity(new Intent(getApplicationContext(), NewPurchase.class));
    }

    @Override
    public void onUndo(@Nullable Parcelable parcelable) {
        Bundle res = (Bundle)parcelable;
        Toast.makeText(this, "Received: " + res.getInt("index"), Toast.LENGTH_SHORT).show();
    }
}
