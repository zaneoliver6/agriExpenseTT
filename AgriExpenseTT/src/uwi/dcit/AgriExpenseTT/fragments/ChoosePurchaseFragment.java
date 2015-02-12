package uwi.dcit.AgriExpenseTT.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uwi.dcit.AgriExpenseTT.EditPurchase;
import uwi.dcit.AgriExpenseTT.Main;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;

import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.helpers.NavigationControl;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;
import uwi.dcit.AgriExpenseTT.models.LocalResourcePurchase;


public class ChoosePurchaseFragment extends ListFragment {
	MyListAdapter myListAdapter;
	ArrayList<LocalResourcePurchase> pList;
	SQLiteDatabase db;
	DbHelper dbh;
	DataManager dm;
	String type = null;
	int cycleId;
	LocalCycle curr = null;
	
	final int req_edit = 1;
	
	@Override
	public void onActivityCreated(Bundle savedState){
		super.onActivityCreated(savedState);
		this.registerForContextMenu(getListView());
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dbh	= new DbHelper(this.getActivity().getBaseContext());
		db	= dbh.getReadableDatabase();
		dm	= new DataManager(getActivity(), db, dbh);
		
		try{//when called by ManageResources we dont need any particular cycle
			curr = getArguments().getParcelable("cycle");
		}catch(Exception e){ }
		
		if(curr != null)cycleId = curr.getId();

		try {//for when called by ManageResources the type will be null so we can see all types of purhases
			type=getArguments().getString("det");
			Log.i(Main.APP_NAME, "type: "+type);
		} catch (Exception e) { }
		
		populateList();
		myListAdapter = new MyListAdapter(getActivity(), R.layout.purchased_item, pList);
		setListAdapter(myListAdapter);
        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("Choose Purchase Fragment");
	}
	
	private void populateList() {
		pList	= new ArrayList<LocalResourcePurchase>();
		
		if(type != null && (type.equals("delete") || type.equals("edit")))
			DbQuery.getPurchases(db, dbh, pList, null, null, true);
		else
			DbQuery.getPurchases(db, dbh, pList, type, null,false);//also the type should 
	
		Collections.sort(pList, new Comparator<LocalResourcePurchase>(){
			@Override
			public int compare(LocalResourcePurchase item1, LocalResourcePurchase item2) {
				return item1.getType().compareTo(item2.getType());
			}			
		});
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		//returns the inflated layout which contains the listview
		return inflater.inflate(R.layout.fragment_choose_purchase, container, false);
	}
		
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuInflater inflater = this.getActivity().getMenuInflater();
		inflater.inflate(R.menu.resource_purchase_context_menu, menu);
	}
	
	/**
	 * Context menu refers to the menu that will be brought up on long press
	 */
	public boolean onContextItemSelected(MenuItem item){
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		switch(item.getItemId()){
			case R.id.resource_edit: 								//Edit Purchase
				Log.i(Main.APP_NAME, "Edit The details for resource: "+pList.get(info.position).getQuantifier());
				editPurchaseOption(info.position);
				break;
			case R.id.resource_delete:								//Delete Purchase
				Log.i(Main.APP_NAME, "Delete The details for resource: "+pList.get(info.position).getQuantifier());
				deletePurchaseOption(this.getListView(), info.position);
				break;
			default:
				return super.onContextItemSelected(item);
		}
		return false;
	}
	 
	 @Override
	 public void onListItemClick(ListView l, View v, int position, long id) {
		 if((type != null) && (type.equals("edit"))){				//when called by edit data
			 editPurchaseOption(position);
	 	}else if(type != null && type.equals("delete")){			//when called by delete data
	 		deletePurchaseOption( l,  position);
		}else if(type != null){										//when called by Use Purchases
			launchPurchaseView(position);
		}
	 }
	 
	 public void editPurchaseOption(int position){
		 Intent i=new Intent(getActivity(),EditPurchase.class);
		 i.putExtra("purchase",pList.get(position));
		 startActivityForResult(i, req_edit);
	 }
	 
	 public void launchPurchaseView(int position){
		 Bundle arguments = new Bundle();
		 if(curr != null)
             arguments.putParcelable("cycleMain", curr);
		 arguments.putString("pId",pList.get(position).getpId()+"");//passes the id of the purchase
		 arguments.putString("cycleId",""+cycleId);					// passes the id of the cycle
         arguments.putString("total",getArguments().getString("total"));
		 
		 Fragment newFrag=new FragmentPurchaseUse();
		 newFrag.setArguments(arguments);
         if(getActivity().getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
             ((NavigationControl) getActivity()).navigate(((NavigationControl) getActivity()).getLeftFrag(),newFrag);
             return;
         }
         if(getActivity() instanceof NavigationControl) {
             if(((NavigationControl) getActivity()).getRightFrag() instanceof  FragmentEmpty
                     ||(((NavigationControl) getActivity()).getRightFrag().getClass()==newFrag.getClass()))
                 ((NavigationControl) getActivity()).navigate(((NavigationControl) getActivity()).getLeftFrag(),newFrag);
             else
                 ((NavigationControl) getActivity()).navigate(((NavigationControl) getActivity()).getRightFrag(),newFrag);
         }
      /*   getFragmentManager()
		 	.beginTransaction()
		 	.replace(R.id.useExpenseFrag,newFragment)			// Replace whatever is in the fragment_container view with this fragment,
		 	.addToBackStack(null)								// and add the transaction to the back stack
		 	.commit();*/
	 }
	 
	 public void deletePurchaseOption(ListView l, int position){
		 AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
	        builder1.setMessage("Are you sure you want to delete");
	        builder1.setCancelable(true);
	        Confirm c=new Confirm(position,(MyListAdapter) l.getAdapter());
	        builder1.setPositiveButton("Yes",c);
	        builder1.setNegativeButton("Nope",c);
	        AlertDialog alert1 = builder1.create();
	        alert1.show();
	 }
	 
	 @Override
	 public void onActivityResult(int requestCode,int resultCode,Intent data){
		 super.onActivityResult(requestCode, resultCode, data);
		 //refill list
		 pList=new ArrayList<LocalResourcePurchase>();
		 DbQuery.getPurchases(db, dbh, pList, null, null,true);
		 myListAdapter.notifyDataSetChanged();
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
				Toast.makeText(getActivity(),"Purchase and its related cycles successfully deleted", Toast.LENGTH_SHORT).show();			
				dialog.cancel();
			}else if(which==DialogInterface.BUTTON_NEGATIVE){
				dialog.cancel();
			}
		}
	 }
	 
	 public class MyListAdapter extends ArrayAdapter<LocalResourcePurchase> {
		  
		 Context myContext;
		
		  public MyListAdapter(Context context, int textViewResourceId,
		    ArrayList<LocalResourcePurchase> objects) {
			  super(context, textViewResourceId, objects);
		   myContext = context;
		  }
		
		  @SuppressLint("ViewHolder")
		@Override
		  public View getView(int position, View convertView, ViewGroup parent) {
			   //return super.getView(position, convertView, parent);
			   
			   LayoutInflater inflater = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			   LocalResourcePurchase curr=pList.get(position);
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
			   
			   
			   //when called by ManageResources we dont want the next icon
			   if(type==null)
				   icon.setImageResource(R.drawable.money_doller1);
			   else
				   icon.setImageResource(R.drawable.money_doller1);
			   return row;
		  }
	 }

}