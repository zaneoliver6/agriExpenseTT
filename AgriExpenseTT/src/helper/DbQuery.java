package helper;

import java.util.ArrayList;

import com.example.agriexpensett.cycleendpoint.model.Cycle;
import com.example.agriexpensett.cycleuseendpoint.model.CycleUse;
import com.example.agriexpensett.rpurchaseendpoint.model.RPurchase;
import com.example.agriexpensett.translogendpoint.model.TransLog;
import com.example.agriexpensett.upaccendpoint.model.UpAcc;

import dataObjects.localCycle;
import dataObjects.localCycleUse;
import dataObjects.localResourcePurchase;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbQuery {
	
	//Used to insert a new Chemical, Crop, Fertilizer, Labourer
	public static int insertResource(SQLiteDatabase db,DbHelper dbh,String type,String name){
		ContentValues cv= new ContentValues();
		cv.put(DbHelper.RESOURCES_NAME,name);
		cv.put(DbHelper.RESOURCES_TYPE,type);
		db.insert(DbHelper.TABLE_RESOURCES, null, cv);
		int rowId=getLast(db, dbh, DbHelper.TABLE_RESOURCES);
		
		return rowId; 
	}
	//this is for when the farmer buys any material crop, fertilizer, chemical NOT WHEN HE USES
	public static int insertResourceExp(SQLiteDatabase db, DbHelper dbh, String type, int resourceId, String quantifier, double qty, double cost, TransactionLog tl){
		ContentValues cv= new ContentValues();
		cv.put(DbHelper.RESOURCE_PURCHASE_RESID, resourceId);
		cv.put(DbHelper.RESOURCE_PURCHASE_TYPE, type);
		cv.put(DbHelper.RESOURCE_PURCHASE_QUANTIFIER, quantifier);
		cv.put(DbHelper.RESOURCE_PURCHASE_QTY, qty);
		cv.put(DbHelper.RESOURCE_PURCHASE_COST, cost);
		cv.put(DbHelper.RESOURCE_PURCHASE_REMAINING, qty);
		cv.put(DbHelper.RESOURCE_PURCHASE_RESOURCE, DbQuery.findResourceName(db, dbh, resourceId));
		db.insert(DbHelper.TABLE_RESOURCE_PURCHASES, null, cv);
		int rowId=getLast(db, dbh, DbHelper.TABLE_RESOURCE_PURCHASES);
		 tl.insertTransLog(DbHelper.TABLE_RESOURCE_PURCHASES, rowId, TransactionLog.TL_INS);//records the insert of a purchase
		return rowId;
	}
	
	//based on the material being used inserts USE of a particular material for a particular CYCLE
	public static int insertResourceUse(SQLiteDatabase db, DbHelper dbh,int cycleId, String type, int resourcePurchasedId, double qty,String quantifier,double useCost,TransactionLog tl){
		ContentValues cv= new ContentValues();
		cv.put(DbHelper.CYCLE_RESOURCE_CYCLEID, cycleId);
		cv.put(DbHelper.CYCLE_RESOURCE_PURCHASE_ID, resourcePurchasedId);
		cv.put(DbHelper.CYCLE_RESOURCE_QTY, qty);
		cv.put(DbHelper.CYCLE_RESOURCE_TYPE, type);
		cv.put(DbHelper.CYCLE_RESOURCE_QUANTIFIER, quantifier);
		cv.put(DbHelper.CYCLE_RESOURCE_USECOST, useCost);
		
		db.insert(DbHelper.TABLE_CYCLE_RESOURCES, null, cv);
		int rowId=getLast(db, dbh, DbHelper.TABLE_CYCLE_RESOURCES);
		tl.insertTransLog(DbHelper.TABLE_CYCLE_RESOURCES, rowId, TransactionLog.TL_INS);
		return rowId; 
	}
	
	public static int insertCycle(SQLiteDatabase db, DbHelper dbh,int cropId, String landType, double landQty,TransactionLog tl,long time){
		ContentValues cv=new ContentValues();
		cv.put(DbHelper.CROPCYCLE_CROPID, cropId);
		cv.put(DbHelper.CROPCYCLE_LAND_TYPE, landType);
		cv.put(DbHelper.CROPCYCLE_LAND_AMOUNT, landQty);
		cv.put(DbHelper.CROPCYCLE_DATE, time);
		cv.put(DbHelper.CROPCYCLE_TOTALSPENT, 0.0);
		cv.put(DbHelper.CROPCYCLE_COSTPER, 0.0);
		cv.put(DbHelper.CROPCYCLE_HARVEST_AMT, 0.0);
		cv.put(DbHelper.CROPCYCLE_HARVEST_TYPE,"Lb");
		cv.put(DbHelper.CROPCYCLE_RESOURCE, DbQuery.findResourceName(db, dbh, cropId));
		db.insert(DbHelper.TABLE_CROPCYLE, null,cv);
		int rowId=getLast(db, dbh, DbHelper.TABLE_CROPCYLE);
		tl.insertTransLog(DbHelper.TABLE_CROPCYLE,rowId,TransactionLog.TL_INS );
		return rowId;
		
	}
	
	public static void getResources(SQLiteDatabase db, DbHelper dbh, String type,ArrayList<String> list){
		String code;
		if(type!=null)
			code="select name from "+DbHelper.TABLE_RESOURCES+" where "+DbHelper.RESOURCES_TYPE+"='"+type+"';";
		else
			code="select name from "+DbHelper.TABLE_RESOURCES;
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return;
		while(cursor.moveToNext()){
			list.add(new String(cursor.getString(cursor.getColumnIndex("name"))));
		}
	}
	public static int getNameResourceId(SQLiteDatabase db,DbHelper dbh,String name){
		String code="select "+DbHelper.RESOURCES_ID+" from "+DbHelper.TABLE_RESOURCES+" where "+DbHelper.RESOURCES_NAME+"='"+name+"';";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return -1;
		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndex(DbHelper.RESOURCES_ID));
	}
	public static void getCycles(SQLiteDatabase db, DbHelper dbh, ArrayList<localCycle> list){
		String code="select * from "+DbHelper.TABLE_CROPCYLE+";";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return;
		while(cursor.moveToNext()){
			localCycle n=new localCycle();
			n.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.CROPCYCLE_ID)));
			n.setCropId(cursor.getInt(cursor.getColumnIndex(DbHelper.CROPCYCLE_CROPID)));
			n.setLandType(cursor.getString(cursor.getColumnIndex(DbHelper.CROPCYCLE_LAND_TYPE)));
			n.setLandQty(cursor.getDouble(cursor.getColumnIndex(DbHelper.CROPCYCLE_LAND_AMOUNT)));
			n.setTime(cursor.getLong(cursor.getColumnIndex(DbHelper.CROPCYCLE_DATE)));
			n.setTotalSpent(cursor.getDouble(cursor.getColumnIndex(DbHelper.CROPCYCLE_TOTALSPENT)));
			
			n.setHarvestAmt(cursor.getDouble(cursor.getColumnIndex(DbHelper.CROPCYCLE_HARVEST_AMT)));
			n.setHarvestType(cursor.getString(cursor.getColumnIndex(DbHelper.CROPCYCLE_HARVEST_TYPE)));
			n.setCostPer(cursor.getDouble(cursor.getColumnIndex(DbHelper.CROPCYCLE_COSTPER)));
			n.setCropName(cursor.getString(cursor.getColumnIndex(DbHelper.CROPCYCLE_RESOURCE)));
			list.add(n);
		}
	}
	
	public static String findResourceName(SQLiteDatabase db, DbHelper dbh, int id){
		String code="select name from "+DbHelper.TABLE_RESOURCES+" where id="+id+";";
		Cursor cursor=db.rawQuery(code,null);
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex("name"));
		}
		return null;
	}
	
	public static void getPurchases(SQLiteDatabase db, DbHelper dbh,ArrayList<localResourcePurchase>list,String type,String quantifier,boolean allowFinished){
		String code;
		if(type==null)
			code="select * from "+DbHelper.TABLE_RESOURCE_PURCHASES+";";
		else
			//if(!allowFinished)//not allowing finished
				//code="select * from "+DbHelper.TABLE_RESOURCE_PURCHASES+" where "+DbHelper.RESOURCE_PURCHASE_TYPE+"='"+type+"' and "+DbHelper.RESOURCE_PURCHASE_REMAINING+">0;";
			//else
				code="select * from "+DbHelper.TABLE_RESOURCE_PURCHASES+" where "+DbHelper.RESOURCE_PURCHASE_TYPE+"='"+type+"';";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1 || cursor==null)
			return;
		while(cursor.moveToNext()){
			localResourcePurchase m=new localResourcePurchase();
			m.setpId(cursor.getInt(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_ID)));
			m.setResourceId(cursor.getInt(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_RESID)));
			m.setType(cursor.getString(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_TYPE)));
			m.setQuantifier(cursor.getString(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_QUANTIFIER)));
			m.setQty(cursor.getDouble(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_QTY)));
			m.setCost(cursor.getDouble(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_COST)));
			m.setQtyRemaining(cursor.getDouble(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_REMAINING)));
			list.add(m);
		}
	}
	public static void getResourcePurchases(SQLiteDatabase db, DbHelper dbh,ArrayList<localResourcePurchase>list,int resId){
		String code;
		code="select * from "+DbHelper.TABLE_RESOURCE_PURCHASES+" where "
		+DbHelper.RESOURCE_PURCHASE_RESID+"="+resId+";";
		Cursor cursor =db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return;
		while(cursor.moveToNext()){
			localResourcePurchase m=new localResourcePurchase();
			m.setpId(cursor.getInt(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_ID)));
			m.setResourceId(cursor.getInt(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_RESID)));
			m.setType(cursor.getString(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_TYPE)));
			m.setQuantifier(cursor.getString(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_QUANTIFIER)));
			m.setQty(cursor.getDouble(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_QTY)));
			m.setCost(cursor.getDouble(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_COST)));
			m.setQtyRemaining(cursor.getDouble(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_REMAINING)));
			list.add(m);
		}
	}
	
	public static RPurchase getARPurchase(SQLiteDatabase db, DbHelper dbh,int id){
		String code="select * from "+DbHelper.TABLE_RESOURCE_PURCHASES+" where "
				+DbHelper.RESOURCE_PURCHASE_ID+"="+id+";";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return null;
		cursor.moveToFirst();
		RPurchase purchase=new RPurchase();
		purchase.setPId(id);
		purchase.setResourceId(cursor.getInt(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_RESID)));
		purchase.setQuantifier(cursor.getString(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_QUANTIFIER)));
		purchase.setQty(cursor.getDouble(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_QTY)));
		purchase.setCost(cursor.getDouble(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_COST)));
		purchase.setQtyRemaining(cursor.getDouble(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_REMAINING)));
		purchase.setType(cursor.getString(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_TYPE)));
		purchase.setElementName(DbQuery.findResourceName(db, dbh, purchase.getResourceId()));
		return purchase;
	}
	public static void getCycleUse(SQLiteDatabase db, DbHelper dbh,int cycleid,ArrayList<localCycleUse> list,String type){
		String code;
		if(type==null)
			code="select * from "+DbHelper.TABLE_CYCLE_RESOURCES+" where "+DbHelper.CYCLE_RESOURCE_CYCLEID+"="+cycleid+";";
		else
			code="select * from "+DbHelper.TABLE_CYCLE_RESOURCES+" where "+DbHelper.CYCLE_RESOURCE_CYCLEID+"="+cycleid+" and "+DbHelper.CYCLE_RESOURCE_TYPE+"='"+type+"';";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return;
		while(cursor.moveToNext()){
			localCycleUse l=new localCycleUse();
			l.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_ID)));
			l.setAmount(cursor.getDouble(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_QTY)));
			l.setCycleid(cursor.getInt(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_CYCLEID)));
			l.setPurchaseId(cursor.getInt(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_PURCHASE_ID)));
			l.setResource(cursor.getString(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_TYPE)));
			l.setUseCost(cursor.getDouble(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_USECOST)));
			l.setQuantifier(cursor.getString(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_QUANTIFIER)));
			list.add(l);
		}
	}
	public static void getCycleUseP(SQLiteDatabase db, DbHelper dbh,int purchaseId,ArrayList<localCycleUse> list,String type){
		String code;
		if(type==null)
			code="select * from "+DbHelper.TABLE_CYCLE_RESOURCES+" where "+DbHelper.CYCLE_RESOURCE_PURCHASE_ID+"="+purchaseId+";";
		else
			code="select * from "+DbHelper.TABLE_CYCLE_RESOURCES+" where "+DbHelper.CYCLE_RESOURCE_PURCHASE_ID+"="+purchaseId+" and "+DbHelper.CYCLE_RESOURCE_TYPE+"='"+type+"';";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return;
		while(cursor.moveToNext()){
			localCycleUse l=new localCycleUse();
			l.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_ID)));
			l.setAmount(cursor.getDouble(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_QTY)));
			l.setCycleid(cursor.getInt(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_CYCLEID)));
			l.setPurchaseId(cursor.getInt(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_PURCHASE_ID)));
			l.setResource(cursor.getString(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_TYPE)));
			l.setUseCost(cursor.getDouble(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_USECOST)));
			list.add(l);
		}
	}
	
	public static CycleUse getACycleUse(SQLiteDatabase db, DbHelper dbh,int id){
		String code="select * from "+DbHelper.TABLE_CYCLE_RESOURCES+" where "+DbHelper.CYCLE_RESOURCE_ID+"="+id+";";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return null;
		CycleUse c = new CycleUse();
		cursor.moveToFirst();
		c.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_ID)));
		c.setCycleid(cursor.getInt(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_CYCLEID)));
		c.setPurchaseId(cursor.getInt(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_PURCHASE_ID)));
		c.setAmount(cursor.getDouble(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_QTY)));
		c.setCost(cursor.getDouble(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_USECOST)));
		c.setResource(cursor.getString(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_TYPE)));
		return c;
	}
	
	public static int getLast(SQLiteDatabase db, DbHelper dbh, String table){
		String code="select id from "+table+"  ORDER BY id DESC LIMIT 1;";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<0)
			return -1;
		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndex("id"));
	}
	public static int insertRedoLog(SQLiteDatabase db, DbHelper dbh, String table, int id, String operation){
		ContentValues cv= new ContentValues();
		cv.put(DbHelper.REDO_LOG_TABLE, table);
		cv.put(DbHelper.REDO_LOG_ROW_ID, id);
		cv.put(DbHelper.REDO_LOG_OPERATION, operation);
		db.insert(DbHelper.TABLE_REDO_LOG, null, cv);
		return getLast(db,dbh,DbHelper.TABLE_REDO_LOG);
	}
	//can be used for all tables so far
	public static void deleteRecord(SQLiteDatabase db,DbHelper dbh,String table,int id){
		db.delete(table, "id="+id, null);
	}
	public static void insertUpAcc(SQLiteDatabase db,UpAcc acc){
		
		ContentValues cv=new ContentValues();
		cv.put(DbHelper.UPDATE_ACCOUNT_CLOUD_KEY, acc.getKeyrep());
		cv.put(DbHelper.UPDATE_ACCOUNT_ACC,acc.getAcc());
		cv.put(DbHelper.UPDATE_ACCOUNT_UPDATED, acc.getLastUpdated());
		cv.put(DbHelper.UPDATE_ACCOUNT_SIGNEDIN,acc.getSignedIn());
		db.insert(DbHelper.TABLE_UPDATE_ACCOUNT, null, cv);
	}
	public static void insertCloudKey(SQLiteDatabase db,DbHelper dbh, String table, String k,int id){
		ContentValues cv = new ContentValues();
		cv.put(DbHelper.CLOUD_KEY_TABLE, table);
		cv.put(DbHelper.CLOUD_KEY, k);
		cv.put(DbHelper.CLOUD_KEY_ROWID, id);
		db.insert(DbHelper.TABLE_CLOUD_KEY, null, cv);
	}
	public static String getKey(SQLiteDatabase db,DbHelper dbh,String table,int id){
		String code="select * from "+DbHelper.TABLE_CLOUD_KEY+" where "
			+DbHelper.CLOUD_KEY_TABLE+"='"+table+"' and "
			+DbHelper.CLOUD_KEY_ROWID+"="+id+";";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1){
			System.out.println("no key found");
			return null;
		}
		cursor.moveToFirst();
		String n=cursor.getString(cursor.getColumnIndex(DbHelper.CLOUD_KEY));
		return n;
	}
	public static int getCloudKeyId(SQLiteDatabase db,DbHelper dbh,String table,int id){String code="select * from "+DbHelper.TABLE_CLOUD_KEY+" where "
			+DbHelper.CLOUD_KEY_TABLE+"='"+table+"' and "
			+DbHelper.CLOUD_KEY_ROWID+"="+id+";";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return -1;
		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndex(DbHelper.CLOUD_KEY_ID));
	}
	public static Cycle getCycle(SQLiteDatabase db,DbHelper dbh,int id){
		String code="select * from "+DbHelper.TABLE_CROPCYLE+" where "+DbHelper.CROPCYCLE_ID+"="+id+";";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return null;
		Cycle c = new Cycle();
		cursor.moveToFirst();
		c.setCropId(cursor.getInt(cursor.getColumnIndex(DbHelper.CROPCYCLE_CROPID)));
		c.setId(id);
		c.setLandQty(cursor.getDouble(cursor.getColumnIndex(DbHelper.CROPCYCLE_LAND_AMOUNT)));
		c.setLandType(cursor.getString(cursor.getColumnIndex(DbHelper.CROPCYCLE_LAND_TYPE)));
		c.setTotalSpent(cursor.getDouble(cursor.getColumnIndex(DbHelper.CROPCYCLE_TOTALSPENT)));
		c.setHarvestType(cursor.getString(cursor.getColumnIndex(DbHelper.CROPCYCLE_HARVEST_TYPE)));
		c.setHarvestAmt(cursor.getDouble(cursor.getColumnIndex(DbHelper.CROPCYCLE_HARVEST_AMT)));
		c.setCostPer(cursor.getDouble(cursor.getColumnIndex(DbHelper.CROPCYCLE_COSTPER)));
		c.setCropName(cursor.getString(cursor.getColumnIndex(DbHelper.CROPCYCLE_RESOURCE)));
		return c;
	}
	
	//pass in the operation and the table you want to get the records to redo
	public static void getRedo(SQLiteDatabase db,DbHelper dbh,ArrayList<Integer> rowIds,ArrayList<Integer> logIds,String operation,String Rtable){
		String code="select * from "+DbHelper.TABLE_REDO_LOG+" where "
				+DbHelper.REDO_LOG_OPERATION+"= '"+operation+"' and "
				+DbHelper.REDO_LOG_TABLE+"= '"+Rtable+"';";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return;
		System.out.println("length:"+cursor.getCount());
		while(cursor.moveToNext()){
			int n=cursor.getInt(cursor.getColumnIndex(DbHelper.REDO_LOG_ROW_ID));
			rowIds.add(new Integer(n));
			n=cursor.getInt(cursor.getColumnIndex(DbHelper.REDO_LOG_LOGID));
			logIds.add(new Integer(n));
		}
		System.out.println("array length:"+logIds.size());
	}
	public static TransLog getLog(SQLiteDatabase db, DbHelper dbh, int rowId) {
		TransLog t=new TransLog();
		String code="select * from "+DbHelper.TABLE_TRANSACTION_LOG+" where id="+rowId;
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return null;
		cursor.moveToFirst();
		t.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_LOGID)));
		t.setOperation(cursor.getString(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_OPERATION)));
		t.setTableKind(cursor.getString(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_TABLE)));
		t.setTransTime(cursor.getLong(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_TRANSTIME)));
		t.setRowId(cursor.getInt(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_ROWID)));
		
		return t;
	}
	public static String getAccount(SQLiteDatabase db){
		String code="select "+DbHelper.UPDATE_ACCOUNT_ACC+" from "+
				DbHelper.TABLE_UPDATE_ACCOUNT;
		Cursor cursor=db.rawQuery(code, null);
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndex(DbHelper.UPDATE_ACCOUNT_ACC));
	}
	public static UpAcc getUpAcc(SQLiteDatabase db){
		String code="select * from "+DbHelper.TABLE_UPDATE_ACCOUNT;
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return null;
		cursor.moveToFirst();
		UpAcc acc=new UpAcc();
		acc.setKeyrep(cursor.getString(cursor.getColumnIndex(DbHelper.UPDATE_ACCOUNT_CLOUD_KEY)));
		acc.setAcc(cursor.getString(cursor.getColumnIndex(DbHelper.UPDATE_ACCOUNT_ACC)));
		acc.setLastUpdated(cursor.getLong(cursor.getColumnIndex(DbHelper.UPDATE_ACCOUNT_UPDATED)));
		acc.setSignedIn(cursor.getInt(cursor.getColumnIndex(DbHelper.UPDATE_ACCOUNT_SIGNEDIN)));
		acc.setCounty(cursor.getString(cursor.getColumnIndex(DbHelper.UPDATE_ACCOUNT_COUNTY)));
		acc.setAddress(cursor.getString(cursor.getColumnIndex(DbHelper.UPDATE_ACCOUNT_ADDRESS)));
		return acc;
	}
	public static void updateAccount(SQLiteDatabase db,long time){
		UpAcc acc=DbQuery.getUpAcc(db);
		if(acc.getLastUpdated()<=time){
			ContentValues cv=new ContentValues();
			cv.put(DbHelper.UPDATE_ACCOUNT_UPDATED, time);
			db.update(DbHelper.TABLE_UPDATE_ACCOUNT, cv, DbHelper.UPDATE_ACCOUNT_ID+"=1", null);
		}
	}
}
