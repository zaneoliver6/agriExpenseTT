package uwi.dcit.AgriExpenseTT;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;

import uwi.dcit.AgriExpenseTT.fragments.FragmentCycleUsage;
import uwi.dcit.AgriExpenseTT.fragments.FragmentCycleUseCategory;
import uwi.dcit.AgriExpenseTT.fragments.FragmentGeneralCategory;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;

public class CycleUsage extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_cycle_usage);
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Cycle Usage");
//		int id = 1;
//		setup();

        setContentView(R.layout.activity_view_navigation);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.navContentLeft, new FragmentCycleUsage())
                .commit();
	}

	private void setup() {
        Fragment catGeneral			= new FragmentGeneralCategory();
        Fragment catPlantMaterial	= new FragmentCycleUseCategory();
        Fragment catFertilizer		= new FragmentCycleUseCategory();
        Fragment catSoilAmendment	= new FragmentCycleUseCategory();
        Fragment catChemical		= new FragmentCycleUseCategory();
        Fragment catLabour 			= new FragmentCycleUseCategory();
        Fragment catOther 			= new FragmentCycleUseCategory();


        Bundle data = getIntent().getExtras();
        LocalCycle curr = data.getParcelable("cycleMain");

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

        android.support.v4.app.FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.add(R.id.cat_general_frag, catGeneral);
        ft.add(R.id.cat_plantMaterial_frag, catPlantMaterial);
        ft.add(R.id.cat_fertilizer_frag, catFertilizer);
        ft.add(R.id.cat_soilAmendment_frag, catSoilAmendment);
        ft.add(R.id.cat_chemical_frag, catChemical);
        ft.add(R.id.cat_labour_frag, catLabour);
        ft.add(R.id.cat_other_frag, catOther);
        ft.commit();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cycle_useage_redesign, menu);
		return true;
	}

}
