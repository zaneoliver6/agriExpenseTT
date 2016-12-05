package uwi.dcit.AgriExpenseTT.dbstruct.structs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.TransactionLog;
import uwi.dcit.AgriExpenseTT.models.CycleContract;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;

public class Cycles {

    //checks to see if there are any crop cycles or not
    public static boolean cyclesExist(SQLiteDatabase db){
        String code="select COUNT(*) FROM "+ CycleContract.CycleEntry.TABLE_NAME;
        boolean res = false;
        Cursor c=db.rawQuery(code,null);
        if(c.moveToFirst()) {
            res = c.getInt(0) > 0;
        }
        c.close();
        return res;
    }

    public static List<LocalCycle> getCycles(SQLiteDatabase db, DbHelper dbh, ArrayList<LocalCycle> list){
        if (list == null)list = new ArrayList<>();

        String code="select * from "+ CycleContract.CycleEntry.TABLE_NAME+";";
        Cursor cursor=db.rawQuery(code, null);
        if(cursor.getCount()<1)
            return list;
        while(cursor.moveToNext()){
            LocalCycle n=new LocalCycle();
            n.setId(cursor.getInt(cursor.getColumnIndex(CycleContract.CycleEntry._ID)));
            n.setCropId(cursor.getInt(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_CROPID)));
            n.setLandType(cursor.getString(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_LAND_TYPE)));
            n.setLandQty(cursor.getDouble(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_LAND_AMOUNT)));
            n.setTime(cursor.getLong(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_DATE)));
            n.setTotalSpent(cursor.getDouble(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_TOTALSPENT)));

            n.setHarvestAmt(cursor.getDouble(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_HARVEST_AMT)));
            n.setHarvestType(cursor.getString(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_HARVEST_TYPE)));
            n.setCostPer(cursor.getDouble(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_COSTPER)));
            n.setCropName(cursor.getString(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_RESOURCE)));
            n.setCycleName(cursor.getString(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_NAME)));
            list.add(n);
        }

        cursor.close();
        return list;
    }



    public static uwi.dcit.agriexpensesvr.cycleApi.model.Cycle getCycle(SQLiteDatabase db, DbHelper dbh, int id){
        String code="select * from "+ CycleContract.CycleEntry.TABLE_NAME+" where "+ CycleContract.CycleEntry._ID+"="+id+";";
        Cursor cursor = db.rawQuery(code, null);
        if(cursor.getCount() < 1)return null;
        uwi.dcit.agriexpensesvr.cycleApi.model.Cycle c = new uwi.dcit.agriexpensesvr.cycleApi.model.Cycle();
        cursor.moveToFirst();
        c.setCropId(cursor.getInt(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_CROPID)));
        c.setId(id);
        c.setLandQty(cursor.getDouble(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_LAND_AMOUNT)));
        c.setLandType(cursor.getString(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_LAND_TYPE)));
        c.setTotalSpent(cursor.getDouble(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_TOTALSPENT)));
        c.setHarvestType(cursor.getString(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_HARVEST_TYPE)));
        c.setHarvestAmt(cursor.getDouble(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_HARVEST_AMT)));
        c.setCostPer(cursor.getDouble(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_COSTPER)));
        c.setCropName(cursor.getString(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_RESOURCE)));
        cursor.close();
        return c;
    }

    public static int insertCycle(SQLiteDatabase db, DbHelper dbh, int cropId, String landType, double landQty, TransactionLog tl, long time){
        ContentValues cv=new ContentValues();
        cv.put(CycleContract.CycleEntry.CROPCYCLE_CROPID, cropId);
        cv.put(CycleContract.CycleEntry.CROPCYCLE_LAND_TYPE, landType);
        cv.put(CycleContract.CycleEntry.CROPCYCLE_LAND_AMOUNT, landQty);
        cv.put(CycleContract.CycleEntry.CROPCYCLE_DATE, time);
        cv.put(CycleContract.CycleEntry.CROPCYCLE_TOTALSPENT, 0.0);
        cv.put(CycleContract.CycleEntry.CROPCYCLE_COSTPER, 0.0);
        cv.put(CycleContract.CycleEntry.CROPCYCLE_HARVEST_AMT, 0.0);
        cv.put(CycleContract.CycleEntry.CROPCYCLE_HARVEST_TYPE,"Lb");
        cv.put(CycleContract.CycleEntry.CROPCYCLE_RESOURCE, Resource.findResourceName(db, dbh, cropId));
        db.insert(CycleContract.CycleEntry.TABLE_NAME, null,cv);
        int rowId=DbQuery.getLast(db, dbh, CycleContract.CycleEntry.TABLE_NAME);
        tl.insertTransLog(CycleContract.CycleEntry.TABLE_NAME,rowId,TransactionLog.TL_INS );
        return rowId;
    }

    public static int insertCycle(SQLiteDatabase db, DbHelper dbh, int cropId, String name, String landType, double landQty, TransactionLog tL, long time) {
        ContentValues cv=new ContentValues();
        cv.put(CycleContract.CycleEntry.CROPCYCLE_CROPID, cropId);
        cv.put(CycleContract.CycleEntry.CROPCYCLE_LAND_TYPE, landType);
        cv.put(CycleContract.CycleEntry.CROPCYCLE_LAND_AMOUNT, landQty);
        cv.put(CycleContract.CycleEntry.CROPCYCLE_DATE, time);
        cv.put(CycleContract.CycleEntry.CROPCYCLE_TOTALSPENT, 0.0);
        cv.put(CycleContract.CycleEntry.CROPCYCLE_COSTPER, 0.0);
        cv.put(CycleContract.CycleEntry.CROPCYCLE_HARVEST_AMT, 0.0);
        cv.put(CycleContract.CycleEntry.CROPCYCLE_HARVEST_TYPE,"Lb");
        cv.put(CycleContract.CycleEntry.CROPCYCLE_RESOURCE, Resource.findResourceName(db, dbh, cropId));
        cv.put(CycleContract.CycleEntry.CROPCYCLE_NAME, name);
        db.insert(CycleContract.CycleEntry.TABLE_NAME, null,cv);
        int rowId=DbQuery.getLast(db, dbh, CycleContract.CycleEntry.TABLE_NAME);
        tL.insertTransLog(CycleContract.CycleEntry.TABLE_NAME,rowId,TransactionLog.TL_INS );
        return rowId;
    }
}
