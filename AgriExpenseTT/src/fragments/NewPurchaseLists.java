package fragments;

import helper.DHelper;
import helper.DbHelper;
import helper.DbQuery;

import java.util.ArrayList;
import java.util.Collections;

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

public class NewPurchaseLists extends ListFragment {
	String type;
	 ArrayList<String> list;
	SQLiteDatabase db;
	DbHelper dbh;
	int cycleId;
	TextView et_main;
	TextView et_search;
	View view;
	ArrayAdapter<String> listAdapt;
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
		if(type.equals("category")){
			list.add(DHelper.cat_plantingMaterial);
			list.add(DHelper.cat_chemical);
			list.add(DHelper.cat_fertilizer);
			list.add(DHelper.cat_soilAmendment);
			list.add(DHelper.cat_other);
		}else if(type.equals("resource")){
			DbQuery.getResources(db, dbh,getArguments().getString("category"), list);
		}else if(type.equals("quantifier")){
			String cat=getArguments().getString("category");
			if(cat.equals(DHelper.cat_plantingMaterial)){
				list.add(DHelper.qtf_plantingMaterial_seed);
				list.add(DHelper.qtf_plantingMaterial_seedling);
				list.add(DHelper.qtf_plantingMaterial_stick);
			}else if(cat.equals(DHelper.cat_chemical)){
				list.add(DHelper.qtf_chemical_ml);
				list.add(DHelper.qtf_chemical_L);
				list.add(DHelper.qtf_chemical_oz);
				list.add(DHelper.qtf_chemical_g);
				list.add(DHelper.qtf_chemical_kg);
			}else if(cat.equals(DHelper.cat_fertilizer)){
				list.add(DHelper.qtf_fertilizer_g);
				list.add(DHelper.qtf_fertilizer_lb);
				list.add(DHelper.qtf_fertilizer_kg);
				list.add(DHelper.qtf_fertilizer_bag);
			}else if(cat.equals(DHelper.cat_soilAmendment)){
				list.add(DHelper.qtf_soilAmendment_bag);
				list.add(DHelper.qtf_soilAmendment_truck);
			}
				
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		//returns the inflated layout which contains the listview
		view= inflater.inflate(R.layout.list_reuse, container, false);
		et_main=(TextView)view.findViewById(R.id.tv_frag_mainHead_new);
		et_search=(TextView)view.findViewById(R.id.et_listReuse_search);
		if(getArguments().getString("type").equals("category")||getArguments().getString("type").equals("quantifier")){
			et_search.setVisibility(View.GONE);
		}else{
			TWatch tw=new TWatch(listAdapt);
			et_search.addTextChangedListener(tw);
		}
		if(type.equals("category")){
			et_main.setText("Select the type of material you are buying");
		}else if(type.equals("resource")){
			String s=getArguments().getString("category");
			et_main.setText("Select the type "+s+" are buying");
		}else if(type.equals("quantifier")){
			String q=getArguments().getString("resource");
			et_main.setText("How is the "+q+" being sold by");
		}
		return view;
	}
		
	
		 
	
	 @Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Toast.makeText(getActivity(), getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
			Fragment newFragment=null;
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			Bundle b=new Bundle();
			if(type.equals("category")){
				//pass type as resource
				((NewPurchaseRedesign)getActivity()).replaceSub("Details: "+list.get(position));
				if(list.get(position).equals(DHelper.cat_other)){
					ArrayList<String> test=new ArrayList<String>();
					DbQuery.getResources(db, dbh, DHelper.cat_other, test);
					if(test.isEmpty()){
						newFragment= new FragmentNewPurchaseOther();
						b.putString("category",DHelper.cat_other);
						b.putString("found", "no");
					}else{
						newFragment= new FragmentOtherResourceList();
						b.putString("category",DHelper.cat_other);
					}
				}else{
					b.putString("type", "resource");
					//pass the category to the resource
					b.putString("category", list.get(position));
					newFragment =new NewPurchaseLists();
				}
				
			}else if(type.equals("resource")){
				//pass the category to quantifier
				b.putString("category",getArguments().getString("category"));
				//pass the resource to quantifier
				b.putString("resource",list.get(position));
				//pass the type as quantifier
				b.putString("type","quantifier");
				((NewPurchaseRedesign)getActivity()).replaceSub("Details: "
				+getArguments().getString("category")+", "+list.get(position));
				
				newFragment =new NewPurchaseLists();
				
			}else if(type.equals("quantifier")){
				//pass the category to quantifier
				b.putString("category",getArguments().getString("category"));
				//pass the resource to quantifier
				b.putString("resource",getArguments().getString("resource"));
				//pass the type as quantifier
				b.putString("quantifier",list.get(position));
				//to final Purchase fragment
				((NewPurchaseRedesign)getActivity()).replaceSub("Details: "+getArguments().getString("category")
						+", "+getArguments().getString("resource")+", "+list.get(position));
				
				newFragment =new Fragment_newpurchaseLast();
			}
			newFragment.setArguments(b);
			// Replace whatever is in the fragment_container view with this fragment,
			// and add the transaction to the back stack
			transaction.replace(R.id.NewCycleListContainer, newFragment);
			transaction.addToBackStack(null);
			
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