package uwi.dcit.AgriExpenseTT.models;

import android.provider.BaseColumns;

public class TransactionLogContract {
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String INT_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	
	public static final String SQL_CREATE_TRANSACTION_LOG =
		"CREATE TABLE IF NOT EXISTS "+ TransactionLogEntry.TABLE_NAME +"("
		+TransactionLogEntry._ID+" integer primary key autoincrement,"
		+TransactionLogEntry.TRANSACTION_LOG_TABLE + TEXT_TYPE + COMMA_SEP
		+TransactionLogEntry.TRANSACTION_LOG_ROWID + INT_TYPE + COMMA_SEP 
		+TransactionLogEntry.TRANSACTION_LOG_OPERATION + TEXT_TYPE + COMMA_SEP
		+TransactionLogEntry.TRANSACTION_LOG_TRANSTIME  + INT_TYPE + ");";
	
	public static final String SQL_DELETE_TRANSACTION_LOG = "DROP TABLE IF EXISTS "+ TransactionLogEntry.TABLE_NAME;

	public static abstract class TransactionLogEntry implements BaseColumns{
		public static final String TABLE_NAME="translog";
		public static final String TRANSACTION_LOG_TABLE="transtable";//the table the update was on
		public static final String TRANSACTION_LOG_ROWID="rowid";
		public static final String TRANSACTION_LOG_OPERATION="operation";
		public static final String TRANSACTION_LOG_TRANSTIME="transtime";
	}
}
