package uwi.dcit.AgriExpenseTT.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import uwi.dcit.AgriExpenseTT.NewPurchase;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;


public class FragmentNewPurchaseOther extends Fragment{
	EditText et_res;
	EditText et_qtfr;
	Button btn_dne;
	TextView tv_error_res;
	TextView  tv_error_qtfr;
	View view;
	
	String resource;
	String quantifier;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.fragment_other_quanifier, container, false);
		setup();

        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("New Purchase Fragment - Other Category");

        view.setOnTouchListener(
            new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!(v instanceof EditText)) ((NewPurchase) getActivity()).hideSoftKeyboard();
                    return false;
                }
            });

		return view;
	}

	private void setup() {
		et_res  = (EditText)view.findViewById(R.id.et_newPurchase_other_res);
		et_qtfr = (EditText)view.findViewById(R.id.et_newPurchase_other_quantifier);
		btn_dne = (Button)view.findViewById(R.id.btn_newPurchase_other_done);

		tv_error_res    = (TextView)view.findViewById(R.id.tv_newPurchase_other_res);
		tv_error_qtfr   = (TextView)view.findViewById(R.id.tv_newPurchase_other_quantifier);

		Click c = new Click();
		btn_dne.setOnClickListener(c);
		if(getArguments().getString("found").equals("yes")){
			resource=getArguments().getString("resource");
			et_res.setVisibility(View.GONE);
			TextView tvLbl=(TextView)view.findViewById(R.id.tv_newPurchase_other_res);
			tvLbl.setVisibility(View.GONE);
		}
	}

    public class Click implements OnClickListener{
		@Override
		public void onClick(View v) {


			if(v.getId() == R.id.btn_newPurchase_other_done){

//				if(getArguments().getString("found").equals("no")) // TODO Not sure what this statement is checking for
//					resource = et_res.getText().toString();
				if((et_res.getText().toString()) == null || (et_res.getText().toString()).equals("")){
					tv_error_res.setText("Enter the name of your resource that can be purchased");
                    tv_error_res.setTextColor(getResources().getColor(R.color.helper_text_error));
                    et_res.getBackground().setColorFilter(getResources().getColor(R.color.helper_text_error), PorterDuff.Mode.SRC_ATOP);
					return;
				}else{
                    et_res.getBackground().setColorFilter(getResources().getColor(R.color.helper_text_color), PorterDuff.Mode.SRC_ATOP);
                    resource = et_res.getText().toString();
                }

                if((et_qtfr.getText().toString()) == null || (et_qtfr.getText().toString()).equals("")){
                    tv_error_qtfr.setTextColor(getResources().getColor(R.color.helper_text_error));
                    tv_error_qtfr.setText("Enter how you are going to measure what you are buying");
                    et_qtfr.getBackground().setColorFilter(getResources().getColor(R.color.helper_text_error), PorterDuff.Mode.SRC_ATOP);
					return;
				}else{
                    et_qtfr.getBackground().setColorFilter(getResources().getColor(R.color.helper_text_color), PorterDuff.Mode.SRC_ATOP);
                    quantifier = et_qtfr.getText().toString();
                }

                // TODO Check if resource name already exists
                DbHelper dbh = new DbHelper(getActivity().getApplicationContext());
                SQLiteDatabase db = dbh.getWritableDatabase();

                resource = resource.trim();
                quantifier = quantifier.trim();

                if (!DbQuery.resourceExistByName(db, dbh, resource)) { // Resource does not exist

                    Bundle b = new Bundle();

                    b.putString("category", getArguments().getString("category"));//pass the category to quantifier
                    b.putString("resource", resource);//pass the resource to quantifier
                    b.putString("quantifier", quantifier);//pass the type as quantifier


                    //to final Purchase fragment
                    if (getActivity() instanceof  NewPurchase)
                        ((NewPurchase) getActivity())
                            .replaceSub("Details: " + getArguments().getString("category") + ", " + resource + ", " + quantifier);


                    Fragment newFragment = new FragmentNewPurchaseLast();
                    newFragment.setArguments(b);

                    getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.NewCycleListContainer, newFragment)
                        .addToBackStack(null)
                        .commit();// Commit the transaction
                }else{
                    tv_error_res.setText("Resource Already Exists. Enter another name for your resource or check list of materials");
                    tv_error_res.setTextColor(getResources().getColor(R.color.helper_text_error));
                    et_res.getBackground().setColorFilter(getResources().getColor(R.color.helper_text_error), PorterDuff.Mode.SRC_ATOP);
                    return;
                }

//                finish();
			}
		}
		
	}
}
