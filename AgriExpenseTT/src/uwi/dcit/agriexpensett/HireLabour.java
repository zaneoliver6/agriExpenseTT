package uwi.dcit.AgriExpenseTT;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import fragments.HireLabourLists;

public class HireLabour extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_cycle_redesigned);
		setupInitial();
	}

	private void setupInitial() {
		TextView tv_main=(TextView)findViewById(R.id.tv_mainNew_header);
		tv_main.setText("Hiring Labour");
		ListFragment start=new HireLabourLists();
		Bundle b=new Bundle();
		b.putString("type","workers");
		//b.putString(key, value);
		start.setArguments(b);
		FragmentManager fm=getFragmentManager();
		FragmentTransaction ft=fm.beginTransaction();
		ft.add(R.id.NewCycleListContainer, start);
		ft.commit();
	}
	
	public void replaceSub(String extras){
		TextView sub_head=(TextView)findViewById(R.id.tv_mainNew_subheader);
		sub_head.setText(extras);
	}
	@Override
	public void onBackPressed(){
	    FragmentManager fm = getFragmentManager();
	    if (fm.getBackStackEntryCount() > 0) {
	        Log.i("MainActivity", "popping backstack");
	        fm.popBackStack();
	    } else {
	        Log.i("MainActivity", "nothing on backstack, calling super");
	        super.onBackPressed();  
	    }
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hire_labour, menu);
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
