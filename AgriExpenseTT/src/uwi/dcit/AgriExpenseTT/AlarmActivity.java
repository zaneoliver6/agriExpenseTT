package uwi.dcit.AgriExpenseTT;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyAlarmPrefs";
    public static final String MyAlarmPreferencesWeekDay = "MyAlarmPrefsWeekDay";
    public static final String MyAlarmPreferencesHour = "MyAlarmPrefsHour";
    public static final String MyAlarmSet = "MyAlarmSet";
    public static final String MyPreferencesSet = "MyPrefSet";
    Spinner timeSpinner;
    Spinner optionDaySpinner;
    RadioGroup rg;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        List<String> spinnerArray = new ArrayList<>();
        // Adding the text from 1 to 12
        for (int i = 1; i < 13; i++) {
            spinnerArray.add(i + ":00");
        }

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner = (Spinner) findViewById(R.id.hour);
        timeSpinner.setAdapter(timeAdapter);
        timeSpinner.setSelection(4);

        List<String> optionDay = new ArrayList<>();
        optionDay.add(" AM ");
        optionDay.add(" PM ");

        ArrayAdapter<String> optionDayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionDay);
        optionDayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        optionDaySpinner = (Spinner) findViewById(R.id.am_pm);
        optionDaySpinner.setAdapter(optionDayAdapter);
        optionDaySpinner.setSelection(1);

    }


    public void setAlarm(View view) {

        int hour = timeSpinner.getSelectedItemPosition() + 1;
        String amPm = optionDaySpinner.getSelectedItem().toString();
        if(amPm.equals(" PM ") && hour==12)
            hour = 0;
        else if(amPm.equals(" PM "))
            hour += 12;

        Log.i("AlarmActivity", "SELECTED " + hour);

        rg = (RadioGroup) findViewById(R.id.interval_option);
        int id = rg.getCheckedRadioButtonId();
        String weekDay;
        if (id == R.id.weekly_option) {
            Log.i("AlarmActivity", "Selected WEEKLY");
            weekDay="W";
        }
        else{
            Log.i("AlarmActivity", "Selected DAILY");
            weekDay="D";
        }
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(MyAlarmPreferencesWeekDay, weekDay);
        editor.putInt(MyAlarmPreferencesHour, hour);
        editor.putBoolean(MyPreferencesSet, true);
        editor.putBoolean(MyAlarmSet,false);
        editor.apply();
        Log.i("AlarmActivity", "PREFERENCES SET!");
        runAlarm();
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert) //TODO Change to alarm icon from material library
                .setTitle("Alarm")
                .setMessage("Alarm Preferences was successfully set. The alarm options can be changed later in the settings menu.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
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
                if (weekDay.toUpperCase().equals("D"))  //A DAY
                    timeValue = 1440;
                else                                    //A WEEK
                    timeValue = 10080;
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
                editor.apply();
                Toast.makeText(this, "Alarm Set Successfully", Toast.LENGTH_SHORT).show();
                Log.i("AlarmActivity", "ALARM SET");
            }
        }
    }
}