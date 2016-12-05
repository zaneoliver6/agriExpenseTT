package uwi.dcit.AgriExpenseTT.dbstruct.structs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.models.ResourceContract;


public class Resource {

    //Used to insert a new Chemical, Crop, Fertilizer, Labourer
    public static int insertResource(SQLiteDatabase db,DbHelper dbh,String type,String name){
        ContentValues cv= new ContentValues();
        cv.put(ResourceContract.ResourceEntry.RESOURCES_NAME,name);
        cv.put(ResourceContract.ResourceEntry.RESOURCES_TYPE,type);
        db.insert(ResourceContract.ResourceEntry.TABLE_NAME, null, cv);
        return DbQuery.getLast(db, dbh, ResourceContract.ResourceEntry.TABLE_NAME);
    }

    public static boolean resourceExistByName(SQLiteDatabase db, DbHelper dbh, String name){
        String code = "SELECT name from " + ResourceContract.ResourceEntry.TABLE_NAME +  " WHERE LOWER(" + ResourceContract.ResourceEntry.RESOURCES_NAME + ") LIKE '%"+name+"%';";

        Cursor cursor = db.rawQuery(code, null);
        if (cursor.getCount() >= 1){
            while(cursor.moveToNext()){
                String res = cursor.getString(cursor.getColumnIndex("name"));
                if (res.equalsIgnoreCase(name))return true;
            }
        }
        return false;
    }

    public static String findResourceName(SQLiteDatabase db, DbHelper dbh, int id){
        String code="select name from "+ ResourceContract.ResourceEntry.TABLE_NAME+" where "+ ResourceContract.ResourceEntry._ID +"="+id+";";
        String res = null;
        Cursor cursor=db.rawQuery(code,null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            res = cursor.getString(cursor.getColumnIndex("name"));
        }
        cursor.close();
        return res;
    }

    public static List<String> getResources(SQLiteDatabase db, DbHelper dbh, String type, ArrayList<String> list){
        String code;
        if(type!=null)
            code="select name from "+ ResourceContract.ResourceEntry.TABLE_NAME+" where "+ ResourceContract.ResourceEntry.RESOURCES_TYPE+"='"+type+"';";
        else
            code="select name from "+ ResourceContract.ResourceEntry.TABLE_NAME;
        Cursor cursor=db.rawQuery(code, null);
        if(cursor.getCount()<1)
            return list;
        while(cursor.moveToNext()){
            list.add(cursor.getString(cursor.getColumnIndex("name")));
        }
        cursor.close();
        return list;
    }

    public static int getNameResourceId(SQLiteDatabase db,DbHelper dbh,String name){
        String code="select "+ ResourceContract.ResourceEntry._ID+" from "+ ResourceContract.ResourceEntry.TABLE_NAME+" where "+ ResourceContract.ResourceEntry.RESOURCES_NAME+"='"+name+"';";
        Cursor cursor=db.rawQuery(code, null);
        if(cursor.getCount()<1)
            return -1;
        cursor.moveToFirst();
        int res = cursor.getInt(cursor.getColumnIndex(ResourceContract.ResourceEntry._ID));
        cursor.close();
        return res;
    }
}
