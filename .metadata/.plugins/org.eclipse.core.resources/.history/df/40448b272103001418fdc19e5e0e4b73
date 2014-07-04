package com.example.agriexpensett;

import java.util.ArrayList;
import java.util.Iterator;

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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class Purchase extends ActionBarActivity {
	private String resType;
	private String res;
	private String quantifier;
	private double amount;
	private double cost;
	private TextView tv_resource;
	private TextView tv_qty;
	PopupWindow curr;
	DbHelper dbh;
	SQLiteDatabase db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase);
		dbh=new DbHelper(this);
		db=dbh.getReadableDatabase();
		setup();
		
	}

	private void setup() {
		//defaults
		resType="crop";
		tv_resource=(TextView)findViewById(R.id.tv_purchase_selRes);
		tv_qty=(TextView)findViewById(R.id.tv_purchase_qty);
		//buttons
		Button btn_resType=(Button)findViewById(R.id.btn_purchase_resType);
		Button btn_selRes=(Button)findViewById(R.id.btn_purchase_selRes);
		Button btn_qtfr=(Button)findViewById(R.id.btn_purchase_qtfr);
		Button btn_done=(Button)findViewById(R.id.btn_purchase_done);
		Click c=new Click();
		btn_resType.setOnClickListener(c);
		btn_selRes.setOnClickListener(c);
		btn_qtfr.setOnClickListener(c);
		btn_done.setOnClickListener(c);

	}
	public class Click implements OnClickListener{

		@Override
		public void onClick(View v) {
			String[] data = null;
			if(v.getId()==R.id.btn_purchase_resType){
				ArrayList<String> results=populateData(1,data);
				data=results.toArray(new String[0]);
				showPopupList(Purchase.this, 1, data);
			}else if(v.getId()==R.id.btn_purchase_selRes){
				ArrayList<String> results=populateData(2,data);
				data=results.toArray(new String[0]);
				showPopupList(Purchase.this, 2, data);
			}else if(v.getId()==R.id.btn_purchase_qtfr){
				ArrayList<String> results=populateData(3,data);
				data=results.toArray(new String[0]);
				showPopupList(Purchase.this, 3, data);
			}else if(v.getId()==R.id.btn_purchase_done){
				ArrayList<String> results=populateData(1,data);
				data=results.toArray(new String[0]);
				showPopupList(Purchase.this, 1, data);
			}
			
		}

		private ArrayList<String> populateData(int i, String[] data) {
			ArrayList<String> results=new ArrayList<String>();
			if(i==1){
				results.add("crop");
				results.add("fertilizer");
				results.add("chemical");
			}else if(i==2){
				DbQuery.getResources(db, dbh,resType, results);
			}else if(i==3){
				if(resType.equals("crop")){
					results.add("seeds");
					results.add("seedlings");
					results.add("sticks");
				}else if(resType.equals("fertilizer")){
					results.add("bags");
					results.add("Lbs");
					results.add("Kg");
				}else if(resType.equals("chemical")){
					results.add("ml");
					results.add("Oz");
					results.add("g");
				}
			}
			return results;
			
			
			//http://stackoverflow.com/questions/7969023/from-arraylist-to-array
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
					Toast.makeText(Purchase.this,tv.getText().toString()+" clicked ", Toast.LENGTH_SHORT).show();
					if(flag==1){
						resType=tv.getText().toString();
						tv_resource.setText("Select "+resType);
					}else if(flag==2){
						res=tv.getText().toString();
					}else if(flag==3){
						quantifier=tv.getText().toString();
						tv_qty.setText("Enter "+quantifier+"'s of "+res);
					}
					curr.dismiss();
				}
			};
			expns.setOnItemClickListener(itemClick);
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.purchase, menu);
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
