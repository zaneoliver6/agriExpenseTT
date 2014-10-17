package uwi.dcit.AgriExpenseTT.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.app.ListFragment;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FragmentReportList extends ListFragment {

	private ArrayList<String> list;
	private ArrayAdapter<String> listAdapt;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		populateList();
		listAdapt = new ArrayAdapter<String>(this.getActivity().getBaseContext(),android.R.layout.simple_list_item_1,list);
		setListAdapter(listAdapt);
	}
	
	public void populateList() {
		if (list == null)list = new ArrayList<String>();
		
		String path = Environment.getExternalStorageDirectory().toString()+"/Agrinet";
		File file[] = (new File(path)).listFiles();
		Log.d(FragmentReportList.class.toString(), "Path: " + path + " Size: "+ file.length);
		for (int i=0; i < file.length; i++){
		    Log.d("Files", "FileName:" + file[i].getName());
		    list.add(file[i].getName());
		}
		Collections.sort(list);
	}
	
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(FragmentReportList.class.toString(), this.list.get(position));
		//TODO Launch Intent to open Excel Files
	}
	
}
