package uwi.dcit.AgriExpenseTT.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import uwi.dcit.AgriExpenseTT.AddData;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class FragmentAddData extends ListFragment {
	String type;
	ArrayList<String> list;
	SQLiteDatabase db;
	DbHelper dbh;
	TextView tv_main;
	View view;
	ArrayAdapter<String> listAdapt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        if (b != null && b.containsKey("action")){
            if (b.get("action").equals("create_labour"))
                startAddData(DHelper.cat_labour);
        }else {
            dbh = new DbHelper(this.getActivity().getBaseContext());
            db = dbh.getWritableDatabase();
            populateList();
            Collections.sort(list);
            listAdapt = new ArrayAdapter<>(this.getActivity().getBaseContext(), android.R.layout.simple_list_item_1, list);
            setListAdapter(listAdapt);
            GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("Add Data Fragment");
        }
	}
		
	private void populateList() {
		list=new ArrayList<>();
        list.add(DHelper.cat_plantingMaterial);
        list.add(DHelper.cat_chemical);
        list.add(DHelper.cat_fertilizer);
        list.add(DHelper.cat_soilAmendment);
        list.add(DHelper.cat_labour);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.list_reuse, container, false); //returns the inflated layout which contains the listview
		tv_main=(TextView)view.findViewById(R.id.tv_frag_mainHead_new);
        view.findViewById(R.id.et_listReuse_search).setVisibility(View.INVISIBLE); // Remove the search bar not necessary
        tv_main.setText("Choose category of resource to be added");
		return view;
	}

    private void startAddData(String option){
        Fragment newFragment = new FragmentAddDataLast();
        Bundle b=new Bundle();
        b.putString("type", option);//pass the category to the resource
        newFragment.setArguments(b);

        if (getActivity() instanceof AddData)
            ((AddData)getActivity()).appendSub(" "+option);

        getFragmentManager()
            .beginTransaction()
            .replace(R.id.NewCycleListContainer, newFragment)
            .commit();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        startAddData(list.get(position));
    }
	 
	 public class TWatch implements TextWatcher{
		 ArrayAdapter<String> adpt;
		 public TWatch(ArrayAdapter<String> adpt){
			 super();
			 this.adpt=adpt;
		 }
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,int count) {
			adpt.getFilter().filter(s);
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			
		}
		 
	 }
}
