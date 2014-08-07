package fragments;


import helper.DHelper;
import helper.DataManager;
import helper.DbHelper;
import helper.DbQuery;

import java.util.ArrayList;
import uwi.dcit.AgriExpenseTT.EditPurchase;
import uwi.dcit.AgriExpenseTT.R;
import dataObjects.localCycle;
import dataObjects.localResourcePurchase;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChoosePurchase extends ListFragment {
	MyListAdapter myListAdapter;
	ArrayList<localResourcePurchase> pList;
	SQLiteDatabase db;
	DbHelper dbh;
	DataManager dm;
	String type=null;
	int cycleId;
	localCycle curr=null;
	
	final int req_edit=1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh=new DbHelper(this.getActivity().getBaseContext());
		db=dbh.getReadableDatabase();
		dm=new DataManager(getActivity(), db, dbh);
		try{//when called by ViewNavigation we dont need any particular cycle
			curr = getArguments().getParcelable("cycle");
		}catch(Exception e){
		}
		if(curr!=null){
			cycleId=curr.getId();
		}
		
		try {//for when called by ViewNavigation the type will be null so we can see all types of purhases
			type=getArguments().getString("det");
		} catch (Exception e) {
		}
		System.out.println("type: "+type);
		
		pList=new ArrayList<localResourcePurchase>();
		if(type!=null&&(type.equals("delete")||type.equals("edit")))
			DbQuery.getPurchases(db, dbh, pList, null, null,true);
		else
			DbQuery.getPurchases(db, dbh, pList, type, null,false);//also the type should 
		
		
		myListAdapter = new MyListAdapter(getActivity(), R.layout.purchased_item, pList);
		setListAdapter(myListAdapter);
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		//returns the inflated layout which contains the listview
		return inflater.inflate(R.layout.fragment_choose_purchase, container, false);
	}
		
	
		 
	 public class MyListAdapter extends ArrayAdapter<localResourcePurchase> {
	  
		 Context myContext;
		
		  public MyListAdapter(Context context, int textViewResourceId,
		    ArrayList<localResourcePurchase> objects) {
			  super(context, textViewResourceId, objects);
		   myContext = context;
		  }
		
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
			   //return super.getView(position, convertView, parent);
			   
			   LayoutInflater inflater = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			   localResourcePurchase curr=pList.get(position);
			   //Get Layout of An Item and Store it in a view
			   View row=inflater.inflate(R.layout.purchased_item, parent, false);
			   //setting the colours
			   ImageView icon=(ImageView)row.findViewById(R.id.icon_pitem_next);
			   View line=row.findViewById(R.id.line_pitem);
			   if(curr.getType().equals(DHelper.cat_plantingMaterial)){
				   line.setBackgroundColor(Color.parseColor(DHelper.colour_pm));
			   }else if(curr.getType().equals(DHelper.cat_fertilizer)){
				   line.setBackgroundColor(Color.parseColor(DHelper.colour_fertilizer));
			   }else if(curr.getType().equals(DHelper.cat_soilAmendment)){
				   line.setBackgroundColor(Color.parseColor(DHelper.colour_soilam));
			   }else if(curr.getType().equals(DHelper.cat_chemical)){
				   line.setBackgroundColor(Color.parseColor(DHelper.colour_chemical));
			   }else if(curr.getType().equals(DHelper.cat_labour)){
				   line.setBackgroundColor(Color.parseColor(DHelper.colour_labour));
			   }else if(curr.getType().equals(DHelper.cat_other)){
				   line.setBackgroundColor(Color.parseColor(DHelper.colour_other));
			   }
			 
			   //get the elements of that view and set them accordingly
			   TextView header=(TextView)row.findViewById(R.id.tv_pItem_header);
			   TextView det1=(TextView)row.findViewById(R.id.tv_pitem_det1);
			   TextView det2=(TextView)row.findViewById(R.id.tv_pitem_det2);
			   //int pId=Integer.parseInt(ids[position]);		
			   header.setText(DbQuery.findResourceName(db, dbh,curr.getResourceId()));
			   det1.setText("Quantity:"+curr.getQty()+" "+curr.getQuantifier());
			   det2.setText("Cost:$"+curr.getCost());
			   
			   
			   //when called by ViewNavigation we dont want the next icon
			   if(type==null)
				   icon.setImageResource(R.drawable.money_doller1);
			   else
				   icon.setImageResource(R.drawable.money_doller1);
			   return row;
		  }
	 }
	 
	 @Override
		public void onListItemClick(ListView l, View v, int position, long id) {
		 	if((type!=null)&&(type.equals("edit"))){//when called by edit data
		 		System.out.println("type+"+type);
		 		Intent i=new Intent(getActivity(),EditPurchase.class);
		 		i.putExtra("purchase",pList.get(position));
		 		startActivityForResult(i, req_edit);
		 	}else if(type!=null&&type.equals("delete")){//when called by delete data
				AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
	            builder1.setMessage("Are you sure you want to delete");
	            builder1.setCancelable(true);
	            Confirm c=new Confirm(position,(MyListAdapter) l.getAdapter());
	            builder1.setPositiveButton("Yes",c);
	            builder1.setNegativeButton("Nope",c);
	            AlertDialog alert1 = builder1.create();
	            alert1.show();
			}else if(type!=null){//when called by Use Purchases
			 	Toast.makeText(getActivity(), getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
				
				Fragment newFragment =new FragmentPurchaseUse();
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				Bundle b=new Bundle();
				if(curr!=null)
					b.putParcelable("cycleMain", curr);
				b.putString("pId",pList.get(position).getpId()+"");//passes the id of the purchase
				b.putString("cycleId",""+cycleId);// passes the id of the cycle
				newFragment.setArguments(b);
				// Replace whatever is in the fragment_container view with this fragment,
				// and add the transaction to the back stack
				transaction.replace(R.id.useExpenseFrag, newFragment);
				transaction.addToBackStack(null);
				//db.close();
				// Commit the transaction
				transaction.commit();
			}
		}
	 @Override
		public void onActivityResult(int requestCode,int resultCode,Intent data){
			super.onActivityResult(requestCode, resultCode, data);
			//refill list
			pList=new ArrayList<localResourcePurchase>();
			DbQuery.getPurchases(db, dbh, pList, null, null,true);
			myListAdapter.notifyDataSetChanged();
			//call notify dataset changed
			Toast.makeText(getActivity(), "yay", Toast.LENGTH_SHORT).show();
		}
	 
	 private class Confirm implements DialogInterface.OnClickListener{
			int position;
			MyListAdapter l;
			public Confirm(int position,MyListAdapter l){
				this.position=position;
				this.l=l;
			}
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==DialogInterface.BUTTON_POSITIVE){
					dm.deletePurchase(pList.get(position).toRPurchase());
					pList.remove(position);
					l.notifyDataSetChanged();
					Toast.makeText(getActivity(),"Purchase and related cycles deleted", Toast.LENGTH_SHORT).show();			
					dialog.cancel();
					//DeleteExpenseList.this.finish();
				}else if(which==DialogInterface.BUTTON_NEGATIVE){
					dialog.cancel();
				}
			}
		}

}