package uwi.dcit.AgriExpenseTT;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import uwi.dcit.AgriExpenseTT.fragments.NewPurchaseLists;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;


public class NewPurchase extends BaseActivity {
	TextView sub_head;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_cycle);
		setupUI();
        //Google Analytics
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("New Purchase");
	}
    @Override
    protected void onResume(){
        super.onResume();
        setupInitial();
    }
	
	private void setupInitial() {//to make the bundle for the add purchase button use the category argument
		Bundle arguments = new Bundle();
		arguments.putString("type","category");
		
		ListFragment listfrag = new NewPurchaseLists();
		listfrag.setArguments(arguments);
		
		getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.NewCycleListContainer,listfrag)
			.commit();
	}

	public void setupUI() {
		View v=findViewById(R.id.container_newcycle);
		TouchL l=new TouchL();
		v.setOnTouchListener(l);
	}
	
	public class TouchL implements OnTouchListener{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
//			if(v.getId()!=R.id.et_listReuse_search)
//				hideSoftKeyboard();
			return false;
		}
	   
   }

	public void replaceSub(String text){
		sub_head=(TextView)findViewById(R.id.tv_mainNew_subheader);
		sub_head.setText(text);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_purchase, menu);
		return true;
	}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Activity activity = this;
        if (item.getItemId() == R.id.action_create_purchase) {
            // Create Dialog to get the appropriate resource category
            final CharSequence [] items = new CharSequence[]{DHelper.cat_chemical, DHelper.cat_fertilizer, DHelper.cat_other, DHelper.cat_soilAmendment, DHelper.cat_plantingMaterial};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog alert;
            builder.setTitle("Select Resource Type")
                .setItems(items,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Bundle b = new Bundle();
                        b.putString("action", items[item].toString());
                        Intent i = new Intent(activity, AddData.class);
                        i.putExtras(b);
                        startActivity(i);
                    }
                });
            alert = builder.create();
            alert.show();


            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
