package uwi.dcit.AgriExpenseTT.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {

    public static final String FIRST_LOGIN = "LOGIN";
    public static final String DB_EXIST = "DBEXIST";

    public static boolean isFirstUse(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(FIRST_LOGIN, true);
    }

    public static void setFirstUse(Context context, final boolean login){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(FIRST_LOGIN, login).commit();
    }

    public static boolean dbExist(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(DB_EXIST, false);
    }

    public static void setDbExist(Context context, final boolean dbexists){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(DB_EXIST, dbexists).commit();
    }

}
