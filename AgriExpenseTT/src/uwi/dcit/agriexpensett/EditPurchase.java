package uwi.dcit.AgriExpenseTT;

import com.example.agriexpensett.rpurchaseendpoint.model.RPurchase;

import helper.DHelper;
import helper.DataManager;
import helper.DbHelper;
import helper.DbQuery;
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
import dataObjects.localResourcePurchase;

public class EditPurchase extends ActionBarActivity {
	Button btn_res;
	Button btn_qtfr;
	EditText et_qty;
	EditText et_cost;
	
	TextView tv_res;
	TextView tv_qtfr;
	TextView tv_qty;
	TextView tv_cost;
	
	final int REQ_RES=1;
	String resource=null;
	final int REQ_QTFR=2;
	String quantifier=null;
	double qty;
	double cost;
	SQLiteDatabase db;
	DbHelper dbh;
	localResourcePurchase p;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_purchase);
		dbh=new DbHelper(this);
		db=dbh.getReadableDatabase();
		initialize();
		
	}
	private void initialize() {
		//setup buttons
		btn_res=(Button)findViewById(R.id.btn_editPurchase_crop);
		btn_qtfr=(Button)findViewById(R.id.btn_editPurchase_quantifier);
		Button btn_dne=(Button)findViewById(R.id.btn_editPurchase_done);
		et_qty=(EditText)findViewById(R.id.et_editPurchase_Qty);
		et_cost=(EditText)findViewById(R.id.et_editPurchase_cost);
		Click c=new Click();
		btn_res.setOnClickListener(c);
		btn_qtfr.setOnClickListener(c);
		btn_dne.setOnClickListener(c);
		
		//get data
		p=getIntent().getExtras().getParcelable("purchase");
		resource=DbQuery.findResourceName(db, dbh, p.getResourceId());
		quantifier=p.getQuantifier();
		qty=p.getQty();
		cost=p.getCost();
		
		//set up text views
		tv_res=(TextView)findViewById(R.id.tv_editPurchase_resVal);
		tv_qtfr=(TextView)findViewById(R.id.tv_editPurchase_quantifierVal);
		tv_qty=(TextView)findViewById(R.id.tv_editPurchase_QtyVal);
		tv_cost=(TextView)findViewById(R.id.tv_editPurchase_costVal);
		//initialize text views
		tv_res.setText(resource);
		tv_qtfr.setText(quantifier);
		tv_qty.setText("previous quantity:"+p.getQty());
		tv_cost.setText("previous cost:$"+p.getCost());
		
		View line=findViewById(R.id.line_header);
		//line.setBackgroundColor(Color.parseColor("#80000000"));
		//line.getBackground().setAlpha(50);
		if(p.getType().equals(DHelper.cat_plantingMaterial)){
			line.setBackgroundResource(R.color.colourPM);
			btn_dne.setBackgroundResource(R.drawable.btn_custom_plantmaterial);
		}else if(p.getType().equals(DHelper.cat_fertilizer)){
			line.setBackgroundResource(R.color.colourFer);
			btn_dne.setBackgroundResource(R.drawable.btn_custom_fertilizer);
		}else if(p.getType().equals(DHelper.cat_soilAmendment)){
			line.setBackgroundResource(R.color.colourSoil);
			btn_dne.setBackgroundResource(R.drawable.btn_custom_soilam);
		}else if(p.getType().equals(DHelper.cat_chemical)){
			line.setBackgroundResource(R.color.colourChem);
			btn_dne.setBackgroundResource(R.drawable.btn_custom_chem);
		}
		
	}
	public class Click implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent i=new Intent(EditPurchase.this,EditChooseLists.class);
			if(v.getId()==R.id.btn_editPurchase_crop){
				i.putExtra("desc",p.getType());
				startActivityForResult(i,REQ_RES);
			}else if(v.getId()==R.id.btn_editPurchase_quantifier){
				i.putExtra("desc", "quantifier");
				i.putExtra("category", p.getType());
				startActivityForResult(i,REQ_QTFR);
			}else if(v.getId()==R.id.btn_editPurchase_done){
				updatePurchase();
			}
			System.out.println("request");
		}
	}
	

	private void updatePurchase() {
		if(!(et_qty.getText().toString().equals(null)||et_qty.getText().toString().equals(""))){
			qty=Double.parseDouble(et_qty.getText().toString());
		}
		if(!(et_cost.getText().toString().equals(null)||et_cost.getText().toString().equals(""))){
			cost=Double.parseDouble(et_cost.getText().toString());
		}
		ContentValues cv = new ContentValues();
		cv.put(DbHelper.RESOURCE_PURCHASE_RESID, DbQuery.getNameResourceId(db, dbh, resource));
		cv.put(DbHelper.RESOURCE_PURCHASE_QUANTIFIER, quantifier);
		cv.put(DbHelper.RESOURCE_PURCHASE_QTY, qty);
		cv.put(DbHelper.RESOURCE_PURCHASE_COST, cost);
		//Toast.makeText(EditPurchase.this, resource+" "+quantifier+" "+qty+" "+cost, Toast.LENGTH_LONG).show();
		DataManager dm=new DataManager(EditPurchase.this, db, dbh);
		RPurchase rp=p.toRPurchase();
		dm.updatePurchase(rp, cv);
		Intent i=new Intent();
		setResult(1,i);
		finish();
	}
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_CANCELED){
			return;
		}
		if(requestCode==REQ_RES){
			resource=data.getExtras().getString("content");
			TextView t=(TextView)findViewById(R.id.tv_editPurchase_resVal);
			t.setText(resource);
		}else if(requestCode==REQ_QTFR){
			quantifier=data.getExtras().getString("content");
			TextView t=(TextView)findViewById(R.id.tv_editPurchase_quantifierVal);
			t.setText(quantifier);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_purchase, menu);
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
