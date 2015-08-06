package uwi.dcit.AgriExpenseTT.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
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
import uwi.dcit.AgriExpenseTT.helpers.TextHelper;

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
    int res = -1;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_newcycle_last, container, false);
		
		dbh = new DbHelper(getActivity().getBaseContext());
		db = dbh.getWritableDatabase();
		
		plantMaterial = getArguments().getString(DHelper.cat_plantingMaterial);
        plantMaterialId= DbQuery.getNameResourceId(db, dbh, plantMaterial);

		land = getArguments().getString("land");

        setDetails(view);
//        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("New Cycle Fragment");

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

    @Override
    public void onDestroy() {
        db.close();
        super.onDestroy();
    }
	
	private void setDetails(View view) {

        TextView landLbl = (TextView) view.findViewById(R.id.tv_newCyclelast_landQty);
        landLbl.setText("Enter " + land + "s planted");

		et_landQty   = (EditText)view.findViewById(R.id.et_newCycleLast_landqty);
        et_CycleName = (EditText)view.findViewById(R.id.et_newCycleLast_name);
        et_CycleName.setText(plantMaterial);
		error = (TextView)view.findViewById(R.id.tv_newCyclelast_landQty);
		
		Button btnDone = (Button)view.findViewById(R.id.btn_newCyclelast_dne);
		btnDate = (Button)view.findViewById(R.id.btn_newCycleLast_date);

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
                    error.setText("Enter the Land Quantity");
                    error.setTextColor(getResources().getColor(R.color.helper_text_error));
                    et_landQty.getBackground().setColorFilter(getResources().getColor(R.color.helper_text_error), PorterDuff.Mode.SRC_ATOP);
                    return;
                }else{
                    error.setText(getResources().getText(R.string.hint_new_cycle_land_quantity));
                    error.setTextColor(getResources().getColor(R.color.helper_text_color));
                    et_landQty.getBackground().setColorFilter(getResources().getColor(R.color.helper_text_color), PorterDuff.Mode.SRC_ATOP);
                }
                if (et_CycleName.getText().toString().equals("")){
                    et_CycleName.setText(plantMaterial);
                }

                if(unixDate == 0){
                    formatDisplayDate(null);
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DataManager dm = new DataManager(getActivity().getBaseContext(), db, dbh);
                        res = dm.insertCycle(plantMaterialId, TextHelper.formatUserText(et_CycleName.getText().toString()) , land,Double.parseDouble(et_landQty.getText().toString()), unixDate);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res != -1)Toast.makeText(getActivity(), "Cycle Successfully Created", Toast.LENGTH_SHORT).show();
                                else Toast.makeText(getActivity(), "Unable to create Cycle", Toast.LENGTH_SHORT).show();
//
                                Bundle bundle = new Bundle();
                                bundle.putString("type", "cycle");
                                bundle.putInt("id", res);
                                Intent i = new Intent();
                                i.putExtras(bundle);
//
                                getActivity().setResult(DHelper.CYCLE_REQUEST_CODE, i );
                                if (!(getActivity() instanceof Main))
                                    getActivity().finish();
                            }
                        });
                    }
                }).start();
            }
        }
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

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
