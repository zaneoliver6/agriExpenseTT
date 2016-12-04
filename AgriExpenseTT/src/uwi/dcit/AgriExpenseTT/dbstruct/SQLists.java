package uwi.dcit.AgriExpenseTT.dbstruct;

import uwi.dcit.AgriExpenseTT.models.CloudKeyContract;
import uwi.dcit.AgriExpenseTT.models.CountryContract;
import uwi.dcit.AgriExpenseTT.models.CountyContract;
import uwi.dcit.AgriExpenseTT.models.CycleContract;
import uwi.dcit.AgriExpenseTT.models.CycleResourceContract;
import uwi.dcit.AgriExpenseTT.models.LabourContract;
import uwi.dcit.AgriExpenseTT.models.RedoLogContract;
import uwi.dcit.AgriExpenseTT.models.ResourceContract;
import uwi.dcit.AgriExpenseTT.models.ResourcePurchaseContract;
import uwi.dcit.AgriExpenseTT.models.TransactionLogContract;
import uwi.dcit.AgriExpenseTT.models.UpdateAccountContract;

class SQLists {
    private String[] list;

    public String[] getCreateList(){
        this.list = new String[]{ ResourceContract.SQL_CREATE_RESOURCE,
                CycleContract.SQL_CREATE_CYCLE,
                ResourcePurchaseContract.SQL_CREATE_RESOURCE_PURCHASE,
                CycleResourceContract.SQL_CREATE_CYCLE_RESOURCE,
                LabourContract.SQL_CREATE_LABOUR,
                CloudKeyContract.SQL_CREATE_CLOUD_KEY,
                RedoLogContract.SQL_CREATE_REDO_LOG,
                TransactionLogContract.SQL_CREATE_TRANSACTION_LOG,
                UpdateAccountContract.SQL_CREATE_UPDATE_ACCOUNT,
                CountryContract.SQL_CREATE_COUNTRIES,
                CountyContract.SQL_CREATE_COUNTIES
        };

        return this.list;
    }

    public String[] getDestroyList(){
        this.list = new String[] {
                CycleResourceContract.SQL_DELETE_CYCLE_RESOURCE,
                ResourcePurchaseContract.SQL_DELETE_RESOURCE_PURCHASE,
                CycleContract.SQL_DELETE_CYCLE,
                ResourceContract.SQL_DELETE_RESOURCE,
                CloudKeyContract.SQL_DELETE_CLOUD_KEY,
                RedoLogContract.SQL_DELETE_REDO_LOG,
                TransactionLogContract.SQL_DELETE_TRANSACTION_LOG,
                UpdateAccountContract.SQL_DELETE_UPDATE_ACCOUNT,
                CountryContract.SQL_DELETE_COUNTRIES,
                CountyContract.SQL_DELETE_COUNTIES
        };
        return this.list;
    }
}
