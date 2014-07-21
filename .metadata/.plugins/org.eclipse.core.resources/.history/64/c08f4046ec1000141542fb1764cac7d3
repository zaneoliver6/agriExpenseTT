package uwi.dcit.AgriExpenseTT;

import helper.DbHelper;
import helper.DbQuery;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import dataObjects.localCycle;

public class SalesCost extends ActionBarActivity {
	
	TextView harvestDesc;
	TextView salesDesc;
	TextView harvestDet1;
	TextView harvestDet2;
	TextView harvestDet3;
	TextView salesDet1;
	TextView salesDet2;
	TextView salesDet3;
	private final int REQ_HARVEST=1;
	String qtfr;
	
	double amtHarvest;
	double costPer;
	localCycle currCycle;
	EditText et_sell;
	double sellp;
	String crop;
	SQLiteDatabase db;
	DbHelper dbh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales_cost);
		//Bundle b=getIntent().getExtras().getBundle("cyc");
		currCycle=getIntent().getParcelableExtra("cycle");
		dbh=new DbHelper(this);
		db=dbh.getReadableDatabase();
		crop=DbQuery.findResourceName(db, dbh, currCycle.getCropId());
		setup();
	}

	private void setup() {
		harvestDesc=(TextView)findViewById(R.id.tv_salesCost_harvestDesc);
		harvestDet1=(TextView)findViewById(R.id.tv_salesCost_harvestDet1);
		harvestDet2=(TextView)findViewById(R.id.tv_salesCost_harvestDet2);
		harvestDet3=(TextView)findViewById(R.id.tv_salesCost_harvestDet3);
		
		salesDesc=(TextView)findViewById(R.id.tv_salesCost_saleDesc);
		salesDet1=(TextView)findViewById(R.id.tv_salesCost_saleDet1);
		salesDet2=(TextView)findViewById(R.id.tv_salesCost_saleDet2);
		salesDet3=(TextView)findViewById(R.id.tv_salesCost_saleDet3);
		et_sell=(EditText)findViewById(R.id.et_salsesCost_sell);
		TWatch t =new TWatch();
		et_sell.addTextChangedListener(t);
		Button harvestDet=(Button)findViewById(R.id.btn_salesCost_harvest);
		Button btn_done=(Button)findViewById(R.id.btn_salesCost_dne);
		Click c=new Click();
		harvestDet.setOnClickListener(c);
		btn_done.setOnClickListener(c);
		currCycle=getIntent().getExtras().getParcelable("cycle");
		qtfr=currCycle.getHarvestType();
		amtHarvest=currCycle.getHarvestAmt();
		costPer=currCycle.getCostPer();
		
		harvestDet1.setText("Measurement:"+qtfr);
		harvestDet2.setText("Harvest amount:"+amtHarvest+" "+qtfr);
		harvestDet3.setText("Cost per "+qtfr+":$"+costPer);
		salesDet1.setText("$0 per "+qtfr+":loss of $"+costPer);
		salesDet2.setText("Total loss:$"+currCycle.getTotalSpent());
		salesDet3.setVisibility(View.GONE);
	}
	public class Click implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_salesCost_harvest){
				Intent i=new Intent(SalesCost.this,HarvestDetails.class);
				Bundle b=new Bundle();
				b.putParcelable("cycle", currCycle);
				System.out.println("dfds"+currCycle.getCropId());
				i.putExtra("cyc",b);
				startActivityForResult(i,REQ_HARVEST);
			}else if(v.getId()==R.id.btn_salesCost_dne){
				save();
				IntentLauncher i=new IntentLauncher();
				i.run();
			}
		}
		private class IntentLauncher extends Thread{
			@Override
			public void run(){
				//Bundle b=new Bundle();
				//b.putParcelable("cycle",currCycle);
				Intent n=new Intent(SalesCost.this,CycleUseageRedesign.class);
				n.putExtra("cycleMain", currCycle);
				startActivity(n);
				finish();
			}
		}
		private void save() {
			ContentValues cv=new ContentValues();
			cv.put(DbHelper.CROPCYCLE_COSTPER, sellp);
			DbHelper dbh=new DbHelper(SalesCost.this);
			SQLiteDatabase db=dbh.getReadableDatabase();
			db.update(DbHelper.TABLE_CROPCYLE, cv, DbHelper.CROPCYCLE_ID+"="+currCycle.getId(), null);
			currCycle.setCostPer(sellp);
			currCycle.setHarvestAmt(amtHarvest);
			currCycle.setHarvestType(qtfr);
			System.out.println("changes saved");
		}
		
	}
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_CANCELED){
			return;
		}
		if(requestCode==REQ_HARVEST){
			qtfr=data.getExtras().getString("qtfr");
			amtHarvest=data.getExtras().getDouble("amt");
			harvestDet1.setText("Measurement:"+qtfr);
			harvestDet2.setText("Harvest amount:"+amtHarvest+" "+qtfr);
			costPer=((currCycle.getTotalSpent())/amtHarvest);
			harvestDet3.setText("Cost per "+qtfr+":$"+costPer);
			salesDet1.setText("$0 per "+qtfr+":loss of $"+costPer);
			salesDet2.setText("Total loss:$"+currCycle.getTotalSpent());
			salesDet3.setVisibility(View.GONE);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sales_cost, menu);
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
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if(!(s.toString().equals(null)||s.toString().equals(""))){
				sellp=Double.parseDouble(s.toString());
				if(sellp<costPer){
					salesDet1.setText("$"+sellp+" per "+qtfr+":loss of $"+(costPer-sellp));
					salesDet2.setText("Total loss:$"+(costPer-sellp)*amtHarvest);
				}else{
					salesDet1.setText("$"+sellp+" per "+qtfr+":profit of $"+(sellp-costPer));
					salesDet2.setText("Total profit:$"+(sellp-costPer)*amtHarvest);
				}
			}
			// TODO Auto-generated method stub
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
		 
	 }
}
