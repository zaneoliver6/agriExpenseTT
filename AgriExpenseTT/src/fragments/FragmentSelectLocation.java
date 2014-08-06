package fragments;

import helper.DbHelper;

import java.util.ArrayList;
import java.util.Collections;

import uwi.dcit.AgriExpenseTT.R;
import android.app.ListFragment;
import android.content.Intent;
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

public class FragmentSelectLocation extends ListFragment {
	String type;
	 ArrayList<String> list;
	SQLiteDatabase db;
	DbHelper dbh;
	TextView tv_main;
	TextView et_search;
	ArrayAdapter<String> listAdapt;
	View view;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh=new DbHelper(this.getActivity().getBaseContext());
		db=dbh.getReadableDatabase();
		populateList();
		Collections.sort(list);
		listAdapt = new ArrayAdapter<String>(this.getActivity().getBaseContext(),android.R.layout.simple_list_item_1,list);
		setListAdapter(listAdapt);
	}
		
	private void populateList() {
		list=new ArrayList<String>();
		list.add("St. George");
		list.add("St. David");
		list.add("Caroni");
		list.add("St. Andrew");
		list.add("Victoria");
		list.add("Nariva");
		list.add("St. Patrick");
		list.add("Mayaro");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		//returns the inflated layout which contains the listview
		view= inflater.inflate(R.layout.list_reuse, container, false);

		tv_main=(TextView)view.findViewById(R.id.tv_frag_mainHead_new);
		et_search=(TextView)view.findViewById(R.id.et_listReuse_search);
		tv_main.setText("Select the county you belong to");
		return view;
	}
		
	
		 
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Toast.makeText(getActivity(), getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
		Intent i=new Intent();
		i.putExtra("county", list.get(position));
		getActivity().setResult(1,i);//used to set the results for the parent activity ( the one that launched this one)
		getActivity().finish();
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
	