package com.example.agriexpensett;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Build;

public class ViewCycles extends ActionBarActivity {
	DbHelper dbh;
	SQLiteDatabase db;
	private ArrayList<localCycle> cycleList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_cycles);
		cycleList=new ArrayList<localCycle>();
		dbh=new DbHelper(this);
		db=dbh.getReadableDatabase();
		populateArrayList();
		populateList();
		registerClick();
		
	}

	
	private void populateArrayList() {
		DbQuery.getCycles(db, dbh, cycleList);
	}
	
	private void populateList() {
		ArrayAdapter<localCycle> cycleAdptr=new cycleAdapter();
		ListView list=(ListView)findViewById(R.id.listView_cycles);
		list.setAdapter(cycleAdptr);
	}
	public class cycleAdapter extends ArrayAdapter{

		public cycleAdapter() {
			super(ViewCycles.this,R.layout.cycle_list_item,cycleList);
			// TODO Auto-generated constructor stub
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView=convertView;
			if(itemView==null){//ensures view is not null
				itemView=getLayoutInflater().inflate(R.layout.cycle_list_item,parent,false);
			}
			//find the expense to work with
			localCycle currCycle=cycleList.get(position);
			
			//fill the layout's views with the relevant information
			
			
			//make:
			TextView Crop=(TextView)itemView.findViewById(R.id.tv_cycleList_crop);
			int cid=currCycle.getCropId();
			String txt=DbQuery.findResourceName(db, dbh, cid);//getting the crop name
			Crop.setText(txt);
			ImageView imageView=(ImageView)itemView.findViewById(R.id.icon_purchaseType);
			if(txt.equals("tomatoe")){
				imageView.setImageResource(R.drawable.icon_tomatoe3);
			}else if(txt.equals("cassava")){
				imageView.setImageResource(R.drawable.icon_cassava1);
			}else if(txt.equals("sweet pepper")){
				imageView.setImageResource(R.drawable.icon_sweetpepper2);
			}
			
			
			TextView Land=(TextView)itemView.findViewById(R.id.tv_cycleList_Land);
			double qty=currCycle.getLandQty();
			txt=currCycle.getLandType();
			txt=qty+" "+txt;
			Land.setText(txt);
	
			TextView DateR=(TextView)itemView.findViewById(R.id.tv_cycleList_date);
			TextView DayL=(TextView)itemView.findViewById(R.id.tv_cycleList_day);
			Long dateMils=currCycle.getTime();
			Calendar calender=Calendar.getInstance();
			calender.setTimeInMillis(dateMils);
			
			cid=calender.get(Calendar.DAY_OF_WEEK);
			String[] days={"Sun","Mon","Tue","Wed","Thur","Fri","Sat"};
			
			if(cid==7){
				DayL.setText(days[6]);
			}else{
				DayL.setText(days[cid]);
			}
			Date d=calender.getTime();
			DateR.setText(d.toLocaleString());
			
			int i=0;
			return itemView;
		}	
	}
	private void registerClick() {
		ListView list=(ListView)findViewById(R.id.listView_cycles);
		AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Intent nextActivity=new Intent(ViewCycles.this,CycleUseageRedesign.class);
				nextActivity.putExtra("cycleMain", cycleList.get(position));
				startActivity(nextActivity);
				//Toast.makeText(Expense_day.this,dExpenses.get(position).getCategory(), Toast.LENGTH_SHORT).show();				
			}
		};
		list.setOnItemClickListener(itemListener);
		
	}

	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_cycles, menu);
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
