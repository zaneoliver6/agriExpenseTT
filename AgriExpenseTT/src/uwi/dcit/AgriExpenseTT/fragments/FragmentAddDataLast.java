package uwi.dcit.AgriExpenseTT.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;


public class FragmentAddDataLast extends Fragment {
	DataManager dm;
	View view;
	EditText et_name;
	TextView tv_error;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		dm = new DataManager(getActivity());
		view = inflater.inflate(R.layout.fragment_adddata, container, false);
		setup();
        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("Add Dataset Fragment - Last");
		return view;
	}
	private void setup() {
		Button btn_dne=(Button)view.findViewById(R.id.btn_addData_dne);
		et_name=(EditText)view.findViewById(R.id.et_addData_name);
		tv_error=(TextView)view.findViewById(R.id.tv_addData_lbl);
        tv_error.setText("Enter name of "+getArguments().getString("type"));
		Click c=new Click();
		btn_dne.setOnClickListener(c);
	}

	public class Click implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_addData_dne){
				String name = et_name.getText().toString();
				if(name == null || name.equals("")){
					tv_error.setText("Please enter name of "+getArguments().getString("type"));
                    tv_error.setTextColor(getResources().getColor(R.color.helper_text_error));
                    et_name.getBackground().setColorFilter(getResources().getColor(R.color.helper_text_error), PorterDuff.Mode.SRC_ATOP);
					return;
				}else{
                    tv_error.setText("Enter name of "+getArguments().getString("type"));
                    tv_error.setTextColor(getResources().getColor(R.color.helper_text_color));
                    et_name.getBackground().setColorFilter(getResources().getColor(R.color.helper_text_color), PorterDuff.Mode.SRC_ATOP);
                }
				new Thread(new Runnable() {
                    public void run() {
                        dm.insertResource(et_name.getText().toString(), getArguments().getString("type"));
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getActivity(),getArguments().getString("type") + " Saved", Toast.LENGTH_SHORT ).show();
                                getActivity().finish();
                            }
                        });

                    }
                }).start();

			}
		}
		
	}
	
	
}
