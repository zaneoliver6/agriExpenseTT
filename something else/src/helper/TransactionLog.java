package helper;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;







import com.example.agrinetexpensestt.cycleendpoint.Cycleendpoint;
import com.example.agrinetexpensestt.cycleuseendpoint.Cycleuseendpoint;
import com.example.agrinetexpensestt.rpurchaseendpoint.Rpurchaseendpoint;
import com.example.agrinetexpensestt.translogendpoint.Translogendpoint;
import com.example.agrinetexpensestt.translogendpoint.model.CollectionResponseTransLog;
import com.example.agrinetexpensestt.translogendpoint.model.TransLog;
//import uwi.dcit.AgriExpenseTT.translogendpoint.model.TransLogCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;

public class TransactionLog {
	private static int transactionId=0;
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
	
	//transaction log hosts 3 operations
		//insert - new row was inserted
		//delete - row deleted
		//update - row updated
	public TransactionLog(DbHelper dbh,SQLiteDatabase db){
		this.dbh=dbh;
		this.db=db;
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
		return row;//returns the row number of the record just inserted
	}
	//given a time the cloud was last updated will try to update the cloud based on the transactions that happened after that point
	public void updateCloud(long lastUpdate){
		//get all the transactions that happened after the cloud's last update
		String code="select * from "+DbHelper.TABLE_TRANSACTION_LOG+
				" where "+DbHelper.TRANSACTION_LOG_TRANSTIME+">"+lastUpdate+";";
		Cursor cursor=db.rawQuery(code, null);
		//will now attempt to recreate such operations on the cloud
		while(cursor.moveToNext()){
			String operation=cursor.getString(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_OPERATION));
			String table=cursor.getString(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_TABLE));
			int rowId=cursor.getInt(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_ROWID));
			//now that i have the table, row and operation i can insert a record into the redo log
			//by inserting it into the redo log I can be assured it will be inserted when possible
			DbQuery.insertRedoLog(db, dbh, table, rowId, operation);
			/*if(operation.equals(this.TL_INS))
				insert(table,rowId);
			if(operation.equals(this.TL_DEL))
				delete(table,rowId);
			if(operation.equals(this.TL_UPDATE))
				update(table,rowId);*/
		}
	}
	/*
	private void insert(String table, int rowId){
		String code="select * from "+table+" where id="+rowId+";";
		Cursor cursor=db.rawQuery(code, null);
		cursor.moveToFirst();
		if(table.equals(dbh.TABLE_CROPCYLE)){
			Cycle c=new Cycle();
			c.setCropId(cropId);
			c.setId(id);
			c.setLandQty(landQty);
			c.setLandType(landType);
		}
			
		if(table.equals(dbh.TABLE_CYCLE_RESOURCES)){
			CycleUse cy=new CycleUse();
		}
			
		if(table.equals(dbh.TABLE_RESOURCE_PURCHASES)){
			RPurchase rp=new RPurchase();
		}
		
	}*/
	
	public void updateLocal(long m){
		new GetLogs().execute(m);
	}
	public class GetLogs extends AsyncTask<Long, Object, Object>{

		@Override
		protected Void doInBackground(Long... params) {
			Translogendpoint.Builder builder = new Translogendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			Translogendpoint endpoint = builder.build();
			long k;
			CollectionResponseTransLog transResp=null;
			Long lastUpdate=params[0];
			Object obj=lastUpdate;
			
			//-------------------
			/*try {
				 l=endpoint.nothing(obj).execute();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			//-------------------
			TransLogCollection tlist = null;
			try {
				
				 TransLog t=new TransLog();
				 t.setTransTime((long) 0);
				//transResp=endpoint.listTransLog().execute();
				 tlist=endpoint.logs((long) 0).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//List<TransLog> transList=transResp.getItems();
			
			List<TransLog> transList=tlist.getItems();
			//List<TransLog> transList=tlist.getItems();
			Iterator<TransLog> i=transList.iterator();
			while(i.hasNext()){
				TransLog t=i.next();
				/*if(t.getTransTime()>lastUpdate){
					if(t.getOperation().equals(TL_INS))
						insertLocal(t);
				}*/
				System.out.println(t.toString());
			}
			return null;
		}
		
	}
	
	
	
	public void insertLocal(TransLog t){
		//get obj from cloud
		if(t.getTableKind().equals(DbHelper.TABLE_CROPCYLE)){
			Cycleendpoint.Builder builder = new Cycleendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			Cycleendpoint endpoint = builder.build();
			
			//Cycle c=endpoint.getCycle(t.getKeyrep());
		}
		if(t.getTableKind().equals(DbHelper.TABLE_CYCLE_RESOURCES)){
			Cycleuseendpoint.Builder builderUse = new Cycleuseendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builderUse = CloudEndpointUtils.updateBuilder(builderUse);
			Cycleuseendpoint endpointUse = builderUse.build();
		}
		if(t.getTableKind().equals(DbHelper.TABLE_RESOURCE_PURCHASES)){
			Rpurchaseendpoint.Builder builderPurchase = new Rpurchaseendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builderPurchase = CloudEndpointUtils.updateBuilder(builderPurchase);
			Rpurchaseendpoint endpointPurchase = builderPurchase.build();
			
		}
	}
}
