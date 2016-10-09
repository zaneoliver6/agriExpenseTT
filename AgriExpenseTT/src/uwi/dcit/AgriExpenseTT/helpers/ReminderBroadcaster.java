package uwi.dcit.AgriExpenseTT.helpers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import uwi.dcit.AgriExpenseTT.Main;
import uwi.dcit.AgriExpenseTT.R;

/**
 * The Reminder Broadcaster will highligh
 */

public class ReminderBroadcaster extends BroadcastReceiver{
	private static final String TAG = "ReminderBroadcaster";
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Received Broadcast for AgriExpense");
		if(intent.getAction().equals("android.intent.CustomAlarm")){
			Log.d(TAG, "Intent for the Custom Alarm received");
			displayReminderNotification(context);
		}

		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Log.i(TAG, "Detected Boot broadcast setting up alarm for reminder");
			configureReminder(context);
		}
	}

	private void configureReminder(Context context) {
		ReminderHelper.setAlarm(context);
	}

	/**
	 * The display reminder notification method will display the notification when the broad cast is received
	 *
	 * @param context
	 */
	private void displayReminderNotification(Context context) {
		Intent resultIntent = new Intent(context, Main.class);
		// Because clicking the notification opens a new ("special") activity, there's no need to create an artificial back stack.
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setSmallIcon(R.drawable.logo_agrinet)
				.setContentTitle("AgriExpense Reminder!")
				.setContentText(context.getString(R.string.reminder_msg))
				.setAutoCancel(true)
				.setContentIntent(PendingIntent.getActivity(context, 0, resultIntent, 0));

		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());
	}


}
