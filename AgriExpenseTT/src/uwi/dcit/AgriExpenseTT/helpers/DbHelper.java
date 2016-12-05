package uwi.dcit.AgriExpenseTT.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;


import uwi.dcit.AgriExpenseTT.dbstruct.structs.Country;
import uwi.dcit.AgriExpenseTT.dbstruct.structs.County;
import uwi.dcit.AgriExpenseTT.dbstruct.structs.Resource;
import uwi.dcit.AgriExpenseTT.dbstruct.structs.UpAccount;
import uwi.dcit.AgriExpenseTT.models.CountryContract;
import uwi.dcit.AgriExpenseTT.models.CountyContract;
import uwi.dcit.AgriExpenseTT.dbstruct.TblMnger;
import uwi.dcit.AgriExpenseTT.models.CycleContract;
import uwi.dcit.AgriExpenseTT.models.CycleResourceContract;
import uwi.dcit.AgriExpenseTT.models.ResourcePurchaseContract;
import uwi.dcit.agriexpensesvr.upAccApi.model.UpAcc;

public class DbHelper extends SQLiteOpenHelper{

	public static final int VERSION = 172;
	public static final String DATABASE_NAME="agriDb";
	public static final String TAG_NAME = "AgriExpenseDBHelper";
	public Context ctx;
	
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null,VERSION);
		this.ctx = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
        Log.i(TAG_NAME, "Creating AgriExpense DB for first time");
		TblMnger.createDb(db);
		populate(db, new TransactionLog(this,db,ctx));
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//We will be required to implement upgrade functionality that is specific to each version of the upgrade
		Log.i(TAG_NAME, "Upgrade detected. Old version: "+ oldVersion + " New version: "+newVersion);


        // TODO Add logic to place the crop name as the cycle name for existing cycle records
        // TODO Add Date to CycleResource and place timestamp automatically for values

		if (oldVersion < 170){
			Log.d(TAG_NAME, "version too old to support Removing all tables so far and restart");
			TblMnger.dropTables(db);
			this.onCreate(db);
		}
        if (oldVersion  <= 171 && !columnExists(db, CycleContract.CycleEntry.TABLE_NAME, CycleContract.CycleEntry._ID)) {
			
			Log.d(TAG_NAME, "Running Update of table structure");
			TblMnger.tableColumnModify(db);
			
			Log.d(TAG_NAME, "Running installation of countries");
			TblMnger.createCountries(db); // Create if not exist so if previously created this will do nothing
			//this.insertDefaultCountries(db);
			
			Log.d(TAG_NAME, "Running installation of counties");
			TblMnger.createCounties(db);
			//this.insertDefaultCounties(db);
			
			Log.d(TAG_NAME, "Running upgrade of crop/plating material lists");
			this.updateCropList(db);
		}
        if (oldVersion < 172){
            db.beginTransaction();

            db.execSQL("ALTER TABLE " + CycleContract.CycleEntry.TABLE_NAME + " ADD COLUMN "+ CycleContract.CycleEntry.CROPCYCLE_NAME + " TEXT");
            // Place the resource name as the default name of the cycle
            //updateCycleCropName(db);

            // Place the update date as the date for the previously created resources purchased
            //updatePurchaseRecs(db);

            // Add Date Column to CycleResource
            db.execSQL("ALTER TABLE " + CycleResourceContract.CycleResourceEntry.TABLE_NAME + " ADD COLUMN "+ CycleResourceContract.CycleResourceEntry.CYCLE_DATE_USED +  " TIMESTAMP");
            //updateCycleResource(db);

            db.setTransactionSuccessful();
            db.endTransaction();
        }
		
		Log.d(TAG_NAME, "Completed upgrading the database to version " + VERSION);

//        db.close();
	}

	private void populate(SQLiteDatabase db, TransactionLog tL) {
		//create user Account
		UpAcc acc = new UpAcc();
		acc.setSignedIn(0);
		acc.setLastUpdated(System.currentTimeMillis() / 1000L);
		UpAccount.insertUpAcc(db, acc);
		
		insertDefaultCrops(db);
		insertDefaultFertilizers(db);
		insertDefaultSoilAdds(db);
		insertDefaultChemicals(db);
		insertDefaultCountries(db);
		insertDefaultCounties(db);
	}
	
	public void insertDefaultCrops(SQLiteDatabase db){
		//planting material - reference cardi - Caribbean Agricultural Research and Development Institute
		//general
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "SOYABEAN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "COCOA");
		
		//fruits
//		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CITRUS");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "ORANGES");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "LIME");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "LEMON");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "GRAPEFRUIT");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "TANGERINE");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PORTUGAL");
		
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "COCONUT");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "GOLDEN APPLE");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "MANGO");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "WATERMELON");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PAW PAW");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PINEAPPLE");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "SUGARCANE");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "SORREL");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "RICE");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "MAIZE (CORN)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BREADFRUIT");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BANANA");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BREADNUT (CHATAIGNE)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CHERRY");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CARAMBOLA");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PEANUTS");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "NUTMEG");
		
		//herbs
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "ANISE SEED");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BASIL");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BAY LEAF");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CELERY");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CHIVE");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CURRY LEAF");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "DILL");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "FENNEL");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "MARJORAM");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "MINT");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "OREGANO");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PARSLEY");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "ROSEMARY");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CULANTRO (SHADON BENI / BANDANIA)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "TARRAGON");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "THYME - FRENCH");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "THYME - SPANISH");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "THYME - FINE");
		
		//root crops
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BEET");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CASSAVA");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CUSH CUSH");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "DASHEEN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "EDDOES");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "GINGER");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "HORSERADISH");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "ONIONS");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "SWEET POTATO");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "TANNIA");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "LEREN (TOPI TAMBU)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "TUMERIC (SAFFRON)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "YAM");
		
		//vegetables
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BHAGI");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BORA (BODI) BEAN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BROCCOLI");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CARROTS");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CABBAGE");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CARAILLI");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CAULIFLOWER");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CHOI SUM (CHINESE CABBAGE)"); //Brassica rapa cv. chinensis
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CHRISTOPHENE");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CORN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CUCUMBER");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "DASHEEN BUSH");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "GREEN FIG");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "COWPEA (GUB GUB)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "HOT PEPPER");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "JACK BEAN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "JHINGI");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "LAUKI");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "LETTUCE");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "EGGPLANT");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "RADISH (MOORAI)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "OCHRO");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PAKCHOY"); //Brassica rapa
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PIGEON PEAS");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PIMENTO PEPPER");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PLANTAIN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "VINE SPINACH (POI BHAGI)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PUMPKIN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "SAIJAN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "SATPUTIYA (LOOFAH)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "SEIM");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "STRING BEAN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "SQUASH");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "SWEET PEPPER");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "TOMATO");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "WATERCRESS");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "WING BEAN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "ESCALLION");
		
	}
	
	public void updateCropList(SQLiteDatabase db){
		//VEGETABLES
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BHAGI");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BORA (BODI) BEAN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CARAILLI");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CHOI SUM (CHINESE CABBAGE)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CORN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CUCUMBER");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "DASHEEN BUSH");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "GREEN FIG");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "COWPEA (GUB GUB)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "JACK BEAN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "JHINGI");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "LAUKI");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "RADISH (MOORAI)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "OCHRO");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PAKCHOY");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PIMENTO PEPPER");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PLANTAIN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "VINE SPINACH (POI BHAGI)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PUMPKIN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "SAIJAN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "SATPUTIYA");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "SEIM");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "STRING BEAN");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "SQUASH");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "TOMATO");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "WATERCRESS");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "WING BEAN");
		
		//ROOT CROPS
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BEET");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CUSH CUSH");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "GINGER");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "LEREN (TOPI TAMBU)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "TUMERIC (SAFFRON)");
		
		//herbs
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "ANISE SEED");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BASIL");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BAY LEAF");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CELERY");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CHIVE");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CURRY LEAF");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "DILL");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "FENNEL");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "MARJORAM");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "MINT");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "OREGANO");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PARSLEY");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "ROSEMARY");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CULANTRO (SHADON BENI / BANDANIA)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "TARRAGON");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "THYME - FRENCH");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "THYME - SPANISH");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "THYME - FINE");
		
		
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PAW PAW");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PEANUTS");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "NUTMEG");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BANANA");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BREADFRUIT");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "BREADNUT (CHATAIGNE)");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CHERRY");
		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "CARAMBOLA");

		Resource.insertResource(db, this, DHelper.cat_plantingMaterial, "PUMPKIN");

	}
	
	public void insertDefaultFertilizers(SQLiteDatabase db){
		//fertilizer -Plant Doctors tt
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "Fersan (7.12.40 + 1TEM)");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "Magic Grow (7.12.40 + TE HYDROPHONIC)");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "Hydro YARA Liva (15.0.15)");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "Techni - Grow (7.12.27 + TE)");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "Ferqidd (10.13.32 + TE)");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "Plant Prod (7.12.27 + TE)");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "Flower Plus (9.18.36 + TE)");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "Iron Chelate Powder (FE - EDTA)");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "Magnesium Sulphate (Mg SO4)");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "12-24-12 FERTILIZER");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "HARVEST MORE 10-55-10");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "HARVEST MORE 13-0-44");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "HARVEST MORE 5-5-45");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "NPK 12-12-17");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "UREA 46-0-0");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "PLANT BOOSTER");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "MIRACLE GRO ALL PROPOSE PLANT FOOD");
		Resource.insertResource(db, this, DHelper.cat_fertilizer, "SCOTTS FLOWER AND VEGETABLE PLANT FOOD");
	}
	
	public void insertDefaultSoilAdds(SQLiteDatabase db){
		//soil amendments -Plant Doctors tt
		Resource.insertResource(db, this, DHelper.cat_soilAmendment, "Cow manure");
		Resource.insertResource(db, this, DHelper.cat_soilAmendment, "Compost");
		Resource.insertResource(db, this, DHelper.cat_soilAmendment, "Gypsum");
		Resource.insertResource(db, this, DHelper.cat_soilAmendment, "Limestone");
		Resource.insertResource(db, this, DHelper.cat_soilAmendment, "Sulphur");
		Resource.insertResource(db, this, DHelper.cat_soilAmendment, "Molasses");
		Resource.insertResource(db, this, DHelper.cat_soilAmendment, "Chicken manure");
		Resource.insertResource(db, this, DHelper.cat_soilAmendment, "Horse manure");
		Resource.insertResource(db, this, DHelper.cat_soilAmendment, "Calphos");
		Resource.insertResource(db, this, DHelper.cat_soilAmendment, "Sharp sand");
	}
	
	public void insertDefaultChemicals(SQLiteDatabase db){
		//chemical --http://en.wikipedia.org/wiki/Pesticide#Classified_by_type_of_pest
		Resource.insertResource(db, this, DHelper.cat_chemical, "Fungicide");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Insecticide");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Weedicide");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Algicides");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Antimicrobials");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Biopesticides");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Biocides");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Fumigants");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Herbicides");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Miticides");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Microbial pesticides");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Molluscicides");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Nematicides");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Ovicides");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Pheromones");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Repellents");
		Resource.insertResource(db, this, DHelper.cat_chemical, "Rodenticides");
	}
	
	public void insertDefaultCountries(SQLiteDatabase db){
		for (String [] country : CountryContract.countries){
			Country.insertCountry(db, country[0], country[1]);
		}
	}

	public void insertDefaultCounties(SQLiteDatabase db) {
		for (String [] county : CountyContract.counties){
			County.insertCounty(db, county[0], county[1]);
		}
	}

    private void updatePurchaseRecs(SQLiteDatabase  db){
        Cursor cursor = db.rawQuery("SELECT * FROM " + ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, null);
        // Update Existing Dates to the current date
        while(cursor.moveToNext()){
            ContentValues cv = new ContentValues();
            cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_DATE,  DateFormatHelper.getDateUnix(new Date()) );
        }
        cursor.close();
    }

    private void updateCycleCropName(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + CycleContract.CycleEntry.TABLE_NAME, null);
        while(cursor.moveToNext()){
            ContentValues cv = new ContentValues();
            cv.put(CycleContract.CycleEntry.CROPCYCLE_NAME, cursor.getColumnIndex(CycleContract.CycleEntry.CROPCYCLE_RESOURCE));
        }
        cursor.close();
    }

    private void updateCycleResource(SQLiteDatabase db){
        Cursor cursor = db.rawQuery("SELECT * FROM " + CycleResourceContract.CycleResourceEntry.TABLE_NAME , null);
        while(cursor.moveToNext()){
            ContentValues cv = new ContentValues();
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cv.put(CycleResourceContract.CycleResourceEntry.CYCLE_DATE_USED, cal.getTimeInMillis());
        }
        cursor.close();
    }

    private boolean columnExists(SQLiteDatabase db, String tableName, String columnName){
        Cursor cursor = db.rawQuery("PRAGMA table_info("+tableName+");", null);
        while (cursor.moveToNext()){
            if (cursor.getString(cursor.getColumnIndex("name")).equalsIgnoreCase(columnName)){
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }
}
