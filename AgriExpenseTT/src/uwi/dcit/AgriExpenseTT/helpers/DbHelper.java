package uwi.dcit.AgriExpenseTT.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import uwi.dcit.AgriExpenseTT.helpers.DefaultDataManager;
import uwi.dcit.AgriExpenseTT.lists.*;
import uwi.dcit.AgriExpenseTT.models.CloudKeyContract;
import uwi.dcit.AgriExpenseTT.models.CountryContract;
import uwi.dcit.AgriExpenseTT.models.CountyContract;
import uwi.dcit.AgriExpenseTT.models.CycleContract;
import uwi.dcit.AgriExpenseTT.models.CycleResourceContract;
import uwi.dcit.AgriExpenseTT.models.CycleResourceContract.CycleResourceEntry;
import uwi.dcit.AgriExpenseTT.models.LabourContract;
import uwi.dcit.AgriExpenseTT.models.RedoLogContract;
import uwi.dcit.AgriExpenseTT.models.RedoLogContract.RedoLogEntry;
import uwi.dcit.AgriExpenseTT.models.ResourceContract;
import uwi.dcit.AgriExpenseTT.models.ResourcePurchaseContract;
import uwi.dcit.AgriExpenseTT.models.TransactionLogContract;
import uwi.dcit.AgriExpenseTT.models.TransactionLogContract.TransactionLogEntry;
import uwi.dcit.AgriExpenseTT.models.UpdateAccountContract;
import uwi.dcit.agriexpensesvr.upAccApi.model.UpAcc;

public class DbHelper extends SQLiteOpenHelper{

	public static final int VERSION = 172;
	public static final String DATABASE_NAME="agriDb";
	public static final String TAG_NAME = "AgriExpenseDBHelper";
	public Context ctx;
	
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null,VERSION);
		this.ctx = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
        Log.i(TAG_NAME, "Creating AgriExpense DB for first time");
		createDb(db);

		DefaultDataManager dfm = new DefaultDataManager(db,this);
		DefaultDataHelper.populate(dfm);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		DefaultDataManager dfm = new DefaultDataManager(db,this);
		//We will be required to implement upgrade functionality that is specific to each version of the upgrade
		Log.i(TAG_NAME, "Upgrade detected. Old version: "+ oldVersion + " New version: "+newVersion);


        // TODO Add logic to place the crop name as the cycle name for existing cycle records
        // TODO Add Date to CycleResource and place timestamp automatically for values

		if (oldVersion < 170){
			Log.d(TAG_NAME, "version too old to support Removing all tables so far and restart");
			this.dropTables(db);
			this.onCreate(db);
		}
        if (oldVersion  <= 171 && !columnExists(db, CycleContract.CycleEntry.TABLE_NAME, CycleContract.CycleEntry._ID)) {
			
			Log.d(TAG_NAME, "Running Update of table structure");
			this.tableColumnModify(db);
			
			Log.d(TAG_NAME, "Running installation of countries");
			this.createCountries(db); // Create if not exist so if previously created this will do nothing
			DefaultDataHelper.insertDefaultCountries(db);

			
			Log.d(TAG_NAME, "Running installation of counties");
			this.createCounties(db);
			DefaultDataHelper.insertDefaultCounties(db);
			
			Log.d(TAG_NAME, "Running upgrade of crop/plating material lists");
			DefaultDataHelper.updateCropList(dfm);
		}
        if (oldVersion < 172){
            db.beginTransaction();

            db.execSQL("ALTER TABLE " + CycleContract.CycleEntry.TABLE_NAME + " ADD COLUMN "+ CycleContract.CycleEntry.CROPCYCLE_NAME + " TEXT");
            // Place the resource name as the default name of the cycle
            updateCycleCropName(db);




            // Place the update date as the date for the previously created resources purchased
            updatePurchaseRecs(db);

            // Add Date Column to CycleResource
            db.execSQL("ALTER TABLE " + CycleResourceContract.CycleResourceEntry.TABLE_NAME + " ADD COLUMN "+ CycleResourceContract.CycleResourceEntry.CYCLE_DATE_USED +  " TIMESTAMP");
            updateCycleResource(db);

            db.setTransactionSuccessful();
            db.endTransaction();
        }
		
		Log.d(TAG_NAME, "Completed upgrading the database to version " + VERSION);

//        db.close();
	}

    private void tableColumnModify(SQLiteDatabase db){
		// Using Transactions to help ensure some level of security
		db.beginTransaction();
		
		// Backup old data by renaming tables
		this.createBackup(db);
		
		// Create table with new structures
		this.createDb(db);
		
		// Translate the data from 
		this.translateData(db);

		//delete the existing old tables
		this.dropBackups(db);
		
		// mark the transaction as successful
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	private void createBackup(SQLiteDatabase db){
		db.execSQL("ALTER TABLE " + ResourceContract.ResourceEntry.TABLE_NAME + " RENAME TO " + ResourceContract.ResourceEntry.TABLE_NAME + "_orig");
		db.execSQL("ALTER TABLE " + CycleContract.CycleEntry.TABLE_NAME + " RENAME TO " + CycleContract.CycleEntry.TABLE_NAME + "_orig");
		db.execSQL("ALTER TABLE " + ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME + " RENAME TO " + ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME + "_orig");
		db.execSQL("ALTER TABLE " + CycleResourceEntry.TABLE_NAME + " RENAME TO " + CycleResourceEntry.TABLE_NAME + "_orig");
		// db.execSQL("ALTER TABLE " + LabourEntry.TABLE_NAME + "RENAME TO " + LabourEntry.TABLE_NAME + "_orig");
		
		db.execSQL("ALTER TABLE " + CloudKeyContract.CloudKeyEntry.TABLE_NAME + " RENAME TO " + CloudKeyContract.CloudKeyEntry.TABLE_NAME + "_orig");
		db.execSQL("ALTER TABLE " + RedoLogEntry.TABLE_NAME + " RENAME TO " + RedoLogEntry.TABLE_NAME + "_orig");
		db.execSQL("ALTER TABLE " + TransactionLogEntry.TABLE_NAME + " RENAME TO " + TransactionLogEntry.TABLE_NAME + "_orig");
		db.execSQL("ALTER TABLE " + UpdateAccountContract.UpdateAccountEntry.TABLE_NAME + " RENAME TO " + UpdateAccountContract.UpdateAccountEntry.TABLE_NAME + "_orig");

		// db.execSQL("ALTER TABLE " + CountryEntry.TABLE_NAME + " RENAME TO " + CountryEntry.TABLE_NAME + "_orig");
		// db.execSQL("ALTER TABLE " + CountyEntry.TABLE_NAME + " RENAME TO " + CountyEntry.TABLE_NAME + "_orig");
	}
	
	private void translateData(SQLiteDatabase db){
		db.execSQL("INSERT INTO " + ResourceContract.ResourceEntry.TABLE_NAME + "(" + ResourceContract.ResourceEntry._ID  + ", name, type)  SELECT _id, name, type FROM  " + ResourceContract.ResourceEntry.TABLE_NAME + "_orig");
		db.execSQL("INSERT INTO " + CycleContract.CycleEntry.TABLE_NAME + "(" + CycleContract.CycleEntry._ID +", cropId, landType, landAmt, cycledate, tspent, hType, hAmt, costPer, county, cropName ) SELECT _id, cropId, landType, landAmt, cycledate, tspent, hType, hAmt, costPer, county, cropName FROM " + CycleContract.CycleEntry.TABLE_NAME + "_orig");
		db.execSQL("INSERT INTO " + ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME + "(" + ResourcePurchaseContract.ResourcePurchaseEntry._ID  + ", rId, type, quantifier, qty, cost, remaining, date, resource)  SELECT _id, rId, type, quantifier, qty, cost, remaining, date, resource FROM " + ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME + "_orig");
		db.execSQL("INSERT INTO " + CycleResourceEntry.TABLE_NAME + "(" + CycleResourceEntry._ID  + ", pId, type, qty, quantifier, cycleId, useCost) SELECT _id, pId, type, qty, quantifier, cycleId, useCost FROM  " + CycleResourceEntry.TABLE_NAME + "_orig");
		// db.execSQL("INSERT INTO " + LabourEntry.TABLE_NAME + "(" + LabourEntry._ID  + ", labour, name) SELECT id, labour, name FROM  " + LabourEntry.TABLE_NAME + "_orig");

		db.execSQL("INSERT INTO " + CloudKeyContract.CloudKeyEntry.TABLE_NAME + "(" + CloudKeyContract.CloudKeyEntry._ID  + ", key, ctable, rowid ) SELECT _id, key, ctable, rowid  FROM " + CloudKeyContract.CloudKeyEntry.TABLE_NAME + "_orig");
		db.execSQL("INSERT INTO " + RedoLogEntry.TABLE_NAME + "(" + RedoLogEntry._ID  + ", redotable, row_id, operation)  SELECT _id, redotable, row_id, operation FROM " + RedoLogEntry.TABLE_NAME + "_orig");
		db.execSQL("INSERT INTO " + TransactionLogEntry.TABLE_NAME + "(" + TransactionLogEntry._ID  + ", transtable, rowid, operation, transtime)  SELECT _id, transtable, rowid, operation, transtime FROM  " + TransactionLogEntry.TABLE_NAME + "_orig");
		db.execSQL("INSERT INTO " + UpdateAccountContract.UpdateAccountEntry.TABLE_NAME + "(" + UpdateAccountContract.UpdateAccountEntry._ID  + ", acc, county, address, lastUpdated, signedIn, cloudKey)  SELECT _id, acc, county, address, lastUpdated, signedIn, cloudKey FROM " + UpdateAccountContract.UpdateAccountEntry.TABLE_NAME + "_orig");
	
		// db.execSQL("INSERT INTO " + CountryEntry.TABLE_NAME + "(" + CountryEntry._ID  + ", country, subdividion) SELECT (id, key, ctable, rowid ) FROM  " + CountryEntry.TABLE_NAME + "_orig");
		// db.execSQL("INSERT INTO " + CountyEntry.TABLE_NAME + "(" + CountyEntry._ID  + ", county, country) SELECT (id, county, country)  FROM  " + CountyEntry.TABLE_NAME + "_orig");
	}

	private void dropBackups(SQLiteDatabase db){
		db.execSQL("DROP TABLE IF EXISTS " + ResourceContract.ResourceEntry.TABLE_NAME + "_orig");
		db.execSQL("DROP TABLE IF EXISTS " + CycleContract.CycleEntry.TABLE_NAME + "_orig");
		db.execSQL("DROP TABLE IF EXISTS " + ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME + "_orig");
		db.execSQL("DROP TABLE IF EXISTS " + CycleResourceEntry.TABLE_NAME + "_orig");
		// db.execSQL("DROP TABLE IF EXISTS " + LabourEntry.TABLE_NAME + "_orig");

		db.execSQL("DROP TABLE IF EXISTS " + CloudKeyContract.CloudKeyEntry.TABLE_NAME + "_orig");
		db.execSQL("DROP TABLE IF EXISTS " + RedoLogEntry.TABLE_NAME + "_orig");
		db.execSQL("DROP TABLE IF EXISTS " + TransactionLogEntry.TABLE_NAME + "_orig");
		db.execSQL("DROP TABLE IF EXISTS " + UpdateAccountContract.UpdateAccountEntry.TABLE_NAME + "_orig");
	}
	
	private void createDb(SQLiteDatabase db) {
		createResources(db);
		createCropCycle(db);
		createResourcePurchases(db);
		createResourceUse(db);
		createLabour(db);

		createCloudKeys(db);
		createRedoLog(db);
		createTransactionLog(db);
		createUpdateAccount(db);
		
		createCountries(db);
		createCounties(db);
	}
	
	public void dropTables(SQLiteDatabase db) {
		db.beginTransaction();

		db.execSQL(CycleResourceContract.SQL_DELETE_CYCLE_RESOURCE);
		db.execSQL(ResourcePurchaseContract.SQL_DELETE_RESOURCE_PURCHASE);
		db.execSQL(CycleContract.SQL_DELETE_CYCLE);
		db.execSQL(ResourceContract.SQL_DELETE_RESOURCE);
		db.execSQL(CloudKeyContract.SQL_DELETE_CLOUD_KEY);
		db.execSQL(RedoLogContract.SQL_DELETE_REDO_LOG);
		db.execSQL(TransactionLogContract.SQL_DELETE_TRANSACTION_LOG);
		db.execSQL(UpdateAccountContract.SQL_DELETE_UPDATE_ACCOUNT);
		db.execSQL(CountryContract.SQL_DELETE_COUNTRIES);
		db.execSQL(CountyContract.SQL_DELETE_COUNTIES);

		db.setTransactionSuccessful();
		db.endTransaction();
	}

	private void createUpdateAccount(SQLiteDatabase db){
		db.execSQL(UpdateAccountContract.SQL_CREATE_UPDATE_ACCOUNT);
	}
	
	private void createCloudKeys(SQLiteDatabase db) {
		db.execSQL(CloudKeyContract.SQL_CREATE_CLOUD_KEY);
	}
	
	private void createCropCycle(SQLiteDatabase db) {
		db.execSQL(CycleContract.SQL_CREATE_CYCLE);
	} 
	
	private void createResources(SQLiteDatabase db) {
		db.execSQL(ResourceContract.SQL_CREATE_RESOURCE);
	}
	
	public void createTransactionLog(SQLiteDatabase db){
		db.execSQL(TransactionLogContract.SQL_CREATE_TRANSACTION_LOG);
	}
	
	private void createResourcePurchases(SQLiteDatabase db) {
		db.execSQL(ResourcePurchaseContract.SQL_CREATE_RESOURCE_PURCHASE);
	}
	
	private void createResourceUse(SQLiteDatabase db) {
		db.execSQL(CycleResourceContract.SQL_CREATE_CYCLE_RESOURCE);
	}
	
	public void createRedoLog(SQLiteDatabase db){
		db.execSQL(RedoLogContract.SQL_CREATE_REDO_LOG);
	}
	
	public void createCountries(SQLiteDatabase db){
		db.execSQL(CountryContract.SQL_CREATE_COUNTRIES);
	}
	
	public void createCounties(SQLiteDatabase db){
		db.execSQL(CountyContract.SQL_CREATE_COUNTIES);
	}

	public void createLabour(SQLiteDatabase db){
		db.execSQL(LabourContract.SQL_CREATE_LABOUR);
	}


    private void updatePurchaseRecs(SQLiteDatabase  db){
        Cursor cursor = db.rawQuery("SELECT * FROM " + ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, null);
        // Update Existing Dates to the current date
        while(cursor.moveToNext()){
            ContentValues cv = new ContentValues();
            cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_DATE,  DateFormatHelper.getDateUnix(new Date()) );
        }
        cursor.close();
    }

    private void updateCycleCropName(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + CycleContract.CycleEntry.TABLE_NAME, null);
        while(cursor.moveToNext()){
            ContentValues cv = new ContentValues();
            cv.put(CycleContract.CycleEntry.CROPCYCLE_NAME, cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_RESOURCE));
        }
        cursor.close();
    }

    private void updateCycleResource(SQLiteDatabase db){
        Cursor cursor = db.rawQuery("SELECT * FROM " + CycleResourceContract.CycleResourceEntry.TABLE_NAME , null);
        while(cursor.moveToNext()){
            ContentValues cv = new ContentValues();
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cv.put(CycleResourceContract.CycleResourceEntry.CYCLE_DATE_USED, cal.getTimeInMillis());
        }
        cursor.close();
    }

    private boolean columnExists(SQLiteDatabase db, String tableName, String columnName){
        Cursor cursor = db.rawQuery("PRAGMA table_info("+tableName+");", null);
        while (cursor.moveToNext()){
            if (cursor.getString(cursor.getColumnIndex("name")).equalsIgnoreCase(columnName)){
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }
}
