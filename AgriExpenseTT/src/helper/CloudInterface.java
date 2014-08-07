package helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import com.example.agriexpensett.cycleendpoint.Cycleendpoint;
import com.example.agriexpensett.cycleendpoint.model.Cycle;
import com.example.agriexpensett.cycleuseendpoint.Cycleuseendpoint;
import com.example.agriexpensett.cycleuseendpoint.model.CycleUse;
import com.example.agriexpensett.rpurchaseendpoint.Rpurchaseendpoint;
import com.example.agriexpensett.rpurchaseendpoint.model.RPurchase;
import com.example.agriexpensett.translogendpoint.Translogendpoint;
import com.example.agriexpensett.translogendpoint.model.TransLog;
import com.example.agriexpensett.upaccendpoint.Upaccendpoint;
import com.example.agriexpensett.upaccendpoint.model.UpAcc;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.appengine.api.datastore.Key;
//import com.google.appengine.api.datastore.KeyFactory;

public class CloudInterface {
	SQLiteDatabase db;
	DbHelper dbh;
	TransactionLog tL;
	public CloudInterface(Context context) {
		dbh= new DbHelper(context);
		db=dbh.getReadableDatabase();
		tL=new TransactionLog(dbh,db,context);
	}
	public CloudInterface(Context context,SQLiteDatabase db,DbHelper dbh) {
		this.dbh= dbh;
		this.db=db;
		tL=new TransactionLog(dbh,db,context);
	}
	public void updateCycleC(){
		new updateCycle().execute();
	}
	public class updateCycle extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			Cycleendpoint.Builder builder = new Cycleendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			Cycleendpoint endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();
			DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_UPDATE, DbHelper.TABLE_CROPCYLE);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();//the current primary key of CROP CYCLE Table
				Cycle c=DbQuery.getCycle(db, dbh, rowId);
				String keyrep=DbQuery.getKey(db, dbh, DbHelper.TABLE_CROPCYLE, c.getId());
				c.setKeyrep(keyrep);
				try{
					//c=endpoint.insertCycle(c).execute();
					c=endpoint.updateCycle(c).execute();
				}catch(Exception e){
					
					System.out.println("could not update cycle");
					return null;
				}
				if(c!=null){
					//we stored they key as text in the account field of c when we returned
					//System.out.println(c.getAccount());
					//store key of inserted cycle into cloud - cloud key table
					//DbQuery.insertCloudKey(db, dbh, DbHelper.TABLE_CROPCYLE, c.getAccount(),rowId);
					//remove from redo log
					DbQuery.deleteRecord(db, dbh, DbHelper.TABLE_REDO_LOG, logId);
					
					//getting the transaction from the transaction log that matches this operation
					String code="select * from "+DbHelper.TABLE_TRANSACTION_LOG+" where "+DbHelper.TRANSACTION_LOG_TABLE+"='"+DbHelper.TABLE_CROPCYLE+"' and "
					+DbHelper.TRANSACTION_LOG_ROWID+"="+rowId+" and "+DbHelper.TRANSACTION_LOG_OPERATION+"='"+TransactionLog.TL_UPDATE+"'";
					Cursor cursor=db.rawQuery(code, null);
					//select from transaction log
						//where transaction's rowId = rowId
						//and transactions's table = table
						//and transactions's operation = operation
					//but there can be multiple operations (updates) on a particular object (row[Id] of a Table)
					//what should we do in this case !? :s 
					cursor.moveToLast();//only for updaates we should move to last because the last update would hold the most current data
					int id=cursor.getInt(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_LOGID));
					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, DbHelper.TABLE_TRANSACTION_LOG, id, TransactionLog.TL_INS);
					insertLog();
				}
			}
			return null;
		}
		
	}
	public void updatePurchaseC(){
		new updatePurchase().execute();
	}
	public class updatePurchase extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			Rpurchaseendpoint.Builder builder = new Rpurchaseendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			Rpurchaseendpoint endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();
			DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_UPDATE, DbHelper.TABLE_RESOURCE_PURCHASES);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();//the current primary key of CROP CYCLE Table
				RPurchase p=DbQuery.getARPurchase(db, dbh, rowId);
				String keyrep=DbQuery.getKey(db, dbh, DbHelper.TABLE_RESOURCE_PURCHASES, p.getPId());
				try{
					System.out.println("purchase key rep"+keyrep);
					p.setKeyrep(keyrep);
					p=endpoint.updateRPurchase(p).execute();
				}catch(Exception e){
					
					System.out.println("could not update Purchase");
					return null;
				}
				if(p!=null){
					//we stored they key as text in the account field of c when we returned
					//System.out.println(c.getAccount());
					//store key of inserted cycle into cloud - cloud key table
					//DbQuery.insertCloudKey(db, dbh, DbHelper.TABLE_CROPCYLE, c.getAccount(),rowId);
					//remove from redo log
					DbQuery.deleteRecord(db, dbh, DbHelper.TABLE_REDO_LOG, logId);
					
					//getting the transaction from the transaction log that matches this operation
					String code="select * from "+DbHelper.TABLE_TRANSACTION_LOG+" where "+DbHelper.TRANSACTION_LOG_TABLE+"='"+DbHelper.TABLE_RESOURCE_PURCHASES+"' and "
					+DbHelper.TRANSACTION_LOG_ROWID+"="+rowId+" and "+DbHelper.TRANSACTION_LOG_OPERATION+"='"+TransactionLog.TL_UPDATE+"'";
					Cursor cursor=db.rawQuery(code, null);
					
					cursor.moveToLast();//only for updates explained in cycleUpdate
					int id=cursor.getInt(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_LOGID));
					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, DbHelper.TABLE_TRANSACTION_LOG, id, TransactionLog.TL_INS);
					insertLog();
				}
			}
			return null;
		}
		
	}
	public void insertCycleC(){
		new InsertCycle().execute();
	}
	public class InsertCycle extends AsyncTask<Void, Object, Object>{
		
		@Override
		protected Cycle doInBackground(Void... params) {
			Cycleendpoint.Builder builder = new Cycleendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			Cycleendpoint endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();
			DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, DbHelper.TABLE_CROPCYLE);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();//the current primary key of CROP CYCLE Table
				Cycle c=DbQuery.getCycle(db, dbh, rowId);
				c.setAccount(DbQuery.getAccount(db));//uses the account rep as the namespace
				try{
					c=endpoint.insertCycle(c).execute();
				}catch(Exception e){
					
					System.out.println("could not insert cycle");
					return null;
				}
				if(c!=null){
					//we stored they key as text in the account field of c when we returned
					System.out.println(c.getAccount());
					//store key of inserted cycle into cloud - cloud key table
					DbQuery.insertCloudKey(db, dbh, DbHelper.TABLE_CROPCYLE, c.getAccount(),rowId);
					//remove from redo log
					DbQuery.deleteRecord(db, dbh, DbHelper.TABLE_REDO_LOG, logId);
					//getting the transaction from the transaction log that matches this operation
					String code="select * from "+DbHelper.TABLE_TRANSACTION_LOG+" where "+DbHelper.TRANSACTION_LOG_TABLE+"='"+DbHelper.TABLE_CROPCYLE+"' and "
					+DbHelper.TRANSACTION_LOG_ROWID+"="+rowId+" and "+DbHelper.TRANSACTION_LOG_OPERATION+"='"+TransactionLog.TL_INS+"'";
					Cursor cursor=db.rawQuery(code, null);
					cursor.moveToFirst();
					int id=cursor.getInt(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_LOGID));
					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, DbHelper.TABLE_TRANSACTION_LOG, id, TransactionLog.TL_INS);
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
			Cycleuseendpoint.Builder builder = new Cycleuseendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			Cycleuseendpoint endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();
			DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, DbHelper.TABLE_CYCLE_RESOURCES);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				CycleUse c=DbQuery.getACycleUse(db, dbh, rowId);
				c.setAccount(DbQuery.getAccount(db));
				
				try{
					c=endpoint.insertCycleUse(c).execute();
				}catch(Exception e){
					System.out.println("could not insert cycleUse");
					return null;
				}
				if(c!=null){
					//we stored they key as text in the account field of c when we returned
					System.out.println(c.getAccount());
					//store key of inserted cycleuse into cloud - cloud key table
					DbQuery.insertCloudKey(db, dbh, DbHelper.TABLE_CYCLE_RESOURCES, c.getAccount(),rowId);
					//remove from redo log
					DbQuery.deleteRecord(db, dbh, DbHelper.TABLE_REDO_LOG, logId);
					//getting the transaction from the transaction log that matches this operation
					String code="select * from "+DbHelper.TABLE_TRANSACTION_LOG+" where "+DbHelper.TRANSACTION_LOG_TABLE+"='"+DbHelper.TABLE_CYCLE_RESOURCES+"' and "
							+DbHelper.TRANSACTION_LOG_ROWID+"="+rowId+" and "+DbHelper.TRANSACTION_LOG_OPERATION+"='"+TransactionLog.TL_INS+"'";
					Cursor cursor=db.rawQuery(code, null);
					cursor.moveToFirst();
					int id=cursor.getInt(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_LOGID));
					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, DbHelper.TABLE_TRANSACTION_LOG, id, TransactionLog.TL_INS);
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
			Rpurchaseendpoint.Builder builder = new Rpurchaseendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			Rpurchaseendpoint endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();
			DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, DbHelper.TABLE_RESOURCE_PURCHASES);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				RPurchase purchase=DbQuery.getARPurchase(db, dbh, rowId);
				purchase.setAccount(DbQuery.getAccount(db));
				try{
					purchase=endpoint.insertRPurchase(purchase).execute();
				}catch(Exception e){
					System.out.println("could not insert purchase");
					return null;
				}
				if(purchase!=null){
					//we stored they key as text in the account field of c when we returned
					System.out.println(purchase.getAccount());
					//store key of inserted cycleuse into cloud - cloud key table
					DbQuery.insertCloudKey(db, dbh, DbHelper.TABLE_RESOURCE_PURCHASES, purchase.getAccount(),rowId);
					//remove from redo log
					DbQuery.deleteRecord(db, dbh, DbHelper.TABLE_REDO_LOG, logId);
					//getting the transaction from the transaction log that matches this operation
					String code="select * from "+DbHelper.TABLE_TRANSACTION_LOG+" where "+DbHelper.TRANSACTION_LOG_TABLE+"='"+DbHelper.TABLE_RESOURCE_PURCHASES+"' and "
							+DbHelper.TRANSACTION_LOG_ROWID+"="+rowId+" and "+DbHelper.TRANSACTION_LOG_OPERATION+"='"+TransactionLog.TL_INS+"'";
					Cursor cursor=db.rawQuery(code, null);
					cursor.moveToFirst();
					int id=cursor.getInt(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_LOGID));
					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, DbHelper.TABLE_TRANSACTION_LOG, id, TransactionLog.TL_INS);
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
			Cycleendpoint.Builder builder = new Cycleendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			Cycleendpoint endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();

			//DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, dbh.TABLE_RESOURCE_PURCHASES);
			DbQuery.getRedo(db, dbh, rowIds, logIds,"del", DbHelper.TABLE_CROPCYLE);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				System.out.println("row to delete from "+DbHelper.TABLE_CROPCYLE+": "+rowId);
				Cycle c=new Cycle();
				c.setId(rowId);
				c.setAccount(DbQuery.getAccount(db));
				String keyrep=DbQuery.getKey(db, dbh, DbHelper.TABLE_CROPCYLE, rowId);
				if(!keyrep.equals(null)){//was never inserted :o
					try{
						endpoint.removeCycle(c.getKeyrep(),c.getAccount()).execute();
					}catch(Exception e){
						c=null;
						System.out.println("could not delete cycle");
						//return null;
					}
				}
				
				if(c!=null){//transactioon was succesful or keyrep was not found
					int id=DbQuery.getCloudKeyId(db, dbh, DbHelper.TABLE_CROPCYLE, rowId);
					if(id!=-1){
						//remove key of cycle that was deleted from cloud
						DbQuery.deleteRecord(db, dbh, DbHelper.TABLE_CLOUD_KEY, id);
					}
					//remove from redo log
					DbQuery.deleteRecord(db, dbh, DbHelper.TABLE_REDO_LOG, logId);
					//getting the transaction from the transaction log that matches this operation
					String code="select * from "+DbHelper.TABLE_TRANSACTION_LOG+" where "+DbHelper.TRANSACTION_LOG_TABLE+"="+DbHelper.TABLE_CROPCYLE+
							" and "+DbHelper.TRANSACTION_LOG_ROWID+"="+rowId+" and "+DbHelper.TRANSACTION_LOG_OPERATION+"='del'";
					Cursor cursor=db.rawQuery(code, null);
					cursor.moveToFirst();
					int Tid=cursor.getInt(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_LOGID));
					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, DbHelper.TABLE_TRANSACTION_LOG, Tid, TransactionLog.TL_INS);
					insertLog();
					//TODO
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			/*db.close();
			db=dbh.getWritableDatabase();
			db.execSQL("drop table if exists "+DbHelper.TABLE_REDO_LOG);
			dbh.createRedoLog(db);
			db.close();
			db=dbh.getReadableDatabase();*/
			super.onPostExecute(result);
			
			//TODO
		}
		
		
	}
	
	public void deleteCycleUse(){
		new DeleteCycleUse().execute();
	}
	public class DeleteCycleUse extends AsyncTask<Void, Object, Object>{

		@Override
		protected Object doInBackground(Void... params) {
			Cycleuseendpoint.Builder builder = new Cycleuseendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			Cycleuseendpoint endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();

			//DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, dbh.TABLE_RESOURCE_PURCHASES);
			DbQuery.getRedo(db, dbh, rowIds, logIds,"del", DbHelper.TABLE_CYCLE_RESOURCES);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				System.out.println("row to delete from "+DbHelper.TABLE_CYCLE_RESOURCES+": "+rowId);
				CycleUse c=new CycleUse();
				c.setId(rowId);
				c.setAccount(DbQuery.getAccount(db));
				String keyrep=DbQuery.getKey(db, dbh, DbHelper.TABLE_CYCLE_RESOURCES, rowId);
				c.setKeyrep(keyrep);
				if(keyrep.equals(null)){
					try{
						System.out.println("Key:"+c);
						endpoint.removeCycleUse(c.getKeyrep(),c.getAccount()).execute();
					}catch(Exception e){
						c=null;
						System.out.println("could not delete cycle");
					}
				}
				if(!c.equals(null)){
					int id=DbQuery.getCloudKeyId(db, dbh, DbHelper.TABLE_CYCLE_RESOURCES, rowId);
					if(id!=-1){
						//remove key of cycle that was deleted from cloud
						DbQuery.deleteRecord(db, dbh, DbHelper.TABLE_CLOUD_KEY, id);
					}
					//remove from redo log
					DbQuery.deleteRecord(db, dbh, DbHelper.TABLE_REDO_LOG, logId);
					//getting the transaction from the transaction log that matches this operation
					String code="select * from "+DbHelper.TABLE_TRANSACTION_LOG+" where "+DbHelper.TRANSACTION_LOG_TABLE+"="+DbHelper.TABLE_CYCLE_RESOURCES+
							" and "+DbHelper.TRANSACTION_LOG_ROWID+"="+rowId+" and "+DbHelper.TRANSACTION_LOG_OPERATION+"='del'";
					Cursor cursor=db.rawQuery(code, null);
					cursor.moveToFirst();
					int Tid=cursor.getInt(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_LOGID));
					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, DbHelper.TABLE_TRANSACTION_LOG, Tid, TransactionLog.TL_INS);
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
			Rpurchaseendpoint.Builder builder = new Rpurchaseendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			Rpurchaseendpoint endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();

			//DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, dbh.TABLE_RESOURCE_PURCHASES);
			DbQuery.getRedo(db, dbh, rowIds, logIds,"del", DbHelper.TABLE_RESOURCE_PURCHASES);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				System.out.println("row to delete from "+DbHelper.TABLE_RESOURCE_PURCHASES+": "+rowId);
				RPurchase p=new RPurchase();
				p.setPId(rowId);
				p.setAccount(DbQuery.getAccount(db));
				String keyrep=DbQuery.getKey(db, dbh, DbHelper.TABLE_RESOURCE_PURCHASES, rowId);
				p.setKeyrep(keyrep);
				if(keyrep!=null){
					try{
						endpoint.removeRPurchase(p.getKeyrep(),p.getAccount()).execute();
					}catch(Exception e){
						e.printStackTrace();
						p=null;
						System.out.println("could not delete Purchase");
					}
				}
				if(p!=null){//the removal was successful OR there was not ever inserted into the cloud
					int id=DbQuery.getCloudKeyId(db, dbh, DbHelper.TABLE_RESOURCE_PURCHASES, rowId);
					if(id!=-1){//if the key exists
						DbQuery.deleteRecord(db, dbh, DbHelper.TABLE_CLOUD_KEY, id);
						//getting the transaction from the transaction log that matches this operation	
					}
					
					//remove from redo log
					DbQuery.deleteRecord(db, dbh, DbHelper.TABLE_REDO_LOG, logId);
					//remove key of cycle that was deleted from cloud
					String code="select * from "+DbHelper.TABLE_TRANSACTION_LOG+" where "+DbHelper.TRANSACTION_LOG_TABLE+"='"+DbHelper.TABLE_RESOURCE_PURCHASES+"'"
							+ " and "+DbHelper.TRANSACTION_LOG_ROWID+"="+rowId+" and "+DbHelper.TRANSACTION_LOG_OPERATION+"='del'";
					Cursor cursor=db.rawQuery(code, null);
					cursor.moveToFirst();
					int Tid=cursor.getInt(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_LOGID));
					//inserting this record of the transaction to the redo log to later be inserted into the cloud
					DbQuery.insertRedoLog(db, dbh, DbHelper.TABLE_TRANSACTION_LOG, Tid, TransactionLog.TL_INS);
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
			Translogendpoint.Builder builder = new Translogendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			Translogendpoint endpoint = builder.build();
			ArrayList<Integer> rowIds=new ArrayList<Integer>();
			ArrayList<Integer> logIds=new ArrayList<Integer>();
			DbQuery.getRedo(db, dbh, rowIds, logIds, TransactionLog.TL_INS, DbHelper.TABLE_TRANSACTION_LOG);
			Iterator<Integer> logI=logIds.iterator();
			Iterator<Integer> rowI=rowIds.iterator();
			while(logI.hasNext()){
				int logId=logI.next(),rowId=rowI.next();
				//Cycle c=DbQuery.getCycle(db, dbh, rowId);
				
				TransLog t=DbQuery.getLog(db,dbh,rowId);
				String k=DbQuery.getKey(db, dbh, t.getTableKind(), t.getRowId());//gets the key for the related object in the cloud
				t.setKeyrep(k);//stores the keyrep for its relating object
				
				t.setAccount(DbQuery.getAccount(db));
				System.out.println(t.toString());
				try{
					System.out.println(t.getId());
					t=endpoint.insertTransLog(t).execute();
					updateUpAccC(t.getTransTime());
				}catch(Exception e){
					t=null;
					System.out.println("could not insert Log");
					return null;
				}
				if(t!=null){
					//WE DO NOT NEED TO STORE THE KEY
					//DbQuery.insertCloudKey(db, dbh, DbHelper.TABLE_CROPCYLE, c.getAccount(),rowId);
					
					//remove insert from redo log
					DbQuery.deleteRecord(db, dbh, DbHelper.TABLE_REDO_LOG, logId);
				}
			
			}
			return null;
		}
		
	}
	public void insertUpAccC(String namespace,long time){
		if(time==-1)
			time=System.currentTimeMillis()/1000L;
		new insertUpAcc(namespace,time).execute();
	}
	public class insertUpAcc extends AsyncTask<Void,Void,Void>{
		String namespace;
		long time;
		public insertUpAcc(String namespace,long time){
			this.namespace=namespace;
			this.time=time;
		}
		@Override
		protected Void doInBackground(Void... params) {
			Upaccendpoint.Builder builder = new Upaccendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			Upaccendpoint endpoint = builder.build();
			UpAcc acc=new UpAcc();
			acc.setAcc(namespace);
			acc.setLastUpdated(time);
			try {
				acc=endpoint.insertUpAcc(acc).execute();
				DbQuery.insertUpAcc(db, acc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}
	public void updateUpAccC(Long time){
		UpAcc acc=DbQuery.getUpAcc(db);
		acc.setLastUpdated(time);
		new updateUpAcc().execute(acc);
	}
	public class updateUpAcc extends AsyncTask<UpAcc,Void,Void>{

		@Override
		protected Void doInBackground(UpAcc... params) {
			Upaccendpoint.Builder builder = new Upaccendpoint.Builder(
			         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
			         null);         
			builder = CloudEndpointUtils.updateBuilder(builder);
			Upaccendpoint endpoint = builder.build();
			UpAcc acc=params[0];
			try {
				endpoint.updateUpAcc(acc).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}
	public UpAcc getUpAcc(String namespace){
		Upaccendpoint.Builder builder = new Upaccendpoint.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
		Upaccendpoint endpoint = builder.build();
		UpAcc acc=null;
		try {
			acc=endpoint.getUpAcc((long) 1,namespace).execute();
		}catch (IOException e) {e.printStackTrace();
			return null;
		}
		return acc;
	}
	public void flushToCloud(){
		insertCycleC();
		insertPurchase();
		insertCycleUseC();
		updateCycleC();
		updatePurchaseC();
		deletePurchase();
		deleteCycle();
	}
	
}
