package uwi.dcit.AgriExpenseTT;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import uwi.dcit.AgriExpenseTT.fragments.NewCycleLists;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class NewCycle extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_cycle);
        //Google Analytics
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("New Cycle");
	}
    @Override
    protected void onResume(){
        super.onResume();
        setupInitial();
    }
	
	public void replaceSub(String text){
		((TextView)findViewById(R.id.tv_mainNew_subheader)).setText(text);
	}
	
	private void setupInitial() {
		Bundle arguments = new Bundle();
		arguments.putString("type",DHelper.cat_plantingMaterial);
		ListFragment listFrag = new NewCycleLists();
		listFrag.setArguments(arguments);
		
		this.getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.NewCycleListContainer,listFrag)
			.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.new_cycle, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.action_create_cycle) {
            Bundle b = new Bundle();
            b.putString("action",DHelper.cat_plantingMaterial);
            Intent i = new Intent(this, AddData.class);
            i.putExtras(b);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
