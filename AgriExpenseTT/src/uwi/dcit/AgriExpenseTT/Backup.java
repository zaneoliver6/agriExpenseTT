package uwi.dcit.AgriExpenseTT;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;

import uwi.dcit.AgriExpenseTT.cloud.SignInManager;
import uwi.dcit.AgriExpenseTT.fragments.FragmentSelectLocation;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class Backup extends BaseActivity {

	public static final int SIGN_IN = 0;
	public static final int SIGN_UP = 1;
	public static final int VIEW = 2;
	
	protected SignInManager signInManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		signInManager = new SignInManager(Backup.this, Backup.this);
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Backup Screen");
        setContentView(R.layout.activity_backup_data);
        Log.d("Backup Activity", "Selected the Sign Up Option as Account was not previously created");

        Bundle arguments = new Bundle();
        arguments.putString("type", DHelper.location_country);
        Fragment fragment = new FragmentSelectLocation();
        fragment.setArguments(arguments);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_backup_Container, fragment)
                .commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.backup_data, menu);
		return true;
	}
}
