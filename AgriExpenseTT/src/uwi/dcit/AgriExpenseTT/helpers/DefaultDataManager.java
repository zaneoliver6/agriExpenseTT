package uwi.dcit.AgriExpenseTT.helpers;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;

import uwi.dcit.AgriExpenseTT.dbstruct.structs.Resource;


/**
 * Created by rcjasmin on 12/3/2016.
 */

public class DefaultDataManager {

    private ArrayList<String> plist;
    private SQLiteDatabase db;
    private String category;
    private DbHelper dbh;


    public DefaultDataManager(SQLiteDatabase db, DbHelper dbh) {
        this.db = db;
        this.dbh = dbh;
    }

    public ArrayList<String> getPlist() {
        return this.plist;
    }

    public void setPlist(ArrayList<String> plist) {
        this.plist = plist;
    }

    public SQLiteDatabase getDB() {
        return this.db;
    }

    public void setDataCategory(String dataCategory) {
        this.category = dataCategory;
    }

    public void setListAndCategory(ArrayList<String> plist, String dataCategory) {
        this.setPlist(plist);
        this.setDataCategory(dataCategory);
    }

    public void insertListToDB() {
        Iterator<String> iterator = this.plist.iterator();
        while (iterator.hasNext()) {
            Resource.insertResource(this.db, this.dbh, this.category, iterator.next());
        }
    }
}
