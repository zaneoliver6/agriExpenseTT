package uwi.dcit.AgriExpenseTT.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uwi.dcit.AgriExpenseTT.CycleUseageRedesign;
import uwi.dcit.AgriExpenseTT.EditCycle;
import uwi.dcit.AgriExpenseTT.HireLabour;
import uwi.dcit.AgriExpenseTT.MainMenu;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.models.localCycle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentViewCycles extends ListFragment{
	String type=null;
	SQLiteDatabase db;
	DbHelper dbh;
	final int req_edit=1;
	final String className = MainMenu.APP_NAME +".FragmentViewCucles";
	
	ArrayList<localCycle> cycleList =new ArrayList<localCycle>();
	CycleListAdapter cycAdapt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh	= new DbHelper(this.getActivity().getBaseContext());
		db	= dbh.getReadableDatabase();
		
		try{//when called from hiring labour
			type = getArguments().getString("type");
			Log.i(this.className, type+" passed as a parameter");
		}catch (Exception e){ 
			Log.w(className, "No Type Passed"); 
		}
		
		populateList();
		
//		cycleList	= new ArrayList<localCycle>();
		cycAdapt 	= new CycleListAdapter(getActivity().getBaseContext(), R.layout.cycle_list_item, cycleList);
		setListAdapter(cycAdapt);
	}
	
	private void populateList() {
		DbQuery.getCycles(db, dbh, cycleList);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
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
		  @SuppressLint("ViewHolder") 
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
			   //return super.getView(position, convertView, parent);
			   
			   LayoutInflater inflater = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			   
			   //Get Layout of An Item and Store it in a view
			   View row = inflater.inflate(R.layout.cycle_list_item, parent, false);
			   //get the elements of that view and set them accordingly
			   TextView Crop = (TextView)row.findViewById(R.id.tv_cycleList_crop);
				
			   localCycle currCycle=cycleList.get(position);
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
		if(type == null){
			launchCycleUsage(position);
		}else if(type.equals(DHelper.cat_labour)){ //Assigning labour to cycle
			assignCycleToLabour(position);			
		}else if(type.equals("delete")){ //When called by delete data
			deletCycleOption(l, position);
		}else if(type.equals("edit")){//when called by edit data
			editCycleCoption(position);
		}
	}
	
	public void launchCycleUsage(int position){
		Intent activity = new Intent(getActivity(),CycleUseageRedesign.class);
		Log.i(this.className, cycleList.get(position).getCropName() + " Selected");
		activity.putExtra("cycleMain",cycleList.get(position));
		startActivity(activity);
	}
	
	public void editCycleCoption(int position){
		Intent i=new Intent(getActivity(),EditCycle.class);
 		i.putExtra("cycle", cycleList.get(position));
 		startActivityForResult(i,req_edit);
	}
	
	public void deletCycleOption(ListView l, int position){
		Confirm c=new Confirm(position,(CycleListAdapter) l.getAdapter());
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		
        alertBuilder.setMessage("Are you sure you want to delete?")
        			.setCancelable(true)        
        			.setPositiveButton("Yes",c)
        			.setNegativeButton("Nope",c)
        			.create() 
        			.show();
	}
	/**
	 * 
	 * @param position
	 */
	public void assignCycleToLabour(int position){
		ListFragment fragment	= new HireLabourLists();
		
		Bundle arguments		= new Bundle();
		arguments.putString("type", "quantifier");
		arguments.putString("name", getArguments().getString("name"));
		arguments.putParcelable("cycle", cycleList.get(position));
		
		StringBuilder stb = new StringBuilder();
		stb.append("Details: ")
			.append(getArguments().getString("name"))
			.append(", cycle#")
			.append(cycleList.get(position).getId());
		
		((HireLabour)getActivity()).replaceSub(stb.toString());
		
		
		fragment.setArguments(arguments);
		getFragmentManager()
			.beginTransaction()
				.replace(R.id.NewCycleListContainer, fragment)
				.commit();
	}
	
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		//refill list
		cycleList=new ArrayList<localCycle>();
		DbQuery.getCycles(db, dbh, cycleList);
		cycAdapt.notifyDataSetChanged();
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
				dm.deleteCycle(cycleList.get(position));
				//DbQuery.deleteRecord(db, dbh, DbHelper.TABLE_CROPCYLE, cList.get(position).getId());
				cycleList.remove(position);
				l.notifyDataSetChanged();
				Toast.makeText(getActivity(),"Cycle successfully deleted", Toast.LENGTH_SHORT).show();			
				dialog.cancel();
				//DeleteExpenseList.this.finish();
			}else if(which==DialogInterface.BUTTON_NEGATIVE){
				dialog.cancel();
			}
		}
	}
}
