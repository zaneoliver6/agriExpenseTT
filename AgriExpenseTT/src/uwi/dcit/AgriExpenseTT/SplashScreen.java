package uwi.dcit.AgriExpenseTT;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.PrefUtils;

public class SplashScreen extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        final Context c = getApplicationContext();
        (new Thread(){
            public void run(){
                try{
                    // Get the database will invoke the onCreate or onUpgrade method based on state of the application
                    (new DbHelper(c)).getWritableDatabase();

                    if (!PrefUtils.dbExist(c)) PrefUtils.setDbExist(c, true);
                    else sleep(2*1000); // Just to show the splash screen for 2 seconds

                    startActivity(new Intent(getBaseContext(), Main.class)); // Start the Main
                }catch(Exception e){e.printStackTrace(); }
                finish();
            }
        }).start();
    }
}
