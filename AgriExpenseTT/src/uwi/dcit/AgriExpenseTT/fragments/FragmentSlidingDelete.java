package uwi.dcit.AgriExpenseTT.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;

public class FragmentSlidingDelete extends FragmentSlidingTabs {


    public void populateList(){
        SQLiteDatabase db;
        DbHelper dbh=new DbHelper(getActivity().getApplicationContext());
        db=dbh.getWritableDatabase();
        Fragment cycleFrag,resFrag;
        Bundle arguments	= new Bundle();

        if(DbQuery.cyclesExist(db)){
            cycleFrag=new FragmentViewCycles();
            arguments.putString("type", "delete");
            cycleFrag.setArguments(arguments);
        }else{
            cycleFrag = new FragmentEmpty();
            arguments.putString("type", "cycle");
            cycleFrag.setArguments(arguments);
        }
        if(DbQuery.resourceExist(db)){
            resFrag =new FragmentChoosePurchase();
            arguments.putString("det", "delete");
            resFrag.setArguments(arguments);
        }else{
            resFrag=new FragmentEmpty();
            arguments.putString("type", "purchase");
            resFrag.setArguments(arguments);
        }

        Fragment viewRes=new FragmentViewResources();
        arguments.putString("type", "delete");
        viewRes.setArguments(arguments);

        fragments.add(new FragItem(cycleFrag, Color.BLUE,"Cycles"));
        fragments.add(new FragItem(resFrag,Color.GREEN,"Purchases"));
        fragments.add(new FragItem(viewRes,Color.RED,"Resources"));
    }
}
