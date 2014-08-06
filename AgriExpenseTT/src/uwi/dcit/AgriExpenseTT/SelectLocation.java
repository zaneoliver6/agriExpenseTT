package uwi.dcit.AgriExpenseTT;

import fragments.FragmentSelectLocation;
import android.support.v7.app.ActionBarActivity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SelectLocation extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_cycle_redesigned);
		setupHeaders();
		setupFrag();
	}

	private void setupHeaders() {
		TextView tv_main=(TextView)findViewById(R.id.tv_mainNew_header);
		TextView tv_sub=(TextView)findViewById(R.id.tv_mainNew_subheader);
		
		tv_main.setText("Selecting your County");
		tv_sub.setText("This is where most/all of your crops are currently located. "
				+ "Basically where is your farm closest to");
		
	}

	private void setupFrag() {
		Fragment frag=new FragmentSelectLocation();
		FragmentManager fm=getFragmentManager();
		FragmentTransaction ft=fm.beginTransaction();
		ft.add(R.id.NewCycleListContainer, frag);
		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_location, menu);
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
}
