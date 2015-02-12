package uwi.dcit.AgriExpenseTT.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.helpers.ReportHelper;

public class FragmentReportList extends ListFragment {

	private ArrayList<String> list;
    private File files[];
		
	@Override
	public void onActivityCreated(Bundle savedState){
		super.onActivityCreated(savedState);
		this.registerForContextMenu(getListView());
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ReportHelper.createReportDirectory();
		populateList();
        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("Report List Fragment");
	}
	
	public void populateList() {
		list = new ArrayList<String>(); //Reinitialise to ensure always a empty list starting with		
        String path = Environment.getExternalStorageDirectory().toString() + "/" + ReportHelper.folderLocation;
		files = (new File(path)).listFiles(); //Store the file in an array of files
		Log.d(FragmentReportList.class.toString(), "Path: " + path + " Size: "+ files.length);
		for (int i=0; i < files.length; i++){
		    Log.d("Files", "FileName:" + files[i].getName());
		    list.add(files[i].getName());
		}
//		Collections.sort(list); //Removed sorting because it would cause the order to be inconsistent with the file array
        ArrayAdapter<String> listAdapt = new ArrayAdapter<String>(this.getActivity().getBaseContext(), android.R.layout.simple_list_item_1, list);
		setListAdapter(listAdapt);
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = this.getActivity().getMenuInflater();
		inflater.inflate(R.menu.report_list_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		
		switch(item.getItemId()){
			case R.id.report_view:
				Log.i("FragmentReportList", "View Report "+info.position);
				this.viewReport(info.position);
				break;
			case R.id.report_delete:
				Log.i("FragmentReportList", "Delete Report "+info.position);
				this.askDeleteReport(info.position);
				break;
			default:
				return super.onContextItemSelected(item);
		}
		return false;
	}
	
	public void askDeleteReport(int position){
		DeleteConfirmator c = new DeleteConfirmator(position);
		(new AlertDialog.Builder(getActivity()))
			.setMessage("Are you sure you want to delete?")
			.setCancelable(true)        
			.setPositiveButton("Yes",c)
			.setNegativeButton("Nope",c)
			.create() 
			.show();
	}
	public void deleteReport(int position){
		File f = files[position];
		if (f.delete()){
			Toast.makeText(this.getActivity(), "Report Successfully Deleted", Toast.LENGTH_LONG).show();
			//Re-populate the List of Files
			populateList();
		}else
			Toast.makeText(this.getActivity(), "Unable to Delete Report", Toast.LENGTH_LONG).show();
	}
	
	
	public void viewReport(int position){
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		String type = "application/vnd.ms-excel";
		intent.setDataAndType(Uri.fromFile(files[position]), type);
		startActivity(intent);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(FragmentReportList.class.toString(), this.list.get(position));
		Log.d("FragmentReportList", files[position].toString() );		
		this.viewReport(position);
	}
	
	/**
	 * Create an Onclicklistener class for the deletion of the report
	 * @author kyle
	 *
	 */
	public class DeleteConfirmator implements DialogInterface.OnClickListener{

		private int position;
		
		public DeleteConfirmator(int position){
			this.position = position;
		}
		@Override
		public void onClick(DialogInterface dialog, int btn) {
			if (btn == DialogInterface.BUTTON_POSITIVE){
				deleteReport(this.position);
			}
			dialog.cancel();
		}
		
	}
	
}
