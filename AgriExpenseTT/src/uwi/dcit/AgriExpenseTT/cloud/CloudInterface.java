package uwi.dcit.AgriExpenseTT.cloud;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import uwi.dcit.AgriExpenseTT.dbstruct.structs.Account;
import uwi.dcit.AgriExpenseTT.dbstruct.structs.Cloud;
import uwi.dcit.AgriExpenseTT.dbstruct.structs.Cycles;
import uwi.dcit.AgriExpenseTT.dbstruct.structs.CyclesUse;
import uwi.dcit.AgriExpenseTT.dbstruct.structs.Redo;
import uwi.dcit.AgriExpenseTT.dbstruct.structs.ResourcePuchase;
import uwi.dcit.AgriExpenseTT.dbstruct.structs.UpAccount;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.TransactionLog;
import uwi.dcit.AgriExpenseTT.models.CloudKeyContract.CloudKeyEntry;
import uwi.dcit.AgriExpenseTT.models.CycleContract.CycleEntry;
import uwi.dcit.AgriExpenseTT.models.CycleResourceContract.CycleResourceEntry;
import uwi.dcit.AgriExpenseTT.models.RedoLogContract.RedoLogEntry;
import uwi.dcit.AgriExpenseTT.models.ResourcePurchaseContract.ResourcePurchaseEntry;
import uwi.dcit.AgriExpenseTT.models.TransactionLogContract.TransactionLogEntry;
import uwi.dcit.agriexpensesvr.cycleApi.CycleApi;
import uwi.dcit.agriexpensesvr.cycleApi.model.Cycle;
import uwi.dcit.agriexpensesvr.cycleUseApi.CycleUseApi;
import uwi.dcit.agriexpensesvr.cycleUseApi.model.CycleUse;
import uwi.dcit.agriexpensesvr.rPurchaseApi.RPurchaseApi;
import uwi.dcit.agriexpensesvr.rPurchaseApi.model.RPurchase;
import uwi.dcit.agriexpensesvr.translogApi.TranslogApi;
import uwi.dcit.agriexpensesvr.translogApi.model.TransLog;
import uwi.dcit.agriexpensesvr.upAccApi.UpAccApi;
import uwi.dcit.agriexpensesvr.upAccApi.model.UpAcc;


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
			Redo.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_UPDATE, CycleEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();//the current primary key of CROP CYCLE Table
                Cycle c = Cycles.getCycle(db, dbh, rowId);
				String keyrep= Cloud.getKey(db, dbh, CycleEntry.TABLE_NAME, c.getId());
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
					Redo.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, id, TransactionLog.TL_INS);
					insertLog();
				}
			}
			return null;
		}
		
	}

	private class PurchaseUpdater extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			RPurchaseApi.Builder builder = new RPurchaseApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			RPurchaseApi endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();
			Redo.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_UPDATE, ResourcePurchaseEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();//the current primary key of CROP CYCLE Table
				RPurchase p= ResourcePuchase.getARPurchase(db, dbh, rowId);
				String keyrep=Cloud.getKey(db, dbh, ResourcePurchaseEntry.TABLE_NAME, p.getPId());
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
					Redo.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, id, TransactionLog.TL_INS);
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

			Redo.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, CycleEntry.TABLE_NAME);

			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();

			while(logI.hasNext()){
				int logId=logI.next(),
                        rowId=rowI.next();              // The current primary key of CROP CYCLE Table

                Cycle c = Cycles.getCycle(db, dbh, rowId);
				c.setAccount(Account.getAccount(db));   // Uses the account rep as the namespace

				try{
					c=endpoint.insertCycle(c).execute();
				}catch(Exception e){
					
					return null;
				}
				if(c!=null){
					//we stored they key as text in the account field of c when we returned
					System.out.println(c.getAccount());
					//store key of inserted cycle into cloud - cloud key table
					Cloud.insertCloudKey(db, dbh, CycleEntry.TABLE_NAME, c.getAccount(),rowId);
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
					Redo.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, id, TransactionLog.TL_INS);
					insertLog();
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
			Redo.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, CycleResourceEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				CycleUse c= CyclesUse.getACycleUse(db, dbh, rowId);
				c.setAccount(Account.getAccount(db));
				
				try{
					c=endpoint.insertCycleUse(c).execute();
				}catch(Exception e){
					return null;
				}
				if(c!=null){
					//we stored they key as text in the account field of c when we returned
					System.out.println(c.getAccount());
					//store key of inserted cycleuse into cloud - cloud key table
					Cloud.insertCloudKey(db, dbh, CycleResourceEntry.TABLE_NAME, c.getAccount(),rowId);
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
					Redo.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, id, TransactionLog.TL_INS);
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
		protected RPurchase doInBackground(Void... params) {
			RPurchaseApi.Builder builder = new RPurchaseApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
            RPurchaseApi endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();
			Redo.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, ResourcePurchaseEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				RPurchase purchase=ResourcePuchase.getARPurchase(db, dbh, rowId);
				purchase.setAccount(Account.getAccount(db));
				try{
					purchase=endpoint.insertRPurchase(purchase).execute();
				}catch(Exception e){
					return null;
				}
				if(purchase!=null){
					//we stored they key as text in the account field of c when we returned
					System.out.println(purchase.getAccount());
					//store key of inserted cycleuse into cloud - cloud key table
					Cloud.insertCloudKey(db, dbh, ResourcePurchaseEntry.TABLE_NAME, purchase.getAccount(),rowId);
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
					Redo.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, id, TransactionLog.TL_INS);
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
			Redo.getRedo(db, dbh, rowIds, logIds,"del", CycleEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				Cycle c=new Cycle();
				c.setId(rowId);
				c.setAccount(Account.getAccount(db));
				String keyrep=Cloud.getKey(db, dbh, CycleEntry.TABLE_NAME, rowId);
				if(keyrep != null){//was never inserted :o
					try{
						endpoint.removeCycle(c.getKeyrep(),c.getAccount()).execute();
					}catch(Exception e){
						c=null;
					}
				}
				
				if(c!=null){//transactioon was succesful or keyrep was not found
					int id=Cloud.getCloudKeyId(db, dbh, CycleEntry.TABLE_NAME, rowId);
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
					Redo.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, Tid, TransactionLog.TL_INS);
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
			Redo.getRedo(db, dbh, rowIds, logIds,"del", CycleResourceEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				CycleUse c=new CycleUse();
				c.setId(rowId);
				c.setAccount(Account.getAccount(db));
				String keyrep=Cloud.getKey(db, dbh, CycleResourceEntry.TABLE_NAME, rowId);
				c.setKeyrep(keyrep);
				if(keyrep == null){
					try{
						endpoint.removeCycleUse(c.getKeyrep(),c.getAccount()).execute();
					}catch(Exception e){
						c=null;
					}
				}
				if(c != null){
					int id=Cloud.getCloudKeyId(db, dbh, CycleResourceEntry.TABLE_NAME, rowId);
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
					Redo.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, Tid, TransactionLog.TL_INS);
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
			RPurchaseApi.Builder builder = new RPurchaseApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
            RPurchaseApi endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();

			//DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, dbh.TABLE_RESOURCE_PURCHASES);
			Redo.getRedo(db, dbh, rowIds, logIds,"del", ResourcePurchaseEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				RPurchase p=new RPurchase();
				p.setPId(rowId);
				p.setAccount(Account.getAccount(db));
				String keyrep=Cloud.getKey(db, dbh, ResourcePurchaseEntry.TABLE_NAME, rowId);
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
					int id=Cloud.getCloudKeyId(db, dbh, ResourcePurchaseEntry.TABLE_NAME, rowId);
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
					Redo.insertRedoLog(db, dbh, TransactionLogEntry.TABLE_NAME, Tid, TransactionLog.TL_INS);
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
			Redo.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, TransactionLogEntry.TABLE_NAME);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				//Cycle c=DbQuery.getCycle(db, dbh, rowId);
				
				TransLog t=DbQuery.getLog(db,dbh,rowId);
				String k=Cloud.getKey(db, dbh, t.getTableKind(), t.getRowId());//gets the key for the related object in the cloud
				t.setKeyrep(k);//stores the keyrep for its relating object
				
				t.setAccount(Account.getAccount(db));
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
	public void insertUpAccC(String namespace,long time,String country, String county){
		if(time==-1)
			time=System.currentTimeMillis()/1000L;
		new insertUpAcc(namespace,time,country,county).execute();
	}
	public class insertUpAcc extends AsyncTask<Void,Void,Void>{
		String namespace;
		long time;
        String country,county;
		public insertUpAcc(String namespace,long time,String country,String county){
			this.namespace=namespace;
			this.time=time;
            this.country=country;
            this.county=county;
		}
		@Override
		protected Void doInBackground(Void... params) {
			UpAccApi.Builder builder = new UpAccApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
            UpAccApi endpoint = builder.build();
			UpAcc acc=new UpAcc();
			acc.setAcc(namespace);
			acc.setLastUpdated(time);
            acc.setCounty(county);
            acc.setCountry(country);
			try {
				acc=endpoint.insertUpAcc(acc).execute();
				UpAccount.insertUpAcc(db, acc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}
	public void updateUpAccC(Long time){
		UpAcc acc=UpAccount.getUpAcc(db);
		acc.setLastUpdated(time);
		new updateUpAcc().execute(acc);
	}
	public class updateUpAcc extends AsyncTask<UpAcc,Void,Void>{

		@Override
		protected Void doInBackground(UpAcc... params) {
            UpAccApi.Builder builder = new UpAccApi.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
            UpAccApi endpoint = builder.build();
			UpAcc acc=params[0];
			try {
				endpoint.updateUpAcc(acc).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}

	public UpAcc getUpAcc(String namespace){
        UpAccApi.Builder builder = new UpAccApi.Builder(AndroidHttp.newCompatibleTransport(), new JacksonFactory(),null);
		builder = CloudEndpointUtils.updateBuilder(builder);
        UpAccApi endpoint = builder.build();
		UpAcc acc = null;
		try {
			acc=endpoint.getUpAcc((long) 1,namespace).execute();
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
