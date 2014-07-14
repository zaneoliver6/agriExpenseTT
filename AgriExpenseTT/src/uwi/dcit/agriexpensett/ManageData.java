package uwi.dcit.AgriExpenseTT;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ManageData extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_data);
		setup();
		
	}

	private void setup() {
		Button add=(Button)findViewById(R.id.btn_manageData_add);
		Button edit=(Button)findViewById(R.id.btn_manageData_edit);
		Button delete=(Button)findViewById(R.id.btn_manageData_delete);
		Click c=new Click();
		edit.setOnClickListener(c);
		delete.setOnClickListener(c);
		add.setOnClickListener(c);
		
	}
	public class Click implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent i=null;
			if(v.getId()==R.id.btn_manageData_add){
				i=new Intent(ManageData.this,AddData.class);
			}else if(v.getId()==R.id.btn_manageData_edit){
				i=new Intent(ManageData.this,EditData.class);
			}else if(v.getId()==R.id.btn_manageData_delete){
				i=new Intent(ManageData.this,DeleteData.class);
			}	
			startActivity(i);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_data, menu);
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
