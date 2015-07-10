package uwi.dcit.AgriExpenseTT.cloud;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.TransactionLog;
import uwi.dcit.AgriExpenseTT.models.CloudKeyContract.CloudKeyEntry;
import uwi.dcit.AgriExpenseTT.models.CycleContract.CycleEntry;
import uwi.dcit.AgriExpenseTT.models.CycleResourceContract.CycleResourceEntry;
import uwi.dcit.AgriExpenseTT.models.RedoLogContract.RedoLogEntry;
import uwi.dcit.AgriExpenseTT.models.ResourcePurchaseContract.ResourcePurchaseEntry;
import uwi.dcit.AgriExpenseTT.models.TransactionLogContract.TransactionLogEntry;
import uwi.dcit.agriexpensesvr.accountApi.AccountApi;
import uwi.dcit.agriexpensesvr.accountApi.model.Account;
import uwi.dcit.agriexpensesvr.cycleApi.CycleApi;
import uwi.dcit.agriexpensesvr.cycleApi.model.Cycle;
import uwi.dcit.agriexpensesvr.cycleUseApi.CycleUseApi;
import uwi.dcit.agriexpensesvr.cycleUseApi.model.CycleUse;
import uwi.dcit.agriexpensesvr.resourcePurchaseApi.ResourcePurchaseApi;
import uwi.dcit.agriexpensesvr.resourcePurchaseApi.model.ResourcePurchase;
import uwi.dcit.agriexpensesvr.translogApi.TranslogApi;
import uwi.dcit.agriexpensesvr.translogApi.model.TransLog;
//import uwi.dcit.agriexpensesvr.AccountApi.AccountApi;
//import uwi.dcit.agriexpensesvr.AccountApi.model.UpAcc;


public class CloudInterface {
	SQLiteDatabase db;
	DbHelper dbh;
	TransactionLog tL;

	public CloudInterface(Context context) {
		dbh= new DbHelper(context);
		db=dbh.getWritableDatabase();
		tL=new TransactionLog(dbh,db,context);
	}

	public CloudInterface(Context context,SQLiteDatabase db,DbHelper dbh) {
		this.dbh = dbh;
		this.db = db;
		tL = new TransactionLog(dbh,db,context);
	}

	public void updateCycle(){
		new CycleUpdater().execute();
	}
    public void updatePurchase(){
        new PurchaseUpdater().execute();
    }
    public void insertCycle(){
        new InsertCycle().execute();
    }

    private class CycleUpdater extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
            CycleApi.Builder builder = new CycleApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
            CycleApi endpoint= builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();
			DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_UPDATE, CycleEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();//the current primary key of CROP CYCLE Table
                Cycle c = DbQuery.getCycle(db, dbh, rowId);
				String keyrep=DbQuery.getKey(db, dbh, CycleEntry.TABLE_NAME, c.getId());
				c.setKeyrep(keyrep);
				try{
					//c=endpoint.insertCycle(c).execute();
					c=endpoint.updateCycle(c).execute();
                    endpoint.updateCycle(c);
				}catch(Exception e){
					
					System.out.println("could not update cycle");
					return null;
				}
				if(c!=null){
					//we stored they key as text in the account field of c when we returned
					//System.out.println(c.getAccount());
					//store key of inserted cycle into cloud - cloud key table
					//DbQuery.insertCloudKey(db, dbh, CycleEntry.TABLE_NAME, c.getAccount(),rowId);
					//remove from redo log
                    try {
                        DbQuery.deleteRecord(db, dbh, RedoLogEntry.TABLE_NAME, logId);
                    }catch (Exception e){e.printStackTrace();}
					//getting the transaction from the transaction log that matches this operation
					String code="select * from "+TransactionLogEntry.TABLE_NAME+" where "+TransactionLogEntry.TRANSACTION_LOG_TABLE+"='"+CycleEntry.TABLE_NAME+"' and "
					+TransactionLogEntry.TRANSACTION_LOG_ROWID+"="+rowId+" and "+TransactionLogEntry.TRANSACTION_LOG_OPERATION+"='"+TransactionLog.TL_UPDATE+"'";
					Cursor cursor=db.rawQuery(code, null);
					//select from transaction log
						//where transaction's rowId = rowId
						//and transactions's table = table
						//and transactions's operation = operation
					//but there can be multiple operations (updates) on a particular object (row[Id] of a Table)
					//what should we do in this case !? :s 
					cursor.moveToLast();//only for updaates we should move to last because the last update would hold the most current data
					int id=cursor.getInt(cursor.getColumnIndex(TransactionLogEntry._ID));
					cursor.close();

					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, id, TransactionLog.TL_INS);
					insertLog();
				}
			}
			return null;
		}
		
	}

	private class PurchaseUpdater extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			ResourcePurchaseApi.Builder builder = new ResourcePurchaseApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			ResourcePurchaseApi endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();
			DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_UPDATE, ResourcePurchaseEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();//the current primary key of CROP CYCLE Table
                ResourcePurchase p=DbQuery.getARPurchase(db, dbh, rowId);
				String keyrep=DbQuery.getKey(db, dbh, ResourcePurchaseEntry.TABLE_NAME, p.getPId());
				try{
					p.setKeyrep(keyrep);
					p=endpoint.updateRPurchase(p).execute();
				}catch(Exception e){
					
					return null;
				}
				if(p!=null){
					//we stored they key as text in the account field of c when we returned
					//System.out.println(c.getAccount());
					//store key of inserted cycle into cloud - cloud key table
					//DbQuery.insertCloudKey(db, dbh, CycleEntry.TABLE_NAME, c.getAccount(),rowId);
					//remove from redo log
                    try {
                        DbQuery.deleteRecord(db, dbh, RedoLogEntry.TABLE_NAME, logId);
                    }catch (Exception e){e.printStackTrace();}
					//getting the transaction from the transaction log that matches this operation
					String code="select * from "+TransactionLogEntry.TABLE_NAME+" where "+TransactionLogEntry.TRANSACTION_LOG_TABLE+"='"+ResourcePurchaseEntry.TABLE_NAME+"' and "
					+TransactionLogEntry.TRANSACTION_LOG_ROWID+"="+rowId+" and "+TransactionLogEntry.TRANSACTION_LOG_OPERATION+"='"+TransactionLog.TL_UPDATE+"'";
					Cursor cursor=db.rawQuery(code, null);
					cursor.moveToLast();//only for updates explained in cycleUpdate
					int id=cursor.getInt(cursor.getColumnIndex(TransactionLogEntry._ID));
					cursor.close();

					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, id, TransactionLog.TL_INS);
					insertLog();
				}
			}
			return null;
		}
		
	}


    public class InsertCycle extends AsyncTask<Void, Object, Object>{
		
		@Override
		protected Cycle doInBackground(Void... params) {
			CycleApi.Builder builder = new CycleApi.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    new JacksonFactory(),
                    null);

			builder = CloudEndpointUtils.updateBuilder(builder);
			CycleApi endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();

			DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, CycleEntry.TABLE_NAME);

			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();

			while(logI.hasNext()){

				int logId=logI.next(),
                        rowId=rowI.next();              // The current primary key of CROP CYCLE Table

                Cycle c = DbQuery.getCycle(db, dbh, rowId);
				c.setAccount(DbQuery.getAccountName(db));   // Uses the account rep as the namespace

				try{
					c=endpoint.insertCycle(c).execute();

				}catch(Exception e){
					
					return null;
				}
				if(c!=null){
					//we stored they key as text in the account field of c when we returned
					System.out.println(c.getAccount());
					//store key of inserted cycle into cloud - cloud key table
					DbQuery.insertCloudKey(db, dbh, CycleEntry.TABLE_NAME, c.getAccount(),rowId);
					//remove from redo log
                    try {
                        DbQuery.deleteRecord(db, dbh, RedoLogEntry.TABLE_NAME, logId);
                    }catch (Exception e){e.printStackTrace();}
					//getting the transaction from the transaction log that matches this operation
					String code="select * from "+TransactionLogEntry.TABLE_NAME+" where "+TransactionLogEntry.TRANSACTION_LOG_TABLE+"='"+CycleEntry.TABLE_NAME+"' and "
					+TransactionLogEntry.TRANSACTION_LOG_ROWID+"="+rowId+" and "+TransactionLogEntry.TRANSACTION_LOG_OPERATION+"='"+TransactionLog.TL_INS+"'";
					Cursor cursor=db.rawQuery(code, null);
					cursor.moveToFirst();
					int id=cursor.getInt(cursor.getColumnIndex(TransactionLogEntry._ID));
					cursor.close();

					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, id, TransactionLog.TL_INS);
					insertLog();
					//When we have finished inserting a cycle, we would like to record the timestamp of the change made to the local database.
					//This would be reflected in the lastUpdated variable.
					DbQuery.updateAccount(db,System.currentTimeMillis()/1000L);

				}
			}
			return null;
		}
		
	}
	
	
	public void insertCycleUseC(){
		new InsertCycleUse().execute();
	
			
	}
	public class InsertCycleUse extends AsyncTask<Void, Object, Object>{
		
		@Override
		protected Cycle doInBackground(Void... params) {
			CycleUseApi.Builder builder = new CycleUseApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
            CycleUseApi endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();
			DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, CycleResourceEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				CycleUse c=DbQuery.getACycleUse(db, dbh, rowId);
				c.setAccount(DbQuery.getAccountName(db));
				
				try{
					c=endpoint.insertCycleUse(c).execute();
				}catch(Exception e){
					return null;
				}
				if(c!=null){
					//we stored they key as text in the account field of c when we returned
					System.out.println(c.getAccount());
					//store key of inserted cycleuse into cloud - cloud key table
					DbQuery.insertCloudKey(db, dbh, CycleResourceEntry.TABLE_NAME, c.getAccount(),rowId);
					//remove from redo log
                    try {
                        DbQuery.deleteRecord(db, dbh, RedoLogEntry.TABLE_NAME, logId);
                    }catch (Exception e){e.printStackTrace();}

					//getting the transaction from the transaction log that matches this operation
					String code="select * from "+TransactionLogEntry.TABLE_NAME+" where "+TransactionLogEntry.TRANSACTION_LOG_TABLE+"='"+CycleResourceEntry.TABLE_NAME+"' and "
							+TransactionLogEntry.TRANSACTION_LOG_ROWID+"="+rowId+" and "+TransactionLogEntry.TRANSACTION_LOG_OPERATION+"='"+TransactionLog.TL_INS+"'";
					Cursor cursor=db.rawQuery(code, null);
					cursor.moveToFirst();
					int id=cursor.getInt(cursor.getColumnIndex(TransactionLogEntry._ID));
					cursor.close();

					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, id, TransactionLog.TL_INS);
					insertLog();
				}
			}
			return null;
		}
		
	}
	public void insertPurchase(){
		new InsertPurchase().execute();
	}
	public class InsertPurchase extends AsyncTask<Void, Object, Object>{
		
		@Override
		protected ResourcePurchase doInBackground(Void... params) {
			ResourcePurchaseApi.Builder builder = new ResourcePurchaseApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
            ResourcePurchaseApi endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();
			DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, ResourcePurchaseEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
                ResourcePurchase purchase=DbQuery.getARPurchase(db, dbh, rowId);
				purchase.setAccount(DbQuery.getAccountName(db));
				try{
					purchase=endpoint.insertRPurchase(purchase).execute();
				}catch(Exception e){
					return null;
				}
				if(purchase!=null){
					//we stored they key as text in the account field of c when we returned
					System.out.println(purchase.getAccount());
					//store key of inserted cycleuse into cloud - cloud key table
					DbQuery.insertCloudKey(db, dbh, ResourcePurchaseEntry.TABLE_NAME, purchase.getAccount(),rowId);
					//remove from redo log
                    try {
                        DbQuery.deleteRecord(db, dbh, RedoLogEntry.TABLE_NAME, logId);
                    }catch (Exception e){e.printStackTrace();}

					//getting the transaction from the transaction log that matches this operation
					String code="select * from "+TransactionLogEntry.TABLE_NAME+" where "+TransactionLogEntry.TRANSACTION_LOG_TABLE+"='"+ResourcePurchaseEntry.TABLE_NAME+"' and "
							+TransactionLogEntry.TRANSACTION_LOG_ROWID+"="+rowId+" and "+TransactionLogEntry.TRANSACTION_LOG_OPERATION+"='"+TransactionLog.TL_INS+"'";
					Cursor cursor=db.rawQuery(code, null);
					cursor.moveToFirst();
					int id=cursor.getInt(cursor.getColumnIndex(TransactionLogEntry._ID));
					cursor.close();

					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, id, TransactionLog.TL_INS);
					insertLog();
				}
			}
			return null;
		}
		
	}
	
	public void deleteCycle(){
		new DeleteCycle().execute();
	}
	public class DeleteCycle extends AsyncTask<Void, Object, Object>{

		@Override
		protected Object doInBackground(Void... params) {
			CycleApi.Builder builder = new CycleApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
            CycleApi endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();

			//DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, dbh.TABLE_RESOURCE_PURCHASES);
			DbQuery.getRedo(db, dbh, rowIds, logIds,"del", CycleEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				Cycle c=new Cycle();
				c.setId(rowId);
				c.setAccount(DbQuery.getAccountName(db));
				String keyrep=DbQuery.getKey(db, dbh, CycleEntry.TABLE_NAME, rowId);
				if(keyrep != null){//was never inserted :o
					try{
						endpoint.removeCycle(c.getKeyrep(),c.getAccount()).execute();
					}catch(Exception e){
						c=null;
					}
				}
				
				if(c!=null){//transactioon was succesful or keyrep was not found
					int id=DbQuery.getCloudKeyId(db, dbh, CycleEntry.TABLE_NAME, rowId);
					if(id!=-1){
						//remove key of cycle that was deleted from cloud
                        try {
                            DbQuery.deleteRecord(db, dbh, CloudKeyEntry.TABLE_NAME, id);
                        }catch (Exception e){e.printStackTrace();}

					}
					//remove from redo log
					try {
                        DbQuery.deleteRecord(db, dbh, RedoLogEntry.TABLE_NAME, logId);
                    }catch (Exception e){e.printStackTrace();}

					//getting the transaction from the transaction log that matches this operation
					String code="select * from "+TransactionLogEntry.TABLE_NAME+" where "+TransactionLogEntry.TRANSACTION_LOG_TABLE+"="+CycleEntry.TABLE_NAME+
							" and "+TransactionLogEntry.TRANSACTION_LOG_ROWID+"="+rowId+" and "+TransactionLogEntry.TRANSACTION_LOG_OPERATION+"='del'";
					Cursor cursor=db.rawQuery(code, null);
					cursor.moveToFirst();
					int Tid=cursor.getInt(cursor.getColumnIndex(TransactionLogEntry._ID));
					cursor.close();

					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, Tid, TransactionLog.TL_INS);
					insertLog();
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			/*db.close();
			db=dbh.getWritableDatabase();
			db.execSQL("drop table if exists "+DbHelper.TABLE_NAME);
			dbh.createRedoLog(db);
			db.close();
			db=dbh.getReadableDatabase();*/
			super.onPostExecute(result);
		}
		
		
	}
	
	public void deleteCycleUse(){
		new DeleteCycleUse().execute();
	}
	public class DeleteCycleUse extends AsyncTask<Void, Object, Object>{

		@Override
		protected Object doInBackground(Void... params) {
			CycleUseApi.Builder builder = new CycleUseApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
            CycleUseApi endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();

			//DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, dbh.TABLE_RESOURCE_PURCHASES);
			DbQuery.getRedo(db, dbh, rowIds, logIds,"del", CycleResourceEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				CycleUse c=new CycleUse();
				c.setId(rowId);
				c.setAccount(DbQuery.getAccountName(db));
				String keyrep=DbQuery.getKey(db, dbh, CycleResourceEntry.TABLE_NAME, rowId);
				c.setKeyrep(keyrep);
				if(keyrep == null){
					try{
						endpoint.removeCycleUse(c.getKeyrep(),c.getAccount()).execute();
					}catch(Exception e){
						c=null;
					}
				}
				if(c != null){
					int id=DbQuery.getCloudKeyId(db, dbh, CycleResourceEntry.TABLE_NAME, rowId);
					if(id!=-1){
						//remove key of cycle that was deleted from cloud
                        try {
                            DbQuery.deleteRecord(db, dbh, CloudKeyEntry.TABLE_NAME, id);
                        }catch (Exception e){e.printStackTrace();}

					}
					//remove from redo log
                    try {
                        DbQuery.deleteRecord(db, dbh, RedoLogEntry.TABLE_NAME, logId);
                    }catch (Exception  e){e.printStackTrace();}

					//getting the transaction from the transaction log that matches this operation
					String code="select * from "+TransactionLogEntry.TABLE_NAME+" where "+TransactionLogEntry.TRANSACTION_LOG_TABLE+"="+CycleResourceEntry.TABLE_NAME+
							" and "+TransactionLogEntry.TRANSACTION_LOG_ROWID+"="+rowId+" and "+TransactionLogEntry.TRANSACTION_LOG_OPERATION+"='del'";
					Cursor cursor = db.rawQuery(code, null);
					cursor.moveToFirst();
					int Tid=cursor.getInt(cursor.getColumnIndex(TransactionLogEntry._ID));
					cursor.close();

					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, Tid, TransactionLog.TL_INS);
					insertLog();
					
				}
			}
			return null;
		}
		
	}
	
	public void deletePurchase(){
		new DeletePurchase().execute();
	}
	public class DeletePurchase extends AsyncTask<Void, Object, Object>{

		@Override
		protected Object doInBackground(Void... params) {
			ResourcePurchaseApi.Builder builder = new ResourcePurchaseApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
            ResourcePurchaseApi endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();

			//DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, dbh.TABLE_RESOURCE_PURCHASES);
			DbQuery.getRedo(db, dbh, rowIds, logIds,"del", ResourcePurchaseEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
                ResourcePurchase p=new ResourcePurchase();
				p.setPId(rowId);
				p.setAccount(DbQuery.getAccountName(db));
				String keyrep=DbQuery.getKey(db, dbh, ResourcePurchaseEntry.TABLE_NAME, rowId);
				p.setKeyrep(keyrep);
				if(keyrep!=null){
					try{
						endpoint.removeRPurchase(p.getKeyrep(), p.getAccount()).execute();
					}catch(Exception e){
						e.printStackTrace();
						p=null;
					}
				}
				if(p!=null){//the removal was successful OR there was not ever inserted into the cloud
					int id=DbQuery.getCloudKeyId(db, dbh, ResourcePurchaseEntry.TABLE_NAME, rowId);
					if(id!=-1){//if the key exists
                        try {
                            DbQuery.deleteRecord(db, dbh, CloudKeyEntry.TABLE_NAME, id);
                        }catch (Exception e){e.printStackTrace();}

						//getting the transaction from the transaction log that matches this operation	
					}
					
					//remove from redo log
					try {
                        DbQuery.deleteRecord(db, dbh, RedoLogEntry.TABLE_NAME, logId);
                    }catch (Exception e){e.printStackTrace();}

					//remove key of cycle that was deleted from cloud
					String code="select * from "+TransactionLogEntry.TABLE_NAME+" where "+TransactionLogEntry.TRANSACTION_LOG_TABLE+"='"+ResourcePurchaseEntry.TABLE_NAME+"'"
							+ " and "+TransactionLogEntry.TRANSACTION_LOG_ROWID+"="+rowId+" and "+TransactionLogEntry.TRANSACTION_LOG_OPERATION+"='del'";
					Cursor cursor = db.rawQuery(code, null);
					cursor.moveToFirst();
					int Tid = cursor.getInt(cursor.getColumnIndex(TransactionLogEntry._ID));
                    cursor.close();

					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, Tid, TransactionLog.TL_INS);
					insertLog();
					
				}
			}
			
			return null;
		}
	}
	
	public void insertLog(){
		new InsertLog().execute();
	}
	public class InsertLog extends AsyncTask<Void, Object, Object>{
		
		@Override
		protected Void doInBackground(Void... params) {
			TranslogApi.Builder builder = new TranslogApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
            TranslogApi endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();
			DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, TransactionLogEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				//Cycle c=DbQuery.getCycle(db, dbh, rowId);
				
				TransLog t=DbQuery.getLog(db,dbh,rowId);
				String k=DbQuery.getKey(db, dbh, t.getTableKind(), t.getRowId());//gets the key for the related object in the cloud
				t.setKeyrep(k);//stores the keyrep for its relating object
				
				t.setAccount(DbQuery.getAccountName(db));
				try{
					t=endpoint.insertTransLog(t).execute();
					updateUpAccC(t.getTransTime());
				}catch(Exception e){
                    e.printStackTrace();
					return null;
				}

                try {
                    DbQuery.deleteRecord(db, dbh, RedoLogEntry.TABLE_NAME, logId);
                }catch (Exception e){e.printStackTrace();}
			}
			return null;
		}
		
	}

	public void insertAccount(String namespace, long time, String country, String county){
		if(time==-1)
			time=System.currentTimeMillis()/1000L;
		AccountApi.Builder builder = new AccountApi.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				null);
		builder = CloudEndpointUtils.updateBuilder(builder);
		AccountApi endpoint = builder.build();
		Account acc=new Account();
		acc.setAccount(namespace);
		acc.setLastUpdated(time);
		acc.setCountry(country);
		acc.setCounty(county);
		try {
			Log.i("myTestToInsertttttt","Name:"+namespace+"Country:"+country+"County:"+county);
			acc=endpoint.getOrInsertAccount(acc.getAccount(), acc.getCounty(), acc.getCountry()).execute();
			DbQuery.insertAccountTask(db,dbh,acc);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void insertAccount2(String namespace, long time, String country, String county){
		if(time==-1)
			time=System.currentTimeMillis()/1000L;
		new insertAccountTask(namespace,time,country,county).execute();
	}
	public class insertAccountTask extends AsyncTask<Void,Void,Void>{
		String namespace;
		long time;
        String country,county;
		public insertAccountTask(String namespace, long time, String country, String county){
			this.namespace=namespace;
			this.time=time;
            this.country=country;
            this.county=county;
		}
		@Override
		protected Void doInBackground(Void... params) {
			AccountApi.Builder builder = new AccountApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);
			builder = CloudEndpointUtils.updateBuilder(builder);
            AccountApi endpoint = builder.build();
			Account acc=new Account();
			acc.setAccount(namespace);
			acc.setLastUpdated(time);
            acc.setCounty(county);
            acc.setCountry(country);
			try {
				Log.i("myTestToInsertttttt","Name:"+namespace+"Country:"+country+"County:"+county);
//				endpoint.getOrInsertAccount(namespace, "SVG", "St George's").execute();
				acc=endpoint.getOrInsertAccount(acc.getAccount(), acc.getCounty(), acc.getCountry()).execute();
				DbQuery.insertAccountTask(db,dbh,acc);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
	return null;
}



	}
	public void updateUpAccC(Long time){
		Account acc=DbQuery.getUpAcc(db);
		acc.setLastUpdated(time);
		new updateUpAcc().execute(acc);
	}
	public class updateUpAcc extends AsyncTask<Account,Void,Void>{
		@Override
		protected Void doInBackground(Account... params) {
            AccountApi.Builder builder = new AccountApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);
			builder = CloudEndpointUtils.updateBuilder(builder);
            AccountApi endpoint = builder.build();
			Account acc=params[0];
			try {
				endpoint.updateAccount(acc).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}



	public Account getAccount(String namespace){
        AccountApi.Builder builder = new AccountApi.Builder(AndroidHttp.newCompatibleTransport(), new JacksonFactory(),null);
		builder = CloudEndpointUtils.updateBuilder(builder);
        AccountApi endpoint = builder.build();
		Account acc = null;
		try {
			acc=endpoint.getAccount(namespace).execute();
//			acc=endpoint.getAccount((long) 1,namespace).execute();
		}catch (IOException e) {
            e.printStackTrace();
		}
		return acc;
	}

	public void flushToCloud(){
		insertCycle();
		insertPurchase();
		insertCycleUseC();
		updateCycle();
		updatePurchase();
		deletePurchase();
		deleteCycle();
	}
	
}
