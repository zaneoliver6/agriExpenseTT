package uwi.dcit.AgriExpenseTT.dbstruct;

import android.database.sqlite.SQLiteDatabase;

import uwi.dcit.AgriExpenseTT.models.CountryContract;
import uwi.dcit.AgriExpenseTT.models.CountyContract;

class DbOmniPotent {
    private SQLists list = new SQLists();

    void createDb(SQLiteDatabase db) {
        String[] createList = list.getCreateList();
        for (String aCreateList : createList) db.execSQL(aCreateList);
    }

    void dropTables(SQLiteDatabase db){
        String[] destroyList = list.getDestroyList();
        db.beginTransaction();
        for (String aDestroyList : destroyList) db.execSQL(aDestroyList);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    void createCountries(SQLiteDatabase db){
        db.execSQL(CountryContract.SQL_CREATE_COUNTRIES);
    }

    void createCounties(SQLiteDatabase db){
        db.execSQL(CountyContract.SQL_CREATE_COUNTIES);
    }
}
