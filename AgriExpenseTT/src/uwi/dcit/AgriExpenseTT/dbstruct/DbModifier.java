package uwi.dcit.AgriExpenseTT.dbstruct;

import android.database.sqlite.SQLiteDatabase;

import uwi.dcit.AgriExpenseTT.models.CloudKeyContract;
import uwi.dcit.AgriExpenseTT.models.CycleContract;
import uwi.dcit.AgriExpenseTT.models.CycleResourceContract.CycleResourceEntry;
import uwi.dcit.AgriExpenseTT.models.RedoLogContract.RedoLogEntry;
import uwi.dcit.AgriExpenseTT.models.ResourceContract;
import uwi.dcit.AgriExpenseTT.models.ResourcePurchaseContract;
import uwi.dcit.AgriExpenseTT.models.TransactionLogContract.TransactionLogEntry;
import uwi.dcit.AgriExpenseTT.models.UpdateAccountContract;

public class DbModifier {
    public static void tableColumnModify(SQLiteDatabase db){
        // Using Transactions to help ensure some level of security
        db.beginTransaction();

        // Backup old data by renaming tables
        createBackup(db);

        // Create table with new structures
        DbOmniPotent.createDb(db);

        // Translate the data from
        translateData(db);

        //delete the existing old tables
        dropBackups(db);

        // mark the transaction as successful
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    private static void createBackup(SQLiteDatabase db){
        db.execSQL("ALTER TABLE " + ResourceContract.ResourceEntry.TABLE_NAME + " RENAME TO " + ResourceContract.ResourceEntry.TABLE_NAME + "_orig");
        db.execSQL("ALTER TABLE " + CycleContract.CycleEntry.TABLE_NAME + " RENAME TO " + CycleContract.CycleEntry.TABLE_NAME + "_orig");
        db.execSQL("ALTER TABLE " + ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME + " RENAME TO " + ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME + "_orig");
        db.execSQL("ALTER TABLE " + CycleResourceEntry.TABLE_NAME + " RENAME TO " + CycleResourceEntry.TABLE_NAME + "_orig");

        db.execSQL("ALTER TABLE " + CloudKeyContract.CloudKeyEntry.TABLE_NAME + " RENAME TO " + CloudKeyContract.CloudKeyEntry.TABLE_NAME + "_orig");
        db.execSQL("ALTER TABLE " + RedoLogEntry.TABLE_NAME + " RENAME TO " + RedoLogEntry.TABLE_NAME + "_orig");
        db.execSQL("ALTER TABLE " + TransactionLogEntry.TABLE_NAME + " RENAME TO " + TransactionLogEntry.TABLE_NAME + "_orig");
        db.execSQL("ALTER TABLE " + UpdateAccountContract.UpdateAccountEntry.TABLE_NAME + " RENAME TO " + UpdateAccountContract.UpdateAccountEntry.TABLE_NAME + "_orig");
    }

    private static void dropBackups(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + ResourceContract.ResourceEntry.TABLE_NAME + "_orig");
        db.execSQL("DROP TABLE IF EXISTS " + CycleContract.CycleEntry.TABLE_NAME + "_orig");
        db.execSQL("DROP TABLE IF EXISTS " + ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME + "_orig");
        db.execSQL("DROP TABLE IF EXISTS " + CycleResourceEntry.TABLE_NAME + "_orig");

        db.execSQL("DROP TABLE IF EXISTS " + CloudKeyContract.CloudKeyEntry.TABLE_NAME + "_orig");
        db.execSQL("DROP TABLE IF EXISTS " + RedoLogEntry.TABLE_NAME + "_orig");
        db.execSQL("DROP TABLE IF EXISTS " + TransactionLogEntry.TABLE_NAME + "_orig");
        db.execSQL("DROP TABLE IF EXISTS " + UpdateAccountContract.UpdateAccountEntry.TABLE_NAME + "_orig");
    }

    private static void translateData(SQLiteDatabase db){
        db.execSQL("INSERT INTO " + ResourceContract.ResourceEntry.TABLE_NAME + "(" + ResourceContract.ResourceEntry._ID  + ", name, type)  SELECT _id, name, type FROM  " + ResourceContract.ResourceEntry.TABLE_NAME + "_orig");
        db.execSQL("INSERT INTO " + CycleContract.CycleEntry.TABLE_NAME + "(" + CycleContract.CycleEntry._ID +", cropId, landType, landAmt, cycledate, tspent, hType, hAmt, costPer, county, cropName ) SELECT _id, cropId, landType, landAmt, cycledate, tspent, hType, hAmt, costPer, county, cropName FROM " + CycleContract.CycleEntry.TABLE_NAME + "_orig");
        db.execSQL("INSERT INTO " + ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME + "(" + ResourcePurchaseContract.ResourcePurchaseEntry._ID  + ", rId, type, quantifier, qty, cost, remaining, date, resource)  SELECT _id, rId, type, quantifier, qty, cost, remaining, date, resource FROM " + ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME + "_orig");
        db.execSQL("INSERT INTO " + CycleResourceEntry.TABLE_NAME + "(" + CycleResourceEntry._ID  + ", pId, type, qty, quantifier, cycleId, useCost) SELECT _id, pId, type, qty, quantifier, cycleId, useCost FROM  " + CycleResourceEntry.TABLE_NAME + "_orig");

        db.execSQL("INSERT INTO " + CloudKeyContract.CloudKeyEntry.TABLE_NAME + "(" + CloudKeyContract.CloudKeyEntry._ID  + ", key, ctable, rowid ) SELECT _id, key, ctable, rowid  FROM " + CloudKeyContract.CloudKeyEntry.TABLE_NAME + "_orig");
        db.execSQL("INSERT INTO " + RedoLogEntry.TABLE_NAME + "(" + RedoLogEntry._ID  + ", redotable, row_id, operation)  SELECT _id, redotable, row_id, operation FROM " + RedoLogEntry.TABLE_NAME + "_orig");
        db.execSQL("INSERT INTO " + TransactionLogEntry.TABLE_NAME + "(" + TransactionLogEntry._ID  + ", transtable, rowid, operation, transtime)  SELECT _id, transtable, rowid, operation, transtime FROM  " + TransactionLogEntry.TABLE_NAME + "_orig");
        db.execSQL("INSERT INTO " + UpdateAccountContract.UpdateAccountEntry.TABLE_NAME + "(" + UpdateAccountContract.UpdateAccountEntry._ID  + ", acc, county, address, lastUpdated, signedIn, cloudKey)  SELECT _id, acc, county, address, lastUpdated, signedIn, cloudKey FROM " + UpdateAccountContract.UpdateAccountEntry.TABLE_NAME + "_orig");

    }
}
