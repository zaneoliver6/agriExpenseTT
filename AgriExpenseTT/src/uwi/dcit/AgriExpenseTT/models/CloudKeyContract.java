package uwi.dcit.AgriExpenseTT.models;

import android.provider.BaseColumns;

public class CloudKeyContract {
	private static final String TEXT_TYPE = " TEXT";
	private static final String INT_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	public static final String SQL_CREATE_CLOUD_KEY = 
			"CREATE TABLE IF NOT EXISTS " + CloudKeyEntry.TABLE_NAME +"("
			+CloudKeyEntry._ID + " integer primary key autoincrement,"
			+CloudKeyEntry.CLOUD_KEY + TEXT_TYPE + COMMA_SEP
			+CloudKeyEntry.CLOUD_KEY_ROWID + INT_TYPE + COMMA_SEP
			+CloudKeyEntry.CLOUD_KEY_TABLE + TEXT_TYPE +");";
	
	public static final String SQL_DELETE_CLOUD_KEY = "DROP TABLE IF EXISTS "+ CloudKeyEntry.TABLE_NAME;

	public static abstract class CloudKeyEntry implements BaseColumns{
		public static final String TABLE_NAME="cloudKey";
		public static final String CLOUD_KEY="key";
		public static final String CLOUD_KEY_TABLE="ctable";
//		public static final String CLOUD_KEY_ID="id";
		public static final String CLOUD_KEY_ROWID="rowid";
	}
}
