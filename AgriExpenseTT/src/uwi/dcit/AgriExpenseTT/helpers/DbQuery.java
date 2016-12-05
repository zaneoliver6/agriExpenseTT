package uwi.dcit.AgriExpenseTT.helpers;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import uwi.dcit.AgriExpenseTT.models.CycleContract.CycleEntry;
import uwi.dcit.AgriExpenseTT.models.CycleResourceContract.CycleResourceEntry;

import uwi.dcit.AgriExpenseTT.models.ResourceContract;
import uwi.dcit.AgriExpenseTT.models.ResourcePurchaseContract.ResourcePurchaseEntry;
import uwi.dcit.AgriExpenseTT.models.TransactionLogContract;
import uwi.dcit.AgriExpenseTT.models.UpdateAccountContract;
import uwi.dcit.agriexpensesvr.translogApi.model.TransLog;


public class DbQuery {

	public static int getLast(SQLiteDatabase db, DbHelper dbh, String table){
		String code="select _id from " + table + "  ORDER BY _id DESC LIMIT 1;";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount() < 0)return -1;
		cursor.moveToFirst();
		int res = cursor.getInt(cursor.getColumnIndex("_id"));
        cursor.close();
        return res;
	}
	
	public static int getLastRec(SQLiteDatabase db, DbHelper dbh, String table){
		String code="select _id from "+table+"  ORDER BY _id DESC LIMIT 1;";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<0)
			return -1;
		cursor.moveToFirst();
		int res = cursor.getInt(cursor.getColumnIndex("_id"));
        cursor.close();
        return res;
	}

	//can be used for all tables so far
	public static void deleteRecord(SQLiteDatabase db,DbHelper dbh,String table,int id)throws Exception{
        if(table.equals(UpdateAccountContract.UpdateAccountEntry.TABLE_NAME)){
            db.delete(table, UpdateAccountContract.UpdateAccountEntry._ID+""+id, null);
        }else if(table.equals(CycleEntry.TABLE_NAME)){
            db.delete(table, CycleEntry._ID+"="+id, null);
        }else if(table.equals(ResourcePurchaseEntry.TABLE_NAME)){
            db.delete(table, ResourcePurchaseEntry._ID+"="+id, null);
        }else if(table.equals(ResourceContract.ResourceEntry.TABLE_NAME)){
            db.delete(table, ResourceContract.ResourceEntry._ID+"="+id, null);
        }else if(table.equals(CycleResourceEntry.TABLE_NAME)){
            db.delete(table,CycleResourceEntry._ID+"="+id,null);
        }else{
            throw new Exception("no contract defined for this table");
        }
	}

	public static TransLog getLog(SQLiteDatabase db, DbHelper dbh, int rowId) {
		TransLog t=new TransLog();
		String code="select * from "+ TransactionLogContract.TransactionLogEntry.TABLE_NAME+" where "+ TransactionLogContract.TransactionLogEntry._ID +"="+rowId;
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return null;
		cursor.moveToFirst();
		t.setId(cursor.getInt(cursor.getColumnIndex(TransactionLogContract.TransactionLogEntry._ID)));
		t.setOperation(cursor.getString(cursor.getColumnIndex(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_OPERATION)));
		t.setTableKind(cursor.getString(cursor.getColumnIndex(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_TABLE)));
		t.setTransTime(cursor.getLong(cursor.getColumnIndex(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_TRANSTIME)));
		t.setRowId(cursor.getInt(cursor.getColumnIndex(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_ROWID)));
		cursor.close();
		return t;
	}

}
