package fragments;

import helper.DHelper;
import helper.DataManager;
import helper.DbHelper;
import helper.DbQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uwi.dcit.AgriExpenseTT.CycleUseageRedesign;
import uwi.dcit.AgriExpenseTT.EditCycle;
import uwi.dcit.AgriExpenseTT.HireLabour;
import uwi.dcit.AgriExpenseTT.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import dataObjects.localCycle;

public class FragmentViewCycles extends ListFragment{
	String type=null;
	SQLiteDatabase db;
	DbHelper dbh;
	final int req_edit=1;
	
	ArrayList<localCycle> cList=new ArrayList<localCycle>();
	CycleListAdapter cycAdapt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh=new DbHelper(this.getActivity().getBaseContext());
		db=dbh.getReadableDatabase();
		try{//when called from hiring labour
			type=getArguments().getString("type");
		}catch (Exception e){}
		populateList();
		cycAdapt=new CycleListAdapter(getActivity().getBaseContext(), R.layout.cycle_list_item, cList);
		setListAdapter(cycAdapt);
	}
	
	private void populateList() {
		DbQuery.getCycles(db, dbh, cList);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		//returns the inflated layout which contains the listview
		return inflater.inflate(R.layout.fragment_choose_purchase, container, false);
	}
	
	public class CycleListAdapter extends ArrayAdapter<localCycle> {
		  
		 Context myContext;
		
		 public CycleListAdapter(Context context, int textViewResourceId, ArrayList<localCycle> objects) {
			 super(context, textViewResourceId, objects);
			 myContext = context;
		  }
		
		  @SuppressWarnings("deprecation")
		@SuppressLint("ViewHolder") @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
			   //return super.getView(position, convertView, parent);
			   
			   LayoutInflater inflater = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			   
			   //Get Layout of An Item and Store it in a view
			   View row=inflater.inflate(R.layout.cycle_list_item, parent, false);
			   //get the elements of that view and set them accordingly
			   TextView Crop=(TextView)row.findViewById(R.id.tv_cycleList_crop);
				
			   localCycle currCycle=cList.get(position);
			   int cid=currCycle.getCropId();
				String txt=DbQuery.findResourceName(db, dbh, cid);//getting the crop name
				Crop.setText(txt);
				ImageView imageView=(ImageView)row.findViewById(R.id.icon_purchaseType);
				imageView.setImageResource(R.drawable.crop_under_rain_solid);
				//TODO
				TextView Land=(TextView)row.findViewById(R.id.tv_cycleList_Land);
				double qty=currCycle.getLandQty();
				txt=currCycle.getLandType();
				txt=qty+" "+txt;
				Land.setText(txt);
		
				TextView DateR=(TextView)row.findViewById(R.id.tv_cycleList_date);
				TextView DayL=(TextView)row.findViewById(R.id.tv_cycleList_day);
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
				
			   return row;
		  }
		  
		  //register click  
	 }
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if(type==null){
			Intent nextActivity=new Intent(getActivity(),CycleUseageRedesign.class);
			nextActivity.putExtra("cycleMain",cList.get(position));
			startActivity(nextActivity);
		}else if(type.equals(DHelper.cat_labour)){
			FragmentManager fm=getFragmentManager();
			FragmentTransaction ft=fm.beginTransaction();
			ListFragment lf=new HireLabourLists();
			Bundle data=new Bundle();
			data.putString("type", "quantifier");
			data.putString("name", getArguments().getString("name"));
			((HireLabour)getActivity()).replaceSub("Details:"+getArguments().getString("name")
					+", cycle#"+cList.get(position).getId());
			data.putParcelable("cycle", cList.get(position));
			lf.setArguments(data);
			ft.replace(R.id.NewCycleListContainer, lf);
			ft.commit();
		}else if(type.equals("delete")){
			AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage("Are you sure you want to delete");
            builder1.setCancelable(true);
            Confirm c=new Confirm(position,(CycleListAdapter) l.getAdapter());
            builder1.setPositiveButton("Yes",c);
            builder1.setNegativeButton("Nope",c);
            AlertDialog alert1 = builder1.create();
            alert1.show();
		}else if(type.equals("edit")){//when called by edit data
	 		Intent i=new Intent(getActivity(),EditCycle.class);
	 		i.putExtra("cycle", cList.get(position));
	 		startActivityForResult(i,req_edit);
		}
	}
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		//refill list
		cList=new ArrayList<localCycle>();
		DbQuery.getCycles(db, dbh, cList);
		cycAdapt.notifyDataSetChanged();
		//call notify dataset changed
		Toast.makeText(getActivity(), "yay", Toast.LENGTH_SHORT).show();
	}

	private class Confirm implements DialogInterface.OnClickListener{
		int position;
		CycleListAdapter l;
		public Confirm(int position,CycleListAdapter l){
			this.position=position;
			this.l=l;
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(which==DialogInterface.BUTTON_POSITIVE){
				DataManager dm=new DataManager(getActivity(), db, dbh);
				dm.deleteCycle(cList.get(position));
				//DbQuery.deleteRecord(db, dbh, DbHelper.TABLE_CROPCYLE, cList.get(position).getId());
				cList.remove(position);
				l.notifyDataSetChanged();
				Toast.makeText(getActivity(),"Cycle deleted", Toast.LENGTH_SHORT).show();			
				dialog.cancel();
				//DeleteExpenseList.this.finish();
			}else if(which==DialogInterface.BUTTON_NEGATIVE){
				dialog.cancel();
			}
		}
	}
}
