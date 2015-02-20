package uwi.dcit.AgriExpenseTT.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import uwi.dcit.AgriExpenseTT.EditCycle;
import uwi.dcit.AgriExpenseTT.HireLabour;
import uwi.dcit.AgriExpenseTT.Main;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.DateFormatStandard;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.helpers.NavigationControl;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;

public class FragmentViewCycles extends ListFragment{
	String type=null;
	SQLiteDatabase db;
	DbHelper dbh;
	final int req_edit=1;
	final String className = "ViewCycles";

	
	ArrayList<LocalCycle> cycleList = new ArrayList<LocalCycle>();
	CycleListAdapter cycAdapt;
	
	@Override
	public void onActivityCreated(Bundle savedState){
		super.onActivityCreated(savedState);
		this.registerForContextMenu(getListView());
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh	= new DbHelper(this.getActivity().getBaseContext());
		db	= dbh.getReadableDatabase();
		
		try{//when called from hiring labour
			type = getArguments().getString("type");
		}catch (Exception e){
			Log.i(className, "No Type Passed");
		}
		populateList();
		cycAdapt = new CycleListAdapter(getActivity().getBaseContext(), R.layout.cycle_list_item, cycleList);
		setListAdapter(cycAdapt);

        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("View Cycles Fragment");
	}
	
	public void populateList() {
		DbQuery.getCycles(db, dbh, cycleList);
		//Attempt to solve the List of Cycles in Descending order of time (Most recent cycle first)
		Collections.sort(cycleList, new Comparator<LocalCycle>(){
			@Override
			public int compare(LocalCycle item1, LocalCycle item2) {
				if (item1.getTime() == item2.getTime())return 0;
				else if (item1.getTime() > item2.getTime())return -1;
				else return 1;
			}			
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_choose_purchase, container, false);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuInflater inflater = this.getActivity().getMenuInflater();
		inflater.inflate(R.menu.resource_crop_context_menu, menu);
	}
	
	public boolean onContextItemSelected(MenuItem item){
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		
		switch(item.getItemId()){
			case R.id.crop_view:
				Log.i(Main.APP_NAME, "View The details for resource: "+cycleList.get(info.position).getCropName());
				launchCycleUsage(info.position);
				break;
			case R.id.crop_edit: //Edit Cycle
				Log.i(Main.APP_NAME, "Edit The details for resource: "+cycleList.get(info.position).getCropName());
				editCycleCoption(info.position);
				break;
			case R.id.crop_delete:
				Log.i(Main.APP_NAME, "Delete The details for resource: "+cycleList.get(info.position).getCropName());
				deletCycleOption(this.getListView(), info.position); //Use the same delete operation from list item click
				break;
			default:
				return super.onContextItemSelected(item);
		}
		return false;
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
//		Intent activity = new Intent(getActivity(),CycleUseageRedesign.class);
        Bundle arguments = new Bundle();
		Log.i(this.className, cycleList.get(position).getCropName() + " Selected");
		arguments.putParcelable("cycleMain",cycleList.get(position));
		Fragment newFrag= new FragmentCycleUseage();
        newFrag.setArguments(arguments);
        boolean isTablet = this.getResources().getBoolean(R.bool.isTablet);

        if(!isTablet || this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            ((NavigationControl) getActivity()).navigate(((NavigationControl) getActivity()).getLeftFrag(),newFrag);
            return;
        }
        if(getActivity() instanceof NavigationControl) {
            if(((NavigationControl) getActivity()).getRightFrag() instanceof  FragmentEmpty
            ||(((NavigationControl) getActivity()).getRightFrag().getClass()==newFrag.getClass()))
                ((NavigationControl) getActivity()).navigate(((NavigationControl) getActivity()).getLeftFrag(),newFrag);
            else
                ((NavigationControl) getActivity()).navigate(((NavigationControl) getActivity()).getRightFrag(),newFrag);
        }
	}
	
	public void editCycleCoption(int position){
		Intent i = new Intent(getActivity(),EditCycle.class);
 		i.putExtra("cycle", cycleList.get(position));
 		startActivityForResult(i,req_edit);
	}
	
	public void deletCycleOption(ListView l, int position){
		DeleteConfirmator c=new DeleteConfirmator(position,(CycleListAdapter) l.getAdapter());
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		
        alertBuilder.setMessage("Are you sure you want to delete?")
        			.setCancelable(true)        
        			.setPositiveButton("Yes",c)
        			.setNegativeButton("Nope",c)
        			.create() 
        			.show();
	}
	
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
		cycleList=new ArrayList<LocalCycle>();
		DbQuery.getCycles(db, dbh, cycleList);
		cycAdapt.notifyDataSetChanged();
	}

	public class DeleteConfirmator implements DialogInterface.OnClickListener{
		int position;
		CycleListAdapter listAdapter;
		
		public DeleteConfirmator(int position,CycleListAdapter l){
			this.position=position;
			this.listAdapter=l;
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			if(which==DialogInterface.BUTTON_POSITIVE){
				
				DataManager dm=new DataManager(getActivity(), db, dbh);
				dm.deleteCycle(cycleList.get(position));
				
				//DbQuery.deleteRecord(db, dbh, DbHelper.TABLE_CROPCYLE, cList.get(position).getId());
				cycleList.remove(position);
				listAdapter.notifyDataSetChanged();
				Toast.makeText(getActivity(),"Cycle successfully deleted", Toast.LENGTH_SHORT).show();			
				dialog.cancel();
				//DeleteExpenseList.this.finish();
			}else if(which==DialogInterface.BUTTON_NEGATIVE){
				dialog.cancel();
			}
		}
	}
	
	public class CycleListAdapter extends ArrayAdapter<LocalCycle> {
        Context myContext;
        public CycleListAdapter(Context context, int textViewResourceId, ArrayList<LocalCycle> objects) {
		    super(context, textViewResourceId, objects);
		    myContext = context;
		}

        @SuppressLint("ViewHolder")
		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Get Layout of An Item and Store it in a view
            View row = inflater.inflate(R.layout.cycle_list_item, parent, false);
            //get the elements of that view and set them accordingly
            LocalCycle currCycle=cycleList.get(position);
            int cid = currCycle.getCropId();

            String txt=DbQuery.findResourceName(db, dbh, cid);//getting the crop name
            ((TextView)row.findViewById(R.id.tv_cycleList_crop)).setText(txt);

            // TODO Use this template to insert an appropriate image for the crop cycle based on crop type
//            ImageView imageView=(ImageView)row.findViewById(R.id.icon_purchaseType);
//            imageView.setImageResource(R.drawable.crop_under_rain_solid);

            double qty=currCycle.getLandQty();
            txt=currCycle.getLandType();
            txt=qty+" "+txt;
            ((TextView)row.findViewById(R.id.tv_cycleList_Land)).setText(txt);

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

            ((TextView)row.findViewById(R.id.tv_cycleList_date)).setText(DateFormatStandard.getDateStr(calender.getTime()));

            return row;
		}
		  
		  //register click  
	 }
}
