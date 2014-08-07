package helper;

import com.example.agriexpensett.upaccendpoint.model.UpAcc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{
	//main table crop cycle
	public static final String TABLE_CROPCYLE="cropCycle";
	public static final String CROPCYCLE_ID="id";
	public static final String CROPCYCLE_CROPID="cropId";
	public static final String CROPCYCLE_LAND_TYPE="landType";
	public static final String CROPCYCLE_LAND_AMOUNT="landAmt";
	public static final String CROPCYCLE_DATE="cycledate";
	public static final String CROPCYCLE_TOTALSPENT="tspent";
	public static final String CROPCYCLE_HARVEST_TYPE="hType";
	public static final String CROPCYCLE_HARVEST_AMT="hAmt";
	public static final String CROPCYCLE_COSTPER="costPer";
	public static final String CROPCYCLE_COUNTY="county";
	public static final String CROPCYCLE_RESOURCE="cropName";
	//resource 
	public static final String TABLE_RESOURCES="resources";
	public static final String RESOURCES_ID="id";
	public static final String RESOURCES_NAME="name";
	public static final String RESOURCES_TYPE="type";
	//resource purchases
	public static final String TABLE_RESOURCE_PURCHASES="resPurchases";
	public static final String RESOURCE_PURCHASE_ID="id";
	public static final String RESOURCE_PURCHASE_RESID="rId";
	public static final String RESOURCE_PURCHASE_TYPE="type";
	public static final String RESOURCE_PURCHASE_QUANTIFIER="quantifier";
	public static final String RESOURCE_PURCHASE_QTY="qty";
	public static final String RESOURCE_PURCHASE_COST="cost";
	public static final String RESOURCE_PURCHASE_REMAINING="remaining";
	public static final String RESOURCE_PURCHASE_DATE="date";
	public static final String RESOURCE_PURCHASE_RESOURCE="resource";
	//Cycle resource use
	public static final String TABLE_CYCLE_RESOURCES="cycleResources";
	public static final String CYCLE_RESOURCE_ID="id";
	public static final String CYCLE_RESOURCE_PURCHASE_ID="pId";
	public static final String CYCLE_RESOURCE_TYPE="type";
	public static final String CYCLE_RESOURCE_QTY="qty";
	public static final String CYCLE_RESOURCE_QUANTIFIER="quantifier";
	public static final String CYCLE_RESOURCE_CYCLEID="cycleId";
	public static final String CYCLE_RESOURCE_USECOST="useCost";
	
	public static final String TABLE_LABOUR="labour";
	public static final String LABOUR_ID="id";
	public static final String LABOUR_NAME="name";
	//CLOUD------------------------------------------------------------------------
	//Log for delete and inserts that need to be done to the cloud
	public static final String TABLE_REDO_LOG="redoLog";
	public static final String REDO_LOG_LOGID="id";
	public static final String REDO_LOG_TABLE="redotable";
	public static final String REDO_LOG_ROW_ID="row_id";
	public static final String REDO_LOG_OPERATION="operation";
	//A table to keep track of the types of objects and thier keys in the cloud
	public static final String TABLE_CLOUD_KEY="cloudKey";
	public static final String CLOUD_KEY="key";
	public static final String CLOUD_KEY_TABLE="ctable";
	public static final String CLOUD_KEY_ID="id";
	public static final String CLOUD_KEY_ROWID="rowid";
	
	public static final String TABLE_TRANSACTION_LOG="translog";
	public static final String TRANSACTION_LOG_LOGID="id";
	public static final String TRANSACTION_LOG_TABLE="transtable";//the table the update was on
	public static final String TRANSACTION_LOG_ROWID="rowid";
	public static final String TRANSACTION_LOG_OPERATION="operation";
	public static final String TRANSACTION_LOG_TRANSTIME="transtime";
	
	public static final String TABLE_UPDATE_ACCOUNT="updateacc";
	public static final String UPDATE_ACCOUNT_COUNTY="county";
	public static final String UPDATE_ACCOUNT_ADDRESS="address";
	public static final String UPDATE_ACCOUNT_ACC="acc";
	public static final String UPDATE_ACCOUNT_UPDATED="lastUpdated";
	public static final String UPDATE_ACCOUNT_CLOUD_KEY="cloudKey";
	public static final String UPDATE_ACCOUNT_ID="id";
	public static final String UPDATE_ACCOUNT_SIGNEDIN="signedIn";
	
	public static final int VERSION=170;
	public static final String DATABASE_NAME="agriDb";
	public Context ctx;
	
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null,VERSION);
		this.ctx=context;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		createDb(db);
		//ExpenseDelTrigger(db);
		TransactionLog tL=new TransactionLog(this,db,ctx);
		populate(db,tL);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropTables(db);
		onCreate(db);
		
		
	}
	private void createDb(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		createCropCycle(db);
		createResources(db);
		createResourcePurchases(db);
		createResourceUse(db);
		createRedoLog(db);
		createCloudKeys(db);
		createUpdateAccount(db);
		createTransactionLog(db);
	}
	public void dropTables(SQLiteDatabase db) {
		db.execSQL("drop table if exists "+DbHelper.TABLE_CROPCYLE);
		db.execSQL("drop table if exists "+DbHelper.TABLE_CYCLE_RESOURCES);
		db.execSQL("drop table if exists "+DbHelper.TABLE_RESOURCE_PURCHASES);
		db.execSQL("drop table if exists "+DbHelper.TABLE_RESOURCES);
		db.execSQL("drop table if exists "+DbHelper.TABLE_REDO_LOG);
		System.out.println("dropped redo");
		db.execSQL("drop table if exists "+DbHelper.TABLE_CLOUD_KEY);
		db.execSQL("drop table if exists "+DbHelper.TABLE_TRANSACTION_LOG);
		db.execSQL("drop table if exists "+DbHelper.TABLE_UPDATE_ACCOUNT);
	}
	

	private void createUpdateAccount(SQLiteDatabase db){
		String code="create table "+DbHelper.TABLE_UPDATE_ACCOUNT+"("
			+DbHelper.UPDATE_ACCOUNT_ID+" integer primary key autoincrement,"
			+DbHelper.UPDATE_ACCOUNT_ACC+" text,"
			+DbHelper.UPDATE_ACCOUNT_COUNTY+" text,"
			+DbHelper.UPDATE_ACCOUNT_ADDRESS+" text,"
			+DbHelper.UPDATE_ACCOUNT_UPDATED+" integer,"
			+DbHelper.UPDATE_ACCOUNT_SIGNEDIN+" integer,"
			+DbHelper.UPDATE_ACCOUNT_CLOUD_KEY+" text);";
		db.execSQL(code);
	}
	private void createCloudKeys(SQLiteDatabase db) {
		String code="create table "+DbHelper.TABLE_CLOUD_KEY+"("
			+DbHelper.CLOUD_KEY_ID+" integer primary key autoincrement,"
			+DbHelper.CLOUD_KEY+" text,"
			+DbHelper.CLOUD_KEY_ROWID+" integer,"
			+DbHelper.CLOUD_KEY_TABLE+" text);";
		db.execSQL(code);
	}
	private void createCropCycle(SQLiteDatabase db) {
		String code="create table "+DbHelper.TABLE_CROPCYLE+"("
			+DbHelper.CROPCYCLE_ID+" integer primary key autoincrement,"
			+DbHelper.CROPCYCLE_CROPID+" integer,"
			+DbHelper.CROPCYCLE_LAND_TYPE+" text,"
			+DbHelper.CROPCYCLE_LAND_AMOUNT+" integer,"
			+DbHelper.CROPCYCLE_DATE+" integer," 
			+DbHelper.CROPCYCLE_TOTALSPENT+" real,"
			+DbHelper.CROPCYCLE_HARVEST_AMT+" real,"
			+DbHelper.CROPCYCLE_HARVEST_TYPE+" text,"
			+DbHelper.CROPCYCLE_COSTPER+" real,"
			+DbHelper.CROPCYCLE_RESOURCE+" text,"
			+DbHelper.CROPCYCLE_COUNTY+" text,"
			+"foreign key("+DbHelper.CROPCYCLE_CROPID+") references "+DbHelper.TABLE_RESOURCES+"("+DbHelper.RESOURCES_ID+"));";
		db.execSQL(code);
	} 
	
	private void createResources(SQLiteDatabase db) {
		String code="create table "+DbHelper.TABLE_RESOURCES+"("
			+DbHelper.RESOURCES_ID+" integer primary key autoincrement,"
			+DbHelper.RESOURCES_NAME+" text,"
			+DbHelper.RESOURCES_TYPE+" text);";
		db.execSQL(code);
	}
	public void createTransactionLog(SQLiteDatabase db){
		String code="create table "+DbHelper.TABLE_TRANSACTION_LOG+"("
			+DbHelper.TRANSACTION_LOG_LOGID+" integer primary key autoincrement,"
			+DbHelper.TRANSACTION_LOG_TABLE+" text,"
			+DbHelper.TRANSACTION_LOG_ROWID+" integer,"
			+DbHelper.TRANSACTION_LOG_OPERATION+" text,"
			+DbHelper.TRANSACTION_LOG_TRANSTIME+" integer);";
		db.execSQL(code);
	}
	private void createResourcePurchases(SQLiteDatabase db) {
		String code="create table "+DbHelper.TABLE_RESOURCE_PURCHASES+"("
				+DbHelper.RESOURCE_PURCHASE_ID+" integer primary key autoincrement,"
				+DbHelper.RESOURCE_PURCHASE_RESID+" integer,"
				+DbHelper.RESOURCE_PURCHASE_TYPE+" text,"
				+DbHelper.RESOURCE_PURCHASE_QUANTIFIER+" text,"
				+DbHelper.RESOURCE_PURCHASE_QTY+" integer,"
				+DbHelper.RESOURCE_PURCHASE_REMAINING+" integer,"
				+DbHelper.RESOURCE_PURCHASE_COST+" real,"
				+DbHelper.RESOURCE_PURCHASE_DATE+" timestamp,"
				+DbHelper.RESOURCE_PURCHASE_RESOURCE+" text,"
				+"foreign key("+DbHelper.RESOURCE_PURCHASE_RESID+") references "+DbHelper.TABLE_RESOURCES+"("+DbHelper.RESOURCES_ID+"));";
		db.execSQL(code);
	}
	
	private void createResourceUse(SQLiteDatabase db) {
		String code="create table "+DbHelper.TABLE_CYCLE_RESOURCES+"("
			+DbHelper.CYCLE_RESOURCE_ID+" integer primary key autoincrement,"
			+DbHelper.CYCLE_RESOURCE_CYCLEID+" integer,"
			+DbHelper.CYCLE_RESOURCE_PURCHASE_ID+" integer,"
			+DbHelper.CYCLE_RESOURCE_TYPE+" text,"
			+DbHelper.CYCLE_RESOURCE_QTY+" integer,"
			+DbHelper.CYCLE_RESOURCE_USECOST+" real,"
			+DbHelper.CYCLE_RESOURCE_QUANTIFIER+" text,"
			+"foreign key("+DbHelper.CYCLE_RESOURCE_CYCLEID+") references "+DbHelper.TABLE_CROPCYLE+"("+DbHelper.CROPCYCLE_ID+"),"
			+"foreign key("+DbHelper.CYCLE_RESOURCE_PURCHASE_ID+") references "+DbHelper.TABLE_RESOURCE_PURCHASES+"("+DbHelper.RESOURCE_PURCHASE_ID+"))";
		db.execSQL(code);
	}
	public void createRedoLog(SQLiteDatabase db){
		String code="create table "+DbHelper.TABLE_REDO_LOG+"("
			+DbHelper.REDO_LOG_LOGID+" integer primary key autoincrement,"
			+DbHelper.REDO_LOG_TABLE+" text,"
			+DbHelper.REDO_LOG_ROW_ID+" integer,"
			+DbHelper.REDO_LOG_OPERATION+" text);";
		db.execSQL(code);
		System.out.println("created redo");
	}
	
	
	private void populate(SQLiteDatabase db,TransactionLog tL) {
		//create user Account
		UpAcc acc=new UpAcc();
		acc.setSignedIn(0);
		acc.setLastUpdated(System.currentTimeMillis() / 1000L);
		DbQuery.insertUpAcc(db, acc);
		
		//planting material - reference cardi - Caribbean Agricultural Research and Development Institute
		
		//general
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "HOT PEPPER");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "SWEET PEPPER");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "PIGEON PEAS");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "SOYABEAN – LEGUMES");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "COCOA");
		
		//fruits
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "CITRUS");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "COCONUT");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "GOLDEN APPLE");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "MANGO");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "WATERMELON");

		
		//root crops
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "CASSAVA");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "DASHEEN");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "EDDOES");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "SWEET POTATO");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "TANNIA");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "YAM");
		//vegetables
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "BROCCOLI");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "CARROTS");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "CABBAGE");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "CAULIFLOWER");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "CHRISTOPHENE");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "EGGPLANT");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "ESCALLION");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "LETTUCE");
		DbQuery.insertResource(db, this, DHelper.cat_plantingMaterial, "ONIONS");
		
		//fertilizer -Plant Doctors tt
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "Fersan (7.12.40 + 1TEM)");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "Magic Grow (7.12.40 + TE HYDROPHONIC)");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "Hydro YARA Liva (15.0.15)");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "Techni - Grow (7.12.27 + TE)");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "Ferqidd (10.13.32 + TE)");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "Plant Prod (7.12.27 + TE)");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "Flower Plus (9.18.36 + TE)");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "Iron Chelate Powder (FE - EDTA)");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "Magnesium Sulphate (Mg SO4)");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "12-24-12 FERTILIZER");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "HARVEST MORE 10-55-10");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "HARVEST MORE 13-0-44");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "HARVEST MORE 5-5-45");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "NPK 12-12-17");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "UREA 46-0-0");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "PLANT BOOSTER");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "MIRACLE GRO ALL PROPOSE PLANT FOOD");
		DbQuery.insertResource(db, this, DHelper.cat_fertilizer, "SCOTTS FLOWER AND VEGETABLE PLANT FOOD");
		
		//soil amendments -Plant Doctors tt
		DbQuery.insertResource(db, this, DHelper.cat_soilAmendment, "Cow manure");
		DbQuery.insertResource(db, this, DHelper.cat_soilAmendment, "Compost");
		DbQuery.insertResource(db, this, DHelper.cat_soilAmendment, "Gypsum");
		DbQuery.insertResource(db, this, DHelper.cat_soilAmendment, "Limestone");
		DbQuery.insertResource(db, this, DHelper.cat_soilAmendment, "Sulphur");
		DbQuery.insertResource(db, this, DHelper.cat_soilAmendment, "Molasses");
		DbQuery.insertResource(db, this, DHelper.cat_soilAmendment, "Chicken manure");
		DbQuery.insertResource(db, this, DHelper.cat_soilAmendment, "Horse manure");
		DbQuery.insertResource(db, this, DHelper.cat_soilAmendment, "Calphos");
		DbQuery.insertResource(db, this, DHelper.cat_soilAmendment, "Sharp sand");
		
		//chemical --http://en.wikipedia.org/wiki/Pesticide#Classified_by_type_of_pest
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Fungicide");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Insecticide");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Weedicide");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Algicides");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Antimicrobials");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Biopesticides");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Biocides");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Fumigants");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Herbicides");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Miticides");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Microbial pesticides");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Molluscicides");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Nematicides");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Ovicides");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Pheromones");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Repellents");
		DbQuery.insertResource(db, this, DHelper.cat_chemical, "Rodenticides");
		
		
		//labour
		DbQuery.insertResource(db, this, DHelper.cat_labour, "John");
		DbQuery.insertResource(db, this, DHelper.cat_labour, "Damian");
		DbQuery.insertResource(db, this, DHelper.cat_labour, "Stephen");
		/*
		DataManager dm=new DataManager(ctx,db,this);
		dm.insertPurchase(3, "mililitres", 800, "chemical",200);
		dm.insertPurchase(1, "seeds", 40, "crop",100);
		dm.insertPurchase(2, "seedlings", 40, "crop",250);
		dm.insertPurchase(4, "bags", 4, "fertilizer",100);
		dm.insertPurchase(6, "seeds", 50, "crop",120);		
		
		dm.insertCycle(1, "acre", 2,24324222);
		dm.insertCycle(6, "bed", 12,34522123);
		
		dm.insertCycleUse(1, 1, 100, "chemical");
		dm.insertCycleUse(1, 2, 25, "crop");
		dm.insertCycleUse(2, 5, 20, "crop");
		
		System.out.println("stuffs inserted");*/
	}
	
}
