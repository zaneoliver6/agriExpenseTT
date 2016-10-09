package uwi.dcit.AgriExpenseTT.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class ReminderHelper {

	public static void setAlarm(Context context) {
		String[] details = PrefUtils.getAlarmDetails(context);
		String weekDay = details[0];
		int hour = Integer.parseInt(details[1]);
		setAlarm(context, weekDay, hour);
	}

	public static void setAlarm(Context context, String weekDay, int hour) {
		int timeValue;
		if (weekDay.toUpperCase().equals("D")) timeValue = 1440; // Day
		else timeValue = 10080; // Evening
		//Set the alarm time.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, 0);
		// Configure the Alarm Manager and Intent needed
		final AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		final Intent intent = new Intent();
		intent.setAction("android.intent.CustomAlarm");
		final PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		alarmMgr.cancel(alarmIntent); // Remove existing alarm to prevent duplication
		alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000 * 60 * timeValue, alarmIntent);
		// NB the Reminder broadcaster also has the code to enable the alarm to be set accordingly
		Log.d("ReminderHelper", "Alarm was successfully configured using a PendingIntent");
	}
}
