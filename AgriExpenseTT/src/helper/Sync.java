package helper;

import uwi.dcit.AgriExpenseTT.SignIn;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import com.example.agriexpensett.upaccendpoint.model.UpAcc;

public class Sync {
	private UpAcc localAcc;
	private UpAcc cloudAcc;
	protected SignIn signin;
	SQLiteDatabase db;
	DbHelper dbh;
	Context context;
	TransactionLog tL;
	public enum Option{
		updateCloudOpt,updateLocalOpt,overwriteCloudOpt,overwriteLocalOpt,createCloudNewOpt
	}
	public Sync(SQLiteDatabase db, DbHelper dbh,Context context,SignIn signin){
		this.db=db;
		this.dbh=dbh;
		this.context=context;
		this.signin=signin;
		tL=new TransactionLog(dbh, db,context);
	}
	public void start(String namespace,UpAcc cloudAcc){
		System.out.println("gonna sync now");
		localAcc=DbQuery.getUpAcc(db);
		this.cloudAcc=cloudAcc;
		//both exist
		if(cloudAcc!=null){System.out.println("Both exist");
			long localUpdate=localAcc.getLastUpdated();
			long cloudUpdate=cloudAcc.getLastUpdated();
			if(localUpdate>=cloudUpdate){//local more recent than cloud
				//the local does not have an account which means it has never been synced 
				if(localAcc.getAcc()==null || localAcc.getAcc().equals("")){System.out.println("confirm sync");
					/*there is no local account, the user must decide 
					 * if he's gonna use the datastore or if he's going to overwrite it*/
					confirmSync(localUpdate,cloudUpdate,namespace);
					
				//the local has an account which means it has already at some point synced
				}else{
					/*since it was synced at some point and the local is more updated
					 * just get the logs and update the cloud
					 */
					//TODO
					new SyncExec(tL, namespace, cloudUpdate, localUpdate).execute(Option.updateCloudOpt);
					
				}
				
				
			}else if(cloudUpdate>localUpdate){//Cloud is more updated
				//the local does not have an account which means it has never been synced 
				if(localAcc.getAcc()==null || localAcc.getAcc().equals("")){System.out.println("confirm sync");
					/*there is no local account, the user must decide 
					 * if he's gonna use the datastore or if he's going to overwrite it*/
					confirmSync(localUpdate,cloudUpdate,namespace);
					
				//the local has an account which means it has already at some point synced
				}else{
					/*since it was synced at some point and the cloud is more updated
					 * just get the logs and update the local
					 */
					//TODO
					new SyncExec(tL, namespace, cloudUpdate, localUpdate).execute(Option.updateLocalOpt);
				}
				
			}
			ContentValues cv=new ContentValues();
			cv.put(DbHelper.UPDATE_ACCOUNT_CLOUD_KEY, cloudAcc.getKeyrep());
		//only local exist
		}else{ 
			System.out.println("cloud doesnt exist so pushing all to cloud");
			//TODO
			new SyncExec(tL, namespace, 0, 0).execute(Option.createCloudNewOpt);
		//only cloud exist
		}
		
	}
	public class SyncExec extends AsyncTask<Option,Void,Boolean>{
		TransactionLog tL;
		String namespace;
		long cloudUpdate,localUpdate;
		public SyncExec(TransactionLog tL,String namespace,long cloudUpdate,long localUpdate){
			this.tL=tL;
			this.namespace=namespace;
			this.cloudUpdate=cloudUpdate;
			this.localUpdate=localUpdate;
		}
		@SuppressLint("UseValueOf") @Override
		protected Boolean doInBackground(Option... params) {
			Option option=params[0];// TODO Auto-generated method stub
			ContentValues cv=new ContentValues();
			Boolean success = new Boolean(true);
			switch(option){
				case updateCloudOpt:
					tL.updateCloud(cloudUpdate);
					cv.put(DbHelper.UPDATE_ACCOUNT_SIGNEDIN, 1);
					db.update(DbHelper.TABLE_UPDATE_ACCOUNT, cv, DbHelper.UPDATE_ACCOUNT_ID+"=1", null);
					break;
					
				case updateLocalOpt:
					tL.logsUpdateLocal(namespace,localUpdate);
					cv.put(DbHelper.UPDATE_ACCOUNT_SIGNEDIN, 1);
					db.update(DbHelper.TABLE_UPDATE_ACCOUNT, cv, DbHelper.UPDATE_ACCOUNT_ID+"=1", null);
					break;
					
				case overwriteCloudOpt:
					tL.removeNamespace(namespace);
					success=tL.createCloud(namespace);
					break;
					
				case overwriteLocalOpt:
					success=tL.pullAllFromCloud(cloudAcc);
					cv.put(DbHelper.UPDATE_ACCOUNT_SIGNEDIN, 1);
					db.update(DbHelper.TABLE_UPDATE_ACCOUNT, cv, DbHelper.UPDATE_ACCOUNT_ID+"=1", null);
					break;
				case createCloudNewOpt:
					CloudInterface cloudIF=new CloudInterface(context, db, dbh);
					cloudIF.insertUpAccC(namespace,0);
					success=tL.createCloud(namespace);
					break;
			}
			return success;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			signin.signInReturn(result, null);
			super.onPostExecute(result);
		}
		
		
	}
	private void confirmSync(long lastLocalUpdated,long lastCloudUpdated,String namespace){
		AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Data found online ? Do you want to overwrite local or overwrite cloud");
        builder1.setCancelable(true);
        Confirm c=new Confirm(lastLocalUpdated,lastCloudUpdated,namespace);
        builder1.setPositiveButton("Overwrite local",c);
        builder1.setNegativeButton("Overwrite cloud",c);
        AlertDialog alert1 = builder1.create();
        alert1.show();
	}
	private class Confirm implements DialogInterface.OnClickListener{
		long lastLocalUpdated,lastCloudUpdated;
		String namespace;
		public Confirm (long localUpdate,long cloudUpdate,String namespace){
			this.lastCloudUpdated=cloudUpdate;
			this.lastLocalUpdated=localUpdate;
			this.namespace=namespace;
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(which==DialogInterface.BUTTON_POSITIVE){//overwrite local
				//TODO
				new SyncExec(tL, namespace, lastCloudUpdated, lastLocalUpdated).execute(Option.overwriteLocalOpt);
				dialog.cancel();
				//DeleteExpenseList.this.finish();
			}else if(which==DialogInterface.BUTTON_NEGATIVE){//overwrite cloud
				System.out.println("updating cloud");
				new SyncExec(tL, namespace, lastCloudUpdated, lastLocalUpdated).execute(Option.overwriteCloudOpt);
				//TODO
				dialog.cancel();
			}
		}
	}
	 
}

