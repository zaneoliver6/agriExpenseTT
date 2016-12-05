package uwi.dcit.AgriExpenseTT.dbstruct;

import android.database.sqlite.SQLiteDatabase;

public class TblMnger {
    public static void tableColumnModify(SQLiteDatabase db){DbModifier.tableColumnModify(db);}

    public static void createDb(SQLiteDatabase db) {
        DbOmniPotent.createDb(db);
    }
    public static void dropTables(SQLiteDatabase db){
        DbOmniPotent.dropTables(db);
    }

    public static void createCountries(SQLiteDatabase db){
        DbOmniPotent.createCountries(db);
    }

    public static void createCounties(SQLiteDatabase db){
        DbOmniPotent.createCounties(db);
    }



}
