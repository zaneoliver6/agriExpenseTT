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

    public static final String CLOUD_CATEGORY = "Cloud_Services";
    private static final String PERFORMANCE_CATEGORY = "Performance_Metrics";

    private static final String TAG = "GAnalytics";
    private static final boolean enableTracking = false;
    private static GAnalyticsHelper instance = null;
    private final String userEmail;
    private Tracker tracker;

    private GAnalyticsHelper(Context context){
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        this.tracker = analytics.newTracker(R.xml.global_tracker);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(false);
        tracker.enableExceptionReporting(true);
        userEmail = PrefUtils.getUserEmail(context);
    }

    // Make sense to keep a singleton class to make sure trackers are not reinitialized every time its called
    public static GAnalyticsHelper getInstance(Context context){
        if (instance == null)instance = new GAnalyticsHelper(context);
        return instance;
    }

    private boolean canSend() {
        return enableTracking;
    }

    /**
     * The method allows the recording of
     * @param category The general section where its related to section
     * @param action The action that the user is currently performed
     * @param eventName The current event
     * @param status the outcome of the event
     */
     public void sendEvent(String category, String action, String eventName, long status){
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

    public void sendPerfMetrics(String action, String eventName, long value){
        sendEvent(PERFORMANCE_CATEGORY, action, eventName, 1);
    }


    public void sendScreenView(String screenName){
        if (canSend()) {
            Log.d(TAG, "Sending Screen View " + screenName);
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.ScreenViewBuilder()
                    .setCustomDimension(1, userEmail)
                    .build());
        }
    }

}
