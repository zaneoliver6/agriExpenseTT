package uwi.dcit.AgriExpenseTT;

import uwi.dcit.AgriExpenseTT.fragments.FragmentReportList;
import uwi.dcit.AgriExpenseTT.helpers.CSVHelper;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ManageReport extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_manager);
		if (savedInstanceState == null) {
			setupInitialFrag();
		}
	}
	
	public void setupInitialFrag(){
		ListFragment fragment = new FragmentReportList();
		
		getFragmentManager()
			.beginTransaction()
			.add(R.id.reportListContainer, fragment)
			.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.report_manager, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void createExcelReport(){
		
	}
	
	public void createNewReport(View view){
		//TODO open time dialog to set time frame
		CSVHelper cvh=new CSVHelper(this);
    	cvh.createReport();
		
	}
}
