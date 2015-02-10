package uwi.dcit.AgriExpenseTT;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;

import uwi.dcit.AgriExpenseTT.fragments.help.HelpListFragment;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class HelpScreen extends BaseActivity {
	@Override
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);
        
        ListFragment fragment = new HelpListFragment(); 
        Log.d(MainMenu.APP_NAME, "Created Instance of HelpListFragment");
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Help Screen");
        
        //Set Initial Fragment
        this.getSupportFragmentManager()
        	.beginTransaction()
        	.replace(R.id.help_lists, fragment)
        	.commit();
	}
}
