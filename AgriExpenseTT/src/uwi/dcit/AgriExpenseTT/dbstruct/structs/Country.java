package uwi.dcit.AgriExpenseTT.dbstruct.structs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.models.CountryContract;

public class Country {

    public static int insertCountry(SQLiteDatabase db, String country, String type){
        ContentValues cv = new ContentValues();
        cv.put(CountryContract.CountryEntry.COLUMN_NAME_COUNTRY, country);
        cv.put(CountryContract.CountryEntry.COLUMN_NAME_TYPE, type);
        return (int)db.insert(CountryContract.CountryEntry.TABLE_NAME, null, cv);
    }

    public static int insertCountry(SQLiteDatabase db, String country){
        return insertCountry(db, country, "parish");
    }

    public static ArrayList<String> getCountries(SQLiteDatabase db, ArrayList<String> list){
        if (list == null)list = new ArrayList<String>();
        String sqlStr = "SELECT country FROM " + CountryContract.CountryEntry.TABLE_NAME +";";
        Cursor cursor = db.rawQuery(sqlStr, null);
        while (cursor.moveToNext()) list.add(cursor.getString(cursor.getColumnIndex("country")));
        cursor.close();
        return list;
    }

    public static int getCountryIdByName(SQLiteDatabase db, String country){
        int id = -1;
        String sqlStr = "SELECT "+ CountryContract.CountryEntry._ID + " FROM "+ CountryContract.CountryEntry.TABLE_NAME +";";
        Cursor cursor = db.rawQuery(sqlStr, null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }
}
