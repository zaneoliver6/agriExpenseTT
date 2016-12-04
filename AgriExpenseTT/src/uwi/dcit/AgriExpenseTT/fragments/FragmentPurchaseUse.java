package uwi.dcit.AgriExpenseTT.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.models.CycleContract;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;
import uwi.dcit.AgriExpenseTT.models.ResourcePurchaseContract;
import uwi.dcit.agriexpensesvr.resourcePurchaseApi.model.ResourcePurchase;
public class FragmentPurchaseUse extends Fragment {
	private View view;
	private SQLiteDatabase db;
	private DbHelper dbh;
	private LocalCycle c = null;
	private ResourcePurchase p;
	private Context context;
	
	private double useAmount= 0.0, //the amount you are going to use
	                calCost = 0.0,
                    typeSpent = 0.0,
	                amtRem  = 0.0,
                    amtPur  = 0.0;

	private String quantifier;
	
	private TextView section1;
	private TextView section2;
	private TextView section3;
	private TextView description;
	private EditText et_amt;
	
	private Button btn_typeUse;
    private TextView amt_hint;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		Log.i("HERE","Beginning");
		this.context = this.getContext();
		view=inflater.inflate(R.layout.activity_use_purchase_frag, container, false);

		dbh = new DbHelper(this.getActivity().getBaseContext());
		db = dbh.getWritableDatabase();

		int pId = Integer.parseInt(getArguments().getString("pId"));
		int cycleId = Integer.parseInt(getArguments().getString("cycleId"));
		String totalStr = getArguments().getString("total");
		typeSpent = (totalStr != null) ? Double.parseDouble(totalStr) : 0.0;
//		Cycle cyc = DbQuery.getCycle(db,dbh,cycleId);

		setDetails(pId,cycleId);

//        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("Purchase Use Fragment");
		return view;
	}
	
	private void setDetails(int pId,int cycleId) {
		Log.i("WOI","WOI");
		p = DbQuery.getARPurchase(db, dbh, pId);
		c = getArguments().getParcelable("cycleMain");
//		Cycle cyc = DbQuery.getCycle(db,dbh,cycleId);
//		c.setCropName(cyc.getCropName());
//		c.setCostPer(cyc.getCostPer());
//		c.setHarvestAmt(cyc.getHarvestAmt());
//		c.setHarvestType(cyc.getHarvestType());
//		c.setId(cyc.getId());
//		c.setLandQty(cyc.getLandQty());
//		c.setTime(cyc.getStartDate());
//		c.setTotalSpent(cyc.getTotalSpent());
//		c.setCropId(cyc.getCropId());
        Log.i("Fragment Purchase","CYCLE:"+c);
        Log.i("Fragment Purchase","PURCHASE:"+p);

		amtRem = p.getQtyRemaining();
		amtPur = p.getQty();
		quantifier = p.getQuantifier();

		btn_typeUse = (Button)view.findViewById(R.id.btn_UsePurchase_useType);
		btn_typeUse.setText(quantifier);


		description = (TextView)view.findViewById(R.id.tv_usePurchase_top_det1);

		section1 = (TextView)view.findViewById(R.id.tv_usageDetails_sec1);
		section2 = (TextView)view.findViewById(R.id.tv_usageDetails_sec2);
		section3 = (TextView)view.findViewById(R.id.tv_usageDetails_sec3);

		et_amt      = (EditText)view.findViewById(R.id.et_useAmt);
		amt_hint    = (TextView)view.findViewById((R.id.tv_amnt_hint));
		amt_hint.setText("Enter the amount of " + quantifier + "s used");

		Button calc=(Button)view.findViewById(R.id.btn_UsePurchase_cal);
		Button dne=(Button)view.findViewById(R.id.btn_usePurchase_done);
		label();

		Click click = new Click();
		calc.setOnClickListener(click);
		dne.setOnClickListener(click);
		btn_typeUse.setOnClickListener(click);
	}

	//sets labels to match data
	private void label(){
		DecimalFormat df = new DecimalFormat("#.00");
		String res=DbQuery.findResourceName(db, dbh,p.getResourceId());
		description.setText("" + res + " has " + quantifier + " " + amtRem + " remaining");

		section1.setText("Using " + useAmount + " " + quantifier + " adds $" + calCost + " to the current crop cycle");
		section2.setText("Total spent on " + p.getType() + " becomes $" + (typeSpent + calCost));
		section3.setText("The crop cycle's new total cost becomes $" + Double.valueOf(df.format((c.getTotalSpent() + calCost))));
	}

	private class Click implements OnClickListener{

		@Override
		public void onClick(View v) {
			Log.i(">>>><<<","Started Onclick!");
			if(v.getId() == R.id.btn_UsePurchase_useType){
				ArrayList<String> arr=new ArrayList<>();
				popArr(arr);
				showPopup(getActivity(), 0, arr);
				return;
			}

			if (et_amt.getText().toString().equals("")) {
				amt_hint.setText("Enter Amount Purchased");
				amt_hint.setTextColor(ContextCompat.getColor(context, R.color.helper_text_error));
				et_amt.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.helper_text_error), PorterDuff.Mode.SRC_ATOP);
			}else{
				useAmount=Double.parseDouble(et_amt.getText().toString());
				if(useAmount>amtRem){
                    amt_hint.setText("Not enough " + quantifier + " remaining");
					amt_hint.setTextColor(ContextCompat.getColor(context, R.color.helper_text_error));
					et_amt.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.helper_text_error), PorterDuff.Mode.SRC_ATOP);
					return;
				}
				et_amt.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.helper_text_color), PorterDuff.Mode.SRC_ATOP);
				amt_hint.setTextColor(ContextCompat.getColor(context, R.color.helper_text_color));

				calCost =(useAmount/amtPur)*p.getCost();
				calCost =Math.round(calCost *100.0)/100.0;

				if(v.getId()==R.id.btn_UsePurchase_cal){
					Toast.makeText(getActivity(), "Total Cost: "+ calCost, Toast.LENGTH_SHORT).show();
					label();//resets labels to match data
				}else if(v.getId()==R.id.btn_usePurchase_done){
					DataManager dm=new DataManager(getActivity().getBaseContext());
					dm.insertCycleUse(c.getId(), p.getPId(), useAmount, p.getType(),quantifier, calCost);
					double rem=(amtRem-useAmount)*convertFromTo(quantifier,p.getQuantifier());
					Toast.makeText(getActivity(), rem+" Remaining", Toast.LENGTH_SHORT).show();
					//updating purchase
					p.setQtyRemaining(rem);
					ContentValues cv=new ContentValues();
					cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING,p.getQtyRemaining());
					Log.i("NEW QUANTITY",">>>"+p.getQtyRemaining());
					dm.updatePurchase(p,cv);

					//updating cycle
					c.setTotalSpent(c.getTotalSpent()+ calCost);
					cv=new ContentValues();
					cv.put(CycleContract.CycleEntry.CROPCYCLE_TOTALSPENT, c.getTotalSpent());
					dm.updateCycle(c,cv);
					Log.i(getTag(), c.getTotalSpent()+" "+c.getId());
					DbQuery.updateAccount(db,System.currentTimeMillis()/1000);
					/*IntentLauncher i=new IntentLauncher();
					i.start();*/

                    //getActivity().recreate(); //TODO Find a way to implement this compatible with API9

                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    getActivity().startActivity(intent);
				}
			}
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
	@SuppressLint("InflateParams")
	private void showPopup(final Activity context,int flag,ArrayList<String> items){
		int pWidth=600;
		int pHeight=550;
		//gets layout inflator for the fragment
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
		ArrayAdapter<String> adapter = new ArrayAdapter<>(
				getActivity().getBaseContext(),				//context for fragment
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
		switch (typePur) {
			case DHelper.qtf_fertilizer_kg: // kg to ...
				if (typeUse.equals(DHelper.qtf_fertilizer_lb)) {
					return 2.20462;
				}
				if (typeUse.equals(DHelper.qtf_fertilizer_g)) {
					return 1000;
				}
				if (typeUse.equals(DHelper.qtf_fertilizer_bag)) {
					return .2;
				}
				break;
			case DHelper.qtf_fertilizer_lb: // lb to ....
				if (typeUse.equals(DHelper.qtf_fertilizer_kg)) {
					return 0.453592;
				}
				if (typeUse.equals(DHelper.qtf_fertilizer_g)) {
					return 453.592;
				}
				if (typeUse.equals(DHelper.qtf_fertilizer_bag)) {
					return (0.453592 * .2);
				}
				break;
			case DHelper.qtf_fertilizer_g: // g to ....
				if (typeUse.equals(DHelper.qtf_fertilizer_kg)) {
					return .001;
				}
				if (typeUse.equals(DHelper.qtf_fertilizer_lb)) {
					return 0.00220462;
				}
				if (typeUse.equals(DHelper.qtf_fertilizer_bag)) {
					return (0.001 * .2);
				}
				break;
			case DHelper.qtf_fertilizer_bag: // bag to ....
				if (typeUse.equals(DHelper.qtf_fertilizer_kg)) {
					return 5;
				}
				if (typeUse.equals(DHelper.qtf_fertilizer_lb)) {
					return (5 * 2.20462);
				}
				if (typeUse.equals(DHelper.qtf_fertilizer_g)) {
					return (5 * 1000);
				}
				break;
			case DHelper.qtf_soilAmendment_truck:
				Log.d("Fragment Purchase Use", "Soil Amendment");
				break;
			case DHelper.qtf_chemical_L: //litre to ...
				if (typeUse.equals(DHelper.qtf_chemical_ml)) {
					return (1000);
				}
				if (typeUse.equals(DHelper.qtf_chemical_oz)) {
					return (35.1951);
				}
				break;
			case DHelper.qtf_chemical_oz: // oz to ...
				if (typeUse.equals(DHelper.qtf_chemical_ml)) {
					return (28.4131);
				}
				if (typeUse.equals(DHelper.qtf_chemical_L)) {
					return (0.0284131);
				}
				break;
			case DHelper.qtf_chemical_ml: // ml to ....
				if (typeUse.equals(DHelper.qtf_chemical_oz)) {
					return (0.0351951);
				}
				if (typeUse.equals(DHelper.qtf_chemical_L)) {
					return (0.001);
				}
				break;
		}
		return -1;
	}
//
}

