package uwi.dcit.AgriExpenseTT.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
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
import uwi.dcit.AgriExpenseTT.models.LocalCycle;

public class FragmentNewCycleLast extends Fragment {
	String plantMaterial;
	String land;
	int plantMaterialId;
	long unixdate=0;
//	View view;
	SQLiteDatabase db;
	DbHelper dbh;
	EditText et_landQty;
	TextView tv_dte;
	TextView error;

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
                        if (!(v instanceof EditText)) {
                            ((NewCycle) getActivity()).hideSoftKeyboard();
                        }
                        return false;
                    }
                }
        );

		return view;
	}
	
	private void setDetails(View view) {
        TextView landLbl = (TextView) view.findViewById(R.id.tv_newCyclelast_landQty);
		et_landQty=(EditText)view.findViewById(R.id.et_newCycleLast_landqty);
		tv_dte=(TextView)view.findViewById(R.id.tv_newCycle_date);
		error=(TextView)view.findViewById(R.id.tv_newCycle_error);
		
		Button btnDone = (Button)view.findViewById(R.id.btn_newCyclelast_dne);
		Button btnDate = (Button)view.findViewById(R.id.btn_newCycleLast_date);//@+id/btn_newCycleLast_date
		landLbl.setText("Enter number of " + land + "s");//TODO revise wording and use string xml
		
		plantMaterialId= DbQuery.getNameResourceId(db, dbh, plantMaterial);
		
		MyClickListener c = new MyClickListener(getActivity());
		btnDate.setOnClickListener(c);
		btnDone.setOnClickListener(c);
		
		formatDisplayDate(null); //Attempt to set the date to default to today//TODO test
	}
	
	
	public String formatDisplayDate(Calendar calender){
		String strDate;
		if ( calender == null){
			calender = Calendar.getInstance();
			calender.set(Calendar.HOUR_OF_DAY, 0);
			calender.set(Calendar.MINUTE, 0);
			calender.set(Calendar.SECOND, 0);
			calender.set(Calendar.MILLISECOND, 0);
		}
		unixdate = calender.getTimeInMillis();
		Date d = calender.getTime();
		strDate = DateFormat.getDateInstance().format(d);
		tv_dte.setText(strDate);
		return strDate;
	}
	

	//------------------------------------------------------------------DATE PICKER POPUP
		PopupWindow curr;
		
		@SuppressLint("InflateParams")
		public void showPopupDate(final Activity c){
				int pWidth=800;
				int pHeight=750;
				LayoutInflater inflater = (LayoutInflater)getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View datePick = inflater.inflate(R.layout.popup_datepicker, null);
				
				registerDateClick(datePick);
				// Creating the PopupWindow
				System.out.println("222222");
				   final PopupWindow popup = new PopupWindow(c);
				   curr=popup;
				   popup.setContentView(datePick);
				   popup.setWidth(pWidth);
				   popup.setHeight(pHeight);
				   popup.setFocusable(true);
				   // Displaying the popup at the specified location, + offsets.
				   popup.showAtLocation(datePick, Gravity.CENTER_HORIZONTAL,0, 0);
			}
		
		
		
		private void registerDateClick(final View datePick) {
			Button btn_getDate=(Button)datePick.findViewById(R.id.btn_newCycle_datepick);
			class popupClick implements OnClickListener{
//				@SuppressWarnings("deprecation")
				@Override
				public void onClick(View v) {
					if(v.getId()==R.id.btn_newCycle_datepick){
						DatePicker datepick=(DatePicker) datePick.findViewById(R.id.datePicker1);
						int day =datepick.getDayOfMonth();
						int month=datepick.getMonth();
						int year=datepick.getYear();
						Calendar calender= Calendar.getInstance();
						calender.set(year, month, day);
						formatDisplayDate(calender);						
					}
					curr.dismiss();
				}
			}
			popupClick pc=new popupClick();
			btn_getDate.setOnClickListener(pc);
		}
		
		
		public class MyClickListener implements OnClickListener{
			Activity activity;
			
			MyClickListener(Activity c){
				this.activity=c;
			}
			
			@Override
			public void onClick(View v) {
				if(v.getId()==R.id.btn_newCycleLast_date){
					System.out.println("11111");
					showPopupDate(activity);
				}else if(v.getId()==R.id.btn_newCyclelast_dne){
					Double landQty;
					if(et_landQty.getText().toString() == null ||et_landQty.getText().toString().equals("")){
						Toast.makeText(getActivity(), "Enter number of "+land+"s", Toast.LENGTH_SHORT).show();
						error.setVisibility(View.VISIBLE);
						error.setText("Enter the Land Quantity");
						return;
					}
					if(unixdate==0){
						Toast.makeText(getActivity().getBaseContext(),"Select a date", Toast.LENGTH_SHORT).show();
						error.setVisibility(View.VISIBLE);
						error.setText("Select date to start crop cycle");
					}else{
						
						DataManager dm=new DataManager(getActivity().getBaseContext(),db,dbh);
						landQty=Double.parseDouble(et_landQty.getText().toString());
						dm.insertCycle(plantMaterialId, land,landQty, unixdate);
						
//						LocalCycle c=new LocalCycle(plantMaterialId,land,landQty,unixdate);
						Intent i=new Intent(getActivity(),Main.class);
//						int n=DbQuery.getLast(db, dbh, CycleContract.CycleEntry.TABLE_NAME);
//						c.setId(n);
//						i.putExtra("cycleMain",c);
                        new IntentLauncher().run();
						startActivity(i);
//						getActivity().finish();
						
					}
				}
				
			}
			
		}
    private class IntentLauncher extends Thread{
        @Override
        public void run(){getActivity().finish();}
    }

}
