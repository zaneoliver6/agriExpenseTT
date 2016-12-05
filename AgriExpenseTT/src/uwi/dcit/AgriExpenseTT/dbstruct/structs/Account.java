package uwi.dcit.AgriExpenseTT.dbstruct.structs;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import uwi.dcit.AgriExpenseTT.models.UpdateAccountContract;

/**
 * Created by gh0st on 04/12/2016.
 */

public class Account {

    public static String getAccount(SQLiteDatabase db){
        String code="select "+ UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_ACC+" from "+
                UpdateAccountContract.UpdateAccountEntry.TABLE_NAME;
        Cursor cursor=db.rawQuery(code, null);
        cursor.moveToFirst();
        String res = cursor.getString(cursor.getColumnIndex(UpdateAccountContract.UpdateAccountEntry.UPDATE_ACCOUNT_ACC));
        cursor.close();
        return res;
    }
}
