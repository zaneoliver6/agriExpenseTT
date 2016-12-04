package uwi.dcit.AgriExpenseTT.dbstruct;

import android.database.sqlite.SQLiteDatabase;

public class TblMnger {

    private DbOmniPotent omniPotent = new DbOmniPotent();
    private DbModifier modifier = new DbModifier();

    public void tableColumnModify(SQLiteDatabase db){
        modifier.tableColumnModify(db);
    }

    public void createDb(SQLiteDatabase db) {
        omniPotent.createDb(db);
    }
    public void dropTables(SQLiteDatabase db){
        omniPotent.dropTables(db);
    }

    public void createCountries(SQLiteDatabase db){
        omniPotent.createCountries(db);
    }

    public void createCounties(SQLiteDatabase db){
        omniPotent.createCounties(db);
    }



}
