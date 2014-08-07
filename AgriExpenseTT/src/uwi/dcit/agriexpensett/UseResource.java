package uwi.dcit.AgriExpenseTT;

import helper.DHelper;
import helper.DbHelper;
import helper.DbQuery;

import java.util.ArrayList;

import dataObjects.localCycle;
import dataObjects.localResourcePurchase;
import fragments.ChoosePurchase;
import fragments.FragmentEmpty;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;

public class UseResource extends ActionBarActivity {
	Double total;
	localCycle c;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data=this.getIntent().getExtras();
		Bundle b=data.getParcelable("cyc");
		c=b.getParcelable("cyc");
		String s=getIntent().getStringExtra("type");
		System.out.println("type before:"+s);
		String tS=getIntent().getStringExtra("total");
		total=Double.parseDouble(tS);
		setContentView(R.layout.activity_use_resource);
		setupUI();
		start(c,s);
			
	}
	public static void hideSoftKeyboard(Activity activity) {
	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Context.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
	public void setupUI() {
		View v=findViewById(R.id.container_useResource);
		TouchL l=new TouchL();
		v.setOnTouchListener(l);
	}
	public class TouchL implements OnTouchListener{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(v.getId()==R.id.container_useResource||v.getId()==R.id.container_useResource_frag)
				hideSoftKeyboard(UseResource.this);
			return false;
		}
	   
   }
	
	private void start(localCycle c, String s) {
		DbHelper dbh=new DbHelper(this);
		SQLiteDatabase db=dbh.getReadableDatabase();
		ArrayList<localResourcePurchase> pList=new ArrayList<localResourcePurchase>();
		DbQuery.getPurchases(db, dbh, pList, s, null,false);
		db.close();
		if(pList.isEmpty()){
			//View view=getLayoutInflater().inflate(R.layout.fragment_empty_purchaselist, R.id.useExpenseFrag);
			FragmentManager fm=getFragmentManager();
			FragmentTransaction ft=fm.beginTransaction();
			Fragment f=new FragmentEmpty();
			Bundle b=new Bundle();
			b.putString("type","purchase");
			b.putString("category", s);
			f.setArguments(b);
			ft.add(R.id.useExpenseFrag, f);
			ft.commit();
		}else{
			initialFrag(c,s);
		}
	}

	public double getTotal(){
		return total;
	}
	
	private void initialFrag(localCycle c,String s) {
		Bundle pass=new Bundle();
		pass.putParcelable("cycle",c);
		pass.putString("det",s);
		FragmentManager fm=getFragmentManager();
		FragmentTransaction ft=fm.beginTransaction();
		ListFragment listfrag=new ChoosePurchase();
		listfrag.setArguments(pass);
		ft.add(R.id.useExpenseFrag,listfrag);
		ft.commit();
		
		View line=findViewById(R.id.line_header_useRes);
		//line.setBackgroundColor(Color.parseColor("#80000000"));
		//line.getBackground().setAlpha(50);
		String category=getIntent().getStringExtra("type");
		if(category.equals(DHelper.cat_plantingMaterial)){
			line.setBackgroundResource(R.color.colourPM);
		}else if(category.equals(DHelper.cat_fertilizer)){
			line.setBackgroundResource(R.color.colourFer);
		}else if(category.equals(DHelper.cat_soilAmendment)){
			line.setBackgroundResource(R.color.colourSoil);
		}else if(category.equals(DHelper.cat_chemical)){
			line.setBackgroundResource(R.color.colourChem);
		}else if(category.equals(DHelper.cat_other)){
			line.setBackgroundResource(R.color.colourOther);
		}
	}
	@Override
	public void onBackPressed(){
	    FragmentManager fm = getFragmentManager();
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
			Intent i =new Intent(UseResource.this,CycleUseageRedesign.class);
	        i.putExtra("cycleMain", c);
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
