package uwi.dcit.AgriExpenseTT;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dataObjects.localCycleUse;
import dataObjects.localResourcePurchase;
import helper.DbHelper;
import helper.DbQuery;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ViewCycleUsege extends ActionBarActivity {
	SQLiteDatabase db;
	DbHelper dbh;
	ArrayList<localCycleUse> list;
	ArrayList<localResourcePurchase> pList;
	String type;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_cycle_usege);
		list=new ArrayList<localCycleUse>();
		type=getIntent().getStringExtra("type");
		int cycleId=Integer.parseInt(getIntent().getStringExtra("id"));
		dbh=new DbHelper(this);
		db=dbh.getReadableDatabase();
		
		pList=new ArrayList<localResourcePurchase>();
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
	public class CycUseAdpt extends ArrayAdapter<localCycleUse>{

		public CycUseAdpt(Context context, int resource,List<localCycleUse> objects) {
			super(context, resource,  objects);
			// TODO Auto-generated constructor stub
		}
		 @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
			 View row;
			 row=getLayoutInflater().inflate(R.layout.cycleuse_item,parent,false);
			 localCycleUse curr=list.get(position);
			 int pos=pPos(curr.getPurchaseId());
			 localResourcePurchase p=pList.get(pos);
			 TextView tv_m1=(TextView)row.findViewById(R.id.tv_cycUseItem_head1);
			 TextView tv_s1_1=(TextView)row.findViewById(R.id.tv_cycUseItem_sub1_1);
			 TextView tv_s1_2=(TextView)row.findViewById(R.id.tv_cycUseItem_sub1_2);
			 tv_m1.setText(curr.getResource()+" "+curr.getQuantifier()+"s Used");
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
		Iterator<localResourcePurchase> itr=pList.iterator();
		
		while(itr.hasNext()){
			if(pId==itr.next().getpId())
				return i;
			i++;
		}
		return -1;
	}
}
