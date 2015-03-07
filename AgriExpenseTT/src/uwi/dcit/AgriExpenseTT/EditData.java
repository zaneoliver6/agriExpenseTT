package uwi.dcit.AgriExpenseTT;


import android.os.Bundle;

import uwi.dcit.AgriExpenseTT.fragments.FragmentSlidingTabsEdit;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class EditData extends BaseActivity {


    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_navigation);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.navContentLeft, new FragmentSlidingTabsEdit())
                .commit();

        // Google Analytics
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Edit Data Screen");
	}
}
