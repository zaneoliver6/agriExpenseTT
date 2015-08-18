package uwi.dcit.AgriExpenseTT;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import java.util.Calendar;

import uwi.dcit.AgriExpenseTT.fragments.FragmentEmpty;
import uwi.dcit.AgriExpenseTT.fragments.FragmentSlidingMain;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.agriexpensesvr.accountApi.model.Account;


public class Main extends BaseActivity{

    private CharSequence mTitle;
    public final static String APP_NAME = "AgriExpenseTT";
    public final static String TAG = "Main";
    public String country="";
    public String county="";
    private String focus = "cycle";
    public SQLiteDatabase db;
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyAlarmPrefs" ;
    public static final String MyAlarmPreferencesWeekDay = "MyAlarmPrefsWeekDay" ;
    public static final String MyAlarmPreferencesHour = "MyAlarmPrefsHour" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_navigation);
        // Needed after setContentView to refer to the appropriate XML View
        setupNavDrawer();

        mTitle = getTitle();

        // Added Google Analytics
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Main Screen");
        //broadcastIntent();

        new Thread(new Runnable() {
            public void run() {
                String weekDay="";
                int hour=0;
                preferences(weekDay,hour);
                //runAlarm(hour,59,weekDay);
                runAlarm(15,"D");
            }
        }).start();
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
            //have to put in something here to purge transaction to ensure its still not running
            getSupportFragmentManager().executePendingTransactions();
        }
        super.onSaveInstanceState(outState);
    }

    public void AddNewCycle (View view){ //the Add Cycle Button calls this function
            Intent i = new Intent(this, NewCycle.class);
            startActivity(i);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case RequestCode_backup:
                if (resultCode == 1) {
                    country=data.getStringExtra("country");
                    county=data.getStringExtra("county");
                    Log.d("Main Activity","returned with "+country+" "+county);
                    signInManager.signIn(country,county);
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

    protected String onGetResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case 1:
                return country;
        }
        return null;
    }



    public void preferences(String weekDay, int hour){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        weekDay = sharedpreferences.getString(MyAlarmPreferencesWeekDay, "DEFAULT");
        hour = sharedpreferences.getInt(MyAlarmPreferencesHour, 99);
        //if(weekDay.equals("DEFAULT")&& hour==99){
            //GET WEEK/DAY VALUE AND HOUR VALUE
            Log.i("NEED TO SETUP ACTIVITY", " SETUP ACTIVITY PLEASE -- USER INFO!");
            SharedPreferences.Editor editor = sharedpreferences.edit();
            hour=15;
            weekDay="D";
            editor.putString(MyAlarmPreferencesWeekDay, weekDay);
            editor.putInt(MyAlarmPreferencesHour, hour);
            editor.commit();

            weekDay = sharedpreferences.getString(MyAlarmPreferencesWeekDay, "DEFAULT");
            hour = sharedpreferences.getInt(MyAlarmPreferencesHour, 99);
            Log.i("SET",""+weekDay+""+hour);
        //}
    }

    public void runAlarm(int hour, String type){
        Context ctx = this.getApplicationContext();
        Intent intent = new Intent();
        intent.setAction("android.intent.CustomAlarm");
        PendingIntent alarmIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        int timeValue;
        if(type.toUpperCase().equals("D"))
            timeValue=60;
        else
            timeValue=10080;
        AlarmManager alarmMgr = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
        if(alarmMgr==null) {
            alarmMgr.cancel(alarmIntent);
            //Set the alarm time.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, 23);
            // setRepeating() lets you specify a precise custom interval--in this case
            alarmMgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    1000 * 60 * timeValue, alarmIntent);
        }
    }
}
