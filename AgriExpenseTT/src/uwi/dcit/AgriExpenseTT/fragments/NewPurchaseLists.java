package uwi.dcit.AgriExpenseTT.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import uwi.dcit.AgriExpenseTT.NewPurchase;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;


public class NewPurchaseLists extends ListFragment {
	public final String TAG = "NewPurchaseList";
	String type;
	 ArrayList<String> list;
	SQLiteDatabase db;
	DbHelper dbh;
	int cycleId;
	ArrayAdapter<String> listAdapt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh=new DbHelper(this.getActivity().getBaseContext());
		db=dbh.getWritableDatabase();
		type=getArguments().getString("type");
		populateList();
		Collections.sort(list);
		listAdapt = new ArrayAdapter<>(this.getActivity().getBaseContext(),android.R.layout.simple_list_item_1,list);
		setListAdapter(listAdapt);
//        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("New Purchase List Fragment");
	}

    @Override
    public void onResume(){
        super.onResume();
        if (list == null){
            populateList();
            Collections.sort(list);
            listAdapt.notifyDataSetChanged();
        }
    }
		
	private void populateList() {
		list = new ArrayList<>();
		
		if(type.equals("category")){
			list.add(DHelper.cat_plantingMaterial);
			list.add(DHelper.cat_chemical);
			list.add(DHelper.cat_fertilizer);
			list.add(DHelper.cat_soilAmendment);
			list.add(DHelper.cat_other);
		}else if(type.equals("resource")){
			DbQuery.getResources(db, dbh, getArguments().getString("category"), list);
		}else if(type.equals("quantifier")){
			String cat=getArguments().getString("category");
			if(cat.equals(DHelper.cat_plantingMaterial)){
				list.add(DHelper.qtf_plantingMaterial_seed);
				list.add(DHelper.qtf_plantingMaterial_seedling);
				list.add(DHelper.qtf_plantingMaterial_stick);
				list.add(DHelper.qtf_plantingMaterial_tubes);
				list.add(DHelper.qtf_plantingMaterial_heads);
				list.add(DHelper.qtf_plantingMaterial_slip);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		//returns the inflated layout which contains the listview
		View view= inflater.inflate(R.layout.list_reuse, container, false);
		TextView et_main=(TextView)view.findViewById(R.id.tv_frag_mainHead_new);
		TextView et_search=(TextView)view.findViewById(R.id.et_listReuse_search);
		et_search.setVisibility(View.INVISIBLE); //TODO Remove the search bar until more reliable performance is achieved

		if(getArguments().getString("type").equals("category")||getArguments().getString("type").equals("quantifier")){
			et_search.setVisibility(View.GONE);
		}else{
			TWatch tw=new TWatch(listAdapt);
			et_search.addTextChangedListener(tw);
		}
		if(type.equals("category")) et_main.setText("Select the type of material you are buying");
        else if(type.equals("resource")){
			String s=getArguments().getString("category");
			et_main.setText("Select the type "+s+" to be purchased");
		}else if(type.equals("quantifier")){
			String q=getArguments().getString("resource");
			et_main.setText("How is the "+q+" being sold by");
		}

        view.setOnTouchListener(
            new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!(v instanceof EditText) && getActivity() instanceof NewPurchase) {
                        ((NewPurchase) getActivity()).hideSoftKeyboard();
                    }
                    return false;
                }
            }
        );
		return view;
	}

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Fragment newFragment=null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle b=new Bundle();
        if(type.equals("category")){
            //pass type as resource
            ((NewPurchase)getActivity()).replaceSub("Details: "+listAdapt.getItem(position));
            if(listAdapt.getItem(position).equals(DHelper.cat_other)){
                ArrayList<String> test=new ArrayList<>();
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
                b.putString("category", listAdapt.getItem(position));
                newFragment =new NewPurchaseLists();
            }

        }else if(type.equals("resource")){
            //pass the category to quantifier
            b.putString("category",getArguments().getString("category"));
            //pass the resource to quantifier
            b.putString("resource",listAdapt.getItem(position));
            //pass the type as quantifier
            b.putString("type","quantifier");
            ((NewPurchase)getActivity()).replaceSub("Details: "
            +getArguments().getString("category")+", "+listAdapt.getItem(position));

            newFragment =new NewPurchaseLists();

        }else if(type.equals("quantifier")){
            //pass the category to quantifier
            b.putString("category",getArguments().getString("category"));
            //pass the resource to quantifier
            b.putString("resource",getArguments().getString("resource"));
            //pass the type as quantifier
            b.putString("quantifier",listAdapt.getItem(position));
            //to final Purchase fragment
            ((NewPurchase)getActivity()).replaceSub("Details: "+getArguments().getString("category")
            +", "+getArguments().getString("resource")+", "+listAdapt.getItem(position));

            newFragment =new FragmentNewPurchaseLast();
        }
        if (newFragment != null) newFragment.setArguments(b);
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
			
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			adpt.getFilter().filter(s);
			
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			
			
		}
		 
	 }
}