package uwi.dcit.AgriExpenseTT.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.util.List;

import uwi.dcit.AgriExpenseTT.cloud.CloudEndpointUtils;
import uwi.dcit.AgriExpenseTT.cloud.CloudInterface;
import uwi.dcit.AgriExpenseTT.models.CycleContract;
import uwi.dcit.AgriExpenseTT.models.CycleResourceContract.CycleResourceEntry;
import uwi.dcit.AgriExpenseTT.models.ResourcePurchaseContract;
import uwi.dcit.AgriExpenseTT.models.TransactionLogContract;
import uwi.dcit.AgriExpenseTT.models.UpdateAccountContract;
import uwi.dcit.agriexpensesvr.cycleApi.CycleApi;
import uwi.dcit.agriexpensesvr.cycleApi.model.Cycle;
import uwi.dcit.agriexpensesvr.cycleApi.model.CycleCollection;
import uwi.dcit.agriexpensesvr.cycleUseApi.CycleUseApi;
import uwi.dcit.agriexpensesvr.cycleUseApi.model.CycleUse;
import uwi.dcit.agriexpensesvr.cycleUseApi.model.CycleUseCollection;
import uwi.dcit.agriexpensesvr.rPurchaseApi.RPurchaseApi;
import uwi.dcit.agriexpensesvr.rPurchaseApi.model.RPurchase;
import uwi.dcit.agriexpensesvr.rPurchaseApi.model.RPurchaseCollection;
import uwi.dcit.agriexpensesvr.translogApi.TranslogApi;
import uwi.dcit.agriexpensesvr.translogApi.model.TransLog;
import uwi.dcit.agriexpensesvr.translogApi.model.TransLogCollection;
import uwi.dcit.agriexpensesvr.upAccApi.model.UpAcc;


public class TransactionLog {
	SQLiteDatabase db;
	DbHelper dbh;
	Context context;
//	public static final String TL_BEGIN="begin";
//	public static final String TL_END="end";
	public static final String TL_INS="ins";
	public static final String TL_DEL="del";
	public static final String TL_UPDATE="updt";
	//a variation of a transaction log, this records the operation and the row that was affected
	//based on the operation associated different tables are associated
	
	public TransactionLog(DbHelper dbh,SQLiteDatabase db,Context context){
		this.dbh=dbh;
		this.db=db;
		this.context=context;
	}
	public boolean pullAllFromCloud(UpAcc cloudAcc){
		System.out.println("----"+cloudAcc.getAcc());
		
		CycleCollection responseCycles;
		CycleApi.Builder builder = new CycleApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
        CycleApi endpoint = builder.build();
		try {
			responseCycles = endpoint.getAllCycles(cloudAcc.getAcc()).execute();
		} catch (IOException e) {e.printStackTrace();
			return false;}
		List<Cycle> cycleList=responseCycles.getItems();
			
			
		CycleUseCollection responseCycleUse;
		CycleUseApi.Builder builderUse = new CycleUseApi.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),			         null);         
		builderUse = CloudEndpointUtils.updateBuilder(builderUse);
        CycleUseApi endpointUse = builderUse.build();
		try {
			responseCycleUse = endpointUse.getAllCycleUse(cloudAcc.getAcc()).execute();
		} catch (IOException e) { e.printStackTrace(); return false; }

        List<CycleUse> cycleUseList=responseCycleUse.getItems();
		
		
		RPurchaseCollection responsePurchase;
		RPurchaseApi.Builder builderPurchase = new RPurchaseApi.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builderPurchase = CloudEndpointUtils.updateBuilder(builderPurchase);
        RPurchaseApi endpointPurchase = builderPurchase.build();
		try {
			responsePurchase = endpointPurchase.getAllPurchases(cloudAcc.getAcc()).execute();
		} catch (IOException e) {e.printStackTrace();return false;}

		List<RPurchase> purchaseList=responsePurchase.getItems();
		
		TransLogCollection responseTranslog;
		TranslogApi.Builder builderTransLog = new TranslogApi.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builderTransLog = CloudEndpointUtils.updateBuilder(builderTransLog);
        TranslogApi endpointTranslog = builderTransLog.build();
		try {
			responseTranslog = endpointTranslog.getAllTranslog(cloudAcc.getAcc()).execute();
		} catch (IOException e) {e.printStackTrace(); return false;}

		List<TransLog> translogList = responseTranslog.getItems();
		
		dbh.tblMnger.dropTables(db);
		dbh.onCreate(db);
		
		ContentValues cv;
		System.out.println("Cycles now");
		for(Cycle c:cycleList){
			System.out.println("***");
			cv=new ContentValues();
			cv.put(CycleContract.CycleEntry._ID, c.getId());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_CROPID, c.getCropId());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_LAND_TYPE, c.getLandType());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_LAND_AMOUNT, c.getLandQty());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_TOTALSPENT, c.getTotalSpent());
			//cv.put(DbHelper.CROPCYCLE_DATE, c.get);
			db.insert(CycleContract.CycleEntry.TABLE_NAME, null, cv);
			DbQuery.insertCloudKey(db, dbh, CycleContract.CycleEntry.TABLE_NAME,c.getKeyrep(), c.getId());
		}
		
		System.out.println("CycleUses now");
		for(CycleUse c: cycleUseList){
			System.out.println("***");
			//try {System.out.println(c.toPrettyString());} catch (IOException e) {e.printStackTrace();}
			cv=new ContentValues();
			cv.put(CycleResourceEntry._ID, c.getId());
			cv.put(CycleResourceEntry.CYCLE_RESOURCE_CYCLEID, c.getCycleid());
			cv.put(CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID, c.getPurchaseId());
			cv.put(CycleResourceEntry.CYCLE_RESOURCE_QTY, c.getAmount());
			cv.put(CycleResourceEntry.CYCLE_RESOURCE_TYPE, c.getResource());
			//cv.put(DbHelper.CYCLE_RESOURCE_QUANTIFIER, c.g);
			cv.put(CycleResourceEntry.CYCLE_RESOURCE_USECOST, c.getCost());
			db.insert(CycleResourceEntry.TABLE_NAME, null, cv);
			DbQuery.insertCloudKey(db, dbh, CycleResourceEntry.TABLE_NAME, c.getKeyrep(), c.getId());
		}

		System.out.println("Purchases");
		for(RPurchase p: purchaseList){
			System.out.println("***");
			cv=new ContentValues();
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry._ID, p.getPId());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE, p.getType());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID, p.getResourceId());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY, p.getQty());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER, p.getQuantifier());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_COST, p.getCost());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING, p.getQtyRemaining());
			db.insert(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, null, cv);
			DbQuery.insertCloudKey(db, dbh, ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, p.getKeyrep(), p.getPId());
		}
		
		System.out.println("transactions");
		for(TransLog t:translogList){
			System.out.println("*** Id:"+t.getId());
			
			cv=new ContentValues();
			cv.put(TransactionLogContract.TransactionLogEntry._ID, t.getId());
			cv.put(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_TABLE, t.getTableKind());
			cv.put(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_ROWID, t.getRowId());
			cv.put(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_OPERATION, t.getOperation());
			cv.put(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_TRANSTIME, t.getTransTime());
			db.insert(TransactionLogContract.TransactionLogEntry.TABLE_NAME, null, cv);
			DbQuery.updateAccount(db, t.getTransTime());
		}
		cv=new ContentValues();
		cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_ACC, cloudAcc.getAcc());
		cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_CLOUD_KEY, cloudAcc.getKeyrep());
		db.update(UpdateAccountContract.UpdateAccountEntry.TABLE_NAME, cv, UpdateAccountContract.UpdateAccountEntry._ID+"=1", null);
		
		return true;
		
	}
	
	
	public int insertTransLog(String table,int rowId,String operation){
		ContentValues cv=new ContentValues();
		long unixTime = System.currentTimeMillis() / 1000L;
		cv.put(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_OPERATION, operation);
		cv.put(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_TABLE, table);
		cv.put(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_ROWID, rowId);
		cv.put(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_TRANSTIME, unixTime);
		db.insert(TransactionLogContract.TransactionLogEntry.TABLE_NAME, null, cv);
		int row=DbQuery.getLast(db, dbh, TransactionLogContract.TransactionLogEntry.TABLE_NAME);
		DbQuery.updateAccount(db, unixTime);
		return row;//returns the row number of the record just inserted
	}
	//given a time the cloud was last updated will try to update the cloud based on the transactions that happened after that point
	public void updateCloud(long lastUpdate){
		//get all the transactions that happened after the cloud's last update
		System.out.println("starting Qquery");
		String code="select * from "+ TransactionLogContract.TransactionLogEntry.TABLE_NAME+
				" where "+ TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_TRANSTIME+">="+lastUpdate+";";
		Cursor cursor=db.rawQuery(code, null);
		
		//will now attempt to recreate such operations on the cloud
		while(cursor.moveToNext()){
			System.out.println("records");
			String operation=cursor.getString(cursor.getColumnIndex(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_OPERATION));
			String table=cursor.getString(cursor.getColumnIndex(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_TABLE));
			int rowId=cursor.getInt(cursor.getColumnIndex(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_ROWID));
			//now that i have the table, row and operation i can insert a record into the redo log
			//by inserting it into the redo log I can be assured it will be inserted when possible
			DbQuery.insertRedoLog(db, dbh, table, rowId, operation);
		}
        cursor.close();
		CloudInterface c=new CloudInterface(context, db, dbh);
		c.flushToCloud();
	}
	public boolean createCloud(String namespace){
		
		CycleApi.Builder builder = new CycleApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
        CycleApi endpointCyc = builder.build();

		CycleUseApi.Builder builderUse = new CycleUseApi.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),			         null);         
		builderUse = CloudEndpointUtils.updateBuilder(builderUse);
        CycleUseApi endpointUse = builderUse.build();
		
		
		RPurchaseApi.Builder builderPurchase = new RPurchaseApi.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builderPurchase = CloudEndpointUtils.updateBuilder(builderPurchase);
        RPurchaseApi endpointPurchase = builderPurchase.build();
		
		TranslogApi.Builder builderTranslog = new TranslogApi.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
        TranslogApi endpointTranslog = builderTranslog.build();
		
		String code="select * from "+ CycleContract.CycleEntry.TABLE_NAME;//TODO where something (constraints unknown yet
		Cursor cursor = db.rawQuery(code, null);
		while(cursor.moveToNext()){
			Cycle c=new Cycle();
			c.setId(cursor.getInt(cursor.getColumnIndex(CycleContract.CycleEntry._ID)));
			c.setCropId(cursor.getInt(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_CROPID)));
			c.setLandQty(cursor.getDouble(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_LAND_AMOUNT)));
			c.setLandType(cursor.getString(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_LAND_TYPE)));
			c.setTotalSpent(cursor.getDouble(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_TOTALSPENT)));
			c.setHarvestType(cursor.getString(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_HARVEST_TYPE)));
			c.setHarvestAmt(cursor.getDouble(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_HARVEST_AMT)));
			c.setCostPer(cursor.getDouble(cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_COSTPER)));
			c.setAccount(namespace);
			try {
				c=endpointCyc.insertCycle(c).execute();
			} catch (IOException e) {e.printStackTrace();
				return false;}
			System.out.println(c.getKeyrep()+"  "+c.getId());
			DbQuery.insertCloudKey(db, dbh, CycleContract.CycleEntry.TABLE_NAME, c.getKeyrep(), c.getId());
		}
        cursor.close();


		code="select * from "+CycleResourceEntry.TABLE_NAME;
		cursor = db.rawQuery(code, null);
		while(cursor.moveToNext()){
			CycleUse c=new CycleUse();
			c.setId(cursor.getInt(cursor.getColumnIndex(CycleResourceEntry._ID)));
			c.setAmount(cursor.getDouble(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_QTY)));
			c.setCycleid(cursor.getInt(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_CYCLEID)));
			c.setPurchaseId(cursor.getInt(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID)));
			c.setResource(cursor.getString(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_TYPE)));
			c.setCost(cursor.getDouble(cursor.getColumnIndex(CycleResourceEntry.CYCLE_RESOURCE_USECOST)));
			//c.setQuantifier(cursor.getString(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_QUANTIFIER)));
			c.setAccount(namespace);
			try {
				c=endpointUse.insertCycleUse(c).execute();
			} catch (IOException e) {e.printStackTrace();
				return false;}
			DbQuery.insertCloudKey(db, dbh, CycleResourceEntry.TABLE_NAME, c.getKeyrep(), c.getId());
		}
        cursor.close();

		code="select * from "+ ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME;
		cursor = db.rawQuery(code, null);
		while(cursor.moveToNext()){
			RPurchase p=new RPurchase();
			p.setPId(cursor.getInt(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry._ID)));
			p.setResourceId(cursor.getInt(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID)));
			p.setType(cursor.getString(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE)));
			p.setQuantifier(cursor.getString(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER)));
			p.setQty(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY)));
			p.setCost(cursor.getDouble(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_COST)));
			p.setQtyRemaining(cursor.getDouble(cursor.getColumnIndex( ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING)));
			p.setAccount(namespace);
			try {
				p=endpointPurchase.insertRPurchase(p).execute();
			} catch (IOException e) {e.printStackTrace();
				return false;}
			DbQuery.insertCloudKey(db, dbh, ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, p.getKeyrep(), p.getPId());
		}
		cursor.close();

		ContentValues cv=new ContentValues();
		cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_ACC, namespace);
		db.update(UpdateAccountContract.UpdateAccountEntry.TABLE_NAME, cv, UpdateAccountContract.UpdateAccountEntry._ID+"=1", null);


		code = "select * from "+ TransactionLogContract.TransactionLogEntry.TABLE_NAME;
		CloudInterface cloudIF=new CloudInterface(context, db, dbh);
		cursor = db.rawQuery(code, null);
		while(cursor.moveToNext()){
			TransLog t=new TransLog();
			t.setId(cursor.getInt(cursor.getColumnIndex(TransactionLogContract.TransactionLogEntry._ID)));
			t.setAccount(namespace);
			t.setOperation(cursor.getString(cursor.getColumnIndex(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_OPERATION)));
			t.setTableKind(cursor.getString(cursor.getColumnIndex(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_TABLE)));
			t.setRowId(cursor.getInt(cursor.getColumnIndex(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_ROWID)));
			t.setTransTime(cursor.getLong(cursor.getColumnIndex(TransactionLogContract.TransactionLogEntry.TRANSACTION_LOG_TRANSTIME)));
			t.setKeyrep(DbQuery.getKey(db, dbh, t.getTableKind(), t.getRowId()));
			try {
				System.out.println(t.toPrettyString());
				t=endpointTranslog.insertTransLog(t).execute();
				cloudIF.updateUpAccC(t.getTransTime());
			} catch (IOException e) {e.printStackTrace();
				return false;}
		}
        cursor.close();

		cv=new ContentValues();
		cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_SIGNEDIN, 1);
		db.update(UpdateAccountContract.UpdateAccountEntry.TABLE_NAME, cv, UpdateAccountContract.UpdateAccountEntry._ID+"=1", null);
		return true;
	}
	
	
	public void logsUpdateLocal(String namespace,long lastLocalUpdate){
		new UpdateLocal(namespace,lastLocalUpdate).execute();
	}

	public class UpdateLocal extends AsyncTask<Void,Void,Void>{
		String namespace;
		long lastLocalUpdate;
		public UpdateLocal(String namespace,long lastLocalUpdate){
			this.namespace=namespace;
			this.lastLocalUpdate=lastLocalUpdate;
		}
		@Override
		protected Void doInBackground(Void... params) {
            TranslogApi.Builder builder = new TranslogApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
            TranslogApi endpoint = builder.build();
			TransLogCollection tlist = null;
			
			try {
				tlist=endpoint.logs(lastLocalUpdate, namespace).execute();
			} catch (IOException e) {e.printStackTrace();}
			if (tlist != null) {
                List<TransLog> transList = tlist.getItems();
                for (TransLog tLog : transList) {
                    if (tLog.getOperation().equals(TransactionLog.TL_INS))
                        logInsertLocal(tLog, namespace);
                    else if (tLog.getOperation().equals(TransactionLog.TL_UPDATE))
                        logUpdateLocal(tLog, namespace);
                    else if (tLog.getOperation().equals(TransactionLog.TL_DEL)) {
                        logDeleteLocal(tLog, namespace);
                    }
                }
            }
			return null;
		}
	}
	public void logInsertLocal (TransLog t,String namespace){
		ContentValues cv=new ContentValues();
		if(t.getTableKind().equals(CycleContract.CycleEntry.TABLE_NAME)){
			Cycle c=getCycle(namespace,t.getKeyrep());
			cv.put(CycleContract.CycleEntry._ID, t.getRowId());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_CROPID, c.getCropId());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_LAND_TYPE, c.getLandType());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_LAND_AMOUNT, c.getLandQty());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_TOTALSPENT, c.getTotalSpent());
			//cv.put(DbHelper.CROPCYCLE_DATE, c.get);
			cv.put(CycleContract.CycleEntry.CROPCYCLE_HARVEST_TYPE, c.getHarvestType());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_HARVEST_AMT, c.getHarvestAmt());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_COSTPER, c.getCostPer());
			db.insert(CycleContract.CycleEntry.TABLE_NAME, null, cv);
		}else if(t.getTableKind().equals(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME)){
			RPurchase p=getPurchase(namespace,t.getKeyrep());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry._ID, t.getRowId());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE, p.getType());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID, p.getResourceId());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY, p.getQty());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER, p.getQuantifier());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_COST, p.getCost());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING, p.getQtyRemaining());
			db.insert(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, null, cv);
		}else if(t.getTableKind().equals(CycleResourceEntry.TABLE_NAME)){
			CycleUse c=getCycleUse(namespace,t.getKeyrep());
			cv.put(CycleResourceEntry._ID, t.getRowId());
			//cv.put(DbHelper.CYCLE_RESOURCE_TYPE, );
			cv.put(CycleResourceEntry.CYCLE_RESOURCE_CYCLEID, c.getCycleid());
			cv.put(CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID, c.getPurchaseId());
			cv.put(CycleResourceEntry.CYCLE_RESOURCE_QTY, c.getAmount());
			//cv.put(DbHelper.CYCLE_RESOURCE_QUANTIFIER, c.get);
			cv.put(CycleResourceEntry.CYCLE_RESOURCE_USECOST, c.getCost());
			db.insert(CycleResourceEntry.TABLE_NAME, null, cv);
		}
	}
	public void logUpdateLocal(TransLog t,String namespace){
		ContentValues cv=new ContentValues();
		if(t.getTableKind().equals(CycleContract.CycleEntry.TABLE_NAME)){
			Cycle c=getCycle(namespace,t.getKeyrep());
			cv.put(CycleContract.CycleEntry._ID, t.getRowId());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_CROPID, c.getCropId());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_LAND_TYPE, c.getLandType());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_LAND_AMOUNT, c.getLandQty());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_TOTALSPENT, c.getTotalSpent());
			//cv.put(DbHelper.CROPCYCLE_DATE, c.ge);
			cv.put(CycleContract.CycleEntry.CROPCYCLE_HARVEST_TYPE, c.getHarvestType());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_HARVEST_AMT, c.getHarvestAmt());
			cv.put(CycleContract.CycleEntry.CROPCYCLE_COSTPER, c.getCostPer());
			db.update(CycleContract.CycleEntry.TABLE_NAME, cv, CycleContract.CycleEntry._ID+"="+t.getRowId(), null);
		}else if(t.getTableKind().equals(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME)){
			RPurchase p=getPurchase(namespace,t.getKeyrep());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry._ID, t.getRowId());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE, p.getType());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID, p.getResourceId());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY, p.getQty());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER, p.getQuantifier());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_COST, p.getCost());
			cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING, p.getQtyRemaining());
			db.update(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, cv, ResourcePurchaseContract.ResourcePurchaseEntry._ID+"="+t.getRowId(), null);
		}else if(t.getTableKind().equals(CycleResourceEntry.TABLE_NAME)){
			CycleUse c=getCycleUse(namespace,t.getKeyrep());
			cv.put(CycleResourceEntry._ID, t.getRowId());
			cv.put(CycleResourceEntry.CYCLE_RESOURCE_TYPE,c.getResource());
			cv.put(CycleResourceEntry.CYCLE_RESOURCE_CYCLEID, c.getCycleid());
			cv.put(CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID, c.getPurchaseId());
			cv.put(CycleResourceEntry.CYCLE_RESOURCE_QTY, c.getAmount());
			//cv.put(DbHelper.CYCLE_RESOURCE_QUANTIFIER, c.get);
			cv.put(CycleResourceEntry.CYCLE_RESOURCE_USECOST, c.getCost());
			db.update(CycleResourceEntry.TABLE_NAME, cv, CycleResourceEntry._ID+"="+t.getRowId(), null);
		}
	}

    //TODO Refactor code to only accept one parameter when full use is determined
	private void logDeleteLocal(TransLog tLog, String namespace2) {
        try {
            DbQuery.deleteRecord(db, dbh, tLog.getTableKind(), tLog.getRowId());
        }catch (Exception e){e.printStackTrace();}
	}

	private Cycle getCycle(String namespace, String keyrep){
		CycleApi.Builder builder = new CycleApi.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
        CycleApi endpoint = builder.build();
		Cycle c = null;
		try {
			c=endpoint.getCycle(namespace,keyrep).execute();
		} catch (IOException e) {e.printStackTrace();}
		return c;
	}
	private RPurchase getPurchase(String namespace,String keyrep){
		RPurchaseApi.Builder builder = new RPurchaseApi.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
        RPurchaseApi endpoint = builder.build();
		RPurchase p = null;
		try {
			p=endpoint.getRPurchase(namespace, keyrep).execute();
		} catch (IOException e) {e.printStackTrace();}
		return p;
	}
	private CycleUse getCycleUse(String namespace, String keyrep){
		CycleUseApi.Builder builder = new CycleUseApi.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
        CycleUseApi endpoint = builder.build();
		CycleUse c=null;
		try {
			c=endpoint.getCycleUse(namespace, keyrep).execute();
		} catch (IOException e) {e.printStackTrace();}
		return c;
	}
	
	
	
	
	public void updateCloud_(TransLog t){
		//get obj from cloud
		if(t.getTableKind().equals(CycleContract.CycleEntry.TABLE_NAME)){
			CycleApi.Builder builder = new CycleApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			builder.build();
			
			//Cycle c=endpoint.getCycle(t.getKeyrep());
		}
		if(t.getTableKind().equals(CycleResourceEntry.TABLE_NAME)){
			CycleUseApi.Builder builderUse = new CycleUseApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builderUse = CloudEndpointUtils.updateBuilder(builderUse);
			builderUse.build();
		}
		if(t.getTableKind().equals(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME)){
			RPurchaseApi.Builder builderPurchase = new RPurchaseApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builderPurchase = CloudEndpointUtils.updateBuilder(builderPurchase);
			builderPurchase.build();
			
		}
	}
	
	//removes all entries of kinds Cycle,CycleUse,RPurchase,TransLog But UpAcc remains
	public void removeNamespace(String namespace){
		CycleApi.Builder builder = new CycleApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
        CycleApi endpoint = builder.build();
		try {
			endpoint.deleteAll(namespace).execute();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		CycleUseApi.Builder builderUse = new CycleUseApi.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),			         null);         
		builderUse = CloudEndpointUtils.updateBuilder(builderUse);
        CycleUseApi endpointUse = builderUse.build();
		try {
			
			endpointUse.deleteAll(namespace).execute();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		RPurchaseApi.Builder builderPurchase = new RPurchaseApi.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builderPurchase = CloudEndpointUtils.updateBuilder(builderPurchase);
        RPurchaseApi endpointPurchase = builderPurchase.build();
		try {
			endpointPurchase.deleteAll(namespace).execute();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		TranslogApi.Builder builderTransLog = new TranslogApi.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builderTransLog = CloudEndpointUtils.updateBuilder(builderTransLog);
        TranslogApi endpointTranslog = builderTransLog.build();
		try {
			endpointTranslog.deleteAll(namespace).execute();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
