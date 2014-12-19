package uwi.dcit.AgriExpenseTT.helpers;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * The Google Analytics Helper will provide a wrapper for ensuring that the functionality defined can be resued across the various application states of the program
 */
public class GAnalyticsHelper {

    public static final String APP_TRACKER = "AgriExpense";

    private Context context;

    public GAnalyticsHelper(Context context){
        this.context = context;
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this.context);
    }



}
