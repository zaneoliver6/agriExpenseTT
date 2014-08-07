package fragments;

import helper.DHelper;
import helper.DataManager;
import helper.DbHelper;
import helper.DbQuery;

import java.util.Calendar;
import java.util.Date;

import uwi.dcit.AgriExpenseTT.CycleUseageRedesign;
import uwi.dcit.AgriExpenseTT.R;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import dataObjects.localCycle;

public class fragmentNewCycleLast extends Fragment{
	String plantMaterial;
	String land;
	int plantMaterialId;
	long unixdate=0;
	View view;
	SQLiteDatabase db;
	DbHelper dbh;
	EditText et_landQty;
	TextView tv_dte;
	TextView error;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.fragment_newcycle_last, container, false);
		//curr=savedInstanceState.getParcelable("details");
		dbh=new DbHelper(getActivity().getBaseContext());
		db=dbh.getReadableDatabase();
		plantMaterial=getArguments().getString(DHelper.cat_plantingMaterial);
		land=getArguments().getString("land");
		setDetails();
		return view;
	}
	private void setDetails() {
		System.out.println("0000000");
		TextView landLbl=(TextView)view.findViewById(R.id.tv_newCyclelast_landQty);
		et_landQty=(EditText)view.findViewById(R.id.et_newCycleLast_landqty);
		tv_dte=(TextView)view.findViewById(R.id.tv_newCycle_date);
		error=(TextView)view.findViewById(R.id.tv_newCycle_error);
		
		Button btn_dne=(Button)view.findViewById(R.id.btn_newCyclelast_dne);
		Button date=(Button)view.findViewById(R.id.btn_newCycleLast_date);//@+id/btn_newCycleLast_date
		System.out.println("land  "+land);
		landLbl.setText("Enter number of "+land+"s");
		plantMaterialId=DbQuery.getNameResourceId(db, dbh, plantMaterial);
		click c=new click(getActivity());
		date.setOnClickListener(c);
		btn_dne.setOnClickListener(c);
	}
	public class click implements OnClickListener{
		Activity c;
		click(Activity c){
			this.c=c;
		}
		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_newCycleLast_date){
				System.out.println("11111");
				showPopupDate(c);
			}else if(v.getId()==R.id.btn_newCyclelast_dne){
				Double landQty=0.0;
				if(et_landQty.getText().toString().equals(null)||et_landQty.getText().toString().equals("")){
					Toast.makeText(getActivity(), "Enter number of "+land+"s", Toast.LENGTH_SHORT).show();
					error.setVisibility(View.VISIBLE);
					error.setText("Enter the Land Quantity");
					return;
				}
				if(unixdate==0){
					Toast.makeText(getActivity().getBaseContext(),"Select a date", Toast.LENGTH_SHORT).show();
					error.setVisibility(View.VISIBLE);
					error.setText("Select date to start crop cycle");
					return;
				}else{
					
					DataManager dm=new DataManager(getActivity().getBaseContext(),db,dbh);
					landQty=Double.parseDouble(et_landQty.getText().toString());
					dm.insertCycle(plantMaterialId, land,landQty, unixdate);
					
					localCycle c=new localCycle(plantMaterialId,land,landQty,unixdate);
					Intent i=new Intent(getActivity(),CycleUseageRedesign.class);
					int n=DbQuery.getLast(db, dbh,DbHelper.TABLE_CROPCYLE);
					c.setId(n);
					i.putExtra("cycleMain",c);
					startActivity(i);
					
					getActivity().finish();
					
				}
			}
			
		}
		
	}
	//------------------------------------------------------------------DATE PICKER POPUP
		PopupWindow curr;
		
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
			//Long k=datepick.	
			Button btn_getDate=(Button)datePick.findViewById(R.id.btn_newCycle_datepick);
			class popupClick implements OnClickListener{
				
				@SuppressWarnings("deprecation")
				@Override
				public void onClick(View v) {
					if(v.getId()==R.id.btn_newCycle_datepick){
						DatePicker datepick=(DatePicker) datePick.findViewById(R.id.datePicker1);
						int day =datepick.getDayOfMonth();
						int month=datepick.getMonth();
						int year=datepick.getYear();
						Calendar calender= Calendar.getInstance();
						calender.set(year, month, day);
						unixdate=calender.getTimeInMillis();
						Date d=calender.getTime();
						tv_dte.setText(d.toLocaleString());
						Toast.makeText(getActivity().getBaseContext(), d.toLocaleString(), Toast.LENGTH_SHORT).show();
					}
					curr.dismiss();
				}
			}
			popupClick pc=new popupClick();
			btn_getDate.setOnClickListener(pc);
			// TODO Auto-generated method stub	
		}

}
