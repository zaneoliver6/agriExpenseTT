package com.example.agriexpensett;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class New_Cycle extends ActionBarActivity {
	private String crop;
	private String landType;
	private int landQty;
	private long unixdate;
	private TextView tvLandQty;
	DbHelper dbh;
	SQLiteDatabase db;
	DataManager dm;
	TransactionLog tl;
	PopupWindow curr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new__cycle);
		dbh=new DbHelper(this);
		db=dbh.getReadableDatabase();
		dm=new DataManager(this, db, dbh);
		//tl=new TransactionLog(dbh, db);
		tvLandQty=(TextView)findViewById(R.id.tv_landQty);
		setupButtons();
		
	}

	private void setupButtons() {
		Button btn_cCrop=(Button)findViewById(R.id.btn_newCycle_cCrop);
		Button btn_sLand=(Button)findViewById(R.id.btn_newCycle_sLand);
		Button btn_date=(Button)findViewById(R.id.btn_newCycle_date);
		Button btn_dne=(Button)findViewById(R.id.btn_NewCycle_done);
		Click c=new Click();
		btn_cCrop.setOnClickListener(c);
		btn_sLand.setOnClickListener(c);
		btn_date.setOnClickListener(c);
		btn_dne.setOnClickListener(c);
	}
	public class Click implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_newCycle_cCrop){
				System.out.println("new cycle");
				ArrayList<String> list=new ArrayList<String>();
				DbQuery.getResources(db, dbh, "crop", list);
				String[] items=list.toArray(new String[0]);
				showPopupList(New_Cycle.this,1,items);
			}else if(v.getId()==R.id.btn_newCycle_sLand){
            	System.out.println("Select land");
            	String[] items={"Acre","Hectre","Bed"};
				showPopupList(New_Cycle.this,2,items);
			}else if(v.getId()==R.id.btn_newCycle_date){
				System.out.println("Select date");
				showPopupDate(New_Cycle.this);
			}else if(v.getId()==R.id.btn_NewCycle_done){
				System.out.println("done");
				int cropId=DbQuery.getNameResourceId(db, dbh, crop);
				//DbQuery.insertCycle(db, dbh, cropId, landType, landQty, tl, unixdate);
				dm.insertCycle(cropId, landType, cropId, unixdate);
			}
		}
		
	}
	//-------------------------------------------------------LIST VIEW POPUP
	public void showPopupList(final Activity context,int flag,String[] items){
		int pWidth=600;
		int pHeight=550;
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		View simpList = inflater.inflate(R.layout.simple_plist, null);
		
		populateListView(simpList,items);
		registerListClick(simpList,flag);
		
		// Creating the PopupWindow
		   final PopupWindow popup = new PopupWindow(context);
		   curr=popup;
		   popup.setContentView(simpList);
		   popup.setWidth(pWidth);
		   popup.setHeight(pHeight);
		   popup.setFocusable(true);
		   // Displaying the popup at the specified location, + offsets.
		   popup.showAtLocation(simpList, Gravity.CENTER_HORIZONTAL,0, 0);
	}
	private void populateListView(View simpList,String[] myItems) {
		//build adaptor
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(
				this,				//context for activity
				android.R.layout.simple_list_item_1,	//layout to use(Create) this is what the options in the list look like
				myItems);			//items to be displayed
		//configure list view
		ListView expns=(ListView) simpList.findViewById(R.id.simpleListText);
		expns.setAdapter(adapter);
	}
	private void registerListClick(View simpList,final int flag) {
		ListView expns=(ListView) simpList.findViewById(R.id.simpleListText);
		final AdapterView.OnItemClickListener itemClick= new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> a,View viewClicked,int position,long id){
				TextView tv=(TextView)viewClicked;
				Toast.makeText(New_Cycle.this,tv.getText().toString()+" clicked ", Toast.LENGTH_SHORT).show();
				if(flag==1){
					crop=tv.getText().toString();
				}else if(flag==2){
					landType=tv.getText().toString();
					tvLandQty.setText("Select "+landType+" amount");
				}
				curr.dismiss();
			}
		};
		expns.setOnItemClickListener(itemClick);
	}
	
	//------------------------------------------------------------------DATE PICKER POPUP
		public void showPopupDate(final Activity context){
			int pWidth=800;
			int pHeight=750;
			LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			View datePick = inflater.inflate(R.layout.popup_datepicker, null);
			registerDateClick(datePick);
			// Creating the PopupWindow
			   final PopupWindow popup = new PopupWindow(context);
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
					Toast.makeText(New_Cycle.this, d.toLocaleString(), Toast.LENGTH_SHORT).show();
				}
				curr.dismiss();
			}
		}
		popupClick pc=new popupClick();
		btn_getDate.setOnClickListener(pc);
		// TODO Auto-generated method stub	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new__cycle, menu);
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
