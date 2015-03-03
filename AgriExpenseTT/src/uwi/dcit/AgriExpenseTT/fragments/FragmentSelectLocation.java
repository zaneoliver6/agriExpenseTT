package uwi.dcit.AgriExpenseTT.fragments;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.models.CountryContract;
import uwi.dcit.AgriExpenseTT.models.CountyContract;

public class FragmentSelectLocation extends ListFragment {
	protected String type;
	protected String country;
	protected String county;
	
	protected ArrayList<String> list;
	protected SQLiteDatabase db;
	protected DbHelper dbh;
	protected TextView tv_main;
//	protected TextView et_search;
	protected ArrayAdapter<String> listAdapt;
	protected View view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh = new DbHelper(this.getActivity().getBaseContext());
		db = dbh.getWritableDatabase();
		
		type = this.getArguments().getString("type");
		if (this.getArguments().containsKey("country"))
			this.country = this.getArguments().getString("country");
		if(this.getArguments().containsKey("county"))
			this.county = this.getArguments().getString("county");
		
		populateList();		
		listAdapt = new ArrayAdapter<String>(this.getActivity().getBaseContext(),android.R.layout.simple_list_item_1,list);
		setListAdapter(listAdapt);
        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("Select Location Fragment");
	}
	
	
		
	private void populateList() {		
		list = new ArrayList<String>();
        //country selection
		if (type.equals(DHelper.location_country)){
            DbQuery.getCountries(db,list);
        //county selection
		}else if (type.equals(DHelper.location_county)){
            county=null;

            Log.d("FragmentSelectLocation",country);
            for(String[] s : CountryContract.countries){
                Log.d("FragmentSelectLocation",s[0]+"-"+country);
                if(s[0].equals(country)){
                    county=s[1];
                    Log.d("FragmentSelectLocation",s[1]);
                    break;
                }
            }

            for(String[] s : CountyContract.counties){
                if(s[0].equals(country)){
                    Log.d("FragmentSelectLocation",s[0]+" "+s[1]);
                    list.add(s[1]);
                }
            }
		}
		
		Collections.sort(list);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		//returns the inflated layout which contains the listview
		view= inflater.inflate(R.layout.list_reuse, container, false);

		tv_main=(TextView)view.findViewById(R.id.tv_frag_mainHead_new);
//		et_search=(TextView)view.findViewById(R.id.et_listReuse_search);

        if (type.equals(DHelper.location_county)) {

            tv_main.setText("Select the " + county + " you belong to");
        }else {
            tv_main.setText("Select the country you belong to");
        }
        return view;
	}
	
		 
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (type.equals(DHelper.location_country)){
			String country = list.get(position);

            Fragment frag= new FragmentSelectLocation();
			Bundle argument = new Bundle();
			argument.putString("type", DHelper.location_county);
			argument.putString("country", country);
			frag.setArguments(argument);
			
			getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragment_backup_Container, frag)						//Load the New Fragment
				.addToBackStack(type)													//add the transaction to the back stack
				.commit();
			
		}else if (type.equals(DHelper.location_county)){
			
			Intent i=new Intent();
			
			county = list.get(position);
			i.putExtra("county", county );
			i.putExtra("country", this.country);
			
			
			getActivity().setResult(1,i);//used to set the results for the parent activity ( the one that launched this one)
			getActivity().finish();
		}
		
		
		
			
	
	}
	
} 
	