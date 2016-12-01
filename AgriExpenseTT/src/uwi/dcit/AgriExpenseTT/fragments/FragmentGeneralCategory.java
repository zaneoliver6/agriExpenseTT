package uwi.dcit.AgriExpenseTT.fragments;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.SalesCost;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;
import uwi.dcit.AgriExpenseTT.models.LocalCycleUse;

public class FragmentGeneralCategory extends Fragment {
	private final String TAG = "FragmentGeneralCategory";
	private SQLiteDatabase db;
	private DbHelper dbh;
	private double pm=0,fer=0,soilam=0,chem=0,labr=0,other=0;//totals
	private View view;
	private LocalCycle currCycle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_general_cat, container, false);
		// Load crop cycle from Bundle
		currCycle = getArguments().getParcelable("cycle");
		// Retrieve Database Resources
		dbh = new DbHelper(getActivity());
		db = dbh.getReadableDatabase();


		calcTotals();
		setup();
//        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("General Category Fragment");
		return view;
	}
	
	private void setup() {
		TextView totalLbl = (TextView) view.findViewById(R.id.tv_catTotal_lbl);
		TextView cat_pm = (TextView) view.findViewById(R.id.tv_catTotal_pm);
		TextView cat_fer = (TextView) view.findViewById(R.id.tv_catTotal_fertilizer);
		TextView cat_soil_amend = (TextView) view.findViewById(R.id.tv_catTotal_soilam);
		TextView cat_chem = (TextView) view.findViewById(R.id.tv_catTotal_chemical);
		TextView cat_labour = (TextView) view.findViewById(R.id.tv_catTotal_labour);
		TextView cat_other = (TextView) view.findViewById(R.id.tv_catTotal_other);

		Button btn_calc=(Button)view.findViewById(R.id.btn_general_calculate);
		btn_calc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btn_general_calculate) {
                    Intent n = new Intent(getActivity(), SalesCost.class);
                    n.putExtra("cycle", currCycle);
                    getActivity().startActivity(n);
                }
            }
        });

		Log.i(TAG, "Loading Data for: " + currCycle);

		//TODO Extract String prefixes
        totalLbl.setText(String.format("Total Expenses For %s", currCycle.getCycleName()));
		DecimalFormat df = new DecimalFormat("#.00"); 
		cat_pm.setText(String.format("Planting Material: $%s", Double.valueOf(df.format(pm))));
		cat_fer.setText(String.format("Fertilizer: $%s", Double.valueOf(df.format(fer))));
		cat_soil_amend.setText(String.format("Soil Amendment: $%s", Double.valueOf(df.format(soilam))));
		cat_chem.setText(String.format("Chemical: $%s", Double.valueOf(df.format(chem))));
		cat_labour.setText(String.format("Labour: $%s", Double.valueOf(df.format(labr))));
		cat_other.setText(String.format("%s%s", getString(R.string.cycle_other_prefix), Double.valueOf(df.format(other))));

		TextView sum     = (TextView)view.findViewById(R.id.tv_catTotal_sum);
        TextView harvest = (TextView)view.findViewById(R.id.tv_catTotal_harvest);

		sum.setText(String.format("%s%s", getString(R.string.cycle_prefix), Double.valueOf(df.format(currCycle.getTotalSpent()))));
		harvest.setText("Harvested: " + currCycle.getHarvestAmt()+" "+currCycle.getHarvestType());

		TextView statement1 = (TextView) view.findViewById(R.id.tv_catTotal_sales);
		statement1.setText("Sales: $" + Double.valueOf(df.format(currCycle.getCostPer()))+" "+currCycle.getHarvestType()+" = "+(currCycle.getCostPer()*currCycle.getHarvestAmt()));
	}

    // TODO This operation is potentially intensive as data size grows. The process of calculating and assigning the value to the view should be done in its individual threads

    private void calcTotals(){
		Log.d(TAG, "Calculating Totals");
		long start = GregorianCalendar.getInstance().getTimeInMillis();

		ArrayList<LocalCycleUse> list = new ArrayList<>();
		DbQuery.getCycleUse(db, dbh, currCycle.getId(), list, DHelper.cat_plantingMaterial);
		pm=getTotal(list);
		
		list=new ArrayList<>();
		DbQuery.getCycleUse(db, dbh, currCycle.getId(), list, DHelper.cat_fertilizer);
		fer=getTotal(list);
		
		list=new ArrayList<>();
		DbQuery.getCycleUse(db, dbh, currCycle.getId(), list, DHelper.cat_soilAmendment);
		soilam=getTotal(list);
		
		list=new ArrayList<>();
		DbQuery.getCycleUse(db, dbh, currCycle.getId(), list, DHelper.cat_chemical);
		chem=getTotal(list);
		
		list=new ArrayList<>();
		DbQuery.getCycleUse(db, dbh, currCycle.getId(), list, DHelper.cat_labour);
		labr=getTotal(list);

		list=new ArrayList<>();
		DbQuery.getCycleUse(db, dbh, currCycle.getId(), list, DHelper.cat_other);
		other=getTotal(list);

		long timeDiff = GregorianCalendar.getInstance().getTimeInMillis() - start;
		Log.d(TAG, "Took " + (timeDiff/1000) + " seconds to retrieve data and calculate totals");
		GAnalyticsHelper.getInstance(getActivity()).sendPerfMetrics("View Cycles", "Calculate Totals", timeDiff);
	}

	private double getTotal(ArrayList<LocalCycleUse> list) {
		double total=0;
		for (LocalCycleUse lc : list){
			total += lc.getUseCost();
		}
		return total;
	}

	
}
