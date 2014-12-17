package uwi.dcit.AgriExpenseTT.models;

import android.provider.BaseColumns;

public class ResourceContract {
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	public static final String SQL_CREATE_RESOURCE = "CREATE TABLE IF NOT EXISTS "+ResourceEntry.TABLE_NAME+"("
			+ResourceEntry._ID+" integer primary key autoincrement,"
			+ResourceEntry.RESOURCES_NAME + TEXT_TYPE + COMMA_SEP
			+ResourceEntry.RESOURCES_TYPE + TEXT_TYPE +");";
	
	public static final String SQL_DELETE_RESOURCE = "DROP TABLE IF EXISTS "+ ResourceEntry.TABLE_NAME;
	

	public static abstract class ResourceEntry implements BaseColumns{
		public static final String TABLE_NAME = "resources";
		public static final String RESOURCES_NAME = "name";
		public static final String RESOURCES_TYPE = "type";
	}
}
