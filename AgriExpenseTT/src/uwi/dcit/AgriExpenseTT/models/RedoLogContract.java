package uwi.dcit.AgriExpenseTT.models;

import android.provider.BaseColumns;

public class RedoLogContract {
	private static final String TEXT_TYPE = " TEXT";
	private static final String INT_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	
	public static final String SQL_CREATE_REDO_LOG = 
			"CREATE TABLE IF NOT EXISTS "+RedoLogEntry.TABLE_NAME+"("
			+RedoLogEntry._ID+" integer primary key autoincrement,"
			+RedoLogEntry.REDO_LOG_TABLE + TEXT_TYPE + COMMA_SEP
			+RedoLogEntry.REDO_LOG_ROW_ID + INT_TYPE + COMMA_SEP 
			+RedoLogEntry.REDO_LOG_OPERATION + TEXT_TYPE +");";
	
	public static final String SQL_DELETE_REDO_LOG = "DROP TABLE IF EXISTS "+ RedoLogEntry.TABLE_NAME;
	
	
	public static abstract class RedoLogEntry implements BaseColumns{
		public static final String TABLE_NAME="redoLog";
		public static final String REDO_LOG_TABLE="redotable";
		public static final String REDO_LOG_ROW_ID="row_id";
		public static final String REDO_LOG_OPERATION="operation";
	}
}
