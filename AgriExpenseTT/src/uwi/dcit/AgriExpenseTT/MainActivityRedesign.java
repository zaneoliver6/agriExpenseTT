package uwi.dcit.AgriExpenseTT;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import uwi.dcit.AgriExpenseTT.fragments.FragmentEmpty;
import uwi.dcit.AgriExpenseTT.fragments.FragmentSlidingMain;
import uwi.dcit.AgriExpenseTT.helpers.NavigationControl;


public class MainActivityRedesign extends ActionBarActivity implements NavigationControl,NavigationDrawerFragment.NavigationDrawerCallbacks {
    Fragment leftFrag,rightFrag;
    //if portrait will use leftfrag
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_navigation);
        if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE) {
            setupLand();
        }else {
            setupPort();
        }
    }
    private void setupPort() {
        Fragment fragment=new FragmentSlidingMain();
        FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.navContentLeft,fragment);
        ft.commit();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_redesign, menu);
        return true;
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
            ft.replace(R.id.navContentRight,newFrag);
            rightFrag=newFrag;
        }else{
            ft.replace(R.id.navContentLeft,newFrag);
        }
        ft.commit();
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
       /* // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();*/
    }
}
