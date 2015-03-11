package uwi.dcit.AgriExpenseTT;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import uwi.dcit.AgriExpenseTT.fragments.FragmentAddData;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class AddData extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_new_cycle);

        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.NewCycleListContainer, new FragmentAddData())
            .commit();

        // Google Analytics
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Add Data");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_data, menu);
		return true;
	}

	public void appendSub(String string) {
		TextView tv_sub=(TextView)findViewById(R.id.tv_mainNew_subheader);
		String text=tv_sub.getText().toString();
		tv_sub.setText(text+" "+string);
	}
}
