package uwi.dcit.AgriExpenseTT.models;

import android.provider.BaseColumns;

public class CycleResourceContract {
	private static final String TEXT_TYPE = " TEXT";
	private static final String INT_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	public static final String SQL_CREATE_CYCLE_RESOURCE = "CREATE TABLE IF NOT EXISTS "
			+CycleResourceEntry.TABLE_NAME + "("
			+CycleResourceEntry._ID+" integer primary key autoincrement,"
			+CycleResourceEntry.CYCLE_RESOURCE_CYCLEID+" integer,"
			+CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID+" integer,"
			+CycleResourceEntry.CYCLE_RESOURCE_TYPE+" text,"
			+CycleResourceEntry.CYCLE_RESOURCE_QTY+" integer,"
			+CycleResourceEntry.CYCLE_RESOURCE_USECOST+" real,"
			+CycleResourceEntry.CYCLE_RESOURCE_QUANTIFIER+" text,"
			+"foreign key("+CycleResourceEntry.CYCLE_RESOURCE_CYCLEID+") references "+ CycleContract.CycleEntry.TABLE_NAME+"("+ CycleContract.CycleEntry._ID+"),"
			+"foreign key("+CycleResourceEntry.CYCLE_RESOURCE_PURCHASE_ID+") references "+ ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME+"("+ ResourcePurchaseContract.ResourcePurchaseEntry._ID+"))";
		
	
	public static final String SQL_DELETE_CYCLE_RESOURCE =
			"DROP TABLE IF EXISTS "+ CycleResourceEntry.TABLE_NAME;
	
	public static abstract class CycleResourceEntry implements BaseColumns{
		public static final String TABLE_NAME="cycleResources";
//		public static final String CYCLE_RESOURCE_ID="id";
		public static final String CYCLE_RESOURCE_PURCHASE_ID="pId";
		public static final String CYCLE_RESOURCE_TYPE="type";
		public static final String CYCLE_RESOURCE_QTY="qty";
		public static final String CYCLE_RESOURCE_QUANTIFIER="quantifier";
		public static final String CYCLE_RESOURCE_CYCLEID="cycleId";
		public static final String CYCLE_RESOURCE_USECOST="useCost";
	}
}
