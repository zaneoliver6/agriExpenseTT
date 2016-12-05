package uwi.dcit.AgriExpenseTT.dbstruct.structs;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.TransactionLog;
import uwi.dcit.AgriExpenseTT.models.CycleResourceContract;

public class ResourceUse {

    //based on the material being used inserts USE of a particular material for a particular CYCLE
    public static int insertResourceUse(SQLiteDatabase db, DbHelper dbh, int cycleId, String type, int resourcePurchasedId, double qty, String quantifier, double useCost, TransactionLog tl){
        ContentValues cv= new ContentValues();
        cv.put(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_CYCLEID, cycleId);
        cv.put(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID, resourcePurchasedId);
        cv.put(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_QTY, qty);
        cv.put(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_TYPE, type);
        cv.put(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_QUANTIFIER, quantifier);
        cv.put(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_USECOST, useCost);

        db.insert(CycleResourceContract.CycleResourceEntry.TABLE_NAME, null, cv);
        int rowId= DbQuery.getLast(db, dbh, CycleResourceContract.CycleResourceEntry.TABLE_NAME);
        tl.insertTransLog(CycleResourceContract.CycleResourceEntry.TABLE_NAME, rowId, TransactionLog.TL_INS);
        return rowId;
    }

    public static int insertResourceUse(SQLiteDatabase db, DbHelper dbh,int cycleId, String type, int resourcePurchasedId, double qty, String quantifier, double useCost, long time, TransactionLog tl){
        ContentValues cv= new ContentValues();
        cv.put(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_CYCLEID, cycleId);
        cv.put(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID, resourcePurchasedId);
        cv.put(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_QTY, qty);
        cv.put(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_TYPE, type);
        cv.put(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_QUANTIFIER, quantifier);
        cv.put(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_USECOST, useCost);
//        cv.put(CycleResourceEntry.)

        db.insert(CycleResourceContract.CycleResourceEntry.TABLE_NAME, null, cv);
        int rowId=DbQuery.getLast(db, dbh, CycleResourceContract.CycleResourceEntry.TABLE_NAME);
        tl.insertTransLog(CycleResourceContract.CycleResourceEntry.TABLE_NAME, rowId, TransactionLog.TL_INS);
        return rowId;
    }

}
