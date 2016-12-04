package uwi.dcit.AgriExpenseTT.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.HireLabour;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;

public class HireLabourLists extends ListFragment {
    
	protected String type;
	protected ArrayList<String> list;
	protected SQLiteDatabase db;
    protected DbHelper dbh;
    protected LocalCycle currC;
    protected TextView et_main;
    protected View view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh=new DbHelper(this.getActivity().getBaseContext());
		db=dbh.getWritableDatabase();
		type=getArguments().getString("type");
		populateList();
		ArrayAdapter<String> listAdapt = new ArrayAdapter<>(this.getActivity().getBaseContext(), android.R.layout.simple_list_item_1, list);
		setListAdapter(listAdapt);
//        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("Hire Labour List Fragment");
	}
		
	private void populateList() {
		list=new ArrayList<>();
		if(type.equals("workers")){
			DbQuery.getResources(db, dbh, DHelper.cat_labour, list);
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
        view = inflater.inflate(R.layout.list_reuse, container, false);

        et_main = (TextView) view.findViewById(R.id.tv_frag_mainHead_new);
        if (type.equals("workers")) {
            et_main.setText("Select the person who is going to work for you");
        } else if (type.equals("quantifier")) {
            et_main.setText("How is this person going to be paid");
        }
        return view;
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Fragment newFragment=null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle b=new Bundle();
        if(type.equals("workers")){
            b.putString("type", "quantifier");
            b.putString("name", list.get(position));
            ((HireLabour)getActivity()).replaceSub("Details:"+list.get(position));
            newFragment=new FragmentLabourType();
        }else if(type.equals("quantifier")){

            try{ currC = getArguments().getParcelable("cycle"); } catch (Exception e) {
	            e.printStackTrace();
            }

            if(currC != null)
                b.putParcelable("cycle",currC);

            b.putString("category", DHelper.cat_labour);
            b.putString("quantifier",list.get(position));
            b.putString("resource",getArguments().getString("name"));
            ((HireLabour)getActivity()).replaceSub("Details:"+getArguments().getString("name") + ", "+list.get(position));
            newFragment=new FragmentNewPurchaseLast();
        }
        if (newFragment != null) newFragment.setArguments(b);
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.NewCycleListContainer, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }
}

