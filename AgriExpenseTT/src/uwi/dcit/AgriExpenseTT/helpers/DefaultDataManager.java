package uwi.dcit.AgriExpenseTT.helpers;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by rcjasmin on 12/3/2016.
 */

public class DefaultDataManager {

    private ArrayList<String> plist;
    private SQLiteDatabase db;
    private String dataCategory;
    private DbHelper dbh;

    public DefaultDataManager() {
    }

    public DefaultDataManager(ArrayList<String> plist, SQLiteDatabase db, String dataCategory, DbHelper dbh) {
        this.plist = plist;
        this.db = db;
        this.dbh = dbh;
        this.dataCategory = dataCategory;
    }

    public DefaultDataManager(SQLiteDatabase db, DbHelper dbh) {
        this.db = db;
        this.dbh = dbh;
    }
    ArrayList<String> getPlist() {
        return this.plist;
    }

    void setPlist(ArrayList<String> plist) {
        this.plist = plist;
    }

    public void setDB(SQLiteDatabase db) {
        this.db = db;
    }

    public SQLiteDatabase getDB() {
        return this.db;
    }

    void setDataCategory(String dataCategory) {
        this.dataCategory = dataCategory;
    }

    void setDataCategory(DbHelper dbh) {
        this.dbh = dbh;
    }

    void insertListToDB() {
        Iterator<String> iterator = this.plist.iterator();
        while (iterator.hasNext()) {
            DbQuery.insertResource(this.db, this.dbh, this.dataCategory, iterator.next());
        }
    }
}
