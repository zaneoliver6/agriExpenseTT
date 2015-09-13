package uwi.dcit.AgriExpenseTT;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmActivity extends AppCompatActivity {
    Spinner sItems;
    Spinner sItems2;
    RadioGroup rg;
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyAlarmPrefs" ;
    public static final String MyAlarmPreferencesWeekDay = "MyAlarmPrefsWeekDay" ;
    public static final String MyAlarmPreferencesHour = "MyAlarmPrefsHour" ;
    public static final String MyAlarmSet = "MyAlarmSet";
    public static final String MyPreferencesSet = "MyPrefSet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("1:00");
        spinnerArray.add("2:00");
        spinnerArray.add("3:00");
        spinnerArray.add("4:00");
        spinnerArray.add("5:00");
        spinnerArray.add("6:00");
        spinnerArray.add("7:00");
        spinnerArray.add("8:00");
        spinnerArray.add("9:00");
        spinnerArray.add("10:00");
        spinnerArray.add("11:00");
        spinnerArray.add("12:00");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems = (Spinner) findViewById(R.id.spinner);
        sItems.setAdapter(adapter);

        List<String> spinnerArray2 = new ArrayList<>();
        spinnerArray2.add(" AM ");
        spinnerArray2.add(" PM ");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray2);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems2 = (Spinner) findViewById(R.id.spinner2);
        sItems2.setAdapter(adapter2);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
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


    public void buttonSelected(View view){

        int hour = sItems.getSelectedItemPosition()+1;
        String amPm = sItems2.getSelectedItem().toString();
        if(amPm.equals(" PM ") && hour==12)
            hour=0;
        else if(amPm.equals(" PM "))
            hour+=12;

        Log.i("Hi","SELECTED:::::---"+hour);

        rg =(RadioGroup) findViewById(R.id.radioGroup);
        int id = rg.getCheckedRadioButtonId();
        String weekDay;
        if(id==R.id.radioButton){
            Log.i("WEEKLY","WEEKLY");
            weekDay="W";
        }
        else{
            Log.i("DAILY","DAILY");
            weekDay="D";
        }
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(MyAlarmPreferencesWeekDay, weekDay);
        editor.putInt(MyAlarmPreferencesHour, hour);
        editor.putBoolean(MyPreferencesSet, true);
        editor.putBoolean(MyAlarmSet,false);
        editor.commit();
        Log.i("PREF SET","PREFERENCES SET!");
        runAlarm();
        finish();
    }

    public void runAlarm() {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean set = sharedpreferences.getBoolean(MyAlarmSet, false);
        if (!set) {
            String weekDay = sharedpreferences.getString(MyAlarmPreferencesWeekDay,"NIL");
            int hour = sharedpreferences.getInt(MyAlarmPreferencesHour, 99);
            if(hour!=99 && !weekDay.equals("NIL")) {
                Context ctx = this.getApplicationContext();
                Intent intent = new Intent();
                intent.setAction("android.intent.CustomAlarm");
                PendingIntent alarmIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
                int timeValue;
                if (weekDay.toUpperCase().equals("D"))
                    timeValue = 1440;
                    //A DAY
                else
                    timeValue = 10080;
                    //A WEEK
                AlarmManager alarmMgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                alarmMgr.cancel(alarmIntent);
                //Set the alarm time.
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, 0);
                // setRepeating() lets you specify a precise custom interval--in this case
                alarmMgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                        1000 * 60 * timeValue, alarmIntent);
                //Now that the alarm has been set, we can keep a track of this!
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(MyAlarmSet, true);
                editor.commit();
                Toast.makeText(this, "Alarm Set Successfully", Toast.LENGTH_SHORT).show();
                Log.i("ALARM", "ALARM SET");
            }
        }
    }
}