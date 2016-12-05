package uwi.dcit.AgriExpenseTT.dbstruct.structs;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.models.CycleResourceContract;
import uwi.dcit.AgriExpenseTT.models.LocalCycleUse;


public class CyclesUse {

    public static uwi.dcit.agriexpensesvr.cycleUseApi.model.CycleUse getACycleUse(SQLiteDatabase db, DbHelper dbh, int id){
        String code="select * from "+ CycleResourceContract.CycleResourceEntry.TABLE_NAME+" where "+ CycleResourceContract.CycleResourceEntry._ID+"="+id+";";
        Cursor cursor=db.rawQuery(code, null);
        if(cursor.getCount()<1)
            return null;
        uwi.dcit.agriexpensesvr.cycleUseApi.model.CycleUse c = new uwi.dcit.agriexpensesvr.cycleUseApi.model.CycleUse();
        cursor.moveToFirst();
        c.setId(cursor.getInt(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry._ID)));
        c.setCycleid(cursor.getInt(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_CYCLEID)));
        c.setPurchaseId(cursor.getInt(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID)));
        c.setAmount(cursor.getDouble(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_QTY)));
        c.setCost(cursor.getDouble(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_USECOST)));
        c.setResource(cursor.getString(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_TYPE)));

        cursor.close();
        return c;
    }

    public static List<LocalCycleUse> getCycleUse(SQLiteDatabase db, DbHelper dbh, int cycleid, ArrayList<LocalCycleUse> list, String type){
        String code;
        if (list == null)list = new ArrayList<>();

        if(type==null)
            code="select * from "+ CycleResourceContract.CycleResourceEntry.TABLE_NAME+" where "+ CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_CYCLEID+"="+cycleid+";";
        else
            code="select * from "+ CycleResourceContract.CycleResourceEntry.TABLE_NAME+" where "+ CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_CYCLEID+"="+cycleid+" and "+ CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_TYPE+"='"+type+"';";
        Cursor cursor=db.rawQuery(code, null);
        if(cursor.getCount()<1)
            return list;
        while(cursor.moveToNext()){
            LocalCycleUse l = new LocalCycleUse(cursor.getInt(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry._ID)),
                    cursor.getInt(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_CYCLEID)),
                    cursor.getInt(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID)),
                    cursor.getDouble(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_QTY)),
                    cursor.getString(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_TYPE)),
                    cursor.getDouble(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_USECOST)),
                    cursor.getString(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_QUANTIFIER)));

            list.add(l);
        }
        cursor.close();
        return list;
    }

    public static List<LocalCycleUse> getCycleUseP(SQLiteDatabase db, DbHelper dbh,int purchaseId,ArrayList<LocalCycleUse> list,String type){
        String code;
        if(type==null)
            code="select * from "+ CycleResourceContract.CycleResourceEntry.TABLE_NAME+" where "+ CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID+"="+purchaseId+";";
        else
            code="select * from "+ CycleResourceContract.CycleResourceEntry.TABLE_NAME+" where "+ CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID+"="+purchaseId+" and "+ CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_TYPE+"='"+type+"';";
        Cursor cursor=db.rawQuery(code, null);
        if(cursor.getCount()<1)
            return list;
        while(cursor.moveToNext()){
            LocalCycleUse l=new LocalCycleUse();
            l.setId(cursor.getInt(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry._ID)));
            l.setAmount(cursor.getDouble(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_QTY)));
            l.setCycleid(cursor.getInt(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_CYCLEID)));
            l.setPurchaseId(cursor.getInt(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID)));
            l.setResource(cursor.getString(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_TYPE)));
            l.setUseCost(cursor.getDouble(cursor.getColumnIndex(CycleResourceContract.CycleResourceEntry.CYCLE_RESOURCE_USECOST)));
            list.add(l);
        }
        cursor.close();
        return list;
    }
}
