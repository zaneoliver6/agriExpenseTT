package uwi.dcit.AgriExpenseTT;

import helper.DHelper;
import helper.DataManager;
import helper.DbHelper;
import helper.DbQuery;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import dataObjects.localCycle;

public class EditCycle extends ActionBarActivity {
	Button btn_crop;
	Button btn_landType;
	EditText et_landQty;
	Button btn_date;
	
	TextView tv_crop;
	TextView tv_landType;
	TextView tv_landQty;
	TextView tv_date;
	
	final int REQ_CROP=1;
	String crop=null;
	final int REQ_LANDTYPE=2;
	String land=null;
	double landQty;
	long date;
	
	SQLiteDatabase db;
	DbHelper dbh;
	
	localCycle c;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_cycle);
		dbh = new DbHelper(this);
		db= dbh.getReadableDatabase();
		initialize();
	}

	private void initialize() {
		//setup buttons
		btn_crop=(Button)findViewById(R.id.btn_editCycle_crop);
		btn_landType=(Button)findViewById(R.id.btn_editCycle_land);
		btn_date=(Button)findViewById(R.id.btn_editCycle_date);
		Button btn_done=(Button)findViewById(R.id.btn_editCycle_done);
		et_landQty=(EditText)findViewById(R.id.et_editCycle_landQty);
		Click click=new Click();
		btn_crop.setOnClickListener(click);
		btn_landType.setOnClickListener(click);
		btn_date.setOnClickListener(click);
		btn_done.setOnClickListener(click);
		
		//get Data
		//p=getIntent().getExtras().getParcelable("purchase");
		c=getIntent().getExtras().getParcelable("cycle");
		crop=DbQuery.findResourceName(db, dbh, c.getCropId());
		land=c.getLandType();
		landQty=c.getLandQty();
		date=c.getTime();
		
		//Get Text Views
		tv_crop=(TextView)findViewById(R.id.tv_editcycle_cropVal);
		tv_landType=(TextView)findViewById(R.id.tv_editcycle_landVal);
		tv_landQty=(TextView)findViewById(R.id.tv_editcycle_landQtyVal);
		tv_date=(TextView)findViewById(R.id.tv_editcycle_dateVal);
		//initialize views
		tv_crop.setText(crop);
		tv_landType.setText(land);
		tv_landQty.setText(""+landQty);
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(date);
		tv_date.setText(cal.getTime().toLocaleString());
	}
	
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_CANCELED){
			return;
		}
		if(requestCode==REQ_CROP){
			crop=data.getExtras().getString("content");
			System.out.println("result String"+crop);
			TextView t=(TextView)findViewById(R.id.tv_editcycle_cropVal);
			t.setText(crop);
		}else if(requestCode==REQ_LANDTYPE){
			land=data.getExtras().getString("content");
			System.out.println("result String"+land);
			TextView t=(TextView)findViewById(R.id.tv_editcycle_landVal);
			t.setText(land);
		}
	}
	
	public class Click implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent i=new Intent(EditCycle.this,EditChooseLists.class);
			if(v.getId()==R.id.btn_editCycle_crop){
				i.putExtra("desc",DHelper.cat_plantingMaterial);
				startActivityForResult(i,REQ_CROP);
			}else if(v.getId()==R.id.btn_editCycle_land){
				i.putExtra("desc", "land");
				startActivityForResult(i,REQ_LANDTYPE);
			}else if(v.getId()==R.id.btn_editCycle_date){
				//i.putExtra("desc", "crop");
			}else if(v.getId()==R.id.btn_editCycle_done){
				updateCycle();
			}
			System.out.println("request");
		}
	}
	
	private void updateCycle() {
		if(!(et_landQty.getText().toString().equals(null)||et_landQty.getText().toString().equals(""))){
			landQty=Double.parseDouble(et_landQty.getText().toString());
		}
		ContentValues cv=new ContentValues();
		cv.put(DbHelper.CROPCYCLE_CROPID, DbQuery.getNameResourceId(db, dbh, crop));
		cv.put(DbHelper.CROPCYCLE_LAND_TYPE,land);
		cv.put(DbHelper.CROPCYCLE_LAND_AMOUNT, landQty);
		cv.put(DbHelper.CROPCYCLE_DATE, date);
		//Toast.makeText(EditCycle.this, crop+" "+land+" "+landQty+" "+date, Toast.LENGTH_SHORT).show();
		DataManager dm=new DataManager(EditCycle.this, db, dbh);
		dm.updateCycle(c, cv);
		Intent i=new Intent();
		setResult(1,i);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_cycle, menu);
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
