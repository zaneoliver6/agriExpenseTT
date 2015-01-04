package uwi.dcit.AgriExpenseTT.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class FragmentBackupList  extends ListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String [] menu = getResources().getStringArray(R.array.backup_menu);
		// We need to use a different list item layout for devices older than Honeycomb
		int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;
		this.setListAdapter(new ArrayAdapter<String>(this.getActivity(), layout, menu));
        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("Backup List Fragment");
	}
	
	public void updateHeading(){
		((TextView)getActivity().findViewById(R.id.tv_mainNew_header)).setText(R.string.backup_header_main);
		((TextView)getActivity().findViewById(R.id.tv_mainNew_subheader)).setText(R.string.backup_header_description);
	}
	
	@Override
    public void onStart() {
        super.onStart();
        updateHeading();
        this.setupListeners();
	}
	
	public void setupListeners(){
		this.getListView().setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Log.d("FragmentBackup Data", String.valueOf(id));
				Bundle arguments = new Bundle();
				arguments.putInt("backup_field", position);
				Fragment fragment = new FragmentBackupDetails();
				fragment.setArguments(arguments);
				getFragmentManager()
					.beginTransaction()
					.replace(R.id.fragment_backup_Container, fragment)
					.addToBackStack("Settings List")
					.commit();
			}
		});
	}
}
