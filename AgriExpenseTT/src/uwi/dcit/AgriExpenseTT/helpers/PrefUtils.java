package uwi.dcit.AgriExpenseTT.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {

    private static final String FIRST_LOGIN = "LOGIN";
    private static final String DB_EXIST = "DBEXIST";
    private static final String USER_EMAIL = "email";
    private static final String REMINDER = "reminder_prefs" ;
    private static final String MyAlarmPreferencesWeekDay = "MyAlarmPrefsWeekDay" ;
    private static final String MyAlarmPreferencesHour = "MyAlarmPrefsHour" ;
    private static final String LocPrefCountry = "country";
    private static final String LocPrefCounty = "country";


    // *** Check User Is First Time ***
    public static boolean isFirstUse(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(FIRST_LOGIN, true);
    }

    public static boolean setFirstUse(Context context, final boolean login){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.edit().putBoolean(FIRST_LOGIN, login).commit();
    }

    public static boolean dbExist(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(DB_EXIST, false);
    }

    public static boolean setDbExist(Context context, final boolean dbexists){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.edit().putBoolean(DB_EXIST, dbexists).commit();
    }

    // *** User Emails ***
    public static boolean setUserEmail(Context context, final String email){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.edit().putString(USER_EMAIL, email).commit();
    }

    public static String getUserEmail(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(USER_EMAIL, "offline_user");
    }

    // ** Check for Alarm Configuration ***
    public static boolean setAlarmSet(Context context, final boolean isSet){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.edit().putBoolean(REMINDER, isSet).commit();
    }

    public static boolean getAlarmSet(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(REMINDER, false);
    }

    public static boolean setAlarmDetails(Context context, String day, int hour){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.edit()
                .putString(MyAlarmPreferencesWeekDay, day)
                .putInt(MyAlarmPreferencesHour, hour)
                .commit();
    }

    public static String [] getAlarmDetails(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String [] alarm = new String[2];
        alarm[0] = sp.getString(MyAlarmPreferencesWeekDay, null);
        alarm[1] = sp.getString(MyAlarmPreferencesHour, null);
        return alarm;
    }


    // **** Helper for Storing Location ****/
    public static boolean setLocationDetails(Context context, String country, String county){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.edit().putString(LocPrefCountry, country)
                .putString(LocPrefCounty, county)
                .commit();
    }

    public static String [] getLocationDetails(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String [] locs = new String[2];
        locs[0] = sp.getString(LocPrefCountry, null);
        locs[1] = sp.getString(LocPrefCounty, null);
        return locs;
    }
}
