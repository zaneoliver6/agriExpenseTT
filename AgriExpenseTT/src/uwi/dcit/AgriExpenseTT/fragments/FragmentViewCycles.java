package uwi.dcit.AgriExpenseTT.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uwi.dcit.AgriExpenseTT.EditCycle;
import uwi.dcit.AgriExpenseTT.HireLabour;
import uwi.dcit.AgriExpenseTT.NewCycle;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.CropDataHelper;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.DateFormatHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.NavigationControl;
import uwi.dcit.AgriExpenseTT.models.CycleContract;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;

public class FragmentViewCycles extends ListFragment{
	private static final String STATE_ACTIVATED_POSITION = "cycle_activated_position";
	final int req_edit = 1;
	private String type=null;
	private View view;
	private ArrayList<LocalCycle> cycleList;
	private CycleListAdapter cycAdapt;
	private int mActivatedPosition = ListView.INVALID_POSITION;
	private DbHelper dbh;
	private SQLiteDatabase db;

	@Override
	public void onActivityCreated(Bundle savedState){
		super.onActivityCreated(savedState);
		this.registerForContextMenu(getListView());
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cycleList = new ArrayList<>();

		dbh	= new DbHelper(getActivity().getBaseContext());
		db = dbh.getReadableDatabase();

        if (getArguments() != null && getArguments().containsKey("type"))
            type = getArguments().getString("type");

//		populateList();

		cycAdapt = new CycleListAdapter(getActivity(), R.layout.cycle_list_item, cycleList);
		setListAdapter(cycAdapt);

//        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("View Cycles Fragment");
	}

	public void populateList(final View v) {
		if (cycleList == null || cycleList.size() > 0)cycleList = new ArrayList<>();
		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Cycles", "Retrieving Cycles", true);
		progressDialog.show();

		(new Thread(new Runnable() {
			@Override
			public void run() {

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


				// Update the UI
				v.post(new Runnable() {
					@Override
					public void run() {
						Log.d("FragmentViewCycles", "Retrieved:" + cycleList.size() +" records");
						cycAdapt.notifyDataSetChanged();
						progressDialog.dismiss();
					}
				});
			}
		})).start();


	}

	private void createNewCycle(){
		Intent intent = new Intent(getActivity().getApplicationContext(), NewCycle.class);
		startActivity(intent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_choose_purchase, container, false);
		final Button button = (Button) view.findViewById(R.id.fragment_choose_purchase_button);
		button.setText(R.string.add_cycle);
		button.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				createNewCycle();
			}
		});
		populateList(view);
		return view;
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
//				Log.i("Checking Fetch","::>>"+cycleList.get(info.position));
//				Log.i(Main.APP_NAME, "View The details for resource: "+cycleList.get(info.position).getCropName());
//				Log.i(Main.APP_NAME, "View The details for resource: "+cycleList.get(info.position).getCycleName());
				launchCycleUsage(info.position);
				break;
			case R.id.crop_edit: //Edit Cycle
//				Log.i(Main.APP_NAME, "Edit The details for resource: "+cycleList.get(info.position).getCropName());
				editCycleOption(info.position);
				break;
			case R.id.crop_delete:
//				Log.i(Main.APP_NAME, "Delete The details for resource: "+cycleList.get(info.position).getCropName());
				deletCycleOption(this.getListView(), info.position); //Use the same delete operation from list item click
				break;
			case R.id.crop_close:
				closeCycleOption(this.getListView(), info.position);
			default:
				return super.onContextItemSelected(item);
		}
		return false;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        setActivatedPosition(position);

		if(type == null){
			launchCycleUsage(position);
		}
		else if(type.equals(DHelper.cat_labour)){ //Assigning labour to cycle
			assignCycleToLabour(position);
		}
		else if(type.equals("delete")){ //When called by delete data
				deletCycleOption(l, position);
		}
		else if(type.equals("edit")){//when called by edit data
			editCycleOption(position);
		}
		else if(type.equals("close"))
			closeCycleOption(l, position);
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            Log.d("ViewCycles", "Saving state: " + mActivatedPosition);
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            Log.d("ViewCycles", "Setting position to: "+position);
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

	public void launchCycleUsage(int position){
        Bundle arguments = new Bundle();
		arguments.putParcelable("cycleMain",cycleList.get(position));
        Log.d("FragmentViewCycles", "Selected: " + cycleList.get(position).toString());

		Fragment newFrag= new FragmentCycleUsage();
        newFrag.setArguments(arguments);

        boolean isTablet = this.getResources().getBoolean(R.bool.isTablet);

        if(!isTablet || this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            ((NavigationControl) getActivity()).navigate(((NavigationControl) getActivity()).getLeftFrag(),newFrag);
            return;
        }
        if(getActivity() instanceof NavigationControl) {
            if(((NavigationControl) getActivity()).getRightFrag() instanceof  FragmentEmpty
                || (((NavigationControl) getActivity()).getRightFrag().getClass() == newFrag.getClass()))
                    ((NavigationControl) getActivity()).navigate(((NavigationControl) getActivity()).getLeftFrag(),newFrag);
            else
                ((NavigationControl) getActivity()).navigate(((NavigationControl) getActivity()).getRightFrag(),newFrag);
        }
	}

	public void editCycleOption(int position){
		if(cycleList.get(position).getClosed().equals("closed")){
			Toast.makeText(getActivity(), "Cannot edit a closed cycle", Toast.LENGTH_SHORT).show();
		}
		else {
			Intent i = new Intent(getActivity(), EditCycle.class);
			i.putExtra("cycle", cycleList.get(position));
			startActivityForResult(i, req_edit);
		}
	}

	public void closeCycleOption(ListView l, int position){
		if(cycleList.get(position).getClosed().equals("open")) {
			Log.i(">>>>>>>>>", "CLOSING");
			CloseConfirmator c = new CloseConfirmator(position, (CycleListAdapter) l.getAdapter());
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

			alertBuilder.setMessage("Are you sure you want to close?")
					.setCancelable(true)
					.setPositiveButton("Close", c)
					.setNegativeButton("Cancel", c)
					.create()
					.show();
		}
		else{
			Toast.makeText(getActivity(), "Cannot close a closed cycle", Toast.LENGTH_SHORT).show();
		}
	}

	public void deletCycleOption(ListView l, int position){
		if(cycleList.get(position).getClosed().equals("closed")){
			Toast.makeText(getActivity(), "Cannot delete a closed cycle", Toast.LENGTH_SHORT).show();
			Log.i("Closed","Clsoed");
		}
		else {
			//deletCycleOption(l, position);
			DeleteConfirmator c = new DeleteConfirmator(position, (CycleListAdapter) l.getAdapter());
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

			alertBuilder.setMessage("Are you sure you want to delete?")
					.setCancelable(true)
					.setPositiveButton("Delete", c)
					.setNegativeButton("Cancel", c)
					.create()
					.show();
		}
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
		populateList(view);
	}

	public class CloseConfirmator implements DialogInterface.OnClickListener{
		int position;
		CycleListAdapter listAdapter;

		public CloseConfirmator(int position,CycleListAdapter l){
			this.position=position;
			this.listAdapter=l;
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {

			if(which==DialogInterface.BUTTON_POSITIVE){

				DataManager dm=new DataManager(getActivity(), db, dbh);
				LocalCycle c = cycleList.get(position);
				c.setClosed("closed");
				ContentValues cv=new ContentValues();
				cv.put(CycleContract.CycleEntry.CROPCYCLE_CLOSED ,(c.getClosed()));
				dm.updateCycle(c,cv);
//				cycleList.remove(position);
//				listAdapter.notifyDataSetChanged();
				Toast.makeText(getActivity(),"Cycle successfully closed", Toast.LENGTH_SHORT).show();
				dialog.dismiss();
//				//DeleteExpenseList.this.finish();
			}
			else if(which==DialogInterface.BUTTON_NEGATIVE){
				dialog.cancel();
			}
		}
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
				//if(cycleList.get(position).getClosed().equals("open")) {
					dm.deleteCycle(cycleList.get(position));

					//DbQuery.deleteRecord(db,dbh, CycleContract.CycleEntry.TABLE_NAME, cycleList.get(position).getId());
					cycleList.remove(position);
					listAdapter.notifyDataSetChanged();
					Toast.makeText(getActivity(), "Cycle successfully deleted", Toast.LENGTH_SHORT).show();
					dialog.dismiss();
				//}
				//else{
				//	Toast.makeText(getActivity(), "Cannot delete a closed cycle", Toast.LENGTH_SHORT).show();
				//	dialog.dismiss();
				//}
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
            LayoutInflater inflater = ((Activity)myContext).getLayoutInflater();
            View row = inflater.inflate(R.layout.cycle_list_item, parent, false);
            //get the elements of that view and set them accordingly
            LocalCycle currCycle = cycleList.get(position);

            String txt = (currCycle.getCropName() != null ) ? currCycle.getCropName() : DbQuery.findResourceName(db, dbh, currCycle.getCropId());
            String cycleName = (currCycle.getCycleName() != null) ? currCycle.getCycleName().toUpperCase() : txt;
			String closed = currCycle.getClosed();
            ((TextView)row.findViewById(R.id.tv_cycleList_crop)).setText("Crop: " + txt);
            ((TextView)row.findViewById(R.id.tv_cycleList_name)).setText(("Name: "+ cycleName));
			if(closed.equals("closed")){
				((ImageView)row.findViewById(R.id.icon_purchaseType)).setImageResource(R.drawable.ic_launcher_web);
			} else {
				int id = CropDataHelper.getCropsDrawable(myContext, txt);
				if (id != -1) {
					((ImageView) row.findViewById(R.id.icon_purchaseType)).setImageResource(id);
				}
			}
            // TODO Use this template to insert an appropriate image for the crop cycle based on crop type

            double qty = currCycle.getLandQty();
            txt = currCycle.getLandType();
            txt = qty +" "+ txt + "s";

            ((TextView)row.findViewById(R.id.tv_cycleList_Land)).setText("Land: " + txt);
//			Log.i("FREDDDDDDDd","Date:"+currCycle.getTime());
            ((TextView)row.findViewById(R.id.tv_cycleList_date)).setText("Planted: " + DateFormatHelper.getDateStr(currCycle.getTime()));
            ((TextView)row.findViewById(R.id.tv_cycleList_harvest)).setText("Harvested: " + currCycle.getHarvestAmt()+" "+currCycle.getHarvestType());

            if (position == getSelectedItemPosition()){
				row.setBackgroundColor(ContextCompat.getColor(myContext, android.R.color.darker_gray));
			}

            return row;
		}

        @Override
        public int getCount(){
            return cycleList.size();
        }
		  
		  //register click  
	 }
}
