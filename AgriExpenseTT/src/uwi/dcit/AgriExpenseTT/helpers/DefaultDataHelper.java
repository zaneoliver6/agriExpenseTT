package uwi.dcit.AgriExpenseTT.helpers;

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

    public static DefaultDataManager manager;

    public static void insertDefaultCrops(){
        manager.setListAndCategory(Crops.plist,DHelper.cat_plantingMaterial);
        manager.insertListToDB();
    }

    public static  void updateCropList(){
        manager.setListAndCategory(Crops.toUpdatelist,DHelper.cat_plantingMaterial);
        manager.insertListToDB();
    }

    public static  void insertDefaultFertilizers(){
        manager.setListAndCategory(Fertilizers.plist,DHelper.cat_fertilizer);
        manager.insertListToDB();
    }

    public static  void insertDefaultSoilAdds(){
        manager.setListAndCategory(Soils.plist,DHelper.cat_soilAmendment);
        manager.insertListToDB();
    }

    public static  void insertDefaultChemicals(){
        manager.setListAndCategory(Chemicals.plist,DHelper.cat_chemical);
        manager.insertListToDB();
    }

    public static void insertDefaultCountries(){
        for (String [] country : CountryContract.countries){
            Country.insertCountry(manager.getDB(), country[0], country[1]);
        }
    }

    public static void insertDefaultCounties() {
        for (String [] county : CountyContract.counties){
            County.insertCounty(manager.getDB(), county[0], county[1]);
        }
    }


    public static void populate(){
        //create user Account
        UpAcc acc = new UpAcc();
        acc.setSignedIn(0);
        acc.setLastUpdated(System.currentTimeMillis() / 1000L);
        UpAccount.insertUpAcc(manager.getDB(), acc);

        insertDefaultCrops();
        insertDefaultFertilizers();
        insertDefaultSoilAdds();
        insertDefaultChemicals();
        insertDefaultCountries();
        insertDefaultCounties();
    }
}
