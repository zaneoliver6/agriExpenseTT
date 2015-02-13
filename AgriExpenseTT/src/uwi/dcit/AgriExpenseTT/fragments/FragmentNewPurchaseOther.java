package uwi.dcit.AgriExpenseTT.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.fragment_other_quanifier, container, false);
		setup();
        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("New Purchase Fragment - Other Category");

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
	private void setup() {
		et_res=(EditText)view.findViewById(R.id.et_newPurchase_other_res);
		et_qtfr=(EditText)view.findViewById(R.id.et_newPurchase_other_quantifier);
		btn_dne=(Button)view.findViewById(R.id.btn_newPurchase_other_done);
		tv_error_res=(TextView)view.findViewById(R.id.tv_error_newpurchase_other_resource);
		tv_error_qtfr=(TextView)view.findViewById(R.id.tv_error_newpurchase_other_quantifer);
		Click c=new Click();
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
			if(v.getId()==R.id.btn_newPurchase_other_done){
				if(getArguments().getString("found").equals("no"))
					resource=et_res.getText().toString();
				quantifier=et_qtfr.getText().toString();
				if(resource == null ||resource.equals("")){
					tv_error_res.setText("enter the name of your resource");
					tv_error_res.setVisibility(View.VISIBLE);
					return;
				}else if(quantifier == null||quantifier.equals("")){
					tv_error_qtfr.setText("enter how you are going to measure what you are buying");
					tv_error_qtfr.setVisibility(View.VISIBLE);
					return;
				}
				Bundle b=new Bundle();
				//pass the category to quantifier
				b.putString("category",getArguments().getString("category"));
				//pass the resource to quantifier
				b.putString("resource",resource);
				//pass the type as quantifier
				b.putString("quantifier",quantifier);
				//to final Purchase fragment
				((NewPurchase)getActivity()).replaceSub("Details: "+getArguments().getString("category")
						+", "+resource+", "+quantifier);
				Fragment newFragment =new FragmentNewPurchaseLast();
				newFragment.setArguments(b);
				FragmentManager fm=getFragmentManager();
				FragmentTransaction transaction=fm.beginTransaction();
				transaction.replace(R.id.NewCycleListContainer, newFragment);
				transaction.addToBackStack(null);
				
				// Commit the transaction
				transaction.commit();
			}
		}
		
	}
}	
