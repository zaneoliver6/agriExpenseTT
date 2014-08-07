package uwi.dcit.AgriExpenseTT;

import helper.DHelper;
import helper.DbHelper;
import helper.DbQuery;

import java.util.ArrayList;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class EditChooseLists extends ActionBarActivity {
	ArrayList<String> list;
	ListView lv;
	
	SQLiteDatabase db;
	DbHelper dbh;
	String content;
	String category;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_reuse);
		//get list
		initial();
		populateList();
		ArrayAdapter<String> listAdapt=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
		lv.setAdapter(listAdapt);
		ItemClick c=new ItemClick();
		lv.setOnItemClickListener(c);
		TWatch tw=new TWatch(listAdapt);
		EditText et_search=(EditText)findViewById(R.id.et_listReuse_search);
		et_search.addTextChangedListener(tw);
		//on click events
		
	}
	public class ItemClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Toast.makeText(EditChooseLists.this, list.get(position), Toast.LENGTH_SHORT).show();
			Intent i=new Intent();
			i.putExtra("desc",content);
			i.putExtra("content", list.get(position));
			setResult(1,i);//used to set the results for the parent activity ( the one that launched this one)
			finish();
		}
		
	}

	private void populateList() {
		if(content.equals(DHelper.cat_plantingMaterial)
				||content.equals(DHelper.cat_fertilizer)
				||content.equals(DHelper.cat_chemical)
				||content.equals(DHelper.cat_soilAmendment)
				||content.equals(DHelper.cat_labour)){
			DbQuery.getResources(db, dbh, content, list);
		}else if(content.equals("land")){
			list.add("acre");
			list.add("hectre");
			list.add("bed");
		}else if(content.equals("quantifier")){
			category=getIntent().getExtras().getString("category");
			if(category.equals(DHelper.cat_plantingMaterial)){
				list.add(DHelper.qtf_plantingMaterial_seed);
				list.add(DHelper.qtf_plantingMaterial_seedling);
				list.add(DHelper.qtf_plantingMaterial_stick);
			}else if(category.equals(DHelper.cat_fertilizer)){
				list.add(DHelper.qtf_fertilizer_g);
				list.add(DHelper.qtf_fertilizer_lb);
				list.add(DHelper.qtf_fertilizer_kg);
				list.add(DHelper.qtf_fertilizer_bag);
			}else if(category.equals(DHelper.cat_soilAmendment)){
				list.add(DHelper.qtf_soilAmendment_bag);
				list.add(DHelper.qtf_soilAmendment_truck);
			}else if(category.equals(DHelper.cat_chemical)){
				list.add(DHelper.qtf_chemical_ml);
				list.add(DHelper.qtf_chemical_L);
				list.add(DHelper.qtf_chemical_oz);
				list.add(DHelper.qtf_chemical_g);
				list.add(DHelper.qtf_chemical_kg);
			}
		}else if(content.equals("measurement")){
			list.add("Lb");
			list.add("Kg");
			list.add("Bag");
			list.add("Bundle");
			list.add("Head");
			list.add("100's");
			list.add("5lb Bundle");
		}
	}


	private void initial() {
		dbh=new DbHelper(EditChooseLists.this);
		db=dbh.getReadableDatabase();
		list=new ArrayList<String>();
		lv=(ListView)findViewById(android.R.id.list);
		Bundle data=getIntent().getExtras();
		content=data.getString("desc");
		System.out.println(content);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_choose_lists, menu);
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
	public class TWatch implements TextWatcher{
		 ArrayAdapter<String> adpt;
		 public TWatch(ArrayAdapter<String> adpt){
			 super();
			 this.adpt=adpt;
		 }
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			adpt.getFilter().filter(s);
			// TODO Auto-generated method stub
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
		 
	 }

}
