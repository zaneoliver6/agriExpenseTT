package uwi.dcit.AgriExpenseTT;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.View;

import uwi.dcit.AgriExpenseTT.fragments.FragmentReportList;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.helpers.ReportHelper;

public class ManageReport extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_manager);

		if (savedInstanceState == null) {
			setupInitialFrag();
		}
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Manage Reports");
	}
	
	public void setupInitialFrag(){
		ListFragment fragment = new FragmentReportList();

        getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.reportListContainer,fragment)
			.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.report_manager, menu);
		return true;
	}
	
	public void createNewReport(View view){
		//TODO open time dialog to set time frame
		ReportHelper cvh = new ReportHelper(this);
    	cvh.createReport();
	}
}