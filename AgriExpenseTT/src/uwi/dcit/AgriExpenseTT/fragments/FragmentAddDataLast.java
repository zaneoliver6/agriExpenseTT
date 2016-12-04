package uwi.dcit.AgriExpenseTT.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.NotifyHelper;
import uwi.dcit.AgriExpenseTT.helpers.TextHelper;


public class FragmentAddDataLast extends Fragment {
	DataManager dm;
	View view;
	EditText et_name;
	TextView tv_error;
	Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		dm = new DataManager(getActivity());
		view = inflater.inflate(R.layout.fragment_adddata, container, false);
		this.context = this.getContext();
		setup();
//        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("Add Dataset Fragment - Last");
		return view;
	}
	private void setup() {
		Button btn_dne=(Button)view.findViewById(R.id.btn_addData_dne);
		et_name=(EditText)view.findViewById(R.id.et_addData_name);
		tv_error=(TextView)view.findViewById(R.id.tv_addData_lbl);
		if (getArguments().getString("type") != null && getArguments().getString("type").equals
				(DHelper.cat_labour))
			tv_error.setText("Enter name of Labourer");
		else
            tv_error.setText("Enter name of "+getArguments().getString("type"));
		Click c=new Click();
		btn_dne.setOnClickListener(c);
	}

	public class Click implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_addData_dne){
				String name = et_name.getText().toString();
				if (name.equals("")) {
					tv_error.setText("Please enter name of "+getArguments().getString("type"));
					tv_error.setTextColor(ContextCompat.getColor(context, R.color.helper_text_error));
					et_name.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.helper_text_error), PorterDuff.Mode.SRC_ATOP);
					return;
				}else{
                    tv_error.setText("Enter name of "+getArguments().getString("type"));
					tv_error.setTextColor(ContextCompat.getColor(context, R.color.helper_text_color));
					et_name.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.helper_text_color), PorterDuff.Mode.SRC_ATOP);
				}
				new Thread(new Runnable() {
                    public void run() {
                        dm.insertResource(TextHelper.formatUserText(et_name.getText().toString()), getArguments().getString("type"));
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                NotifyHelper.notify(getActivity(),getArguments().getString("type") + " Saved" );
                                getActivity().finish();
                            }
                        });
                    }
                }).start();

			}
		}
		
	}
	
	
}
