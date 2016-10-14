package uwi.dcit.AgriExpenseTT.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import java.util.Calendar;

import uwi.dcit.AgriExpenseTT.Main;
import uwi.dcit.AgriExpenseTT.NewCycle;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.DateFormatHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.TextHelper;

public class FragmentNewCycleLast extends Fragment implements DatePickerDialog.OnDateSetListener {
	private String plantMaterial;
	private String land;
    private int plantMaterialId;
    private long unixDate =0;
    private SQLiteDatabase db;
    private DbHelper dbh;
    private EditText et_landQty;
    private TextView error;
    private Button btnDate;
    private EditText et_CycleName;
    private int res = -1;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_newcycle_last, container, false);
		
		dbh = new DbHelper(getActivity().getBaseContext());
		db = dbh.getWritableDatabase();
		
		plantMaterial = getArguments().getString(DHelper.cat_plantingMaterial);
        plantMaterialId= DbQuery.getNameResourceId(db, dbh, plantMaterial);

		land = getArguments().getString("land");

        setDetails(view);
        unixDate = Calendar.getInstance().getTimeInMillis();

        view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!(v instanceof EditText)) ((NewCycle) getActivity()).hideSoftKeyboard();
                    return false;
                }
            });

		return view;
	}

	private void setDetails(View view) {

        TextView landLbl = (TextView) view.findViewById(R.id.tv_newCyclelast_landQty);
        landLbl.setText(String.format("Enter %ss planted", land));
        error = landLbl;

		et_landQty   = (EditText)view.findViewById(R.id.et_newCycleLast_landqty);
        et_CycleName = (EditText)view.findViewById(R.id.et_newCycleLast_name);
        et_CycleName.setText(plantMaterial);


		Button btnDone = (Button)view.findViewById(R.id.btn_newCyclelast_dne);
		btnDate = (Button)view.findViewById(R.id.btn_newCycleLast_date);

		NewCycleClickListener c = new NewCycleClickListener(this);
		btnDate.setOnClickListener(c);
		btnDone.setOnClickListener(c);
		
		String format = DateFormatHelper.formatDisplayDate(null);
        btnDate.setText(format);
        unixDate = Calendar.getInstance().getTimeInMillis();
	}

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar cal = Calendar.getInstance();
        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        String format = DateFormatHelper.formatDisplayDate(cal);
        btnDate.setText(format);
        unixDate = cal.getTimeInMillis();
    }


    public class NewCycleClickListener implements OnClickListener{
        FragmentNewCycleLast fragment;

        NewCycleClickListener(FragmentNewCycleLast c){
            this.fragment =c;
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.btn_newCycleLast_date){
                final Calendar c = Calendar.getInstance();
                final int year = c.get(Calendar.YEAR);
                final int month = c.get(Calendar.MONTH);
                final int day = c.get(Calendar.DAY_OF_MONTH);
                (new DatePickerDialog(fragment.getActivity(), fragment, year, month, day)).show();

            }else if(v.getId()==R.id.btn_newCyclelast_dne){

                if (et_landQty.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), String.format("Enter number of %ss", land), Toast.LENGTH_SHORT).show();
                    error.setText(R.string.enter_land_quantity);
                    error.setTextColor(ContextCompat.getColor(fragment.getActivity(), R.color.helper_text_error));
                    et_landQty.getBackground().setColorFilter(ContextCompat.getColor(fragment.getActivity(), R.color.helper_text_error), PorterDuff.Mode.SRC_ATOP);
                    return;
                }else{
                    error.setText(getResources().getText(R.string.hint_new_cycle_land_quantity));
                    error.setTextColor(ContextCompat.getColor(fragment.getActivity(), R.color.helper_text_color));
                    et_landQty.getBackground().setColorFilter(ContextCompat.getColor(fragment.getActivity(), R.color.helper_text_color), PorterDuff.Mode.SRC_ATOP);
                }
                if (et_CycleName.getText().toString().equals("")){
                    et_CycleName.setText(plantMaterial);
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DataManager dm = new DataManager(getActivity().getBaseContext(), db, dbh);
//                        if(DbQuery.getAccount(db)!=null){
                            res = dm.insertCycle(plantMaterialId, TextHelper.formatUserText(et_CycleName.getText().toString()) , land,Double.parseDouble(et_landQty.getText().toString()), unixDate, "open");

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
}
