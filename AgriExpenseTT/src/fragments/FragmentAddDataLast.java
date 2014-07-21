package fragments;

import uwi.dcit.AgriExpenseTT.R;
import helper.DataManager;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class FragmentAddDataLast extends Fragment{
	DataManager dm;
	View view;
	EditText et_name;
	TextView tv_error;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		dm=new DataManager(getActivity());
		view=inflater.inflate(R.layout.fragment_adddata, container, false);
		setup();
		return view;
	}
	private void setup() {
		Button btn_dne=(Button)view.findViewById(R.id.btn_addData_dne);
		et_name=(EditText)view.findViewById(R.id.et_addData_name);
		tv_error=(TextView)view.findViewById(R.id.tv_addData_error);
		Click c=new Click();
		btn_dne.setOnClickListener(c);
		
		
	}
	public class Click implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_addData_dne){
				String name=et_name.getText().toString();
				if(name.equals(null)||name.equals("")){
					tv_error.setText("Please enter name of "+getArguments().getString("type"));
					tv_error.setVisibility(View.VISIBLE);
					return;
				}
				dm.insertResource(name,getArguments().getString("type"));
				getActivity().finish();
			}
		}
		
	}
	
	
}
