package uwi.dcit.AgriExpenseTT;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import uwi.dcit.AgriExpenseTT.fragments.HireLabourLists;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;


public class HireLabour extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_cycle);
//		setupInitial(); // Commented out because it may run twice based on => http://developer.android.com/reference/android/app/Activity.html
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Hire Labour");
	}

    @Override
    public void onResume(){
        super.onResume();
        setupInitial();
    }

	private void setupInitial() {
		ListFragment start = new HireLabourLists();
		Bundle b = new Bundle();
		b.putString("type","workers");
		start.setArguments(b);

		getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.NewCycleListContainer, start)
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
	    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
	    } else {
	        super.onBackPressed();
	    }
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.hire_labour, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create_labour) {
            Bundle b = new Bundle();
            b.putString("action", "create_labour");
            Intent i = new Intent(this, AddData.class);
            i.putExtras(b);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
