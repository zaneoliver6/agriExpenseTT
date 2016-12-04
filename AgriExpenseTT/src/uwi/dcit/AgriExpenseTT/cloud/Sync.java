package uwi.dcit.AgriExpenseTT.cloud;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.TransactionLog;
import uwi.dcit.AgriExpenseTT.models.UpdateAccountContract;
import uwi.dcit.agriexpensesvr.accountApi.model.Account;


public class Sync {
	private final String TAG_NAME = "Sync";
	protected SignInManager signin;
	SQLiteDatabase db;
	DbHelper dbh;
	Context context;
	TransactionLog tL;
	private Account cloudAccount;
	public Sync(SQLiteDatabase db, DbHelper dbh,Context context,SignInManager signin){
		this.db=db;
		this.dbh=dbh;
		this.context=context;
		this.signin=signin;
		tL=new TransactionLog(dbh, db,context);
	}

	public void start(String namespace,Account cloudAccount){
        Account localAccount = DbQuery.getUpAcc(db);
		//both exist
		if (localAccount != null && cloudAccount != null) {
			long localUpdateTime = localAccount.getLastUpdated();
			long cloudUpdateTime = cloudAccount.getLastUpdated();
			Log.d(TAG_NAME, "Cloud: " + cloudUpdateTime + " Local: " + localUpdateTime);

			if (localUpdateTime > cloudUpdateTime) {    // local more recent than cloud
				Log.d(TAG_NAME, "Local was more recently updated");

				if (localAccount.getAccount() == null || localAccount.getAccount().equals("")) { //the local does not have an account which means it has never been synced
					Log.d(TAG_NAME, "No Local Account Exists. Assuming user never created one");

					confirmSync(localUpdateTime, cloudUpdateTime, namespace); //there is no localaccount, the user must decide if he's gonna use the datastore or if he's going to overwrite it

				//the local has an account which means it has already at some point synced
				}else{
					/*since it was synced at some point and the local is more updated
					 * just get the logs and update the cloud
					 */

					(new SyncExec(tL, namespace, cloudUpdateTime, localUpdateTime)).execute
							(Option.updateCloudOpt);
				}


			} else if (cloudUpdateTime > localUpdateTime) {//Cloud is more updated
				Log.d(TAG_NAME, "Cloud was more recently updated");
				//the local does not have an account which means it has never been synced
				if(localAccount.getAccount()==null || localAccount.getAccount().equals("")){
					/*there is no local account, the user must decide
					 * if he's gonna use the datastore or if he's going to overwrite it*/
					Log.d(TAG_NAME, "No Local Account Exists. Assuming user never created one");
					confirmSync(localUpdateTime, cloudUpdateTime, namespace);

				//the local has an account which means it has already at some point synced
				}else{
					/*since it was synced at some point and the cloud is more updated
					 * just get the logs and update the local
					 */

					new SyncExec(tL, namespace, cloudUpdateTime, localUpdateTime).execute(Option.updateLocalOpt);
				}

			} else if (cloudUpdateTime == -1 && localUpdateTime == -1) {
				// The point of creating a new account the lastupdated time set to -1
				new SyncExec(tL, namespace, cloudUpdateTime, localUpdateTime).execute(Option.updateLocalOpt);
			} else if (cloudUpdateTime == localUpdateTime) {
				//Do Nothing Since Both Are The Same
				Log.d("CLOUD VS LOCAL", "Cloud:" + cloudUpdateTime + "Local:" + localUpdateTime);

			}
			ContentValues cv=new ContentValues();
			cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_CLOUD_KEY, localAccount.getKeyrep());
		//only local exist
		}else{
			new SyncExec(tL, namespace, 0, 0).execute(Option.createCloudNewOpt);
			//only cloud exist
			//create local account
		}
	}

	private void confirmSync(long lastLocalUpdated, long lastCloudUpdated, String namespace) {
		Log.d(TAG_NAME, "Confirm Sync Command Called Initiated");
		AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
		builder1.setMessage("Data found online ? Do you want to overwrite local or overwrite cloud");
		builder1.setCancelable(true);
		Confirm c = new Confirm(lastLocalUpdated, lastCloudUpdated, namespace);
		builder1.setPositiveButton("Overwrite local", c);
		builder1.setNegativeButton("Overwrite cloud", c);
		AlertDialog alert1 = builder1.create();
		alert1.show();
	}

	public enum Option {
		updateCloudOpt, updateLocalOpt, overwriteCloudOpt, overwriteLocalOpt, createCloudNewOpt
	}

	public class SyncExec extends AsyncTask<Option,Void,Boolean>{
		TransactionLog tL;
		String namespace;
		long cloudUpdateTime, localUpdateTime;
		public SyncExec(TransactionLog tL,String namespace,long cloudUpdate,long localUpdate){
			this.tL=tL;
			this.namespace=namespace;
			this.cloudUpdateTime = cloudUpdate;
			this.localUpdateTime = localUpdate;
		}
		@Override
		protected Boolean doInBackground(Option... params) {
			Option option = params[0];
			ContentValues cv=new ContentValues();
			Boolean success = Boolean.valueOf("true");
			switch(option){
				case updateCloudOpt:
					tL.updateCloud(cloudUpdateTime);
					cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_SIGNEDIN, 1);
					db.update(UpdateAccountContract.UpdateAccountEntry.TABLE_NAME, cv, UpdateAccountContract.UpdateAccountEntry._ID+"=1", null);
					break;

				case updateLocalOpt:
					tL.logsUpdateLocal(namespace, localUpdateTime);
					cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_SIGNEDIN, 1);
					db.update(UpdateAccountContract.UpdateAccountEntry.TABLE_NAME, cv, UpdateAccountContract.UpdateAccountEntry._ID+"=1", null);
					break;

				case overwriteCloudOpt:
					tL.removeNamespace(namespace);
					success=tL.createCloud(namespace);
					break;

				case overwriteLocalOpt:
//					success=tL.pullAllFromCloud(cloudAccount);
					cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_SIGNEDIN, 1);
					db.update(UpdateAccountContract.UpdateAccountEntry.TABLE_NAME, cv, UpdateAccountContract.UpdateAccountEntry._ID+"=1", null);
					break;
				case createCloudNewOpt:
					CloudInterface cloudIF=new CloudInterface(context, db, dbh);
//					cloudIF.insertAccount(namespace,0,signin.getCountry(),signin.getCounty());
					success=tL.createCloud(namespace);
					break;
			}
			return success;
		}
		@Override
		protected void onPostExecute(Boolean result) {
//			signin.signInReturn(result, null);
			super.onPostExecute(result);
		}


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
				
				new SyncExec(tL, namespace, lastCloudUpdated, lastLocalUpdated).execute(Option.overwriteLocalOpt);
				dialog.cancel();
				//DeleteExpenseList.this.finish();
			}else if(which==DialogInterface.BUTTON_NEGATIVE){//overwrite cloud
				new SyncExec(tL, namespace, lastCloudUpdated, lastLocalUpdated).execute(Option.overwriteCloudOpt);
				
				dialog.cancel();
			}
		}
	}
	 
}

