package uwi.dcit.AgriExpenseTT.dbstruct.structs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import uwi.dcit.AgriExpenseTT.models.UpdateAccountContract;
import uwi.dcit.agriexpensesvr.upAccApi.model.UpAcc;

public class UpAccount {
    public static void insertUpAcc(SQLiteDatabase db, UpAcc acc){
        ContentValues cv=new ContentValues();
        cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_CLOUD_KEY, acc.getKeyrep());
        cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_ACC,acc.getAcc());
        cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_UPDATED, acc.getLastUpdated());
        cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_SIGNEDIN,acc.getSignedIn());
        db.insert(UpdateAccountContract.UpdateAccountEntry.TABLE_NAME, null, cv);
    }

    public static void updateAccount(SQLiteDatabase db,long time){
        UpAcc acc=getUpAcc(db);
        if(acc.getLastUpdated()<=time){
            ContentValues cv=new ContentValues();
            cv.put(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_UPDATED, time);
            db.update(UpdateAccountContract.UpdateAccountEntry.TABLE_NAME, cv, UpdateAccountContract.UpdateAccountEntry._ID+"=1", null);
        }
    }

    public static UpAcc getUpAcc(SQLiteDatabase db){
        String code="select * from " + UpdateAccountContract.UpdateAccountEntry.TABLE_NAME;
        Cursor cursor=db.rawQuery(code, null);
        if(cursor.getCount() < 1)return null;  	// No records exist so return null

        cursor.moveToFirst();					// Only one record should exist (TODO If only one record exist do we need an entire table?)
        UpAcc acc = new UpAcc();

        acc.setKeyrep(cursor.getString(cursor.getColumnIndex(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_CLOUD_KEY)));
        acc.setAcc(cursor.getString(cursor.getColumnIndex(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_ACC)));
        acc.setLastUpdated(cursor.getLong(cursor.getColumnIndex(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_UPDATED)));
        acc.setSignedIn(cursor.getInt(cursor.getColumnIndex(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_SIGNEDIN)));
        acc.setCounty(cursor.getString(cursor.getColumnIndex(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_COUNTY)));
        acc.setAddress(cursor.getString(cursor.getColumnIndex(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_ADDRESS)));

        cursor.close();
        return acc;
    }
}
