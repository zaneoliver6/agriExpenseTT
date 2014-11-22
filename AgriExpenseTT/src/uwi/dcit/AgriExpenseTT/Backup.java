package uwi.dcit.AgriExpenseTT;

import uwi.dcit.AgriExpenseTT.fragments.FragmentBackupList;
import uwi.dcit.AgriExpenseTT.helpers.SignInManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class Backup extends ActionBarActivity {

	protected SignInManager signInObject;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		signInObject = new SignInManager(Backup.this, Backup.this);
		
		setContentView(R.layout.activity_backup_data);
		if (savedInstanceState == null) 
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.fragment_backup_Container, new FragmentBackupList())
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
