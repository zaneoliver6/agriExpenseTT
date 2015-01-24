package uwi.dcit.AgriExpenseTT.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;

/**
 * Created by Steffan on 12/01/2015.
 */
public class FragmentUseResource extends Fragment{
    View view;
    LocalCycle cycle;
    String type;
    double total;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_use_resource, container, false);
        cycle=getArguments().getParcelable("cycle");
        type=getArguments().getString("type");
        total=Double.parseDouble(getArguments().getString("total"));
        setup();
        return view;
    }

    private void setup() {
        SQLiteDatabase db=new DbHelper(getActivity().getApplicationContext()).getReadableDatabase();
        if(!DbQuery.resourceExist(db)){
            Fragment fragment	= new FragmentEmpty();
            Bundle parameter 	= new Bundle();
            parameter.putString("type","purchase");
            parameter.putString("category", type);
            fragment.setArguments(parameter);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.useExpenseFrag, fragment)
                    .commit();
        }else{
            initialFrag();
        }
    }
    private void initialFrag() {
        Bundle pass=new Bundle();
        pass.putParcelable("cycle",cycle);
        pass.putString("det",type);
        pass.putString("total",""+total);
        ListFragment listfrag	= new ChoosePurchaseFragment();
        listfrag.setArguments(pass);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.useExpenseFrag,listfrag)
                .commit();

        View line=view.findViewById(R.id.line_header_useRes);
        String category=getArguments().getString("type");
        if(category.equals(DHelper.cat_plantingMaterial)){
            line.setBackgroundResource(R.color.colourPM);
        }else if(category.equals(DHelper.cat_fertilizer)){
            line.setBackgroundResource(R.color.colourFer);
        }else if(category.equals(DHelper.cat_soilAmendment)){
            line.setBackgroundResource(R.color.colourSoil);
        }else if(category.equals(DHelper.cat_chemical)){
            line.setBackgroundResource(R.color.colourChem);
        }else if(category.equals(DHelper.cat_other)){
            line.setBackgroundResource(R.color.colourOther);
        }
    }
}
