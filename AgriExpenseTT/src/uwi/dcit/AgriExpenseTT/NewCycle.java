package uwi.dcit.AgriExpenseTT;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import uwi.dcit.AgriExpenseTT.fragments.NewCycleLists;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class NewCycle extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_cycle);
		setupInitialFrag();
//        setupNavDrawer();

        //Google Analytics
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("New Cycle");
	}
	
	public void replaceSub(String text){
		((TextView)findViewById(R.id.tv_mainNew_subheader)).setText(text);
	}
	
	private void setupInitialFrag() {
		Bundle arguments = new Bundle();
		arguments.putString("type",DHelper.cat_plantingMaterial);
		
		ListFragment listfrag = new NewCycleLists();
		listfrag.setArguments(arguments);
		
		this.getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.NewCycleListContainer,listfrag)
			.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_cycle, menu);
		return true;
	}

	@Override
	public void onBackPressed(){
	    FragmentManager fm = this.getSupportFragmentManager();
	    if (fm.getBackStackEntryCount() > 0) {
	        Log.i("MainActivity", "popping backstack");
	        fm.popBackStack();
	    } else {
	        Log.i("MainActivity", "nothing on backstack, calling super");
	        super.onBackPressed();  
	    }
	}

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
