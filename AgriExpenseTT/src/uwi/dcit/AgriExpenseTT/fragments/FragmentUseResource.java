package uwi.dcit.AgriExpenseTT.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;

public class FragmentUseResource extends Fragment{
    View view;
    LocalCycle cycle;
    String type;
    double total;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_use_resource, container, false);
        cycle = getArguments().getParcelable("cycle");
        type  = getArguments().getString("type");
        total = Double.parseDouble(getArguments().getString("total"));

        setup();
        return view;
    }

    private void setup() {
        SQLiteDatabase db=new DbHelper(getActivity().getApplicationContext()).getWritableDatabase();

        if(!DbQuery.resourceExist(db, type)){
            Fragment fragment	= new FragmentEmpty();
            Bundle parameter 	= new Bundle();
            parameter.putString("type","purchase");

            parameter.putString("category", type);
            fragment.setArguments(parameter);

            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.useExpenseFrag, fragment)
                    .commit();
        }else{
            initialFrag();
        }
    }
    private void initialFrag() {
        Bundle pass = new Bundle();
        pass.putParcelable("cycle",cycle);
        pass.putString("det",type);
        pass.putString("total",""+total);
        ListFragment listFrag = new FragmentChoosePurchase();
        listFrag.setArguments(pass);

       getChildFragmentManager()
                .beginTransaction()
                .add(R.id.useExpenseFrag,listFrag)
                .commit();

        String category = getArguments().getString("type");

        ActionBar bar = null;
        if (this.getActivity() instanceof AppCompatActivity){
            bar = ((AppCompatActivity)this.getActivity()).getSupportActionBar();
        }

        if(category.equals(DHelper.cat_plantingMaterial)){
            if (bar != null)bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colourPM)));
        }else if(category.equals(DHelper.cat_fertilizer)){
            if (bar != null)bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colourFer)));
        }else if(category.equals(DHelper.cat_soilAmendment)){
            if (bar != null)bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colourSoil)));
        }else if(category.equals(DHelper.cat_chemical)){
            if (bar != null)bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colourChem)));
        }else if(category.equals(DHelper.cat_other)){
            if (bar != null)bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colourChem)));
        }else if (category.equals((DHelper.cat_labour))){
            if (bar != null)bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colourLabour)));
        }
    }
}
