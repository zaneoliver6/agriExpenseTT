package uwi.dcit.AgriExpenseTT.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dcit.agriexpensett.rPurchaseApi.model.RPurchase;

import uwi.dcit.AgriExpenseTT.Main;
import uwi.dcit.AgriExpenseTT.NewPurchase;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.models.CycleContract.CycleEntry;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;
import uwi.dcit.AgriExpenseTT.models.ResourcePurchaseContract.ResourcePurchaseEntry;

public class FragmentNewPurchaseLast extends Fragment{
	private View view;
	private EditText et_qty;
	private EditText et_cost;
	private TextView error;
	private String category;
	private String resource;
	private String quantifier;
	private LocalCycle currC=null;
	private int resId;
	private SQLiteDatabase db;
	private DbHelper dbh;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.fragment_newpurchase_last, container, false);
		//curr=savedInstanceState.getParcelable("details");
		et_qty=(EditText)view.findViewById(R.id.et_newPurchaselast_qty);
		et_cost=(EditText)view.findViewById(R.id.et_newPurchaselast_cost);
		
		error=(TextView)view.findViewById(R.id.tv_newPurchase_error);
		category=getArguments().getString("category");
		resource=getArguments().getString("resource");
		quantifier=getArguments().getString("quantifier");
		if(category.equals(DHelper.cat_labour)){
			et_qty.setHint("Number of "+quantifier+"'s "+resource+" is going to work");
			et_cost.setHint("Cost of all "+quantifier+"'s "+resource+" will work for");
		}else if(category.equals(DHelper.cat_fertilizer)||category.equals(DHelper.cat_soilAmendment)){
			et_qty.setHint("Number of "+quantifier+"'s of "+resource);
			et_cost.setHint("Cost of all "+quantifier+"s");
		}else if(category.equals(DHelper.cat_chemical)){
			et_qty.setHint("Number of "+quantifier+"'s of "+resource);
			et_cost.setHint("Total cost of all "+resource);
		}else{
			et_qty.setHint("Number of "+resource+" "+quantifier+"s");
			et_cost.setHint("Cost of all "+resource+" "+quantifier+"s");
		}
		dbh=new DbHelper(getActivity().getBaseContext());
		db=dbh.getReadableDatabase();
		
		Button btn_done=(Button)view.findViewById(R.id.btn_newpurchaselast_done);
		resId= DbQuery.getNameResourceId(db, dbh, resource);
		Click c=new Click();
		btn_done.setOnClickListener(c);

        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("New Purchase Fragment");

        view.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (!(v instanceof EditText)) {
                            ((NewPurchase) getActivity()).hideSoftKeyboard();
                        }
                        return false;
                    }
                }
        );

		return view;
	}
	public class Click implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_newpurchaselast_done){
				double qty,cost;
				if( ((et_qty.getText().toString()) == null)||((et_qty.getText().toString()).equals(""))  ){
					error.setVisibility(View.VISIBLE);
					error.setText("Enter Quantity");
					return;
				}else{
					qty=Double.parseDouble(et_qty.getText().toString());
				}
				if( (et_cost.getText().toString() == null) || ((et_cost.getText().toString()).equals("")) ){
					error.setVisibility(View.VISIBLE);
					error.setText("Enter cost");
					return;
				}else{
					cost=Double.parseDouble(et_cost.getText().toString());
				}
				
				DataManager dm=new DataManager(getActivity().getBaseContext(),db,dbh);
				try{
					currC=getArguments().getParcelable("cycle");
				}catch (Exception e){}
				//this is for when labour is 'purchased'/hired for a single cycle
				if(category.equals(DHelper.cat_labour)&&currC!=null){
					//insert purchase
					dm.insertPurchase(resId, quantifier, qty, category, cost);
					int pId=DbQuery.getLast(db, dbh,ResourcePurchaseEntry.TABLE_NAME);
					RPurchase p=DbQuery.getARPurchase(db, dbh, pId);
					//use all of the qty of that purchase in the given cycle
					dm.insertCycleUse(currC.getId(), p.getPId(), qty, p.getType(),quantifier,p.getCost());
					//update purchase
					p.setQtyRemaining(p.getQtyRemaining()-qty);
					ContentValues cv=new ContentValues();
					cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING,p.getQtyRemaining());
					dm.updatePurchase(p,cv);
					//update cycle
					currC.setTotalSpent(currC.getTotalSpent()+cost);
					cv=new ContentValues();
					cv.put(CycleEntry.CROPCYCLE_TOTALSPENT, currC.getTotalSpent());
					dm.updateCycle(currC,cv);
				}else{
					if(category.equals(DHelper.cat_other)){//if its the other category
						if(resId==-1){//and the resource does not exist
							resId=DbQuery.insertResource(db, dbh, DHelper.cat_other, resource);//then insert it !
						}
					}
					dm.insertPurchase(resId, quantifier, qty, category, cost);
				}
				//dm.insertPurchase(resourceId, quantifier, qty, type, cost);
                Intent n=new Intent(getActivity(),Main.class);
                new IntentLauncher().run();
                getActivity().startActivity(n);
			}
		}
		
	}
    private class IntentLauncher extends Thread{
		@Override
		public void run(){getActivity().finish();}
	}
}
