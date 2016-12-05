package uwi.dcit.AgriExpenseTT.dbstruct.structs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.models.CloudKeyContract;


public class Cloud {

    public static void insertCloudKey(SQLiteDatabase db, DbHelper dbh, String table, String k, int id){
        ContentValues cv = new ContentValues();
        cv.put(CloudKeyContract.CloudKeyEntry.CLOUD_KEY_TABLE, table);
        cv.put(CloudKeyContract.CloudKeyEntry.CLOUD_KEY, k);
        cv.put(CloudKeyContract.CloudKeyEntry.CLOUD_KEY_ROWID, id);
        db.insert(CloudKeyContract.CloudKeyEntry.TABLE_NAME, null, cv);
    }

    public static int getCloudKeyId(SQLiteDatabase db,DbHelper dbh,String table,int id){String code="select * from "+ CloudKeyContract.CloudKeyEntry.TABLE_NAME+" where "
            + CloudKeyContract.CloudKeyEntry.CLOUD_KEY_TABLE+"='"+table+"' and "
            + CloudKeyContract.CloudKeyEntry.CLOUD_KEY_ROWID+"="+id+";";
        Cursor cursor=db.rawQuery(code, null);
        if(cursor.getCount()<1)
            return -1;
        cursor.moveToFirst();
        int res = cursor.getInt(cursor.getColumnIndex(CloudKeyContract.CloudKeyEntry._ID));
        cursor.close();
        return res;
    }

    public static String getKey(SQLiteDatabase db,DbHelper dbh,String table,int id){
        String code="select * from "+ CloudKeyContract.CloudKeyEntry.TABLE_NAME+" where "
                + CloudKeyContract.CloudKeyEntry.CLOUD_KEY_TABLE+"='"+table+"' and "
                + CloudKeyContract.CloudKeyEntry.CLOUD_KEY_ROWID+"="+id+";";
        Cursor cursor = db.rawQuery(code, null);
        if(cursor.getCount()<1){
            return null;
        }
        cursor.moveToFirst();
        String res =  cursor.getString(cursor.getColumnIndex(CloudKeyContract.CloudKeyEntry.CLOUD_KEY));
        cursor.close();
        return res;
    }
}
