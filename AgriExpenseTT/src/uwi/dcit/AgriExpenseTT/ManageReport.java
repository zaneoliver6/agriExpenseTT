package uwi.dcit.AgriExpenseTT;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import uwi.dcit.AgriExpenseTT.fragments.FragmentReportList;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.helpers.ReportHelper;


public class ManageReport extends BaseActivity implements ReportHelper.OnReportSuccess {

	private static final int REQUEST_READ_CONTACTS = 23;
	private int fromMonth;
	private int fromYear;
	private int fromDay;
	private int toMonth;
	private int toYear;
	private int toDay;
	private AlertDialog optionDialog;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GAnalyticsHelper.getInstance(this).sendScreenView("Manage Reports");
		Log.d("ManageReport", "Checking if Has Permission");
		if (hasFilePermissions()){
			Log.d("ManageReport", "Has The appropriate Permissions");
			setContentView(R.layout.activity_report_manager);
			setupInitialFrag();
		}else{
			Log.d("ManageReport", "Do not have the appropriate Permissions. Requesting ...");
			requestFilePermission();
		}
	}

	public boolean hasFilePermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			Log.d("ManageReport", "Launch the Request Activity for user confirmation");
			return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
						ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
						== PackageManager.PERMISSION_GRANTED;
		}else{
			return true;
		}
	}

	public void requestFilePermission(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			ActivityCompat.requestPermissions(this,
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
				REQUEST_READ_CONTACTS);
	}


	public void setupInitialFrag(){
		FragmentReportList fragment = new FragmentReportList();
		fragment.setReportActivity(this);

        getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.reportListContainer,fragment)
			.commit();
	}
	
	public void createNewReport(View view){
		// Starts the Builder to create the dialog with the User to select option for report
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater(); // Get the layout inflater
		// Using a layout xml file to format the dialog for a better interaction
		View dialogView = inflater.inflate(R.layout.dialog_create_report, null);
		builder.setView(dialogView);

		// Handle Click for Custom Button
		Button customBtn = (Button)dialogView.findViewById(R.id.custom_btn);
		customBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				optionDialog.dismiss();
				handleCustomReport();
			}
		});
		// Handle Click Standard Button
		Button standardBtn = (Button)dialogView.findViewById(R.id.standard_btn);
		standardBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				optionDialog.dismiss();
				handleStandardReport();
			}
		});

		// Display Dialog and assign to the field so we can access later (for dismissing)
		optionDialog = builder.show();
	}

	// Retrieve the Data from The fields set by the dialogs
	private void generateCustomReport(){
		final Calendar toCalendar = Calendar.getInstance();
		final Calendar fromCalendar = Calendar.getInstance();

		toCalendar.set(Calendar.YEAR, toYear);
		toCalendar.set(Calendar.MONTH, toMonth);
		toCalendar.set(Calendar.DAY_OF_MONTH, toDay);

		fromCalendar.set(Calendar.YEAR, fromYear);
		fromCalendar.set(Calendar.MONTH, fromMonth);
		fromCalendar.set(Calendar.DAY_OF_MONTH, fromDay);

		progressDialog = ProgressDialog.show(this, "Report", "Creating the Report", true);
		ReportHelper cvh = new ReportHelper(this, this);
		cvh.createReport(toCalendar.getTimeInMillis(),fromCalendar.getTimeInMillis());
	}


	private void handleCustomReport(){
		final Calendar startCalendar = Calendar.getInstance();
		toYear = startCalendar.get(Calendar.YEAR);
		toMonth = startCalendar.get(Calendar.MONTH);
		toDay = startCalendar.get(Calendar.DAY_OF_MONTH);

		// to Date Dialog will call the end date dialog
		(new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
				toYear = datePicker.getYear();
				toMonth = datePicker.getMonth();
				toDay = datePicker.getDayOfMonth();

				// Set is one year later from the date selected
				fromYear = toYear - 1;
				fromMonth = toMonth;
				fromDay = toDay;

				// End date dialog to show for user to select date after choosing date from before
				(new DatePickerDialog(ManageReport.this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
						toYear = datePicker.getYear();
						toMonth = datePicker.getMonth();
						toDay = datePicker.getDayOfMonth();

						// Run the generated report after user selects the end date
						generateCustomReport();
					}
				}, fromYear, fromMonth, fromDay)).show();
			}
		}, toYear, toMonth, toDay)).show();
	}

	private void handleStandardReport(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		long timeYearinMilli = (long) 1000 * 60 * 60 * 24 * 365;
		long fromTimeStamp = calendar.getTimeInMillis() - timeYearinMilli; // 60 secs in 60 mins 24 hrs

		progressDialog = ProgressDialog.show(this, "Report", "Creating the Report", true);
		ReportHelper cvh = new ReportHelper(this, this);
		cvh.createReport(calendar.getTimeInMillis(), fromTimeStamp);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_READ_CONTACTS: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Restart Current Activity
					Intent intent = getIntent();
					finish();
					startActivity(intent);
				} else {
					Toast.makeText(this, "Unable to retrieve permission to create reports", Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		}
	}

	@Override
	public void handleResult(boolean result, String msg) {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		String title = "Error";
		if (result) {
			title = "Success";
		}
		new AlertDialog.Builder(this)
				.setTitle(title)
				.setMessage(msg)
				.setNeutralButton("OK", null)
				.show();

		setupInitialFrag();

	}
}