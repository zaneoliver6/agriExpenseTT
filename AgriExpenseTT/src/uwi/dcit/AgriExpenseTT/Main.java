package uwi.dcit.AgriExpenseTT;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;

import uwi.dcit.AgriExpenseTT.fragments.FragmentEmpty;
import uwi.dcit.AgriExpenseTT.fragments.FragmentSlidingMain;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;


public class Main extends BaseActivity{

    private CharSequence mTitle;
    public final static String APP_NAME = "AgriExpenseTT";
    public final static String TAG = "Main";

    private String focus = "cycle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_navigation);
        // Needed after setContentView to refer to the appropriate XML View
        setupNavDrawer();

        mTitle = getTitle();

        // Added Google Analytics
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Main Screen");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume Method was called");
        buildScreen();
    }

    private void buildScreen(){
        Log.d(TAG, "Value of Focus is: " + focus + " where build screen was called");
        if(this.isTablet && this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setupLand();
        }else {
            setupPort();
        }
    }

    private void setupPort() {
        Fragment frag = new FragmentSlidingMain();
        Bundle bundle = new Bundle();
        bundle.putString("type", focus);
        frag.setArguments(bundle);

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.navContentLeft,frag)
            .commit();
    }

    private void setupLand() {
        leftFrag = new FragmentSlidingMain();
        rightFrag = new FragmentEmpty();

        Bundle bundle = new Bundle();
        bundle.putString("type", focus);
        leftFrag.setArguments(bundle);

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
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

        switch (requestCode){
            case RequestCode_backup:
                if (resultCode == 1) {
                    String country=data.getStringExtra("country");
                    String county=data.getStringExtra("county");
                    Log.d("Main Activity","returned with "+country+" "+county);
                    signInManager.signIn();
                }
                break;
            case DHelper.CYCLE_REQUEST_CODE:
                focus = "cycle";
                buildScreen();
                break;
            case DHelper.PURCHASE_REQUEST_CODE:
                focus = "purchase";
                buildScreen();
                break;
        }
    }
}
