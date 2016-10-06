package uwi.dcit.AgriExpenseTT.helpers;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import uwi.dcit.AgriExpenseTT.R;

/**
 * The Google Analytics Helper will provide a wrapper for ensuring that the functionality defined can be resued across the various application states of the program
 */
public class GAnalyticsHelper {

    public static final String APP_TRACKER = "AgriExpense"; //Tracker used only in this app
    private static final String TAG = "GAnalytics";
    private final String userEmail;
    private Tracker tracker;
    private final Context context;

    private static final boolean enableTracking = false;

    private static GAnalyticsHelper instance = null;

    private GAnalyticsHelper(Context context){
        this.context = context;
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        this.tracker = analytics.newTracker(R.xml.global_tracker);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        tracker.enableExceptionReporting(true);
        userEmail = PrefUtils.getUserEmail(context);
    }

    private boolean canSend(){
        return enableTracking;
    }
    // Make sense to keep a singleton class to make sure trackers are not reinitialized every time its called
    public static GAnalyticsHelper getInstance(Context context){
        if (instance == null)instance = new GAnalyticsHelper(context);
        return instance;
    }

    public Tracker getTracker(){
        return this.tracker;
    }

    /**
     *
     * @param category
     * @param action
     * @param eventName
     * @param status
     */
     public void sendEvent(String category, String action, String eventName, int status){
         if (canSend()) {
             tracker.send(new HitBuilders.EventBuilder()
                 .setCategory(category)
                 .setAction(action)
                 .setLabel(eventName)
                 .setValue(status)
                 .setCustomDimension(1, userEmail)
                 .build());
         }
     }

    public void sendPreference(String action, String eventName, int status){
        sendEvent("Preferences", action, eventName, status); // 0 status for failed 1 for success
    }


    public void sendScreenView(String screenName){
        if (canSend()) {
            Log.d(TAG, "Sending Screen View " + screenName);
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.
                    ScreenViewBuilder()
                    .setCustomDimension(1, userEmail)
                    .build());
        }
    }

}
