package uwi.dcit.AgriExpenseTT;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class ManageData extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_data);
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Manage Data");
	}

	public void openDataEdit(View view){
		startActivity(new Intent(ManageData.this,EditData.class));
	}

    public void openDataDelete(View view){
		startActivity(new Intent(ManageData.this,DeleteData.class));
	}

    public void openDataAdd(View view){
		startActivity(new Intent(ManageData.this,AddData.class));
	}

	public void alarmPreferencesEdit(View view){
		startActivity(new Intent(ManageData.this,AlarmActivity.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_data, menu);
		return true;
	}
}
