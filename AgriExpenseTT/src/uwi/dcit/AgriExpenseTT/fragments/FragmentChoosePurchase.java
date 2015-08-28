package uwi.dcit.AgriExpenseTT.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uwi.dcit.AgriExpenseTT.EditPurchase;
import uwi.dcit.AgriExpenseTT.NewCycle;
import uwi.dcit.AgriExpenseTT.NewPurchase;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.CurrencyFormatHelper;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.DateFormatHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.NavigationControl;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;
import uwi.dcit.AgriExpenseTT.models.LocalResourcePurchase;

public class FragmentChoosePurchase extends ListFragment {
	PurchaseListAdapter myListAdapter;
	ArrayList<LocalResourcePurchase> pList;
	SQLiteDatabase db;
	DbHelper dbh;
	DataManager dm;
	String type = null;
	int cycleId;
	LocalCycle curr = null;
	View view;
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
		db	= dbh.getWritableDatabase();
		dm	= new DataManager(getActivity(), db, dbh);

        if (getArguments() != null){
            curr = getArguments().getParcelable("cycle");
            type = getArguments().getString("det");
        }else{
            Log.d("ChoosePurchase", "No Arguments Received");
        }
		
		if(curr != null)cycleId = curr.getId();


		populateList();
		myListAdapter = new PurchaseListAdapter(getActivity(), R.layout.purchased_item, pList);
		setListAdapter(myListAdapter);
//        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("Choose Purchase Fragment");
	}

	private void createNewPurchase(){
			Intent intent = new Intent(getActivity().getApplicationContext(), NewPurchase.class);
			startActivity(intent);
	}

	private void populateList() {
		pList	= new ArrayList<>();
		
		if(type != null && (type.equals("delete") || type.equals("edit")))
			DbQuery.getPurchases(db, dbh, pList, null, null, true);
		else
			DbQuery.getPurchases(db, dbh, pList, type, null,false);//also the type should
	
		Collections.sort(pList, new Comparator<LocalResourcePurchase>() {
			@Override
			public int compare(LocalResourcePurchase item1, LocalResourcePurchase item2) {
				return item1.getType().compareTo(item2.getType());
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//returns the inflated layout which contains the list view
		view = inflater.inflate(R.layout.fragment_choose_purchase, container, false);

		final Button button = (Button) view.findViewById(R.id.fragment_choose_purchase_button);

		button.setText("Add Purchase");
		button.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				createNewPurchase();
			}
		});
		return view;
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
				editPurchaseOption(info.position);
				break;
			case R.id.resource_delete:								//Delete Purchase
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
         LocalResourcePurchase l = pList.get(position);
         Log.d(this.getClass().getName(),"Date of the Purchase as " + l.getDate());
         i.putExtra("purchase", l );
//		 i.putExtra("purchase",pList.get(position));
		 startActivityForResult(i, req_edit);
	 }
	 
	 public void launchPurchaseView(int position){
		 Bundle arguments = new Bundle();
		 if(curr != null)
             arguments.putParcelable("cycleMain", curr);
		 arguments.putString("pId", pList.get(position).getpId() + "");//passes the id of the purchase
		 arguments.putString("cycleId",""+cycleId);					// passes the id of the cycle
         arguments.putString("total", getArguments().getString("total"));
		 
		 Fragment newFrag=new FragmentPurchaseUse();
		 newFrag.setArguments(arguments);

         if(getActivity().getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT || ((NavigationControl) getActivity()).getRightFrag()==null){
             ((NavigationControl) getActivity()).navigate(((NavigationControl) getActivity()).getLeftFrag(),newFrag);
             return;
         }
         if(getActivity() instanceof NavigationControl) {
             if(((NavigationControl) getActivity()).getRightFrag() instanceof  FragmentEmpty  ||(((NavigationControl) getActivity()).getRightFrag().getClass()==newFrag.getClass()))
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
	        Confirm c=new Confirm(position,(PurchaseListAdapter) l.getAdapter());
	        builder1.setPositiveButton("Delete",c);
	        builder1.setNegativeButton("Cancel",c);
	        AlertDialog alert1 = builder1.create();
	        alert1.show();
	 }
	 
	 @Override
	 public void onActivityResult(int requestCode,int resultCode,Intent data){
		 super.onActivityResult(requestCode, resultCode, data);
		 //refill list
		 pList=new ArrayList<>();
		 DbQuery.getPurchases(db, dbh, pList, null, null,true);
		 myListAdapter.notifyDataSetChanged();
	 }
	 
	 private class Confirm implements DialogInterface.OnClickListener{
		int position;
		PurchaseListAdapter l;
		public Confirm(int position,PurchaseListAdapter l){
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
	 
	 public class PurchaseListAdapter extends ArrayAdapter<LocalResourcePurchase> {
		  
		 Context myContext;
         public PurchaseListAdapter(Context context, int textViewResourceId,ArrayList<LocalResourcePurchase> objects) {
             super(context, textViewResourceId, objects);
             myContext = context;
		 }
         @SuppressLint("ViewHolder")
         @Override
         public View getView(int position, View convertView, ViewGroup parent) {
             LayoutInflater inflater = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             LocalResourcePurchase curr = pList.get(position);

             //Get Layout of An Item and Store it in a view
			 View row=inflater.inflate(R.layout.purchased_item, parent, false);

			 if(curr.getQtyRemaining()==0.00){
				 ((ImageView)row.findViewById(R.id.icon_pitem_next)).setImageResource(R.drawable.ic_empty2);
			 }

			 //setting the colours
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
             TextView det3=(TextView)row.findViewById(R.id.tv_pitem_det3);
             TextView dateTV = (TextView)row.findViewById(R.id.tv_pitem_date);

			   //int pId=Integer.parseInt(ids[position]);		
			 header.setText(DbQuery.findResourceName(db, dbh,curr.getResourceId()));
			 det1.setText("Purchased: "+curr.getQty()+" "+curr.getQuantifier());
             det2.setText("Remaining: "+curr.getQtyRemaining()+" "+curr.getQuantifier());
             det3.setText("Cost: $" + CurrencyFormatHelper.getCurrency(curr.getCost()));
             dateTV.setText("Date: " + DateFormatHelper.getDateStr(curr.getDate()));
			   
			   
			 //TODO Set a custom icon based on the type of the resource purchased
			 return row;
		 }
	 }

}