package uwi.dcit.AgriExpenseTT.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;

/**
 * Created by Steffan on 12/01/2015.
 */
public class FragmentCycleUseage extends Fragment {
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_cycle_useage_redesign, container, false);
        setup();
        return view;
    }

    private void setup() {
        Fragment catGeneral			= new FragmentGeneralCategory();
        Fragment catPlantMaterial	= new FragmentCycleUseCategory();
        Fragment catFertilizer		= new FragmentCycleUseCategory();
        Fragment catSoilAmendment	= new FragmentCycleUseCategory();
        Fragment catChemical		= new FragmentCycleUseCategory();
        Fragment catLabour 			= new FragmentCycleUseCategory();
        Fragment catOther 			= new FragmentCycleUseCategory();


        Bundle data = getArguments();
        LocalCycle curr = (LocalCycle) data.getParcelable("cycleMain");

        Bundle generalArguments = new Bundle();
        generalArguments.putString("category","general");
        generalArguments.putParcelable("cycle",curr);
        catGeneral.setArguments(generalArguments);

        Bundle plantingArguments = new Bundle();
        plantingArguments.putString("category", DHelper.cat_plantingMaterial);
        plantingArguments.putParcelable("cycle", curr);
        catPlantMaterial.setArguments(plantingArguments);

        Bundle fertilizerArguments = new Bundle();
        fertilizerArguments.putParcelable("cycle", curr);
        fertilizerArguments.putString("category", DHelper.cat_fertilizer);
        catFertilizer.setArguments(fertilizerArguments);

        Bundle amendmentsArguments = new Bundle();
        amendmentsArguments.putParcelable("cycle", curr);
        amendmentsArguments.putString("category", DHelper.cat_soilAmendment);
        catSoilAmendment.setArguments(amendmentsArguments);

        Bundle chemicalArguments = new Bundle();
        chemicalArguments.putParcelable("cycle", curr);
        chemicalArguments.putString("category", DHelper.cat_chemical);
        catChemical.setArguments(chemicalArguments);

        Bundle labourArguments = new Bundle();
        labourArguments.putParcelable("cycle", curr);
        labourArguments.putString("category", DHelper.cat_labour);
        catLabour.setArguments(labourArguments);

        Bundle otherArguments = new Bundle();
        otherArguments.putParcelable("cycle", curr);
        otherArguments.putString("category", DHelper.cat_other);
        catOther.setArguments(otherArguments);

        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.cat_general_frag, catGeneral);
        ft.add(R.id.cat_plantMaterial_frag, catPlantMaterial);
        ft.add(R.id.cat_fertilizer_frag, catFertilizer);
        ft.add(R.id.cat_soilAmendment_frag, catSoilAmendment);
        ft.add(R.id.cat_chemical_frag, catChemical);
        ft.add(R.id.cat_labour_frag, catLabour);
        ft.add(R.id.cat_other_frag, catOther);
        ft.commit();
    }
}
