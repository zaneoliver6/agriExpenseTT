package uwi.dcit.AgriExpenseTT.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import uwi.dcit.AgriExpenseTT.NewPurchase;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class FragmentOtherResourceList  extends ListFragment{
	SQLiteDatabase db;
	DbHelper dbh;
	ArrayList<String>list;
	View view;
	ArrayAdapter<String> listAdapt;
	EditText et_search;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh=new DbHelper(this.getActivity().getBaseContext());
		db=dbh.getWritableDatabase();
		populateList();
		Collections.sort(list);
		listAdapt = new ArrayAdapter<>(this.getActivity().getBaseContext(), android.R.layout.simple_list_item_1, list);
		setListAdapter(listAdapt);
		
	}
		

	private void populateList() {
		list = new ArrayList<>();
		DbQuery.getResources(db, dbh, DHelper.cat_other, list);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		//returns the inflated layout which contains the listview
		view= inflater.inflate(R.layout.fragment_purchaselist_other, container, false);
		et_search=(EditText)view.findViewById(R.id.et_search_other);
		TWatch tw=new TWatch(listAdapt);
		et_search.addTextChangedListener(tw);
		setupButton(view);
        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("New Resource List Fragment - Other Category");

        view.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (!(v instanceof EditText)) {
                            ((NewPurchase) getActivity()).hideSoftKeyboard();
                        }
                        return false;
                    }
                }
        );

		return view;
	}
	 private void setupButton(View v) {
		Button btn_ntFnd=(Button)v.findViewById(R.id.btn_purchaseOther_notFound);
		Click c=new Click();
		btn_ntFnd.setOnClickListener(c);
	 }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Fragment f = new FragmentNewPurchaseOther();
		Bundle b = new Bundle();
		b.putString("category", DHelper.cat_other);
		b.putString("resource", list.get(position));
		b.putString("found", "yes");
		f.setArguments(b);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.NewCycleListContainer, f);
		ft.addToBackStack(null);
		ft.commit();
	}

	 public class Click implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_purchaseOther_notFound){
				Fragment f=new FragmentNewPurchaseOther();
				Bundle b=new Bundle();
				b.putString("category", DHelper.cat_other);
				b.putString("found","no");
				f.setArguments(b);
				FragmentManager fm=getFragmentManager();
				FragmentTransaction ft=fm.beginTransaction();
				ft.replace(R.id.NewCycleListContainer, f);
				ft.addToBackStack(null);
				ft.commit();
			}
		}

	 }

	 public class TWatch implements TextWatcher{
		 ArrayAdapter<String> adpt;
		 public TWatch(ArrayAdapter<String> adpt){
			 super();
			 this.adpt=adpt;
		 }
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
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
