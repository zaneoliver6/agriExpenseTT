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

import uwi.dcit.AgriExpenseTT.ManageReport;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.ReportHelper;

public class FragmentReportList extends ListFragment {

	private ArrayList<String> list;
    private File files[];
	private ManageReport activity;

	@Override
	public void onActivityCreated(Bundle savedState){
		super.onActivityCreated(savedState);
		this.registerForContextMenu(getListView());
		if (activity != null && !activity.hasFilePermissions())
			activity.requestFilePermission();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(activity != null && activity.hasFilePermissions()){
			ReportHelper.createReportDirectory();
			populateList();
		}
	}
	
	public void populateList() {
		list = new ArrayList<>(); //Reinitialise to ensure always a empty list starting with
        String path = Environment.getExternalStorageDirectory().toString() + "/" + ReportHelper.folderLocation;
		files = (new File(path)).listFiles(); //Store the file in an array of files
		if (files != null) {
			Log.d(FragmentReportList.class.toString(), "Path: " + path + " Size: " + files.length);
			for (File file : files) {
				Log.d("Files", "FileName:" + file.getName());
				list.add(file.getName());
			}
		}
//		Collections.sort(list); //Removed sorting because it would cause the order to be inconsistent with the file array
        ArrayAdapter<String> listAdapt = new ArrayAdapter<>(this.getActivity().getBaseContext(), android.R.layout.simple_list_item_1, list);
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
			.setPositiveButton("Delete",c)
			.setNegativeButton("Cancel",c)
			.create() 
			.show();
	}
	public void deleteReport(int position){
		File f = files[position];
		if (f.delete()){
			Toast.makeText(this.getActivity(), "Report Successfully Deleted", Toast.LENGTH_LONG).show();
			populateList();//Re-populate the List of Files
		}else
			Toast.makeText(this.getActivity(), "Unable to Delete Report", Toast.LENGTH_LONG).show();
	}
	
	
	public void viewReport(int position){
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		String type = "application/vnd.ms-excel";
        Log.d("URI",Uri.fromFile(files[position]).toString());
		intent.setDataAndType(Uri.fromFile(files[position]), type);
		startActivity(intent);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("FragmentReportList", this.list.get(position) + " : " + files[position].toString());
		this.viewReport(position);
	}

	public void setReportActivity(ManageReport reportActivity) {
		this.activity = reportActivity;
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
