package uwi.dcit.AgriExpenseTT;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import uwi.dcit.AgriExpenseTT.dbstruct.structs.Resource;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.models.CycleContract;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;

public class SalesCost extends BaseActivity {
	
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
	LocalCycle currCycle;
	EditText et_sell;
	double sellp;
	String crop;
	SQLiteDatabase db;
	DbHelper dbh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales_cost);
		currCycle = getIntent().getParcelableExtra("cycle");
		dbh = new DbHelper(this);
		db  = dbh.getWritableDatabase();
		crop= Resource.findResourceName(db, dbh, currCycle.getCropId());
		setup();

        // Added Google Analytics
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("Sales cost Screen");
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
		Click c = new Click(this);

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

        Context context;

        public Click(Context context){
            this.context = context;
        }

		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_salesCost_harvest){
				Intent i=new Intent(SalesCost.this,HarvestDetails.class);
				Bundle b=new Bundle();
				b.putParcelable("cycle", currCycle);
				i.putExtra("cyc",b);
				startActivityForResult(i,REQ_HARVEST);

			}else if(v.getId()==R.id.btn_salesCost_dne){
				save();

                Intent n=new Intent(SalesCost.this,CycleUseage.class);
                n.putExtra("cycleMain", currCycle);
                startActivity(n);
			}
		}

		private void save() {
			ContentValues cv=new ContentValues();
			cv.put(CycleContract.CycleEntry.CROPCYCLE_COSTPER, sellp);
			DbHelper dbh=new DbHelper(SalesCost.this);
			SQLiteDatabase db=dbh.getWritableDatabase();
			int res = db.update(CycleContract.CycleEntry.TABLE_NAME, cv, CycleContract.CycleEntry._ID+"="+currCycle.getId(), null);
            if (res != -1){
                Toast.makeText(context, "Saved Sale Successfully", Toast.LENGTH_SHORT).show();
            }
			currCycle.setCostPer(sellp);
			currCycle.setHarvestAmt(amtHarvest);
			currCycle.setHarvestType(qtfr);
		}
	}

	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_CANCELED){
			return;
		}
		if(requestCode == REQ_HARVEST){
			qtfr = data.getExtras().getString("qtfr");
			amtHarvest = data.getExtras().getDouble("amt");
			harvestDet1.setText("Measurement: "+qtfr);
			harvestDet2.setText("Harvest amount: "+amtHarvest+" "+qtfr);
			costPer = ((currCycle.getTotalSpent())/amtHarvest);
			harvestDet3.setText("Cost per "+qtfr+": $"+costPer);
			salesDet1.setText("$0 per "+qtfr+": loss of $"+costPer);
			salesDet2.setText("Total loss: $"+currCycle.getTotalSpent());
			salesDet3.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sales_cost, menu);
		return true;
	}

	public class TWatch implements TextWatcher{
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if(!(s.toString() == null||s.toString().equals(""))){
				sellp=Double.parseDouble(s.toString());
				if(sellp<costPer){
					salesDet1.setText("$"+sellp+" per "+qtfr+":loss of $"+(costPer-sellp));
					salesDet2.setText("Total loss:$"+(costPer-sellp)*amtHarvest);
				}else{
					salesDet1.setText("$"+sellp+" per "+qtfr+":profit of $"+(sellp-costPer));
					salesDet2.setText("Total profit:$"+(sellp-costPer)*amtHarvest);
				}
			}
			
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			
			
		}
		 
	 }
}
