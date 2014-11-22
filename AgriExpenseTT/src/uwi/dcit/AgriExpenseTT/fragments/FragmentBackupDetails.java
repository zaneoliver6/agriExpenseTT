package uwi.dcit.AgriExpenseTT.fragments;

import uwi.dcit.AgriExpenseTT.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

public class FragmentBackupDetails extends Fragment{

	private int selectedOption;
	private String selectedStrOption;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.selectedOption = this.getArguments().getInt("backup_field");
		this.selectedStrOption = getResources().getStringArray(R.array.backup_menu)[selectedOption];
		updateHeading();
	}
	
	public void updateHeading(){
		((TextView)getActivity().findViewById(R.id.tv_mainNew_header)).setText(this.selectedStrOption);
		((TextView)getActivity().findViewById(R.id.tv_mainNew_subheader)).setText(getResources().getStringArray(R.array.backup_menu_description)[selectedOption]);
	}
	
}
