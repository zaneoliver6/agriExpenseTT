package uwi.dcit.AgriExpenseTT.dbstruct.structs;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import uwi.dcit.AgriExpenseTT.models.CountyContract;

public class County {
    public static int insertCounty(SQLiteDatabase db, String country, String county){
        int countryId = Country.getCountryIdByName(db, country);
        if (countryId == -1)return -1; //not a valid country specified
        return insertCounty(db, countryId, county);
    }

    public static int insertCounty(SQLiteDatabase db, int country, String county){
        ContentValues cv = new ContentValues();
        cv.put(CountyContract.CountyEntry.COLUMN_NAME_COUNTRY, country);
        cv.put(CountyContract.CountyEntry.COLUMN_NAME_COUNTY, county);
        return (int)db.insert(CountyContract.CountyEntry.TABLE_NAME, null, cv);
    }
}
