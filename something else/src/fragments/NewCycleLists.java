package fragments;

import helper.DHelper;
import helper.DbHelper;
import helper.DbQuery;

import java.util.ArrayList;
import java.util.Collections;

import uwi.dcit.agriexpensett.NewCycleRedesigned;

import com.example.agrinetexpensestt.R;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NewCycleLists extends ListFragment {
	String type;
	 ArrayList<String> list;
	SQLiteDatabase db;
	DbHelper dbh;
	int cycleId;
	TextView et_main;
	TextView et_search;
	ArrayAdapter<String> listAdapt;
	View view;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh=new DbHelper(this.getActivity().getBaseContext());
		db=dbh.getReadableDatabase();
		type=getArguments().getString("type");
		populateList();
		Collections.sort(list);
		listAdapt = new ArrayAdapter<String>(this.getActivity().getBaseContext(),android.R.layout.simple_list_item_1,list);
		setListAdapter(listAdapt);
	}
		
	private void populateList() {
		list=new ArrayList<String>();
		if(type.equals(DHelper.cat_plantingMaterial)){
			DbQuery.getResources(db, dbh,DHelper.cat_plantingMaterial, list);
		}else if(type.equals("land")){
			list.add("Acre");
			list.add("Hectre");
			list.add("Bed");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		//returns the inflated layout which contains the listview
		view= inflater.inflate(R.layout.list_reuse, container, false);

		et_main=(TextView)view.findViewById(R.id.tv_frag_mainHead_new);
		et_search=(TextView)view.findViewById(R.id.et_listReuse_search);
		if(getArguments().getString("type").equals("land")){
			et_search.setVisibility(View.GONE);
		}else{
			TWatch tw=new TWatch(listAdapt);
			et_search.addTextChangedListener(tw);
		}
		if(type.equals(DHelper.cat_plantingMaterial)){
			et_main.setText("Select the crop to plant for this cycle");
		}else if(type.equals("land")){
			et_main.setText("Select the type of land you are using");
		}
		return view;
	}
		
	
		 
	
	 @Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Toast.makeText(getActivity(), getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
			Fragment newFragment=null;
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			Bundle b=new Bundle();
			
			
			if(type.equals(DHelper.cat_plantingMaterial)){
				b.putString("type","land");//passes the type of the data we want in the new listfragment
				b.putString(DHelper.cat_plantingMaterial, list.get(position));//passes the crop chosen to the land listfragment
				((NewCycleRedesigned)getActivity()).replaceSub("Details: "+list.get(position));
				newFragment =new NewCycleLists();
			}else if(type.equals("land")){
				b.putString(DHelper.cat_plantingMaterial, getArguments().getString(DHelper.cat_plantingMaterial));
				//System.out.println("planting material: "+getArguments().getString(DHelper.cat_plantingMaterial));
				//System.out.println(list.get(position));
				b.putString("land", list.get(position));
				((NewCycleRedesigned)getActivity()).replaceSub("Details: "+getArguments().getString(DHelper.cat_plantingMaterial)
						+" "+list.get(position));
				newFragment =new fragmentNewCycleLast();
			}
			newFragment.setArguments(b);
			transaction.replace(R.id.NewCycleListContainer, newFragment);
			
			//add the transaction to the back stack
			transaction.addToBackStack(type);
			
			// Commit the transaction
			transaction.commit();
	 }
	 public class TWatch implements TextWatcher{
		 ArrayAdapter<String> adpt;
		 public TWatch(ArrayAdapter<String> adpt){
			 super();
			 this.adpt=adpt;
		 }
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			adpt.getFilter().filter(s);
			// TODO Auto-generated method stub
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
		 
	 }
} 
	