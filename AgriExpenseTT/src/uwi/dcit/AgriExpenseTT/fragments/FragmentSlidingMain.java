package uwi.dcit.AgriExpenseTT.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;

public class FragmentSlidingMain extends FragmentSlidingTabs {

    private String focus;

    public void populateList(){
        DbHelper dbh = new DbHelper(getActivity().getApplicationContext());
        SQLiteDatabase db = dbh.getReadableDatabase();

        Bundle arguments;
        arguments = getArguments();
        if (arguments.containsKey("type")) {
            focus = arguments.getString("type");
        }


        Fragment cycleFrag, resFrag;
        arguments = new Bundle();

        // Determine If cycles exist to display the empty fragment of list of cycles
        if(DbQuery.cyclesExist(db)){
            cycleFrag = new FragmentViewCycles();
        }else{
            cycleFrag = new FragmentEmpty();
            arguments.putString("type", "cycle");
            cycleFrag.setArguments(arguments);
        }

        // Determine If purchases exist to display the empty fragment of list of purchases
        if(DbQuery.resourceExist(db)){
            resFrag = new FragmentChoosePurchase();
        }else{
            resFrag = new FragmentEmpty();
            arguments.putString("type", "purchase");
            resFrag.setArguments(arguments);
        }

        fragments.add(new FragItem(cycleFrag, Color.BLUE,"Cycles"));
        fragments.add(new FragItem(resFrag,Color.GREEN,"Purchases"));

        db.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (mViewPager != null) {
            if (focus != null && focus == "cycle") {
                mViewPager.setCurrentItem(0);
            } else {
                mViewPager.setCurrentItem(1);
            }
        } else Log.d("SlidingMainFragment", "Unable to access the view pager");
        return view;
    }

}

