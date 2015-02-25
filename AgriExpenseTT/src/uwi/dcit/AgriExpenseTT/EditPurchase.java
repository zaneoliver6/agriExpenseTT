package uwi.dcit.AgriExpenseTT;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.dcit.agriexpensett.rPurchaseApi.model.RPurchase;

import java.util.Calendar;

import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.DateFormatStandard;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.models.LocalResourcePurchase;
import uwi.dcit.AgriExpenseTT.models.ResourceContract;
import uwi.dcit.AgriExpenseTT.models.ResourcePurchaseContract;

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
    long date;

	SQLiteDatabase db;
	DbHelper dbh;
	LocalResourcePurchase p;
    private TextView tv_date;
    private Button btn_date;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_purchase);
		dbh=new DbHelper(this);
		db=dbh.getReadableDatabase();
		initialize();
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Edit Purchase");
		
	}
	private void initialize() {
		//setup text views
        et_qty=(EditText)findViewById(R.id.et_editPurchase_Qty);
		et_cost=(EditText)findViewById(R.id.et_editPurchase_cost);


        //setup buttons
        btn_res=(Button)findViewById(R.id.btn_editPurchase_crop);
        btn_qtfr=(Button)findViewById(R.id.btn_editPurchase_quantifier);
        Button btn_dne=(Button)findViewById(R.id.btn_editPurchase_done);
        btn_date = (Button)findViewById((R.id.btn_editPurchase_date));
        Click c=new Click();
        btn_res.setOnClickListener(c);
		btn_qtfr.setOnClickListener(c);
        btn_date.setOnClickListener(c);
		btn_dne.setOnClickListener(c);
		
		//get data
		p = getIntent().getExtras().getParcelable("purchase");
		resource = DbQuery.findResourceName(db, dbh, p.getResourceId());
		quantifier = p.getQuantifier();
		qty = p.getQty();
		cost = p.getCost();
        date = p.getDate();

        Log.d("EditPurchase", "Integer is: " + date);

        //process date
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
		
		//set up text views
		tv_res=(TextView)findViewById(R.id.tv_editPurchase_resVal);
		tv_qtfr=(TextView)findViewById(R.id.tv_editPurchase_quantifierVal);
		tv_qty=(TextView)findViewById(R.id.tv_editPurchase_QtyVal);
		tv_cost=(TextView)findViewById(R.id.tv_editPurchase_costVal);
        tv_date = (TextView) findViewById(R.id.tv_editPurchase_dateVal);

		//initialize text views
		tv_res.setText(resource);
		tv_qtfr.setText(quantifier);
		tv_qty.setText("previous quantity:"+p.getQty());
		tv_cost.setText("previous cost:$"+p.getCost());
        tv_date.setText(DateFormatStandard.getDateStr(cal.getTime()));


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

        View v=findViewById(R.id.contEditPurchase);
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
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
			}else if (v.getId() == R.id.btn_editPurchase_date){
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "Choose Date");
            }
		}
	}
	

	private void updatePurchase() {
		if(!(et_qty.getText().toString() == null||et_qty.getText().toString().equals(""))){
			qty=Double.parseDouble(et_qty.getText().toString());
		}
		if(!(et_cost.getText().toString() == null ||et_cost.getText().toString().equals(""))){
			cost=Double.parseDouble(et_cost.getText().toString());
		}
		ContentValues cv = new ContentValues();
		cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID, DbQuery.getNameResourceId(db, dbh, resource));
		cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QUANTIFIER, quantifier);
		cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_QTY, qty);
		cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_COST, cost);
        cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_DATE, date);
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

    private void formatDisplayDate(Calendar cal) {
        tv_date.setText(DateFormatStandard.getDateStr(cal.getTime()));
        date = cal.getTimeInMillis();
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
            Calendar cal = Calendar.getInstance();
            cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
            formatDisplayDate(cal);
        }
    }
}
