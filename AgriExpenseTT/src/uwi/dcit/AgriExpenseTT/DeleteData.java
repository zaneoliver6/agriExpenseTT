package uwi.dcit.AgriExpenseTT;

import android.os.Bundle;

import uwi.dcit.AgriExpenseTT.fragments.FragmentSlidingDelete;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class DeleteData extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_navigation);

        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Delete Data");

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.navContentLeft,new FragmentSlidingDelete())
                .commit();
	}
}
