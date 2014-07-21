package fragments;

import uwi.dcit.AgriExpenseTT.NewPurchaseRedesign;
import uwi.dcit.AgriExpenseTT.R;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class FragmentNewPurchaseOther extends Fragment{
	EditText et_res;
	EditText et_qtfr;
	Button btn_dne;
	TextView tv_error_res;
	TextView  tv_error_qtfr;
	View view;
	
	String resource;
	String quantifier;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.fragment_other_quanifier, container, false);
		setup();
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
				if(resource.equals(null)||resource.equals("")){
					tv_error_res.setText("enter the name of your resource");
					tv_error_res.setVisibility(View.VISIBLE);
					return;
				}else if(quantifier.equals(null)||quantifier.equals("")){
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
				((NewPurchaseRedesign)getActivity()).replaceSub("Details: "+getArguments().getString("category")
						+", "+resource+", "+quantifier);
				Fragment newFragment =new Fragment_newpurchaseLast();
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
