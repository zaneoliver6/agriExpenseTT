package uwi.dcit.AgriExpenseTT;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Calendar;

import uwi.dcit.AgriExpenseTT.helpers.InputFilterMinMax;

public class AlarmActivity extends AppCompatActivity {

    EditText eText;
    Button btn;
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
        eText = (EditText) findViewById(R.id.editText);
        eText.setFilters(new InputFilter[]{new InputFilterMinMax("1", "24")});
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

    public void buttonSelected(View view) {
        btn = (Button) findViewById(R.id.button2);
        String hour = eText.getText().toString();
        int hr=Integer.parseInt(hour);

        rg =(RadioGroup) findViewById(R.id.radioGroup);
        int id = rg.getCheckedRadioButtonId();
        String weekDay=null;
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
        editor.putInt(MyAlarmPreferencesHour, hr);
        editor.putBoolean(MyPreferencesSet, true);
        editor.putBoolean(MyAlarmSet,false);
        editor.commit();
        Log.i("PREF SET","PREFERENCES SET");
        runAlarm();
        return;
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
                    //AN HOUR
                else
                    timeValue = 10080;
                //A DAY
                AlarmManager alarmMgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                alarmMgr.cancel(alarmIntent);
                //Set the alarm time.
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, 35);
                // setRepeating() lets you specify a precise custom interval--in this case
                alarmMgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                        1000 * 60 * timeValue, alarmIntent);
                //Now that the alarm has been set, we can keep a track of this!
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(MyAlarmSet, true);
                editor.commit();
                Log.i("ALARM", "ALARM SET");
            }
        }
    }
}
