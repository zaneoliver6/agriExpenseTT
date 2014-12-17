package uwi.dcit.AgriExpenseTT.models;

import uwi.dcit.AgriExpenseTT.models.ResourceContract.ResourceEntry;
import android.provider.BaseColumns;

public class ResourcePurchaseContract {
	private static final String TEXT_TYPE = " TEXT";
	private static final String INT_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	
	public static final String SQL_CREATE_RESOURCE_PURCHASE =
			"CREATE TABLE IF NOT EXISTS "+ResourcePurchaseEntry.TABLE_NAME+"("
			+ResourcePurchaseEntry._ID+" integer primary key autoincrement,"
			+ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID+" integer,"
			+ResourcePurchaseEntry.RESOURCE_PURCHASE_TYPE+" text,"
			+ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER+" text,"
			+ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY+" integer,"
			+ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING+" integer,"
			+ResourcePurchaseEntry.RESOURCE_PURCHASE_COST+" real,"
			+ResourcePurchaseEntry.RESOURCE_PURCHASE_DATE+" timestamp,"
			+ResourcePurchaseEntry.RESOURCE_PURCHASE_RESOURCE+" text,"
			+"foreign key("+ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID+") references "+ ResourceEntry.TABLE_NAME + "(" + ResourceEntry._ID + "));";
	
	public static final String SQL_DELETE_RESOURCE_PURCHASE = 
			"DROP TABLE IF EXISTS " + ResourcePurchaseEntry.TABLE_NAME;

	public static abstract class ResourcePurchaseEntry implements BaseColumns{
		public static final String TABLE_NAME = "resPurchases";
//		public static final String RESOURCE_PURCHASE_ID = "id";
		public static final String RESOURCE_PURCHASE_RESID = "rId";
		public static final String RESOURCE_PURCHASE_TYPE = "type";
		public static final String RESOURCE_PURCHASE_QUANTIFIER = "quantifier";
		public static final String RESOURCE_PURCHASE_QTY = "qty";
		public static final String RESOURCE_PURCHASE_COST = "cost";
		public static final String RESOURCE_PURCHASE_REMAINING = "remaining";
		public static final String RESOURCE_PURCHASE_DATE = "date";
		public static final String RESOURCE_PURCHASE_RESOURCE = "resource";
	}
}
