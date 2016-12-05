package uwi.dcit.AgriExpenseTT.dbstruct;

import android.database.sqlite.SQLiteDatabase;

import uwi.dcit.AgriExpenseTT.models.CountryContract;
import uwi.dcit.AgriExpenseTT.models.CountyContract;

class DbOmniPotent {

    public static void createDb(SQLiteDatabase db) {
        String[] createList = SQLists.getCreateList();
        for (String aCreateList : createList) db.execSQL(aCreateList);
    }

    public static void dropTables(SQLiteDatabase db){
        String[] destroyList = SQLists.getDestroyList();
        db.beginTransaction();
        for (String aDestroyList : destroyList) db.execSQL(aDestroyList);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public static void createCountries(SQLiteDatabase db){
        db.execSQL(CountryContract.SQL_CREATE_COUNTRIES);
    }

    public static void createCounties(SQLiteDatabase db){
        db.execSQL(CountyContract.SQL_CREATE_COUNTIES);
    }
}
