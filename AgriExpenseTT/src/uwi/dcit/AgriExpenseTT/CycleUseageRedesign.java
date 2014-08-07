package uwi.dcit.AgriExpenseTT;

import helper.DHelper;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import dataObjects.localCycle;
import fragments.FragmentCycleUseCategory;
import fragments.FragmentGeneralCategory;

public class CycleUseageRedesign extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cycle_useage_redesign);
		int id=1;
		setup(id);
	}

	private void setup(int id) {
		Fragment catGeneral=new FragmentGeneralCategory();
		Fragment catPlantMaterial=new FragmentCycleUseCategory();
		Fragment catFertilizer=new FragmentCycleUseCategory();
		Fragment catSoilAmendment=new FragmentCycleUseCategory();
		Fragment catChemical=new FragmentCycleUseCategory();
		Fragment catLabour = new FragmentCycleUseCategory();
		Fragment catOther = new FragmentCycleUseCategory();
		
		
		Bundle data = getIntent().getExtras();
		localCycle curr = (localCycle) data.getParcelable("cycleMain");
		
		Bundle data0=new Bundle();
		data0.putString("category","general");
		data0.putParcelable("cycle",curr);
		catGeneral.setArguments(data0);
		Bundle data1=new Bundle();
		data1.putString("category", DHelper.cat_plantingMaterial);
		data1.putParcelable("cycle", curr);
		catPlantMaterial.setArguments(data1);
		Bundle data2=new Bundle();
		data2.putParcelable("cycle", curr);
		data2.putString("category", DHelper.cat_fertilizer);
		catFertilizer.setArguments(data2);
		Bundle data3=new Bundle();
		data3.putParcelable("cycle", curr);
		data3.putString("category", DHelper.cat_soilAmendment);
		catSoilAmendment.setArguments(data3);
		Bundle data4=new Bundle();
		data4.putParcelable("cycle", curr);
		data4.putString("category", DHelper.cat_chemical);
		catChemical.setArguments(data4);
		Bundle data5=new Bundle();
		data5.putParcelable("cycle", curr);
		data5.putString("category", DHelper.cat_labour);
		catLabour.setArguments(data5);
		
		Bundle data6=new Bundle();
		data6.putParcelable("cycle", curr);
		data6.putString("category", DHelper.cat_other);
		catOther.setArguments(data6);
		
		FragmentManager fm=getFragmentManager();
		FragmentTransaction ft=fm.beginTransaction();
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	

}
