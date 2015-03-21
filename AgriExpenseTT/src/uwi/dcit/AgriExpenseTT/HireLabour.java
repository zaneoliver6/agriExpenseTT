package uwi.dcit.AgriExpenseTT;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import uwi.dcit.AgriExpenseTT.fragments.HireLabourLists;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;


public class HireLabour extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_cycle);
		setupInitial();
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Hire Labour");
	}

	private void setupInitial() {
		ListFragment start = new HireLabourLists();
		Bundle b = new Bundle();
		b.putString("type","workers");
		start.setArguments(b);

		getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.NewCycleListContainer, start)
                .commit();
	}
	
	public void replaceSub(String extras){
		TextView sub_head = (TextView)findViewById(R.id.tv_mainNew_subheader);
		sub_head.setText(extras);
	}

    public void hideSoftKeyboard() {
        if(getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
	@Override
	public void onBackPressed(){
	    FragmentManager fm = getSupportFragmentManager();
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

	/**
	 * A placeholder fragment containing a simple view.
	 */

}
