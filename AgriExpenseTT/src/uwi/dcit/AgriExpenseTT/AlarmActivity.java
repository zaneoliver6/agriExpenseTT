package uwi.dcit.AgriExpenseTT;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.helpers.PrefUtils;
import uwi.dcit.AgriExpenseTT.helpers.ReminderHelper;

public class AlarmActivity extends AppCompatActivity {

    Spinner timeSpinner;
    Spinner optionDaySpinner;
    RadioGroup rg;

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

        // If Preferences and Alarm Configured Correctly Notify the User
        if (PrefUtils.setAlarmDetails(this, weekDay,  hour) && runAlarm(weekDay, hour)){
            GAnalyticsHelper.getInstance(this).sendPreference("Alarm", "Alarm Set", 1);// Alarm was set successfully
            Log.i("AlarmActivity", "Alarm Preferences Set Correctly");
            PrefUtils.setAlarmSet(this, true);
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_popup_reminder)
                    .setTitle("Alarm")
                    .setMessage("Alarm Preferences was successfully set. The alarm options can be changed later in the settings menu.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }else{
            GAnalyticsHelper.getInstance(this).sendPreference("Alarm", "Alarm Set", 1);
            PrefUtils.setAlarmSet(this, false);
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Unable to Save Alarm Details. Try again later.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    public boolean runAlarm(String weekDay, int hour) {
        try {
            if (hour != -1 && weekDay != null && !weekDay.equals("NIL")) {
                ReminderHelper.setAlarm(this, weekDay, hour);
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
            Log.e("AlarmActivity", "Alarm Failed To Set");
        }
        return false;
    }
}