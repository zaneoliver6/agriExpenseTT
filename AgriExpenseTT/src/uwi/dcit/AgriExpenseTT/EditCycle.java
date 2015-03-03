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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.DateFormatHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.models.CycleContract.CycleEntry;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;

//import android.app.DatePickerDialog;
//import android.app.Dialog;

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
	
	LocalCycle cycle;
    private EditText et_name;
    private String name;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_cycle);
		dbh = new DbHelper(this);
//		db= dbh.getReadableDatabase();
        db = dbh.getWritableDatabase();
		initialize();
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Edit Cycle");
        View v=findViewById(R.id.contEditCycle);
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

	private void initialize() {

        cycle   = getIntent().getExtras().getParcelable("cycle");
		crop    = DbQuery.findResourceName(db, dbh, cycle.getCropId());
		land    = cycle.getLandType();
		landQty = cycle.getLandQty();
		date    = cycle.getTime();
        name    = cycle.getCycleName();
		
		//Get Text Views
		tv_crop     = (TextView)findViewById(R.id.tv_editcycle_cropVal);
		tv_landType = (TextView)findViewById(R.id.tv_editcycle_landVal);
		tv_landQty  = (TextView)findViewById(R.id.tv_editcycle_landQtyVal);
		tv_date     = (TextView)findViewById(R.id.tv_editcycle_dateVal);
        et_landQty  = (EditText)findViewById(R.id.et_editCycle_landQty);
        et_name     = (EditText)findViewById(R.id.et_editCycle_name);

		//initialize views
		tv_crop.setText(crop);
		tv_landType.setText(land);
		tv_landQty.setText(String.valueOf(landQty));
        et_name.setText(name);

		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(date);

		tv_date.setText(DateFormatHelper.getDateStr(cal.getTime()));
	}
	
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_CANCELED){
			return;
		}
		if(requestCode==REQ_CROP){
			crop=data.getExtras().getString("content");
			TextView t=(TextView)findViewById(R.id.tv_editcycle_cropVal);
			t.setText(crop);
		}else if(requestCode==REQ_LANDTYPE){
			land=data.getExtras().getString("content");
			TextView t=(TextView)findViewById(R.id.tv_editcycle_landVal);
			t.setText(land);
		}
	}

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    public void editCrop(View v){
        Intent i=new Intent(EditCycle.this,EditChooseLists.class);
        i.putExtra("desc",DHelper.cat_plantingMaterial);
        startActivityForResult(i,REQ_CROP);
    }

    public void editLand(View v){
        Intent i=new Intent(EditCycle.this,EditChooseLists.class);
        i.putExtra("desc", "land");
        startActivityForResult(i,REQ_LANDTYPE);
    }

    public void editDate(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "Choose Date");
    }
	
	public void updateCycle(View v) {
		if(et_landQty.getText().toString() != null && !et_landQty.getText().toString().equals("")){
			landQty = Double.parseDouble(et_landQty.getText().toString());
		}
        name = et_name.getText().toString();

		ContentValues cv = new ContentValues();
		cv.put(CycleEntry.CROPCYCLE_CROPID, DbQuery.getNameResourceId(db, dbh, crop));
		cv.put(CycleEntry.CROPCYCLE_LAND_TYPE,land);
		cv.put(CycleEntry.CROPCYCLE_LAND_AMOUNT, landQty);
		cv.put(CycleEntry.CROPCYCLE_DATE, date);
        cv.put(CycleEntry.CROPCYCLE_NAME, name);

		Toast.makeText(getApplicationContext(),"Updating "+ " "+name+crop+" "+land+" "+landQty+" "+date, Toast.LENGTH_SHORT).show();
//
		DataManager dm=new DataManager(EditCycle.this, db, dbh);
		boolean result = dm.updateCycle(cycle, cv);

        if (result) Toast.makeText(getApplicationContext(), "Cycle was successfully Updated", Toast.LENGTH_SHORT).show();
        else Toast.makeText(getApplicationContext(), "Cycle was not updated", Toast.LENGTH_SHORT).show();
//
//		Intent i=new Intent();
//		setResult(1,i);
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

    private void formatDisplayDate(Calendar cal) {
        tv_date.setText(DateFormatHelper.getDateStr(cal.getTime()));
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
