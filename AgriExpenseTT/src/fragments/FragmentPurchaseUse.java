package fragments;

import helper.DHelper;
import helper.DataManager;
import helper.DbHelper;
import helper.DbQuery;

import java.text.DecimalFormat;
import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.CycleUseageRedesign;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.UseResource;

import com.example.agriexpensett.rpurchaseendpoint.model.RPurchase;

import dataObjects.localCycle;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentPurchaseUse extends Fragment {
	View view;
	SQLiteDatabase db;
	DbHelper dbh;
	localCycle c=null;
	
	RPurchase p;
	double useAmount=0;//the amount you are going to use
	
	double calcost=0.0,TypeSpent=0.0;
	
	double amtRem,amtPur;
	String quantifier;
	//TODO 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.activity_use_purchase_frag, container, false);
		dbh=new DbHelper(this.getActivity().getBaseContext());
		db=dbh.getReadableDatabase();
		int pId=Integer.parseInt(getArguments().getString("pId"));
		int cycleId=Integer.parseInt(getArguments().getString("cycleId"));
		TypeSpent=((UseResource)getActivity()).getTotal();
		setDetails(pId,cycleId);
		return view;
	}
	TextView d_buttom1;
	TextView d_buttom2;
	TextView d_buttom3;
	TextView d_top;
	EditText et_amt;
	
	Button btn_typeUse;
	String typeUse;
	private void setDetails(int pId,int cycleId) {
		p=DbQuery.getARPurchase(db, dbh,pId);
		c=getArguments().getParcelable("cycleMain");
		amtRem=p.getQtyRemaining();amtPur=p.getQty();quantifier=p.getQuantifier();
		btn_typeUse=(Button)view.findViewById(R.id.btn_UsePurchase_useType);
		btn_typeUse.setText(quantifier);
		
		TextView h_main=(TextView)view.findViewById(R.id.tv_usePurchase_header1);
		TextView h_sub=(TextView)view.findViewById(R.id.tv_usePurchase_header2);
		d_top=(TextView)view.findViewById(R.id.tv_usePurchase_top_det1);
		
		d_buttom1=(TextView)view.findViewById(R.id.tv_usePurchase_buttom_det1);
		d_buttom2=(TextView)view.findViewById(R.id.tv_usePurchase_buttom_det2);
		d_buttom3=(TextView)view.findViewById(R.id.tv_usePurchase_buttom_det3);
		et_amt=(EditText)view.findViewById(R.id.et_useAmt);
		
		//Setting Labels
		System.out.println("resId"+p.getResourceId());
		h_main.setText("Currently");
		h_sub.setText("Use");
		
		DecimalFormat df = new DecimalFormat("#.00");   
		d_buttom1.setText("Using "+useAmount+" "+quantifier+" adds $"+calcost+" to the current crop cycle");
		d_buttom2.setText("Total spent on "+p.getType()+" becomes $"+Double.valueOf(df.format(TypeSpent))); 
		d_buttom3.setText("The crop cycle's new total cost becomes $"+Double.valueOf(df.format(c.getTotalSpent())));
	
		Button calc=(Button)view.findViewById(R.id.btn_UsePurchase_cal);
		Button dne=(Button)view.findViewById(R.id.btn_usePurchase_done);
		label();
		
		Click clic=new Click();
		calc.setOnClickListener(clic);
		dne.setOnClickListener(clic);
		btn_typeUse.setOnClickListener(clic);
		
	}
	//sets labels to match data
	private void label(){
		DecimalFormat df = new DecimalFormat("#.00"); 
		String res=DbQuery.findResourceName(db, dbh,p.getResourceId());
		d_top.setText("Curently this "+res+" has "+quantifier+" "+amtRem+" remaining");
		
		d_buttom1.setText("Using "+useAmount+" "+quantifier+" adds $"+calcost+" to the current crop cycle");
		d_buttom2.setText("Total spent on "+p.getType()+" becomes $"+(TypeSpent+calcost));
		d_buttom3.setText("The crop cycle's new total cost becomes $"+Double.valueOf(df.format((c.getTotalSpent()+calcost))));
	
	}
	private class Click implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_UsePurchase_useType){
				ArrayList<String> arr=new ArrayList<String>();
				popArr(arr);
				showPopup(getActivity(), 0, arr);
				return;
			}
			
			if(et_amt.getText().toString().equals(null)||et_amt.getText().toString().equals("")){
				Toast.makeText(getActivity().getBaseContext(), "Enter Amount", Toast.LENGTH_SHORT).show();
				return;
			}else{
				useAmount=Double.parseDouble(et_amt.getText().toString());
				if(useAmount>amtRem){
					Toast.makeText(getActivity().getBaseContext(), "Not enough "+quantifier+" remaining", Toast.LENGTH_SHORT).show();
					return;
				}
				calcost=(useAmount/amtPur)*p.getCost();
				calcost=Math.round(calcost*100.0)/100.0;
				if(v.getId()==R.id.btn_UsePurchase_cal){
					Toast.makeText(getActivity(), ""+calcost, Toast.LENGTH_SHORT).show();
					label();//resets labels to match data
				}else if(v.getId()==R.id.btn_usePurchase_done){
					DataManager dm=new DataManager(getActivity().getBaseContext());
					dm.insertCycleUse(c.getId(), p.getPId(), useAmount, p.getType(),quantifier,calcost);
					
					double rem=(amtRem-useAmount)*convertFromTo(quantifier,p.getQuantifier());
					Toast.makeText(getActivity(), rem+" ", Toast.LENGTH_SHORT).show();
					//updating purchase
					p.setQtyRemaining(rem);
					ContentValues cv=new ContentValues();
					cv.put(DbHelper.RESOURCE_PURCHASE_REMAINING,p.getQtyRemaining());
					dm.updatePurchase(p,cv);
					//updating cycle
					c.setTotalSpent(c.getTotalSpent()+calcost);
					cv=new ContentValues();
					cv.put(DbHelper.CROPCYCLE_TOTALSPENT, c.getTotalSpent());
					dm.updateCycle(c,cv); 
					Log.i(getTag(), c.getTotalSpent()+" "+c.getId());
					IntentLauncher i=new IntentLauncher();
					i.start();
				}
			}
		}
	}
	private class IntentLauncher extends Thread{
		@Override
		public void run(){
			//Bundle b=new Bundle();
			//b.putParcelable("cycleMain",c);
			System.out.println("i am here !!!");
			Intent n=new Intent(getActivity(),CycleUseageRedesign.class);
			n.putExtra("cycleMain", c);
			//n.putExtra("cycleMain",b);
			getActivity().startActivity(n);
			getActivity().finish();
		}
	}
	//--------------------------------------------------------------------------------
	//-------------------------------CONVERSTION OF QUANTIFIERS----------------------------
	//--------------------------------------------------------------------------------
	
	//Popup for selecting type --------------------------------------
	private void popArr(ArrayList<String> arr) {
		String qtfr=p.getQuantifier();
		//better to use the quantifier of how it was bought
		if(qtfr.equals(DHelper.qtf_fertilizer_g) || qtfr.equals(DHelper.qtf_fertilizer_kg) 
	   || qtfr.equals(DHelper.qtf_fertilizer_lb) || qtfr.equals(DHelper.qtf_fertilizer_bag)){
			arr.add(DHelper.qtf_fertilizer_g);
			arr.add(DHelper.qtf_fertilizer_lb);
			arr.add(DHelper.qtf_fertilizer_kg);
			arr.add(DHelper.qtf_fertilizer_bag);
		}else if(qtfr.equals(DHelper.qtf_chemical_L) || qtfr.equals(DHelper.qtf_chemical_ml) 
				|| qtfr.equals(DHelper.qtf_chemical_oz)){
			arr.add(DHelper.qtf_chemical_ml);
			arr.add(DHelper.qtf_chemical_L);
			arr.add(DHelper.qtf_chemical_oz);
		}
	}
	private PopupWindow curr=null;
	private void showPopup(final Activity context,int flag,ArrayList<String> items){
		int pWidth=600;
		int pHeight=550;
		//gets layout inflator for the activity
		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		//using the layout inflator u js got, inflate a layout into a view
		View simpList = inflater.inflate(R.layout.simple_plist, null);
		
		populateListView(simpList,items);
		registerListClick(simpList,flag);
		// Creating the PopupWindow
		   final PopupWindow popup = new PopupWindow(context);
		   curr=popup;
		   popup.setContentView(simpList);
		   popup.setWidth(pWidth);
		   popup.setHeight(pHeight);
		   popup.setFocusable(true);
		   // Displaying the popup at the specified location, + offsets.
		   popup.showAtLocation(simpList, Gravity.CENTER_HORIZONTAL,0, 0);
	}
	private void populateListView(View simpList,ArrayList<String> items) {
		//build adaptor
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(
				getActivity().getBaseContext(),				//context for activity
				android.R.layout.simple_list_item_1,	//layout to use(Create) this is what the options in the list look like
				items);			//items to be displayed
		//configure list view
		ListView expns=(ListView) simpList.findViewById(R.id.simpleListText);
		expns.setAdapter(adapter);
	}
	private void registerListClick(View simpList,final int flag) {
		ListView expns=(ListView) simpList.findViewById(R.id.simpleListText);
		final AdapterView.OnItemClickListener itemClick= new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> a,View viewClicked,int position,long id){
				TextView tv=(TextView)viewClicked;
				Toast.makeText(getActivity().getBaseContext(),tv.getText().toString()+" clicked ", Toast.LENGTH_SHORT).show();
				double converter=convertFromTo(btn_typeUse.getText().toString(),tv.getText().toString());
				amtRem=Math.round(amtRem*converter*100.0)/100.0;
				amtPur=Math.round(amtPur*converter*100.0)/100.0;
				quantifier=tv.getText().toString();
				label();
				btn_typeUse.setText(quantifier);
				curr.dismiss();
			}
		};
		expns.setOnItemClickListener(itemClick);
	}
	//returns the value you have to multiply to move 'FROM' -> 'TO'
	private double convertFromTo(String typePur,String typeUse){
		//one bag is used as 5 kg
		//general case converting to itself
		if(typePur.equals(typeUse))
			return 1;
		if(typePur.equals(DHelper.qtf_fertilizer_kg)){// kg to ...
			if(typeUse.equals(DHelper.qtf_fertilizer_lb)){
				return 2.20462;
			}
			if(typeUse.equals(DHelper.qtf_fertilizer_g)){
				return 1000;
			}
			if(typeUse.equals(DHelper.qtf_fertilizer_bag)){
				return .2;
			}
		}else if(typePur.equals(DHelper.qtf_fertilizer_lb)){// lb to ....
			if(typeUse.equals(DHelper.qtf_fertilizer_kg)){
				return 0.453592;
			}
			if(typeUse.equals(DHelper.qtf_fertilizer_g)){
				return 453.592;
			}
			if(typeUse.equals(DHelper.qtf_fertilizer_bag)){
				return (0.453592*.2);
			}
		}else if(typePur.equals(DHelper.qtf_fertilizer_g)){// g to ....
			if(typeUse.equals(DHelper.qtf_fertilizer_kg)){
				return .001;
			}
			if(typeUse.equals(DHelper.qtf_fertilizer_lb)){
				return 0.00220462;
			}
			if(typeUse.equals(DHelper.qtf_fertilizer_bag)){
				return (0.001*.2);
			}
		}else if(typePur.equals(DHelper.qtf_fertilizer_bag)){// bag to ....
			if(typeUse.equals(DHelper.qtf_fertilizer_kg)){
				return 5;
			}
			if(typeUse.equals(DHelper.qtf_fertilizer_lb)){
				return (5*2.20462);
			}
			if(typeUse.equals(DHelper.qtf_fertilizer_g)){
				return (5*1000);
			}
		}else if(typePur.equals(DHelper.qtf_soilAmendment_truck)){
			
		}else if(typePur.equals(DHelper.qtf_chemical_L)){//litre to ...
			if(typeUse.equals(DHelper.qtf_chemical_ml)){
				return (1000);
			}
			if(typeUse.equals(DHelper.qtf_chemical_oz)){
				return (35.1951);
			}
		}else if(typePur.equals(DHelper.qtf_chemical_oz)){// oz to ...
			if(typeUse.equals(DHelper.qtf_chemical_ml)){
				return (28.4131);
			}
			if(typeUse.equals(DHelper.qtf_chemical_L)){
				return (0.0284131);
			}
		}else if(typePur.equals(DHelper.qtf_chemical_ml)){// ml to ....
			if(typeUse.equals(DHelper.qtf_chemical_oz)){
				return (0.0351951);
			}
			if(typeUse.equals(DHelper.qtf_chemical_L)){
				return (0.001);
			}
		}
		return -1;
	}

}

