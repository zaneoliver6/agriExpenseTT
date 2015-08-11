package uwi.dcit.AgriExpenseTT.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import uwi.dcit.AgriExpenseTT.Main;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.agriexpensesvr.accountApi.model.Account;

public class ReminderBroadcaster extends BroadcastReceiver {
	SQLiteDatabase db;
	DbHelper dbh;
	public static String NOTIFICATION_ID = "agriexpense_id";
    public static String NOTIFICATION = "agriexpesne";
    
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("AgriExpense- Reminder", "Received Broadcast");
		dbh = new DbHelper(context);
		db = dbh.getWritableDatabase();

//		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//		Notification notification = intent.getParcelableExtra(NOTIFICATION);
//        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
//        notificationManager.notify(id, notification);
		if(intent.getAction().equals("android.CUSTOM_INTENT")){
			Account acc = DbQuery.getUpAcc(db);
			long time= System.currentTimeMillis();
			//Setting reminder to happen if account doesn't get updated in an hour(Using an hour for testing purposes!)!
			if(acc!=null && time-acc.getLastUpdated()>600) {

				Intent resultIntent = new Intent(context, Main.class);
				// Because clicking the notification opens a new ("special") activity, there's
				// no need to create an artificial back stack.
				PendingIntent resultPendingIntent =
					PendingIntent.getActivity(
							context,
							0,
							resultIntent,
							PendingIntent.FLAG_UPDATE_CURRENT
					);
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
				mBuilder.setSmallIcon(R.drawable.logo_agrinet);
				mBuilder.setContentTitle("Agri-Expense Alert!");
				mBuilder.setContentText("Hi, Please Don't Forget to Enter Your Data!");
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(0, mBuilder.build());

			}
		}
	}

}
