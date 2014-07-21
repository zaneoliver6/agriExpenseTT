package fragments;

import helper.DHelper;
import helper.DbHelper;

import java.util.ArrayList;
import java.util.Collections;

import uwi.dcit.agriexpensett.AddData;

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

public class FragmentAddData extends ListFragment {
	String type;
	 ArrayList<String> list;
	SQLiteDatabase db;
	DbHelper dbh;
	int cycleId;
	TextView tv_main;
	TextView et_search;
	View view;
	ArrayAdapter<String> listAdapt;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh=new DbHelper(this.getActivity().getBaseContext());
		db=dbh.getReadableDatabase();
		//TextView tv_subMain=(TextView)view.findViewById(R.id.tv_frag_mainHead_new);
		//tv_main.setText("Choose category of resource");
		populateList();
		Collections.sort(list);
		listAdapt = new ArrayAdapter<String>(this.getActivity().getBaseContext(),android.R.layout.simple_list_item_1,list);
		setListAdapter(listAdapt);
	}
		
	private void populateList() {
		list=new ArrayList<String>();
		//if(type.equals("category")){
			list.add(DHelper.cat_plantingMaterial);
			list.add(DHelper.cat_chemical);
			list.add(DHelper.cat_fertilizer);
			list.add(DHelper.cat_soilAmendment);
			list.add(DHelper.cat_labour);
		//}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		//returns the inflated layout which contains the listview
		view= inflater.inflate(R.layout.list_reuse, container, false);
		tv_main=(TextView)view.findViewById(R.id.tv_frag_mainHead_new);
		et_search=(TextView)view.findViewById(R.id.et_listReuse_search);
		tv_main.setText("Choose category of resource");
		return view;
	}
		
	
		 
	
	 @Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Toast.makeText(getActivity(), getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
			Fragment newFragment=new FragmentAddDataLast();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			Bundle b=new Bundle();
			
			//pass the category to the resource
			b.putString("type", list.get(position));
			newFragment.setArguments(b);
			((AddData)getActivity()).appendSub(" "+list.get(position));
			transaction.replace(R.id.NewCycleListContainer, newFragment);
			transaction.commit();
			// Replace whatever is in the fragment_container view with this fragment,
			
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
