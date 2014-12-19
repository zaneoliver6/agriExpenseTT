package uwi.dcit.AgriExpenseTT.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReminderBroadcaster extends BroadcastReceiver {

	public static String NOTIFICATION_ID = "agriexpense_id";
    public static String NOTIFICATION = "agriexpesne";
    
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("AgriExpense- Reminder", "Received Broadcast");
		
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		 
		Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);
	}

}
