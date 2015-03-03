package uwi.dcit.AgriExpenseTT;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.fragments.FragmentChoosePurchase;
import uwi.dcit.AgriExpenseTT.fragments.FragmentEmpty;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;
import uwi.dcit.AgriExpenseTT.models.LocalResourcePurchase;

public class UseResource extends ActionBarActivity {
	private Double total;
	private LocalCycle mainCycle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data = this.getIntent().getExtras();
		Bundle b 	= data.getParcelable("cyc");
		mainCycle	= b.getParcelable("cyc");
		
		String stype= getIntent().getStringExtra("type");		
		String tStr	= getIntent().getStringExtra("total");
		total		= Double.parseDouble(tStr);
		
		setContentView(R.layout.activity_use_resource);
		start(mainCycle,stype);
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Use Resources");
        View v=findViewById(R.id.cont_UseResource_main);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!(v instanceof EditText)){
                    hideSoftKeyboard();
                }
                return false;
            }
        });
	}
	
	private void start(LocalCycle cycle, String type) {
		DbHelper dbh=new DbHelper(this);
		SQLiteDatabase db=dbh.getReadableDatabase();
		ArrayList<LocalResourcePurchase> pList=new ArrayList<LocalResourcePurchase>();
		DbQuery.getPurchases(db, dbh, pList, type, null, false);
		db.close();
		if(pList.isEmpty()){
			Fragment fragment	= new FragmentEmpty();
			Bundle parameter 	= new Bundle();
			parameter.putString("type","purchase");
			parameter.putString("category", type);
			fragment.setArguments(parameter);
			
			getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.useExpenseFrag, fragment)
				.commit();
		}else{
			initialFrag(cycle,type);
		}
	}
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
	public double getTotal(){
		return total;
	}
	
	private void initialFrag(LocalCycle c,String s) {
		Bundle pass=new Bundle();
		pass.putParcelable("cycle",c);
		pass.putString("det",s);
		
		ListFragment listFrag	= new FragmentChoosePurchase();
		listFrag.setArguments(pass);
		
		getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.useExpenseFrag,listFrag)
			.commit();

		ActionBar bar = getSupportActionBar();

        String category=getIntent().getStringExtra("type");
        if(category.equals(DHelper.cat_plantingMaterial)){
            if (bar != null)bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colourPM)));
        }else if(category.equals(DHelper.cat_fertilizer)){
            if (bar != null)bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colourFer)));
        }else if(category.equals(DHelper.cat_soilAmendment)){
            if (bar != null)bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colourSoil)));
        }else if(category.equals(DHelper.cat_chemical)){
            if (bar != null)bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colourChem)));
        }else if(category.equals(DHelper.cat_other)){
            if (bar != null)bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colourChem)));
        }else if (category.equals((DHelper.cat_labour))){
            if (bar != null)bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colourLabour)));
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
	        IntentLauncher l=new IntentLauncher();
	        l.run();
	        //super.onBackPressed();  
	    }
	}
	private class IntentLauncher extends Thread{
		@Override
		public void run(){
			Intent i =new Intent(UseResource.this,CycleUseage.class);
	        i.putExtra("cycleMain", mainCycle);
			startActivity(i);
			finish();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.use_resource, menu);
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
