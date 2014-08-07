package fragments;

import uwi.dcit.AgriExpenseTT.R;
import helper.DHelper;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FragmentLabourType extends Fragment{
	
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.fragment_labour_type, container, false);
		
		setDetails();
		return view;
	}
	private void setDetails() {
		TextView tv_head=(TextView)view.findViewById(R.id.tv_labourType_choice);
		Button btn_many=(Button)view.findViewById(R.id.btn_labour_multipleCycle);
		Button btn_one=(Button)view.findViewById(R.id.btn_labour_oneCycle);
		Click c=new Click();
		tv_head.setText("Will "+getArguments().getString("name")+" be working on one or many cycles");
		btn_many.setOnClickListener(c);
		btn_one.setOnClickListener(c);
	}
	public class Click implements OnClickListener{

		@Override
		public void onClick(View v) {
			FragmentManager fm=getFragmentManager();
			FragmentTransaction ft=fm.beginTransaction();
			Bundle data=new Bundle();
			if(v.getId()==R.id.btn_labour_multipleCycle){
				ListFragment lf=new HireLabourLists();
				//((HireLabour)getActivity()).appendSub(",many cycles");
				data.putString("type", "quantifier");
				data.putString("name",getArguments().getString("name"));
				lf.setArguments(data);
				//ft.add(R.id.cont_labour_frag,lf);
				ft.replace(R.id.NewCycleListContainer, lf);
				ft.commit();
			}else if(v.getId()==R.id.btn_labour_oneCycle){
				ListFragment lf=new FragmentViewCycles();
				//((HireLabour)getActivity()).appendSub(",one cycle");
				data.putString("name",getArguments().getString("name"));
				data.putString("type", DHelper.cat_labour);
				lf.setArguments(data);
				ft.replace(R.id.NewCycleListContainer,lf);
				ft.addToBackStack(null);
				ft.commit();
			}
			
		}
		
	}
}
