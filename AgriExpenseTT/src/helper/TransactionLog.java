package helper;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.example.agriexpensett.cycleendpoint.Cycleendpoint;
import com.example.agriexpensett.cycleendpoint.model.Cycle;
import com.example.agriexpensett.cycleendpoint.model.CycleCollection;
import com.example.agriexpensett.cycleuseendpoint.Cycleuseendpoint;
import com.example.agriexpensett.cycleuseendpoint.model.CycleUse;
import com.example.agriexpensett.cycleuseendpoint.model.CycleUseCollection;
import com.example.agriexpensett.rpurchaseendpoint.Rpurchaseendpoint;
import com.example.agriexpensett.rpurchaseendpoint.model.RPurchase;
import com.example.agriexpensett.rpurchaseendpoint.model.RPurchaseCollection;
import com.example.agriexpensett.translogendpoint.Translogendpoint;
import com.example.agriexpensett.translogendpoint.model.TransLog;
import com.example.agriexpensett.translogendpoint.model.TransLogCollection;
import com.example.agriexpensett.upaccendpoint.model.UpAcc;
//import uwi.dcit.AgriExpenseTT.translogendpoint.model.TransLogCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;

public class TransactionLog {
	SQLiteDatabase db;
	DbHelper dbh;
	Context context;
	public static final String TL_BEGIN="begin";
	public static final String TL_END="end";
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
		
		CycleCollection responseCycles = null; 
		Cycleendpoint.Builder builder = new Cycleendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
		Cycleendpoint endpoint = builder.build();
		try {
			responseCycles = endpoint.getAllCycles(cloudAcc.getAcc()).execute();
		} catch (IOException e) {e.printStackTrace();
			return false;}
		List<Cycle> cycleList=responseCycles.getItems();
			
			
		CycleUseCollection responseCycleUse = null;
		Cycleuseendpoint.Builder builderUse = new Cycleuseendpoint.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),			         null);         
		builderUse = CloudEndpointUtils.updateBuilder(builderUse);
		Cycleuseendpoint endpointUse = builderUse.build();
		try {
			responseCycleUse = endpointUse.getAllCycleUse(cloudAcc.getAcc()).execute();
		} catch (IOException e) {e.printStackTrace();
			return false;}
		List<CycleUse> cycleUseList=responseCycleUse.getItems();
		
		
		RPurchaseCollection responsePurchase = null;
		Rpurchaseendpoint.Builder builderPurchase = new Rpurchaseendpoint.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builderPurchase = CloudEndpointUtils.updateBuilder(builderPurchase);
		Rpurchaseendpoint endpointPurchase = builderPurchase.build();
		try {
			responsePurchase = endpointPurchase.getAllPurchases(cloudAcc.getAcc()).execute();
		} catch (IOException e) {e.printStackTrace();
			return false;}
		List<RPurchase> purchaseList=responsePurchase.getItems();
		
		TransLogCollection responseTranslog = null;
		Translogendpoint.Builder builderTransLog = new Translogendpoint.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builderTransLog = CloudEndpointUtils.updateBuilder(builderTransLog);
		Translogendpoint endpointTranslog = builderTransLog.build();
		try {
			responseTranslog = endpointTranslog.getAllTranslog(cloudAcc.getAcc()).execute();
		} catch (IOException e) {e.printStackTrace();
			return false;}
		List<TransLog> translogList=responseTranslog.getItems();
		
		dbh.dropTables(db);
		dbh.onCreate(db);
		
		ContentValues cv;
		System.out.println("Cycles now");
		for(Cycle c:cycleList){
			System.out.println("***");
			cv=new ContentValues();
			cv.put(DbHelper.CROPCYCLE_ID, c.getId());
			cv.put(DbHelper.CROPCYCLE_CROPID, c.getCropId());
			cv.put(DbHelper.CROPCYCLE_LAND_TYPE, c.getLandType());
			cv.put(DbHelper.CROPCYCLE_LAND_AMOUNT, c.getLandQty());
			cv.put(DbHelper.CROPCYCLE_TOTALSPENT, c.getTotalSpent());
			//cv.put(DbHelper.CROPCYCLE_DATE, c.get);
			db.insert(DbHelper.TABLE_CROPCYLE, null, cv);
			DbQuery.insertCloudKey(db, dbh, DbHelper.TABLE_CROPCYLE,c.getKeyrep(), c.getId());
		}
		
		System.out.println("CycleUses now");
		for(CycleUse c: cycleUseList){
			System.out.println("***");
			//try {System.out.println(c.toPrettyString());} catch (IOException e) {e.printStackTrace();}
			cv=new ContentValues();
			cv.put(DbHelper.CYCLE_RESOURCE_ID, c.getId());
			cv.put(DbHelper.CYCLE_RESOURCE_CYCLEID, c.getCycleid());
			cv.put(DbHelper.CYCLE_RESOURCE_PURCHASE_ID, c.getPurchaseId());
			cv.put(DbHelper.CYCLE_RESOURCE_QTY, c.getAmount());
			cv.put(DbHelper.CYCLE_RESOURCE_TYPE, c.getResource());
			//cv.put(DbHelper.CYCLE_RESOURCE_QUANTIFIER, c.g);
			cv.put(DbHelper.CYCLE_RESOURCE_USECOST, c.getCost());
			db.insert(DbHelper.TABLE_CYCLE_RESOURCES, null, cv);
			DbQuery.insertCloudKey(db, dbh, DbHelper.TABLE_CYCLE_RESOURCES, c.getKeyrep(), c.getId());
		}

		System.out.println("Purchases");
		for(RPurchase p: purchaseList){
			System.out.println("***");
			cv=new ContentValues();
			cv.put(DbHelper.RESOURCE_PURCHASE_ID, p.getPId());
			cv.put(DbHelper.RESOURCE_PURCHASE_TYPE, p.getType());
			cv.put(DbHelper.RESOURCE_PURCHASE_RESID, p.getResourceId());
			cv.put(DbHelper.RESOURCE_PURCHASE_QTY, p.getQty());
			cv.put(DbHelper.RESOURCE_PURCHASE_QUANTIFIER, p.getQuantifier());
			cv.put(DbHelper.RESOURCE_PURCHASE_COST, p.getCost());
			cv.put(DbHelper.RESOURCE_PURCHASE_REMAINING, p.getQtyRemaining());
			db.insert(DbHelper.TABLE_RESOURCE_PURCHASES, null, cv);
			DbQuery.insertCloudKey(db, dbh, DbHelper.TABLE_RESOURCE_PURCHASES, p.getKeyrep(), p.getPId());
		}
		
		System.out.println("transactions");
		for(TransLog t:translogList){
			System.out.println("*** Id:"+t.getId());
			
			cv=new ContentValues();
			cv.put(DbHelper.TRANSACTION_LOG_LOGID, t.getId());
			cv.put(DbHelper.TRANSACTION_LOG_TABLE, t.getTableKind());
			cv.put(DbHelper.TRANSACTION_LOG_ROWID, t.getRowId());
			cv.put(DbHelper.TRANSACTION_LOG_OPERATION, t.getOperation());
			cv.put(DbHelper.TRANSACTION_LOG_TRANSTIME, t.getTransTime());
			db.insert(DbHelper.TABLE_TRANSACTION_LOG, null, cv);
			DbQuery.updateAccount(db, t.getTransTime());
		}
		cv=new ContentValues();
		cv.put(DbHelper.UPDATE_ACCOUNT_ACC, cloudAcc.getAcc());
		cv.put(DbHelper.UPDATE_ACCOUNT_CLOUD_KEY, cloudAcc.getKeyrep());
		db.update(DbHelper.TABLE_UPDATE_ACCOUNT, cv, DbHelper.UPDATE_ACCOUNT_ID+"=1", null);
		
		return true;
		
	}
	
	
	public int insertTransLog(String table,int rowId,String operation){
		ContentValues cv=new ContentValues();
		long unixTime = System.currentTimeMillis() / 1000L;
		cv.put(DbHelper.TRANSACTION_LOG_OPERATION, operation);
		cv.put(DbHelper.TRANSACTION_LOG_TABLE, table);
		cv.put(DbHelper.TRANSACTION_LOG_ROWID, rowId);
		cv.put(DbHelper.TRANSACTION_LOG_TRANSTIME, unixTime);
		db.insert(DbHelper.TABLE_TRANSACTION_LOG, null, cv);
		int row=DbQuery.getLast(db, dbh, DbHelper.TABLE_TRANSACTION_LOG);
		DbQuery.updateAccount(db, unixTime);
		return row;//returns the row number of the record just inserted
	}
	//given a time the cloud was last updated will try to update the cloud based on the transactions that happened after that point
	public void updateCloud(long lastUpdate){
		//get all the transactions that happened after the cloud's last update
		System.out.println("starting Qquery");
		String code="select * from "+DbHelper.TABLE_TRANSACTION_LOG+
				" where "+DbHelper.TRANSACTION_LOG_TRANSTIME+">="+lastUpdate+";";
		Cursor cursor=db.rawQuery(code, null);
		
		//will now attempt to recreate such operations on the cloud
		while(cursor.moveToNext()){
			System.out.println("records");
			String operation=cursor.getString(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_OPERATION));
			String table=cursor.getString(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_TABLE));
			int rowId=cursor.getInt(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_ROWID));
			//now that i have the table, row and operation i can insert a record into the redo log
			//by inserting it into the redo log I can be assured it will be inserted when possible
			DbQuery.insertRedoLog(db, dbh, table, rowId, operation);
		}
		CloudInterface c=new CloudInterface(context, db, dbh);
		c.flushToCloud();
	}
	public boolean createCloud(String namespace){
		
		Cycleendpoint.Builder builder = new Cycleendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
		Cycleendpoint endpointCyc = builder.build();	
			
		Cycleuseendpoint.Builder builderUse = new Cycleuseendpoint.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),			         null);         
		builderUse = CloudEndpointUtils.updateBuilder(builderUse);
		Cycleuseendpoint endpointUse = builderUse.build();
		
		
		Rpurchaseendpoint.Builder builderPurchase = new Rpurchaseendpoint.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builderPurchase = CloudEndpointUtils.updateBuilder(builderPurchase);
		Rpurchaseendpoint endpointPurchase = builderPurchase.build();
		
		Translogendpoint.Builder builderTranslog = new Translogendpoint.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
		Translogendpoint endpointTranslog = builderTranslog.build();
		
		String code="select * from "+DbHelper.TABLE_CROPCYLE;// where something (constraints unknown yet TODO
		Cursor cursor=db.rawQuery(code, null);
		while(cursor.moveToNext()){
			Cycle c=new Cycle();
			c.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.CROPCYCLE_ID)));
			c.setCropId(cursor.getInt(cursor.getColumnIndex(DbHelper.CROPCYCLE_CROPID)));
			c.setLandQty(cursor.getDouble(cursor.getColumnIndex(DbHelper.CROPCYCLE_LAND_AMOUNT)));
			c.setLandType(cursor.getString(cursor.getColumnIndex(DbHelper.CROPCYCLE_LAND_TYPE)));
			c.setTotalSpent(cursor.getDouble(cursor.getColumnIndex(DbHelper.CROPCYCLE_TOTALSPENT)));
			c.setHarvestType(cursor.getString(cursor.getColumnIndex(DbHelper.CROPCYCLE_HARVEST_TYPE)));
			c.setHarvestAmt(cursor.getDouble(cursor.getColumnIndex(DbHelper.CROPCYCLE_HARVEST_AMT)));
			c.setCostPer(cursor.getDouble(cursor.getColumnIndex(DbHelper.CROPCYCLE_COSTPER)));
			c.setAccount(namespace);
			try {
				c=endpointCyc.insertCycle(c).execute();
			} catch (IOException e) {e.printStackTrace();
				return false;}
			System.out.println(c.getKeyrep()+"  "+c.getId());
			DbQuery.insertCloudKey(db, dbh, DbHelper.TABLE_CROPCYLE, c.getKeyrep(), c.getId());
		}
		code="select * from "+DbHelper.TABLE_CYCLE_RESOURCES;
		cursor=db.rawQuery(code, null);
		while(cursor.moveToNext()){
			CycleUse c=new CycleUse();
			c.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_ID)));
			c.setAmount(cursor.getDouble(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_QTY)));
			c.setCycleid(cursor.getInt(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_CYCLEID)));
			c.setPurchaseId(cursor.getInt(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_PURCHASE_ID)));
			c.setResource(cursor.getString(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_TYPE)));
			c.setCost(cursor.getDouble(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_USECOST)));
			//c.setQuantifier(cursor.getString(cursor.getColumnIndex(DbHelper.CYCLE_RESOURCE_QUANTIFIER)));
			c.setAccount(namespace);
			try {
				c=endpointUse.insertCycleUse(c).execute();
			} catch (IOException e) {e.printStackTrace();
				return false;}
			DbQuery.insertCloudKey(db, dbh, DbHelper.TABLE_CYCLE_RESOURCES, c.getKeyrep(), c.getId());
		}
		code="select * from "+DbHelper.TABLE_RESOURCE_PURCHASES;
		cursor=db.rawQuery(code, null);
		while(cursor.moveToNext()){
			RPurchase p=new RPurchase();
			p.setPId(cursor.getInt(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_ID)));
			p.setResourceId(cursor.getInt(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_RESID)));
			p.setType(cursor.getString(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_TYPE)));
			p.setQuantifier(cursor.getString(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_QUANTIFIER)));
			p.setQty(cursor.getDouble(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_QTY)));
			p.setCost(cursor.getDouble(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_COST)));
			p.setQtyRemaining(cursor.getDouble(cursor.getColumnIndex(DbHelper.RESOURCE_PURCHASE_REMAINING)));
			p.setAccount(namespace);
			try {
				p=endpointPurchase.insertRPurchase(p).execute();
			} catch (IOException e) {e.printStackTrace();
				return false;}
			DbQuery.insertCloudKey(db, dbh, DbHelper.TABLE_RESOURCE_PURCHASES, p.getKeyrep(), p.getPId());
		}
		
		ContentValues cv=new ContentValues();
		cv.put(DbHelper.UPDATE_ACCOUNT_ACC, namespace);
		db.update(DbHelper.TABLE_UPDATE_ACCOUNT, cv, DbHelper.UPDATE_ACCOUNT_ID+"=1", null);
		
		code="select * from "+DbHelper.TABLE_TRANSACTION_LOG;
		CloudInterface cloudIF=new CloudInterface(context, db, dbh);
		cursor=db.rawQuery(code, null);
		while(cursor.moveToNext()){
			TransLog t=new TransLog();
			t.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_LOGID)));
			t.setAccount(namespace);
			t.setOperation(cursor.getString(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_OPERATION)));
			t.setTableKind(cursor.getString(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_TABLE)));
			t.setRowId(cursor.getInt(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_ROWID)));
			t.setTransTime(cursor.getLong(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_TRANSTIME)));
			t.setKeyrep(DbQuery.getKey(db, dbh, t.getTableKind(), t.getRowId()));
			try {
				System.out.println(t.toPrettyString());
				t=endpointTranslog.insertTransLog(t).execute();
				cloudIF.updateUpAccC(t.getTransTime());
			} catch (IOException e) {e.printStackTrace();
				return false;}
		}
		cv=new ContentValues();
		cv.put(DbHelper.UPDATE_ACCOUNT_SIGNEDIN, 1);
		db.update(DbHelper.TABLE_UPDATE_ACCOUNT, cv, DbHelper.UPDATE_ACCOUNT_ID+"=1", null);
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
			Translogendpoint.Builder builder = new Translogendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			Translogendpoint endpoint = builder.build();
			TransLogCollection tlist = null;
			
			try {
				tlist=endpoint.logs(lastLocalUpdate, namespace).execute();
			} catch (IOException e) {e.printStackTrace();}
			List<TransLog> transList=tlist.getItems();
			Iterator<TransLog> i=transList.iterator();
			while(i.hasNext()){
				TransLog tLog=i.next();
				if(tLog.getOperation().equals(TransactionLog.TL_INS))
					logInsertLocal(tLog,namespace);
				else if(tLog.getOperation().equals(TransactionLog.TL_UPDATE))
					logUpdateLocal(tLog,namespace);
				else if(tLog.getOperation().equals(TransactionLog.TL_DEL)){
					logDeleteLocal(tLog,namespace);
				}
					
			}
			return null;
		}
	}
	public void logInsertLocal (TransLog t,String namespace){
		ContentValues cv=new ContentValues();
		if(t.getTableKind().equals(DbHelper.TABLE_CROPCYLE)){
			Cycle c=getCycle(namespace,t.getKeyrep());
			cv.put(DbHelper.CROPCYCLE_ID, t.getRowId());
			cv.put(DbHelper.CROPCYCLE_CROPID, c.getCropId());
			cv.put(DbHelper.CROPCYCLE_LAND_TYPE, c.getLandType());
			cv.put(DbHelper.CROPCYCLE_LAND_AMOUNT, c.getLandQty());
			cv.put(DbHelper.CROPCYCLE_TOTALSPENT, c.getTotalSpent());
			//cv.put(DbHelper.CROPCYCLE_DATE, c.get);
			cv.put(DbHelper.CROPCYCLE_HARVEST_TYPE, c.getHarvestType());
			cv.put(DbHelper.CROPCYCLE_HARVEST_AMT, c.getHarvestAmt());
			cv.put(DbHelper.CROPCYCLE_COSTPER, c.getCostPer());
			db.insert(DbHelper.TABLE_CROPCYLE, null, cv);
		}else if(t.getTableKind().equals(DbHelper.TABLE_RESOURCE_PURCHASES)){
			RPurchase p=getPurchase(namespace,t.getKeyrep());
			cv.put(DbHelper.RESOURCE_PURCHASE_ID, t.getRowId());
			cv.put(DbHelper.RESOURCE_PURCHASE_TYPE, p.getType());
			cv.put(DbHelper.RESOURCE_PURCHASE_RESID, p.getResourceId());
			cv.put(DbHelper.RESOURCE_PURCHASE_QTY, p.getQty());
			cv.put(DbHelper.RESOURCE_PURCHASE_QUANTIFIER, p.getQuantifier());
			cv.put(DbHelper.RESOURCE_PURCHASE_COST, p.getCost());
			cv.put(DbHelper.RESOURCE_PURCHASE_REMAINING, p.getQtyRemaining());
			db.insert(DbHelper.TABLE_RESOURCE_PURCHASES, null, cv);
		}else if(t.getTableKind().equals(DbHelper.TABLE_CYCLE_RESOURCES)){
			CycleUse c=getCycleUse(namespace,t.getKeyrep());
			cv.put(DbHelper.CYCLE_RESOURCE_ID, t.getRowId());
			//cv.put(DbHelper.CYCLE_RESOURCE_TYPE, );
			cv.put(DbHelper.CYCLE_RESOURCE_CYCLEID, c.getCycleid());
			cv.put(DbHelper.CYCLE_RESOURCE_PURCHASE_ID, c.getPurchaseId());
			cv.put(DbHelper.CYCLE_RESOURCE_QTY, c.getAmount());
			//cv.put(DbHelper.CYCLE_RESOURCE_QUANTIFIER, c.get);
			cv.put(DbHelper.CYCLE_RESOURCE_USECOST, c.getCost());
			db.insert(DbHelper.TABLE_CYCLE_RESOURCES, null, cv);
		}
	}
	public void logUpdateLocal(TransLog t,String namespace){
		ContentValues cv=new ContentValues();
		if(t.getTableKind().equals(DbHelper.TABLE_CROPCYLE)){
			Cycle c=getCycle(namespace,t.getKeyrep());
			cv.put(DbHelper.CROPCYCLE_ID, t.getRowId());
			cv.put(DbHelper.CROPCYCLE_CROPID, c.getCropId());
			cv.put(DbHelper.CROPCYCLE_LAND_TYPE, c.getLandType());
			cv.put(DbHelper.CROPCYCLE_LAND_AMOUNT, c.getLandQty());
			cv.put(DbHelper.CROPCYCLE_TOTALSPENT, c.getTotalSpent());
			//cv.put(DbHelper.CROPCYCLE_DATE, c.ge);
			cv.put(DbHelper.CROPCYCLE_HARVEST_TYPE, c.getHarvestType());
			cv.put(DbHelper.CROPCYCLE_HARVEST_AMT, c.getHarvestAmt());
			cv.put(DbHelper.CROPCYCLE_COSTPER, c.getCostPer());
			db.update(DbHelper.TABLE_CROPCYLE, cv, DbHelper.CROPCYCLE_ID+"="+t.getRowId(), null);
		}else if(t.getTableKind().equals(DbHelper.TABLE_RESOURCE_PURCHASES)){
			RPurchase p=getPurchase(namespace,t.getKeyrep());
			cv.put(DbHelper.RESOURCE_PURCHASE_ID, t.getRowId());
			cv.put(DbHelper.RESOURCE_PURCHASE_TYPE, p.getType());
			cv.put(DbHelper.RESOURCE_PURCHASE_RESID, p.getResourceId());
			cv.put(DbHelper.RESOURCE_PURCHASE_QTY, p.getQty());
			cv.put(DbHelper.RESOURCE_PURCHASE_QUANTIFIER, p.getQuantifier());
			cv.put(DbHelper.RESOURCE_PURCHASE_COST, p.getCost());
			cv.put(DbHelper.RESOURCE_PURCHASE_REMAINING, p.getQtyRemaining());
			db.update(DbHelper.TABLE_RESOURCE_PURCHASES, cv, DbHelper.RESOURCE_PURCHASE_ID+"="+t.getRowId(), null);
		}else if(t.getTableKind().equals(DbHelper.TABLE_CYCLE_RESOURCES)){
			CycleUse c=getCycleUse(namespace,t.getKeyrep());
			cv.put(DbHelper.CYCLE_RESOURCE_ID, t.getRowId());
			cv.put(DbHelper.CYCLE_RESOURCE_TYPE,c.getResource());
			cv.put(DbHelper.CYCLE_RESOURCE_CYCLEID, c.getCycleid());
			cv.put(DbHelper.CYCLE_RESOURCE_PURCHASE_ID, c.getPurchaseId());
			cv.put(DbHelper.CYCLE_RESOURCE_QTY, c.getAmount());
			//cv.put(DbHelper.CYCLE_RESOURCE_QUANTIFIER, c.get);
			cv.put(DbHelper.CYCLE_RESOURCE_USECOST, c.getCost());
			db.update(DbHelper.TABLE_CYCLE_RESOURCES, cv, DbHelper.CYCLE_RESOURCE_ID+"="+t.getRowId(), null);
		}
	}
	private void logDeleteLocal(TransLog tLog, String namespace2) {
		DbQuery.deleteRecord(db, dbh, tLog.getTableKind(), tLog.getRowId());
	}
	private Cycle getCycle(String namespace, String keyrep){
		Cycleendpoint.Builder builder = new Cycleendpoint.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
		Cycleendpoint endpoint = builder.build();
		Cycle c = null;
		try {
			c=endpoint.getCycle(namespace,keyrep).execute();
		} catch (IOException e) {e.printStackTrace();}
		return c;
	}
	private RPurchase getPurchase(String namespace,String keyrep){
		Rpurchaseendpoint.Builder builder = new Rpurchaseendpoint.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
		Rpurchaseendpoint endpoint = builder.build();
		RPurchase p = null;
		try {
			p=endpoint.getRPurchase(namespace, keyrep).execute();
		} catch (IOException e) {e.printStackTrace();}
		return p;
	}
	private CycleUse getCycleUse(String namespace, String keyrep){
		Cycleuseendpoint.Builder builder = new Cycleuseendpoint.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
		Cycleuseendpoint endpoint = builder.build();
		CycleUse c=null;
		try {
			c=endpoint.getCycleUse(namespace, keyrep).execute();
		} catch (IOException e) {e.printStackTrace();}
		return c;
	}
	
	
	
	
	public void updateCloud_(TransLog t){
		//get obj from cloud
		if(t.getTableKind().equals(DbHelper.TABLE_CROPCYLE)){
			Cycleendpoint.Builder builder = new Cycleendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			builder.build();
			
			//Cycle c=endpoint.getCycle(t.getKeyrep());
		}
		if(t.getTableKind().equals(DbHelper.TABLE_CYCLE_RESOURCES)){
			Cycleuseendpoint.Builder builderUse = new Cycleuseendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builderUse = CloudEndpointUtils.updateBuilder(builderUse);
			builderUse.build();
		}
		if(t.getTableKind().equals(DbHelper.TABLE_RESOURCE_PURCHASES)){
			Rpurchaseendpoint.Builder builderPurchase = new Rpurchaseendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builderPurchase = CloudEndpointUtils.updateBuilder(builderPurchase);
			builderPurchase.build();
			
		}
	}
	
	//removes all entries of kinds Cycle,CycleUse,RPurchase,TransLog But UpAcc remains
	public void removeNamespace(String namespace){
		Cycleendpoint.Builder builder = new Cycleendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
		Cycleendpoint endpoint = builder.build();
		try {
			endpoint.deleteAll(namespace).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Cycleuseendpoint.Builder builderUse = new Cycleuseendpoint.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),			         null);         
		builderUse = CloudEndpointUtils.updateBuilder(builderUse);
		Cycleuseendpoint endpointUse = builderUse.build();
		try {
			
			endpointUse.deleteAll(namespace).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Rpurchaseendpoint.Builder builderPurchase = new Rpurchaseendpoint.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builderPurchase = CloudEndpointUtils.updateBuilder(builderPurchase);
		Rpurchaseendpoint endpointPurchase = builderPurchase.build();
		try {
			endpointPurchase.deleteAll(namespace).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Translogendpoint.Builder builderTransLog = new Translogendpoint.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builderTransLog = CloudEndpointUtils.updateBuilder(builderTransLog);
		Translogendpoint endpointTranslog = builderTransLog.build();
		try {
			endpointTranslog.deleteAll(namespace).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
