package uwi.dcit.AgriExpenseTT;

import android.os.Bundle;

import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;


public class AboutScreen extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
        //Google Analytics
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("About Screen");
	}
}
