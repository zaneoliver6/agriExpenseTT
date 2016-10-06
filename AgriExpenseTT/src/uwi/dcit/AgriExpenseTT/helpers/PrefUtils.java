package uwi.dcit.AgriExpenseTT.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {

    public static final String FIRST_LOGIN = "LOGIN";
    public static final String DB_EXIST = "DBEXIST";
    public static final String USER_EMAIL = "email";
    public static final String REMINDER = "reminder_prefs" ;
    public static final String MyAlarmPreferencesWeekDay = "MyAlarmPrefsWeekDay" ;
    public static final String MyAlarmPreferencesHour = "MyAlarmPrefsHour" ;
    public static final String MyAlarmSet = "MyAlarmSet";
    public static final String MyPreferencesSet = "MyPrefSet";
    public static final String FirstRunPreferences = "MyFirstRunPrefs";
    public static final String FirstRun="MyPrefs-FirstRun";


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
}
