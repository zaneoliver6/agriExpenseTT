package uwi.dcit.AgriExpenseTT;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;

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

		EditText to = (EditText) findViewById(R.id.report_start_date);
		String date = to.getText().toString();
		Log.i("YEAR",""+date);
		int dayF = Integer.parseInt(date.substring(0,2));
		int monthF = Integer.parseInt(date.substring(3,5));
		int yearF = Integer.parseInt(date.substring(6,10));
		Log.i("START",""+dayF+""+monthF+""+yearF);
		Calendar toDate = Calendar.getInstance();
		toDate.set(Calendar.DAY_OF_MONTH,dayF);
		toDate.set(Calendar.MONTH,monthF);
		toDate.set(Calendar.YEAR,yearF);
		toDate.set(Calendar.HOUR_OF_DAY, 0);
		toDate.set(Calendar.MINUTE, 0);
		toDate.set(Calendar.SECOND, 0);
		toDate.set(Calendar.MILLISECOND, 0);


		EditText from = (EditText) findViewById(R.id.Report_end_date);
		date = from.getText().toString();
		int dayT= Integer.parseInt(date.substring(0,2));
		int monthT = Integer.parseInt(date.substring(3,5));
		int yearT = Integer.parseInt(date.substring(6,10));

		Calendar fromDate = Calendar.getInstance();
		fromDate.set(Calendar.DAY_OF_MONTH,dayT);
		fromDate.set(Calendar.MONTH,monthT);
		fromDate.set(Calendar.YEAR,yearT);
		fromDate.set(Calendar.HOUR_OF_DAY, 0);
		fromDate.set(Calendar.MINUTE, 0);
		fromDate.set(Calendar.SECOND, 0);
		fromDate.set(Calendar.MILLISECOND, 0);

		ReportHelper cvh = new ReportHelper(this);
    	cvh.createReport(toDate.getTimeInMillis(),fromDate.getTimeInMillis());
	}
}