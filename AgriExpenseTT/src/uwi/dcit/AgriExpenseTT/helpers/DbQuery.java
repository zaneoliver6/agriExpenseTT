package uwi.dcit.AgriExpenseTT.helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.models.CloudKeyContract.CloudKeyEntry;
import uwi.dcit.AgriExpenseTT.models.CountryContract.CountryEntry;
import uwi.dcit.AgriExpenseTT.models.CountyContract.CountyEntry;
import uwi.dcit.AgriExpenseTT.models.CycleContract.CycleEntry;
import uwi.dcit.AgriExpenseTT.models.CycleResourceContract.CycleResourceEntry;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;
import uwi.dcit.AgriExpenseTT.models.LocalCycleUse;
import uwi.dcit.AgriExpenseTT.models.LocalResourcePurchase;
import uwi.dcit.AgriExpenseTT.models.RedoLogContract.RedoLogEntry;
import uwi.dcit.AgriExpenseTT.models.ResourceContract;
import uwi.dcit.AgriExpenseTT.models.ResourcePurchaseContract.ResourcePurchaseEntry;
import uwi.dcit.AgriExpenseTT.models.TransactionLogContract.TransactionLogEntry;
import uwi.dcit.AgriExpenseTT.models.UpdateAccountContract;
import uwi.dcit.agriexpensesvr.cycleApi.model.Cycle;
import uwi.dcit.agriexpensesvr.cycleUseApi.model.CycleUse;
import uwi.dcit.agriexpensesvr.rPurchaseApi.model.RPurchase;
import uwi.dcit.agriexpensesvr.translogApi.model.TransLog;
import uwi.dcit.agriexpensesvr.upAccApi.model.UpAcc;

public class DbQuery {
	
	//Used to insert a new Chemical, Crop, Fertilizer, Labourer
	public static int insertResource(SQLiteDatabase db,DbHelper dbh,String type,String name){
		ContentValues cv= new ContentValues();
		cv.put(ResourceContract.ResourceEntry.RESOURCES_NAME,name);
		cv.put(ResourceContract.ResourceEntry.RESOURCES_TYPE,type);
		db.insert(ResourceContract.ResourceEntry.TABLE_NAME, null, cv);
		return getLast(db, dbh, ResourceContract.ResourceEntry.TABLE_NAME);
	}

	//this is for when the farmer buys any material crop, fertilizer, chemical NOT WHEN HE USES
	public static int insertResourceExp(SQLiteDatabase db, DbHelper dbh, String type, int resourceId, String quantifier, double qty, double cost, TransactionLog tl){
		ContentValues cv= new ContentValues();
		cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID, resourceId);
		cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE, type);
		cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER, quantifier);
		cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY, qty);
		cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_COST, cost);
		cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING, qty);
		cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_RESOURCE, DbQuery.findResourceName(db, dbh, resourceId));
		db.insert(ResourcePurchaseEntry.TABLE_NAME, null, cv);
		int rowId=getLast(db, dbh, ResourcePurchaseEntry.TABLE_NAME);
		 tl.insertTransLog(ResourcePurchaseEntry.TABLE_NAME, rowId, TransactionLog.TL_INS);//records the insert of a purchase
		return rowId;
	}

    public static int insertResourceExp(SQLiteDatabase db, DbHelper dbh, String type, int resourceId, String quantifier, double qty, double cost, long time, TransactionLog tl){
        ContentValues cv= new ContentValues();
        cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID, resourceId);
        cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE, type);
        cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER, quantifier);
        cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY, qty);
        cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_COST, cost);
        cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING, qty);
        cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_RESOURCE, DbQuery.findResourceName(db, dbh, resourceId));
        cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_DATE, time);

        db.insert(ResourcePurchaseEntry.TABLE_NAME, null, cv);
        int rowId=getLast(db, dbh, ResourcePurchaseEntry.TABLE_NAME);
        tl.insertTransLog(ResourcePurchaseEntry.TABLE_NAME, rowId, TransactionLog.TL_INS);//records the insert of a purchase
        return rowId;
    }
	
	//based on the material being used inserts USE of a particular material for a particular CYCLE
	public static int insertResourceUse(SQLiteDatabase db, DbHelper dbh,int cycleId, String type, int resourcePurchasedId, double qty,String quantifier,double useCost,TransactionLog tl){
		ContentValues cv= new ContentValues();
		cv.put(CycleResourceEntry.CYCLE_RESOURCE_CYCLEID, cycleId);
		cv.put(CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID, resourcePurchasedId);
		cv.put(CycleResourceEntry.CYCLE_RESOURCE_QTY, qty);
		cv.put(CycleResourceEntry.CYCLE_RESOURCE_TYPE, type);
		cv.put(CycleResourceEntry.CYCLE_RESOURCE_QUANTIFIER, quantifier);
		cv.put(CycleResourceEntry.CYCLE_RESOURCE_USECOST, useCost);
		
		db.insert(CycleResourceEntry.TABLE_NAME, null, cv);
		int rowId=getLast(db, dbh, CycleResourceEntry.TABLE_NAME);
		tl.insertTransLog(CycleResourceEntry.TABLE_NAME, rowId, TransactionLog.TL_INS);
		return rowId; 
	}

    public static int insertResourceUse(SQLiteDatabase db, DbHelper dbh,int cycleId, String type, int resourcePurchasedId, double qty, String quantifier, double useCost, long time, TransactionLog tl){
        ContentValues cv= new ContentValues();
        cv.put(CycleResourceEntry.CYCLE_RESOURCE_CYCLEID, cycleId);
        cv.put(CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID, resourcePurchasedId);
        cv.put(CycleResourceEntry.CYCLE_RESOURCE_QTY, qty);
        cv.put(CycleResourceEntry.CYCLE_RESOURCE_TYPE, type);
        cv.put(CycleResourceEntry.CYCLE_RESOURCE_QUANTIFIER, quantifier);
        cv.put(CycleResourceEntry.CYCLE_RESOURCE_USECOST, useCost);
//        cv.put(CycleResourceEntry.)

        db.insert(CycleResourceEntry.TABLE_NAME, null, cv);
        int rowId=getLast(db, dbh, CycleResourceEntry.TABLE_NAME);
        tl.insertTransLog(CycleResourceEntry.TABLE_NAME, rowId, TransactionLog.TL_INS);
        return rowId;
    }
	
	public static int insertCycle(SQLiteDatabase db, DbHelper dbh,int cropId, String landType, double landQty,TransactionLog tl,long time){
		ContentValues cv=new ContentValues();
		cv.put(CycleEntry.CROPCYCLE_CROPID, cropId);
		cv.put(CycleEntry.CROPCYCLE_LAND_TYPE, landType);
		cv.put(CycleEntry.CROPCYCLE_LAND_AMOUNT, landQty);
		cv.put(CycleEntry.CROPCYCLE_DATE, time);
		cv.put(CycleEntry.CROPCYCLE_TOTALSPENT, 0.0);
		cv.put(CycleEntry.CROPCYCLE_COSTPER, 0.0);
		cv.put(CycleEntry.CROPCYCLE_HARVEST_AMT, 0.0);
		cv.put(CycleEntry.CROPCYCLE_HARVEST_TYPE,"Lb");
		cv.put(CycleEntry.CROPCYCLE_RESOURCE, DbQuery.findResourceName(db, dbh, cropId));
		db.insert(CycleEntry.TABLE_NAME, null,cv);
		int rowId=getLast(db, dbh, CycleEntry.TABLE_NAME);
		tl.insertTransLog(CycleEntry.TABLE_NAME,rowId,TransactionLog.TL_INS );
		return rowId;
	}

    public static int insertCycle(SQLiteDatabase db, DbHelper dbh, int cropId, String name, String landType, double landQty, TransactionLog tL, long time) {
        ContentValues cv=new ContentValues();
        cv.put(CycleEntry.CROPCYCLE_CROPID, cropId);
        cv.put(CycleEntry.CROPCYCLE_LAND_TYPE, landType);
        cv.put(CycleEntry.CROPCYCLE_LAND_AMOUNT, landQty);
        cv.put(CycleEntry.CROPCYCLE_DATE, time);
        cv.put(CycleEntry.CROPCYCLE_TOTALSPENT, 0.0);
        cv.put(CycleEntry.CROPCYCLE_COSTPER, 0.0);
        cv.put(CycleEntry.CROPCYCLE_HARVEST_AMT, 0.0);
        cv.put(CycleEntry.CROPCYCLE_HARVEST_TYPE,"Lb");
        cv.put(CycleEntry.CROPCYCLE_RESOURCE, DbQuery.findResourceName(db, dbh, cropId));
        cv.put(CycleEntry.CROPCYCLE_NAME, name);
        db.insert(CycleEntry.TABLE_NAME, null,cv);
        int rowId=getLast(db, dbh, CycleEntry.TABLE_NAME);
        tL.insertTransLog(CycleEntry.TABLE_NAME,rowId,TransactionLog.TL_INS );
        return rowId;
    }
	
	public static int insertCountry(SQLiteDatabase db, String country, String type){
		ContentValues cv = new ContentValues();
		cv.put(CountryEntry.COLUMN_NAME_COUNTRY, country);
		cv.put(CountryEntry.COLUMN_NAME_TYPE, type);
		return (int)db.insert(CountryEntry.TABLE_NAME, null, cv);
	}
	
	public static int insertCountry(SQLiteDatabase db, String country){
		return insertCountry(db, country, "parish");
	}
	
	public static int insertCounty(SQLiteDatabase db, String country, String county){
		int countryId = getCountryIdByName(db, country);
		if (countryId == -1)return -1; //not a valid country specified
		return insertCounty(db, countryId, county);
	}
	
	public static int insertCounty(SQLiteDatabase db, int country, String county){
		ContentValues cv = new ContentValues();
		cv.put(CountyEntry.COLUMN_NAME_COUNTRY, country);
		cv.put(CountyEntry.COLUMN_NAME_COUNTY, county);
		return (int)db.insert(CountyEntry.TABLE_NAME, null, cv);
	}
	
	public static ArrayList<String> getCountries(SQLiteDatabase db, ArrayList<String> list){
		if (list == null)list = new ArrayList<String>();
		String sqlStr = "SELECT country FROM " + CountryEntry.TABLE_NAME +";";
		Cursor cursor = db.rawQuery(sqlStr, null);
		while (cursor.moveToNext()) list.add(cursor.getString(cursor.getColumnIndex("country")));
		cursor.close();
		return list;
	}
	
	public static int getCountryIdByName(SQLiteDatabase db, String country){
		int id = -1;
		String sqlStr = "SELECT "+ CountryEntry._ID + " FROM "+ CountryEntry.TABLE_NAME +";";
		Cursor cursor = db.rawQuery(sqlStr, null);
		if (cursor.getCount() > 0){
			cursor.moveToFirst();
			id = cursor.getInt(0);
		}
		cursor.close();
		return id;
	}
	
	
	public static void getResources(SQLiteDatabase db, DbHelper dbh, String type,ArrayList<String> list){
		String code;
		if(type!=null)
			code="select name from "+ ResourceContract.ResourceEntry.TABLE_NAME+" where "+ ResourceContract.ResourceEntry.RESOURCES_TYPE+"='"+type+"';";
		else
			code="select name from "+ ResourceContract.ResourceEntry.TABLE_NAME;
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return;
		while(cursor.moveToNext()){
			list.add(cursor.getString(cursor.getColumnIndex("name")));
		}
		cursor.close();
	}
	public static int getNameResourceId(SQLiteDatabase db,DbHelper dbh,String name){
		String code="select "+ ResourceContract.ResourceEntry._ID+" from "+ ResourceContract.ResourceEntry.TABLE_NAME+" where "+ ResourceContract.ResourceEntry.RESOURCES_NAME+"='"+name+"';";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return -1;
		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndex(ResourceContract.ResourceEntry._ID));
	}
	public static void getCycles(SQLiteDatabase db, DbHelper dbh, ArrayList<LocalCycle> list){
		String code="select * from "+CycleEntry.TABLE_NAME+";";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return;
		while(cursor.moveToNext()){
			LocalCycle n=new LocalCycle();
			n.setId(cursor.getInt(cursor.getColumnIndex(CycleEntry._ID)));
			n.setCropId(cursor.getInt(cursor.getColumnIndex(CycleEntry.CROPCYCLE_CROPID)));
			n.setLandType(cursor.getString(cursor.getColumnIndex(CycleEntry.CROPCYCLE_LAND_TYPE)));
			n.setLandQty(cursor.getDouble(cursor.getColumnIndex(CycleEntry.CROPCYCLE_LAND_AMOUNT)));
			n.setTime(cursor.getLong(cursor.getColumnIndex(CycleEntry.CROPCYCLE_DATE)));
			n.setTotalSpent(cursor.getDouble(cursor.getColumnIndex(CycleEntry.CROPCYCLE_TOTALSPENT)));
			
			n.setHarvestAmt(cursor.getDouble(cursor.getColumnIndex(CycleEntry.CROPCYCLE_HARVEST_AMT)));
			n.setHarvestType(cursor.getString(cursor.getColumnIndex(CycleEntry.CROPCYCLE_HARVEST_TYPE)));
			n.setCostPer(cursor.getDouble(cursor.getColumnIndex(CycleEntry.CROPCYCLE_COSTPER)));
			n.setCropName(cursor.getString(cursor.getColumnIndex(CycleEntry.CROPCYCLE_RESOURCE)));
			list.add(n);
		}
	}
	
	public static String findResourceName(SQLiteDatabase db, DbHelper dbh, int id){
		String code="select name from "+ ResourceContract.ResourceEntry.TABLE_NAME+" where "+ ResourceContract.ResourceEntry._ID +"="+id+";";
		Cursor cursor=db.rawQuery(code,null);
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex("name"));
		}
		return null;
	}
	
	public static void getPurchases(SQLiteDatabase db, DbHelper dbh,ArrayList<LocalResourcePurchase>list,String type,String quantifier,boolean allowFinished){
		String code;
		if(type == null)
			code="select * from "+ResourcePurchaseEntry.TABLE_NAME+";";
		else
			code="select * from "+ResourcePurchaseEntry.TABLE_NAME+" where "+ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE+"='"+type+"';";

        Cursor cursor = db.rawQuery(code, null);
		if(cursor == null || cursor.getCount() < 1 )
			return;

        while(cursor.moveToNext()){
			LocalResourcePurchase m=new LocalResourcePurchase();
			m.setpId(cursor.getInt(cursor.getColumnIndex(ResourcePurchaseEntry._ID)));
			m.setResourceId(cursor.getInt(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID)));
			m.setType(cursor.getString(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE)));
			m.setQuantifier(cursor.getString(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER)));
			m.setQty(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY)));
			m.setCost(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_COST)));
			m.setQtyRemaining(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING)));
            m.setDate(cursor.getLong(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_DATE)));
			list.add(m);
		}
	}
	public static void getResourcePurchases(SQLiteDatabase db, DbHelper dbh,ArrayList<LocalResourcePurchase>list,int resId){
		String code;
		code="select * from "+ResourcePurchaseEntry.TABLE_NAME+" where "
		+ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID+"="+resId+";";
		Cursor cursor =db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return;
		while(cursor.moveToNext()){
			LocalResourcePurchase m=new LocalResourcePurchase();
			m.setpId(cursor.getInt(cursor.getColumnIndex(ResourcePurchaseEntry._ID)));
			m.setResourceId(cursor.getInt(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID)));
			m.setType(cursor.getString(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE)));
			m.setQuantifier(cursor.getString(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER)));
			m.setQty(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY)));
			m.setCost(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_COST)));
			m.setQtyRemaining(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING)));
			list.add(m);
		}
	}
	
	public static RPurchase getARPurchase(SQLiteDatabase db, DbHelper dbh,int id){
		String code="select * from "+ResourcePurchaseEntry.TABLE_NAME+" where "
				+ResourcePurchaseEntry._ID+"="+id+";";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return null;
		cursor.moveToFirst();
		RPurchase purchase=new RPurchase();
		purchase.setPId(id);
		purchase.setResourceId(cursor.getInt(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID)));
		purchase.setQuantifier(cursor.getString(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER)));
		purchase.setQty(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY)));
		purchase.setCost(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_COST)));
		purchase.setQtyRemaining(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING)));
		purchase.setType(cursor.getString(cursor.getColumnIndex(ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE)));
		purchase.setElementName(DbQuery.findResourceName(db, dbh, purchase.getResourceId()));
		return purchase;
	}
	public static void getCycleUse(SQLiteDatabase db, DbHelper dbh,int cycleid,ArrayList<LocalCycleUse> list,String type){
		String code;
		if(type==null)
			code="select * from "+CycleResourceEntry.TABLE_NAME+" where "+CycleResourceEntry.CYCLE_RESOURCE_CYCLEID+"="+cycleid+";";
		else
			code="select * from "+CycleResourceEntry.TABLE_NAME+" where "+CycleResourceEntry.CYCLE_RESOURCE_CYCLEID+"="+cycleid+" and "+CycleResourceEntry.CYCLE_RESOURCE_TYPE+"='"+type+"';";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return;
		while(cursor.moveToNext()){
			LocalCycleUse l=new LocalCycleUse();
			l.setId(cursor.getInt(cursor.getColumnIndex(CycleResourceEntry._ID)));
			l.setAmount(cursor.getDouble(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_QTY)));
			l.setCycleid(cursor.getInt(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_CYCLEID)));
			l.setPurchaseId(cursor.getInt(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID)));
			l.setResource(cursor.getString(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_TYPE)));
			l.setUseCost(cursor.getDouble(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_USECOST)));
			l.setQuantifier(cursor.getString(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_QUANTIFIER)));
			list.add(l);
		}
	}
	public static void getCycleUseP(SQLiteDatabase db, DbHelper dbh,int purchaseId,ArrayList<LocalCycleUse> list,String type){
		String code;
		if(type==null)
			code="select * from "+CycleResourceEntry.TABLE_NAME+" where "+CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID+"="+purchaseId+";";
		else
			code="select * from "+CycleResourceEntry.TABLE_NAME+" where "+CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID+"="+purchaseId+" and "+CycleResourceEntry.CYCLE_RESOURCE_TYPE+"='"+type+"';";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return;
		while(cursor.moveToNext()){
			LocalCycleUse l=new LocalCycleUse();
			l.setId(cursor.getInt(cursor.getColumnIndex(CycleResourceEntry._ID)));
			l.setAmount(cursor.getDouble(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_QTY)));
			l.setCycleid(cursor.getInt(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_CYCLEID)));
			l.setPurchaseId(cursor.getInt(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID)));
			l.setResource(cursor.getString(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_TYPE)));
			l.setUseCost(cursor.getDouble(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_USECOST)));
			list.add(l);
		}
	}
	
	public static CycleUse getACycleUse(SQLiteDatabase db, DbHelper dbh,int id){
		String code="select * from "+CycleResourceEntry.TABLE_NAME+" where "+CycleResourceEntry._ID+"="+id+";";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return null;
		CycleUse c = new CycleUse();
		cursor.moveToFirst();
		c.setId(cursor.getInt(cursor.getColumnIndex(CycleResourceEntry._ID)));
		c.setCycleid(cursor.getInt(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_CYCLEID)));
		c.setPurchaseId(cursor.getInt(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID)));
		c.setAmount(cursor.getDouble(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_QTY)));
		c.setCost(cursor.getDouble(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_USECOST)));
		c.setResource(cursor.getString(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_TYPE)));
		return c;
	}
	
	public static int getLast(SQLiteDatabase db, DbHelper dbh, String table){
		String code="select _id from " + table + "  ORDER BY _id DESC LIMIT 1;";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<0)
			return -1;
		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndex("_id"));
	}
	
	public static int getLastRec(SQLiteDatabase db, DbHelper dbh, String table){
		String code="select _id from "+table+"  ORDER BY _id DESC LIMIT 1;";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<0)
			return -1;
		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndex("_id"));
	}
	public static int insertRedoLog(SQLiteDatabase db, DbHelper dbh, String table, int id, String operation){
		ContentValues cv= new ContentValues();
		cv.put(RedoLogEntry.REDO_LOG_TABLE, table);
		cv.put(RedoLogEntry.REDO_LOG_ROW_ID, id);
		cv.put(RedoLogEntry.REDO_LOG_OPERATION, operation);
		db.insert(RedoLogEntry.TABLE_NAME, null, cv);
		return getLast(db,dbh,RedoLogEntry.TABLE_NAME);
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
	public static void insertUpAcc(SQLiteDatabase db,UpAcc acc){
		
		ContentValues cv=new ContentValues();
		cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_CLOUD_KEY, acc.getKeyrep());
		cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_ACC,acc.getAcc());
		cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_UPDATED, acc.getLastUpdated());
		cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_SIGNEDIN,acc.getSignedIn());
		db.insert(UpdateAccountContract.UpdateAccountEntry.TABLE_NAME, null, cv);
	}
	public static void insertCloudKey(SQLiteDatabase db,DbHelper dbh, String table, String k,int id){
		ContentValues cv = new ContentValues();
		cv.put(CloudKeyEntry.CLOUD_KEY_TABLE, table);
		cv.put(CloudKeyEntry.CLOUD_KEY, k);
		cv.put(CloudKeyEntry.CLOUD_KEY_ROWID, id);
		db.insert(CloudKeyEntry.TABLE_NAME, null, cv);
	}
	public static String getKey(SQLiteDatabase db,DbHelper dbh,String table,int id){
		String code="select * from "+CloudKeyEntry.TABLE_NAME+" where "
			+CloudKeyEntry.CLOUD_KEY_TABLE+"='"+table+"' and "
			+CloudKeyEntry.CLOUD_KEY_ROWID+"="+id+";";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1){
			return null;
		}
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndex(CloudKeyEntry.CLOUD_KEY));
	}
	public static int getCloudKeyId(SQLiteDatabase db,DbHelper dbh,String table,int id){String code="select * from "+CloudKeyEntry.TABLE_NAME+" where "
			+CloudKeyEntry.CLOUD_KEY_TABLE+"='"+table+"' and "
			+CloudKeyEntry.CLOUD_KEY_ROWID+"="+id+";";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return -1;
		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndex(CloudKeyEntry._ID));
	}
	public static Cycle getCycle(SQLiteDatabase db,DbHelper dbh,int id){
		String code="select * from "+CycleEntry.TABLE_NAME+" where "+CycleEntry._ID+"="+id+";";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return null;
		Cycle c = new Cycle();
		cursor.moveToFirst();
		c.setCropId(cursor.getInt(cursor.getColumnIndex(CycleEntry.CROPCYCLE_CROPID)));
		c.setId(id);
		c.setLandQty(cursor.getDouble(cursor.getColumnIndex(CycleEntry.CROPCYCLE_LAND_AMOUNT)));
		c.setLandType(cursor.getString(cursor.getColumnIndex(CycleEntry.CROPCYCLE_LAND_TYPE)));
		c.setTotalSpent(cursor.getDouble(cursor.getColumnIndex(CycleEntry.CROPCYCLE_TOTALSPENT)));
		c.setHarvestType(cursor.getString(cursor.getColumnIndex(CycleEntry.CROPCYCLE_HARVEST_TYPE)));
		c.setHarvestAmt(cursor.getDouble(cursor.getColumnIndex(CycleEntry.CROPCYCLE_HARVEST_AMT)));
		c.setCostPer(cursor.getDouble(cursor.getColumnIndex(CycleEntry.CROPCYCLE_COSTPER)));
		c.setCropName(cursor.getString(cursor.getColumnIndex(CycleEntry.CROPCYCLE_RESOURCE)));
		return c;
	}
	
	//pass in the operation and the table you want to get the records to redo
	public static void getRedo(SQLiteDatabase db,DbHelper dbh,ArrayList<Integer> rowIds,ArrayList<Integer> logIds,String operation,String Rtable){
		String code="select * from "+RedoLogEntry.TABLE_NAME+" where "
				+RedoLogEntry.REDO_LOG_OPERATION+"= '"+operation+"' and "
				+RedoLogEntry.REDO_LOG_TABLE+"= '"+Rtable+"';";
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return;
		while(cursor.moveToNext()){
			int n=cursor.getInt(cursor.getColumnIndex(RedoLogEntry.REDO_LOG_ROW_ID));
			rowIds.add(Integer.valueOf(n));
			n=cursor.getInt(cursor.getColumnIndex(RedoLogEntry._ID));
			logIds.add(Integer.valueOf(n));
		}
	}
	public static TransLog getLog(SQLiteDatabase db, DbHelper dbh, int rowId) {
		TransLog t=new TransLog();
		String code="select * from "+TransactionLogEntry.TABLE_NAME+" where "+ TransactionLogEntry._ID +"="+rowId;
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return null;
		cursor.moveToFirst();
		t.setId(cursor.getInt(cursor.getColumnIndex(TransactionLogEntry._ID)));
		t.setOperation(cursor.getString(cursor.getColumnIndex(TransactionLogEntry.TRANSACTION_LOG_OPERATION)));
		t.setTableKind(cursor.getString(cursor.getColumnIndex(TransactionLogEntry.TRANSACTION_LOG_TABLE)));
		t.setTransTime(cursor.getLong(cursor.getColumnIndex(TransactionLogEntry.TRANSACTION_LOG_TRANSTIME)));
		t.setRowId(cursor.getInt(cursor.getColumnIndex(TransactionLogEntry.TRANSACTION_LOG_ROWID)));
		
		return t;
	}
	public static String getAccount(SQLiteDatabase db){
		String code="select "+ UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_ACC+" from "+
				UpdateAccountContract.UpdateAccountEntry.TABLE_NAME;
		Cursor cursor=db.rawQuery(code, null);
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndex(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_ACC));
	}
	public static UpAcc getUpAcc(SQLiteDatabase db){
		String code="select * from " + UpdateAccountContract.UpdateAccountEntry.TABLE_NAME;
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount() < 1)return null;  	// No records exist so return null
		
		cursor.moveToFirst();					// Only one record should exist (TODO If only one record exist do we need an entire table?)
		UpAcc acc = new UpAcc();
		
		acc.setKeyrep(cursor.getString(cursor.getColumnIndex(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_CLOUD_KEY)));
		acc.setAcc(cursor.getString(cursor.getColumnIndex(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_ACC)));
		acc.setLastUpdated(cursor.getLong(cursor.getColumnIndex(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_UPDATED)));
		acc.setSignedIn(cursor.getInt(cursor.getColumnIndex(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_SIGNEDIN)));
		acc.setCounty(cursor.getString(cursor.getColumnIndex(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_COUNTY)));
		acc.setAddress(cursor.getString(cursor.getColumnIndex(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_ADDRESS)));
		
		return acc;
	}
	public static void updateAccount(SQLiteDatabase db,long time){
		UpAcc acc=DbQuery.getUpAcc(db);
		if(acc.getLastUpdated()<=time){
			ContentValues cv=new ContentValues();
			cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_UPDATED, time);
			db.update(UpdateAccountContract.UpdateAccountEntry.TABLE_NAME, cv, UpdateAccountContract.UpdateAccountEntry._ID+"=1", null);
		}
	}

    //checks to see if there are any crop cycles or not
    public static boolean cyclesExist(SQLiteDatabase db){
        String code="select COUNT(*) FROM "+CycleEntry.TABLE_NAME;
        Cursor c=db.rawQuery(code,null);
       if(c.moveToFirst()) {
           return c.getInt(0) > 0;
       }
       return false;
    }
    public static boolean resourceExist(SQLiteDatabase db){
        String code="select COUNT(*) FROM "+ResourcePurchaseEntry.TABLE_NAME+" where "+ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING+">0";
        Cursor c=db.rawQuery(code,null);
        if(c.moveToFirst()) {
            return c.getInt(0) > 0;
        }
        return false;
    }


}
