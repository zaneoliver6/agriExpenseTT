package uwi.dcit.AgriExpenseTT.dbstruct.structs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.models.RedoLogContract;


public class Redo {

    public static int insertRedoLog(SQLiteDatabase db, DbHelper dbh, String table, int id, String operation){
        ContentValues cv= new ContentValues();
        cv.put(RedoLogContract.RedoLogEntry.REDO_LOG_TABLE, table);
        cv.put(RedoLogContract.RedoLogEntry.REDO_LOG_ROW_ID, id);
        cv.put(RedoLogContract.RedoLogEntry.REDO_LOG_OPERATION, operation);
        db.insert(RedoLogContract.RedoLogEntry.TABLE_NAME, null, cv);
        return DbQuery.getLast(db,dbh, RedoLogContract.RedoLogEntry.TABLE_NAME);
    }

    public static void getRedo(SQLiteDatabase db, DbHelper dbh, ArrayList<Integer> rowIds, ArrayList<Integer> logIds, String operation, String rTable){

        String code="select * from "+ RedoLogContract.RedoLogEntry.TABLE_NAME+" where "
                + RedoLogContract.RedoLogEntry.REDO_LOG_OPERATION+" = '"+operation+"' and "
                + RedoLogContract.RedoLogEntry.REDO_LOG_TABLE+"= '"+rTable+"';";

        Cursor cursor = db.rawQuery(code, null);

        if(cursor.getCount() < 1)
            return;

        while(cursor.moveToNext()){
            int n = cursor.getInt(cursor.getColumnIndex(RedoLogContract.RedoLogEntry.REDO_LOG_ROW_ID));
            rowIds.add(Integer.valueOf(n));
            n=cursor.getInt(cursor.getColumnIndex(RedoLogContract.RedoLogEntry._ID));
            logIds.add(Integer.valueOf(n));
        }
        cursor.close();
    }
}
