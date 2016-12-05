package uwi.dcit.AgriExpenseTT.helpers;

import android.database.sqlite.SQLiteDatabase;

import uwi.dcit.AgriExpenseTT.dbstruct.structs.Country;
import uwi.dcit.AgriExpenseTT.dbstruct.structs.County;
import uwi.dcit.AgriExpenseTT.dbstruct.structs.UpAccount;
import uwi.dcit.AgriExpenseTT.lists.Chemicals;
import uwi.dcit.AgriExpenseTT.lists.Crops;
import uwi.dcit.AgriExpenseTT.lists.Fertilizers;
import uwi.dcit.AgriExpenseTT.lists.Soils;
import uwi.dcit.AgriExpenseTT.models.CountryContract;
import uwi.dcit.AgriExpenseTT.models.CountyContract;
import uwi.dcit.agriexpensesvr.upAccApi.model.UpAcc;

/**
 * Created by rcjasmin on 12/3/2016.
 */

public class DefaultDataHelper {

    public static void insertDefaultCrops(DefaultDataManager dfm){
        dfm.setDataCategory(DHelper.cat_plantingMaterial);
        dfm.setPlist(Crops.plist);
        dfm.insertListToDB();
    }


    public static  void updateCropList(DefaultDataManager dfm ){
        dfm.setDataCategory(DHelper.cat_plantingMaterial);
        dfm.setPlist(Crops.toUpdatelist);
        dfm.insertListToDB();
    }

    public static  void insertDefaultFertilizers(DefaultDataManager dfm ){
        dfm.setDataCategory(DHelper.cat_fertilizer);
        dfm.setPlist(Fertilizers.plist);
        dfm.insertListToDB();
    }

    public static  void insertDefaultSoilAdds(DefaultDataManager dfm ){
        dfm.setDataCategory(DHelper.cat_soilAmendment);
        dfm.setPlist(Soils.plist);
        dfm.insertListToDB();
    }

    public static  void insertDefaultChemicals(DefaultDataManager dfm ){
        dfm.setDataCategory(DHelper.cat_chemical);
        dfm.setPlist(Chemicals.plist);
        dfm.insertListToDB();
    }

    public static void insertDefaultCountries(SQLiteDatabase db){
        for (String [] country : CountryContract.countries){
            Country.insertCountry(db, country[0], country[1]);
        }
    }

    public static void insertDefaultCounties(SQLiteDatabase db) {
        for (String [] county : CountyContract.counties){
            County.insertCounty(db, county[0], county[1]);
        }
    }


    public static void populate(DefaultDataManager dfm ){
        //create user Account
        UpAcc acc = new UpAcc();
        acc.setSignedIn(0);
        acc.setLastUpdated(System.currentTimeMillis() / 1000L);
        UpAccount.insertUpAcc(dfm.getDB(), acc);

        insertDefaultCrops(dfm);
        insertDefaultFertilizers(dfm);
        insertDefaultSoilAdds(dfm);
        insertDefaultChemicals(dfm);
        insertDefaultCountries(dfm.getDB());
        insertDefaultCounties(dfm.getDB());
    }
}
