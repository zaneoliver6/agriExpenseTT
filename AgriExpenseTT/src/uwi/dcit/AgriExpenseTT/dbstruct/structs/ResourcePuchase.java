package uwi.dcit.AgriExpenseTT.dbstruct.structs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.TransactionLog;
import uwi.dcit.AgriExpenseTT.models.LocalResourcePurchase;
import uwi.dcit.AgriExpenseTT.models.ResourcePurchaseContract;
import uwi.dcit.agriexpensesvr.rPurchaseApi.model.RPurchase;


public class ResourcePuchase {

    public static boolean resourceExist(SQLiteDatabase db){
        String code="select COUNT(*) FROM "+ ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME+" where "+ ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING+">0";
        boolean res = false;
        Cursor c=db.rawQuery(code,null);
        if(c.moveToFirst()) {
            res = c.getInt(0) > 0;
        }
        c.close();
        return res;
    }

    public static boolean resourceExist(SQLiteDatabase db, String type){
        String code = "select COUNT(*) FROM " + ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME + " where " + ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE +" = '"+type+"' and "+ ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING+">0";
        boolean res = false;
        Cursor c=db.rawQuery(code,null);
        if(c.moveToFirst()) {
            res = c.getInt(0) > 0;
        }
        c.close();
        return res;
    }

    //this is for when the farmer buys any material crop, fertilizer, chemical NOT WHEN HE USES
    public static int insertResourceExp(SQLiteDatabase db, DbHelper dbh, String type, int resourceId, String quantifier, double qty, double cost, TransactionLog tl){
        ContentValues cv= new ContentValues();
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID, resourceId);
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE, type);
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER, quantifier);
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY, qty);
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_COST, cost);
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING, qty);
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_RESOURCE, Resource.findResourceName(db, dbh, resourceId));
        db.insert(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, null, cv);
        int rowId=DbQuery.getLast(db, dbh, ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME);
        tl.insertTransLog(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, rowId, TransactionLog.TL_INS);//records the insert of a purchase
        return rowId;
    }

    public static int insertResourceExp(SQLiteDatabase db, DbHelper dbh, String type, int resourceId, String quantifier, double qty, double cost, long time, TransactionLog tl){
        ContentValues cv= new ContentValues();
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID, resourceId);
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE, type);
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER, quantifier);
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY, qty);
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_COST, cost);
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING, qty);
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_RESOURCE, Resource.findResourceName(db, dbh, resourceId));
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_DATE, time);

        db.insert(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, null, cv);
        int rowId=DbQuery.getLast(db, dbh, ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME);
        tl.insertTransLog(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, rowId, TransactionLog.TL_INS);//records the insert of a purchase
        return rowId;
    }



    public static RPurchase getARPurchase(SQLiteDatabase db, DbHelper dbh, int id){
        String code="select * from "+ ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME+" where "
                + ResourcePurchaseContract.ResourcePurchaseEntry._ID+"="+id+";";
        Cursor cursor=db.rawQuery(code, null);
        if(cursor.getCount() < 1)return null;

        cursor.moveToFirst();

        RPurchase purchase=new RPurchase();
        purchase.setPId(id);
        purchase.setResourceId(cursor.getInt(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID)));
        purchase.setQuantifier(cursor.getString(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER)));
        purchase.setQty(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY)));
        purchase.setCost(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_COST)));
        purchase.setQtyRemaining(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING)));
        purchase.setType(cursor.getString(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE)));
        purchase.setElementName(Resource.findResourceName(db, dbh, purchase.getResourceId()));

        cursor.close();
        return purchase;
    }

    public static List<LocalResourcePurchase> getResourcePurchases(SQLiteDatabase db, DbHelper dbh, ArrayList<LocalResourcePurchase> list, int resId){
        if (list == null)list = new ArrayList<>();

        String code = "select * from "+ ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME+" where "+ ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID+"="+resId+";";
        Cursor cursor =db.rawQuery(code, null);
        if(cursor.getCount()<1)
            return list;
        while(cursor.moveToNext()){
            LocalResourcePurchase m=new LocalResourcePurchase();
            m.setpId(cursor.getInt(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry._ID)));
            m.setResourceId(cursor.getInt(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID)));
            m.setType(cursor.getString(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE)));
            m.setQuantifier(cursor.getString(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER)));
            m.setQty(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY)));
            m.setCost(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_COST)));
            m.setQtyRemaining(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING)));
            list.add(m);
        }
        cursor.close();
        return list;
    }

    public static List<LocalResourcePurchase> getPurchases(SQLiteDatabase db, DbHelper dbh, ArrayList<LocalResourcePurchase> list, String type, String quantifier, boolean allowFinished){
        if (list == null)list = new ArrayList<>();

        String code;
        if(type == null)
            code="select * from "+ ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME+";";
        else
            code="select * from "+ ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME+" where "+ ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE+"='"+type+"';";

        Cursor cursor = db.rawQuery(code, null);
        if(cursor == null || cursor.getCount() < 1 )
            return list;

        while(cursor.moveToNext()){
            LocalResourcePurchase m=new LocalResourcePurchase();
            m.setpId(cursor.getInt(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry._ID)));
            m.setResourceId(cursor.getInt(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID)));
            m.setType(cursor.getString(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE)));
            m.setQuantifier(cursor.getString(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER)));
            m.setQty(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY)));
            m.setCost(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_COST)));
            m.setQtyRemaining(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING)));
            m.setDate(cursor.getLong(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_DATE)));
            list.add(m);
        }

        cursor.close();
        return list;
    }
}
