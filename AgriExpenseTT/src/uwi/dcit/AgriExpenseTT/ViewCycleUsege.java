package uwi.dcit.AgriExpenseTT;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.models.LocalCycleUse;
import uwi.dcit.AgriExpenseTT.models.LocalResourcePurchase;

public class ViewCycleUsege extends BaseActivity {
	SQLiteDatabase db;
	DbHelper dbh;
	ArrayList<LocalCycleUse> list;
	ArrayList<LocalResourcePurchase> pList;
	String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_cycle_usege);
        GAnalyticsHelper.getInstance(this.getApplicationContext()).sendScreenView("View Cycle Usage");
		list=new ArrayList<LocalCycleUse>();
		type=getIntent().getStringExtra("type");
		int cycleId=Integer.parseInt(getIntent().getStringExtra("id"));
		dbh=new DbHelper(this);
		db=dbh.getWritableDatabase();
		
		pList=new ArrayList<LocalResourcePurchase>();
		DbQuery.getPurchases(db, dbh, pList, type, null,true);
		ListView listview=(ListView)findViewById(R.id.listview_cycleUse);
		
		DbQuery.getCycleUse(db, dbh, cycleId, list, type);
		CycUseAdpt c=new CycUseAdpt(this,R.layout.cycleuse_item,list);
		listview.setAdapter(c);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_cycle_usege, menu);
		return true;
	}


	public class CycUseAdpt extends ArrayAdapter<LocalCycleUse>{

		public CycUseAdpt(Context context, int resource,List<LocalCycleUse> objects) {
			super(context, resource,  objects);
		}
		 @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
			 View row;
			 row=getLayoutInflater().inflate(R.layout.cycleuse_item,parent,false);
			 LocalCycleUse curr=list.get(position);
			 int pos=pPos(curr.getPurchaseId());
			 LocalResourcePurchase p=pList.get(pos);
			 TextView tv_m1=(TextView)row.findViewById(R.id.tv_cycUseItem_head1);
			 TextView tv_s1_1=(TextView)row.findViewById(R.id.tv_cycUseItem_sub1_1);
			 TextView tv_s1_2=(TextView)row.findViewById(R.id.tv_cycUseItem_sub1_2);
			 tv_m1.setText(curr.getResource()+" "+curr.getQuantifier()+"s Used");
			 Log.i("TEST QUANTIFIER",">>>>:"+curr.getQuantifier());
			 tv_s1_1.setText("Used:"+curr.getAmount()+" "+curr.getQuantifier());
			 tv_s1_2.setText("Cost:$"+curr.getUseCost());
			 
			 TextView tv_m2=(TextView)row.findViewById(R.id.tv_cycUseItem_head2);
			 TextView tv_s2_1=(TextView)row.findViewById(R.id.tv_cycUseItem_sub2_1);
			 TextView tv_s2_2=(TextView)row.findViewById(R.id.tv_cycUseItem_sub2_2);
			 tv_m2.setText("Used from purchase of "+curr.getResource()+" "+p.getQuantifier()+"s at");
			 tv_s2_1.setText("Quantity:"+p.getQty());
			 tv_s2_2.setText("Cost:$"+p.getCost());
			 return row;
		 }
	}

	public int pPos(int pId){
		int i=0;
		Iterator<LocalResourcePurchase> itr=pList.iterator();
		
		while(itr.hasNext()){
			if(pId==itr.next().getpId())
				return i;
			i++;
		}
		return -1;
	}
}
