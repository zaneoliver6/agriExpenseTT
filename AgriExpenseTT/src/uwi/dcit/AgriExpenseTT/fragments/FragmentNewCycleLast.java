package uwi.dcit.AgriExpenseTT.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import uwi.dcit.AgriExpenseTT.Main;
import uwi.dcit.AgriExpenseTT.NewCycle;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class FragmentNewCycleLast extends Fragment {
	String plantMaterial;
	String land;
	int plantMaterialId;
	long unixDate =0;
	SQLiteDatabase db;
	DbHelper dbh;
	EditText et_landQty;
	TextView error;
    private Button btnDate;
    private EditText et_CycleName;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_newcycle_last, container, false);
		
		dbh = new DbHelper(getActivity().getBaseContext());
		db = dbh.getReadableDatabase();
		
		plantMaterial = getArguments().getString(DHelper.cat_plantingMaterial);
		land = getArguments().getString("land");
		Log.i(Main.APP_NAME, "Retrieved: "+plantMaterial+" "+land+" to be saved");
		setDetails(view);
        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("New Cycle Fragment");

        view.setOnTouchListener(
            new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!(v instanceof EditText)) ((NewCycle) getActivity()).hideSoftKeyboard();
                    return false;
                }
            }
        );

		return view;
	}
	
	private void setDetails(View view) {
        TextView landLbl = (TextView) view.findViewById(R.id.tv_newCyclelast_landQty);
		et_landQty=(EditText)view.findViewById(R.id.et_newCycleLast_landqty);
        et_CycleName = (EditText)view.findViewById(R.id.et_newCycleLast_name);
        et_CycleName.setText(plantMaterial);

		error=(TextView)view.findViewById(R.id.tv_newCycle_error);
		
		Button btnDone = (Button)view.findViewById(R.id.btn_newCyclelast_dne);
		btnDate = (Button)view.findViewById(R.id.btn_newCycleLast_date);

		landLbl.setText("Enter number of " + land + "s");//TODO revise wording and use string xml
		
		plantMaterialId= DbQuery.getNameResourceId(db, dbh, plantMaterial);
		
		NewCycleClickListener c = new NewCycleClickListener(getActivity());
		btnDate.setOnClickListener(c);
		btnDone.setOnClickListener(c);
		
		formatDisplayDate(null);
	}
	
	
	public String formatDisplayDate(Calendar calendar){
		String strDate;
		if ( calendar == null){
			calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		}
		unixDate = calendar.getTimeInMillis();
		Date d = calendar.getTime();
		strDate = DateFormat.getDateInstance().format(d);
        btnDate.setText(strDate);

		return strDate;
	}

    public class NewCycleClickListener implements OnClickListener{
        FragmentActivity activity;

        NewCycleClickListener(FragmentActivity c){
            this.activity=c;
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.btn_newCycleLast_date){
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(activity.getSupportFragmentManager(), "Choose Date");
            }else if(v.getId()==R.id.btn_newCyclelast_dne){

                if(et_landQty.getText().toString() == null || et_landQty.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Enter number of "+land+"s", Toast.LENGTH_SHORT).show();
                    error.setVisibility(View.VISIBLE);
                    error.setText("Enter the Land Quantity");
                    return;
                }
                if (et_CycleName.getText().toString().equals("")){
                    et_CycleName.setText(plantMaterial);
                }
                if(unixDate == 0){
                    Toast.makeText(getActivity().getBaseContext(),"Select a date", Toast.LENGTH_SHORT).show();
                    error.setVisibility(View.VISIBLE);
                    error.setText("Select date to start crop cycle");
                }else{


                    DataManager dm = new DataManager(getActivity().getBaseContext(), db, dbh);
//                    dm.insertCycle(plantMaterialId, land,Double.parseDouble(et_landQty.getText().toString()), unixDate);
                    dm.insertCycle(plantMaterialId,et_CycleName.getText().toString() , land,Double.parseDouble(et_landQty.getText().toString()), unixDate);

                    new IntentLauncher().run();
                    Intent i=new Intent(getActivity(),Main.class);
                    startActivity(i);
                }
            }
        }
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

    private class IntentLauncher extends Thread{
        @Override
        public void run(){getActivity().finish();}
    }

}
