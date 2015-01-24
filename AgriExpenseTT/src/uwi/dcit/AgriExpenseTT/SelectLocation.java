package uwi.dcit.AgriExpenseTT;

import uwi.dcit.AgriExpenseTT.fragments.FragmentSelectLocation;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SelectLocation extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_cycle_redesigned);
//		setupInitialFrag();
	}
	
	public void replaceSub(int main, int sub) {
		((TextView)findViewById(R.id.tv_mainNew_header)).setText(main);
		((TextView)findViewById(R.id.tv_mainNew_subheader)).setText(sub);		
	}	
	
	public void setupInitialFrag() {
		replaceSub(R.id.tv_mainNew_header, R.id.tv_mainNew_subheader);
		
		Bundle arguments = new Bundle();
		arguments.putString("type", DHelper.location_country);
		
		Fragment listfrag = new FragmentSelectLocation();
		listfrag.setArguments(arguments);
		
		getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.NewCycleListContainer,listfrag)
			.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_location, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
