package uwi.dcit.AgriExpenseTT.helpers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

import uwi.dcit.AgriExpenseTT.Main;
import uwi.dcit.AgriExpenseTT.R;


public class ReminderBroadcaster extends BroadcastReceiver{
	private SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyAlarmPrefs" ;
	public static final String MyAlarmPreferencesWeekDay = "MyAlarmPrefsWeekDay" ;
	public static final String MyAlarmPreferencesHour = "MyAlarmPrefsHour" ;
	public static final String MyAlarmSet = "MyAlarmSet";

	SQLiteDatabase db;
	DbHelper dbh;
	public static String NOTIFICATION_ID = "agriexpense_id";
    public static String NOTIFICATION = "agriexpesne";
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;
    
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("AgriExpense- Reminder", "Received Broadcast");
		dbh = new DbHelper(context);
		db = dbh.getWritableDatabase();

		//		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		//        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
		//        notificationManager.notify(id, notification);

		if(intent.getAction().equals("android.intent.CustomAlarm")){
			Intent resultIntent = new Intent(context, Main.class);
			// Because clicking the notification opens a new ("special") activity, there's
			// no need to create an artificial back stack.
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
			mBuilder.setSmallIcon(R.drawable.logo_agrinet)
					.setContentTitle("Agri-Expense Alert!")
					.setContentText("Hi, Please Don't Forget to Enter Your Data!")
					.setAutoCancel(true)
					.setContentIntent(PendingIntent.getActivity(context, 0, resultIntent, 0));

			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(0, mBuilder.build());
		}

		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Log.i("HELLO","WE REBOOTED!");
			alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			int hour;
			String weekDay;
			sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
			weekDay = sharedpreferences.getString(MyAlarmPreferencesWeekDay, "DEFAULT");
			hour = sharedpreferences.getInt(MyAlarmPreferencesHour, 99);

			if(weekDay.equals("DEFAULT")&& hour==99){
				//GET WEEK/DAY VALUE AND HOUR VALUE
				Log.i("NEED TO SETUP ACTIVITY", " SETUP ACTIVITY PLEASE -- USER INFO!");
			}
			else {
				int timeValue;
				if (weekDay.toUpperCase().equals("D"))
					timeValue = 60;
				else
					timeValue = 25200;
				intent = new Intent();
				intent.setAction("android.intent.CustomAlarm");
				alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
				// Set the alarm to start at 8:30 a.m.
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				//NOTE MINUTE HAS BEEN SET TO 0
				calendar.set(Calendar.MINUTE, 23);
				// setRepeating() lets you specify a precise custom interval
				alarmMgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
						1000 * 60 * timeValue, alarmIntent);
				//Now that the alarm has been set, we can keep a track of this!
				SharedPreferences.Editor editor = sharedpreferences.edit();
				editor.putBoolean(MyAlarmSet,true);
				editor.commit();
			}
		}
	}
}
