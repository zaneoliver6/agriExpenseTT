package uwi.dcit.AgriExpenseTT;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import uwi.dcit.AgriExpenseTT.fragments.FragmentBackupList;
import uwi.dcit.AgriExpenseTT.fragments.FragmentSelectLocation;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.helpers.SignInManager;

public class Backup extends ActionBarActivity {

	public static final int SIGN_IN = 0;
	public static final int SIGN_UP = 1;
	public static final int VIEW = 2;
	
	protected SignInManager signInManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		signInManager = new SignInManager(Backup.this, Backup.this);
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Backup Screen");

		Fragment fragment = new FragmentBackupList();
		
		Bundle prev_argument = this.getIntent().getExtras();
		if (prev_argument != null && prev_argument.containsKey("ACTION")){
			switch ( prev_argument.getInt("ACTION")){
				case SIGN_IN:
					Log.d("Backup Activity", "Selected the Sign In Option as Account was already created");
					signInManager.signIn();
					break;
				case SIGN_UP:
					Log.d("Backup Activity", "Selected the Sign Up Option as Account was not previously created");
					Bundle arguments = new Bundle();
					arguments.putString("type", DHelper.location_country);					
					fragment = new FragmentSelectLocation();
					fragment.setArguments(arguments);
					break;
				case VIEW:
					Log.d("Backup Activity", "Selected the View Option as Account was already created");
					break;
				default:
					Log.d("Backup Activity", "No valid option found, reverting to view");
			}
		}
		
		setContentView(R.layout.activity_backup_data);
		if (savedInstanceState == null) 
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
