  package uwi.dcit.AgriExpenseTT.models;

import android.provider.BaseColumns;

public class UpdateAccountContract {
	private static final String TEXT_TYPE = " TEXT";
	private static final String INT_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	public static final String SQL_CREATE_UPDATE_ACCOUNT =
			"CREATE TABLE IF NOT EXISTS "+UpdateAccountEntry.TABLE_NAME+"("
			+UpdateAccountEntry._ID+" integer primary key autoincrement,"
			+UpdateAccountEntry.UPDATE_ACCOUNT_ACC + TEXT_TYPE + COMMA_SEP
			+UpdateAccountEntry.UPDATE_ACCOUNT_COUNTY + TEXT_TYPE + COMMA_SEP 
			+UpdateAccountEntry.UPDATE_ACCOUNT_COUNTRY + TEXT_TYPE + COMMA_SEP 
			+UpdateAccountEntry.UPDATE_ACCOUNT_ADDRESS + TEXT_TYPE + COMMA_SEP 
			+UpdateAccountEntry.UPDATE_ACCOUNT_UPDATED + INT_TYPE + COMMA_SEP 
			+UpdateAccountEntry.UPDATE_ACCOUNT_SIGNEDIN + INT_TYPE + COMMA_SEP 
			+UpdateAccountEntry.UPDATE_ACCOUNT_CLOUD_KEY + TEXT_TYPE +");";
	
	public static final String SQL_DELETE_UPDATE_ACCOUNT = 
			"DROP TABLE IF EXISTS "+ UpdateAccountEntry.TABLE_NAME;
	
	public static final String SQL_UPDATE_UPDATE_ACCOUNT_TABLE = "ALTER TABLE "
			+ UpdateAccountEntry.TABLE_NAME 
			+ "ADD " + UpdateAccountEntry.UPDATE_ACCOUNT_COUNTRY +TEXT_TYPE +";";

	public static abstract class UpdateAccountEntry implements BaseColumns{
		public static final String TABLE_NAME = "updateacc";
		public static final String UPDATE_ACCOUNT_ACC = "acc";
		public static final String UPDATE_ACCOUNT_COUNTY = "county";
		public static final String UPDATE_ACCOUNT_COUNTRY="country";
		public static final String UPDATE_ACCOUNT_ADDRESS = "address";
		public static final String UPDATE_ACCOUNT_UPDATED = "lastUpdated";
		public static final String UPDATE_ACCOUNT_SIGNEDIN = "signedIn";
		public static final String UPDATE_ACCOUNT_CLOUD_KEY="cloudKey";
		
	}
}
