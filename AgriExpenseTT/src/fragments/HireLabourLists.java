package fragments;

import helper.DHelper;
import helper.DbHelper;
import helper.DbQuery;

import java.util.ArrayList;

import uwi.dcit.agriexpensett.HireLabour;
import uwi.dcit.agriexpensett.localCycle;

import com.example.agriexpensett.R;
import com.example.agriexpensett.R.id;
import com.example.agriexpensett.R.layout;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HireLabourLists extends ListFragment {
	String type;
	 ArrayList<String> list;
	SQLiteDatabase db;
	DbHelper dbh;
	localCycle currC;
	TextView et_main;
	View view;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh=new DbHelper(this.getActivity().getBaseContext());
		db=dbh.getReadableDatabase();
		type=getArguments().getString("type");
		populateList();
		ArrayAdapter<String> listAdapt = new ArrayAdapter<String>(this.getActivity().getBaseContext(),android.R.layout.simple_list_item_1,list);
		setListAdapter(listAdapt);
	}
		
	private void populateList() {
		list=new ArrayList<String>();
		if(type.equals("workers")){
			DbQuery.getResources(db, dbh,DHelper.cat_labour, list);
		}else if(type.equals("quantifier")){
			list.add("hour");
			list.add("day");
			list.add("month");
			list.add("crop cycle");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		//returns the inflated layout which contains the listview
		view= inflater.inflate(R.layout.list_reuse, container, false);

		et_main=(TextView)view.findViewById(R.id.tv_frag_mainHead_new);
		if(type.equals("workers")){
			et_main.setText("Select the person who is going to work for you");
		}else if(type.equals("quantifier")){
			et_main.setText("How is this person going to be paid");
		}
		return view;
	}
		
	
		 
	
	 @Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Toast.makeText(getActivity(), getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
			Fragment newFragment=null;
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			Bundle b=new Bundle();
			if(type.equals("workers")){
				b.putString("type", "quantifier");
				b.putString("name", list.get(position));
				((HireLabour)getActivity()).appendSub(list.get(position));
				newFragment=new FragmentLabourType();
			}else if(type.equals("quantifier")){
				try{
					currC=getArguments().getParcelable("cycle");
				}catch (Exception e){
				}
				if(currC!=null){
					b.putParcelable("cycle",currC);
				}
				b.putString("category", DHelper.cat_labour);
				b.putString("quantifier",list.get(position));
				System.out.println("res "+getArguments().getString("name"));
				b.putString("resource",getArguments().getString("name"));
				((HireLabour)getActivity()).appendSub(","+list.get(position));
				newFragment=new Fragment_newpurchaseLast();
			}
			newFragment.setArguments(b);
			// Replace whatever is in the fragment_container view with this fragment,
			// and add the transaction to the back stack
			transaction.replace(R.id.NewCycleListContainer, newFragment);
			transaction.addToBackStack(null);
			
			// Commit the transaction
			transaction.commit();
		}
}

