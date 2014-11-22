package uwi.dcit.AgriExpenseTT;

import uwi.dcit.AgriExpenseTT.fragments.help.HelpListFragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class HelpScreen extends ActionBarActivity {
	@Override
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);
        
        ListFragment fragment = new HelpListFragment(); 
        Log.d(MainMenu.APP_NAME, "Created Instance of HelpListFragment");
        
        //Set Initial Fragment
        this.getSupportFragmentManager()
        	.beginTransaction()
        	.replace(R.id.help_lists, fragment)
        	.commit();
	}
}
