package fragments;

import helper.DHelper;
import helper.DbHelper;
import helper.DbQuery;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.SalesCost;
import android.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import dataObjects.localCycle;
import dataObjects.localCycleUse;

public class FragmentGeneralCategory extends Fragment{
	TextView totalLbl;
	TextView cat_pm;//planting material
	TextView cat_fer;//fertilizer
	TextView cat_soilam;//soil amendment
	TextView cat_chem;//chemical
	TextView cat_labr;//labour
	TextView cat_other;
	TextView statement1;
	SQLiteDatabase db;
	DbHelper dbh;
	
	double pm=0,fer=0,soilam=0,chem=0,labr=0,other=0;//totals
	View view;
	
	localCycle currCycle;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.fragment_general_cat, container, false);
		dbh=new DbHelper(getActivity());
		db=dbh.getReadableDatabase();
		calcTotals();
		setup();
		return view;
	}
	
	private void setup() {
		//totalLbl=(TextView)view.findViewById(R.id.tv_catTotal_lbl);
		cat_pm=(TextView)view.findViewById(R.id.tv_catTotal_pm);
		cat_fer=(TextView)view.findViewById(R.id.tv_catTotal_fertilizer);
		cat_soilam=(TextView)view.findViewById(R.id.tv_catTotal_soilam);
		cat_chem=(TextView)view.findViewById(R.id.tv_catTotal_chemical);
		cat_labr=(TextView)view.findViewById(R.id.tv_catTotal_labour);
		cat_other=(TextView)view.findViewById(R.id.tv_catTotal_other);;
		Button btn_calc=(Button)view.findViewById(R.id.btn_general_calculate);
		Click c=new Click();
		btn_calc.setOnClickListener(c);
		DecimalFormat df = new DecimalFormat("#.00"); 
		cat_pm.setText("Planting Material:$"+Double.valueOf(df.format(pm)));
		cat_fer.setText("Fertilizer:$"+Double.valueOf(df.format(fer)));
		cat_soilam.setText("Soil Amendment:$"+Double.valueOf(df.format(soilam)));
		cat_chem.setText("Chemical:$"+Double.valueOf(df.format(chem)));
		cat_labr.setText("Labour:$"+Double.valueOf(df.format(labr)));
		cat_other.setText("Other:$"+Double.valueOf(df.format(other)));
		
		TextView sum=(TextView)view.findViewById(R.id.tv_catTotal_sum);
		sum.setText("Total:$"+Double.valueOf(df.format(currCycle.getTotalSpent())));
		TextView harvest=(TextView)view.findViewById(R.id.tv_catTotal_harvest);
		harvest.setText("Harvested:"+currCycle.getHarvestAmt()+" "+currCycle.getHarvestType());
		statement1=(TextView)view.findViewById(R.id.tv_catTotal_harvest1);
		statement1.setText("Sales:$"+Double.valueOf(df.format(currCycle.getCostPer()))+" "
				+currCycle.getHarvestType()+" = "+(currCycle.getCostPer()*currCycle.getHarvestAmt()));
	}
	public class Click implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_general_calculate){
				IntentLauncher i=new IntentLauncher();
				i.run();
			}
		}	
	}
	private class IntentLauncher extends Thread{
		@Override
		public void run(){
			//Bundle b=new Bundle();
			//b.putParcelable("cycle",currCycle);
			Intent n=new Intent(getActivity(),SalesCost.class);
			n.putExtra("cycle", currCycle);
			getActivity().startActivity(n);
			getActivity().finish();
		}
	}
	private void calcTotals(){
		currCycle=getArguments().getParcelable("cycle");
		ArrayList<localCycleUse> list=new ArrayList<localCycleUse>();
		DbQuery.getCycleUse(db, dbh, currCycle.getId(), list, DHelper.cat_plantingMaterial);
		pm=getTotal(list);
		
		list=new ArrayList<localCycleUse>();
		DbQuery.getCycleUse(db, dbh, currCycle.getId(), list, DHelper.cat_fertilizer);
		fer=getTotal(list);
		
		list=new ArrayList<localCycleUse>();
		DbQuery.getCycleUse(db, dbh, currCycle.getId(), list, DHelper.cat_soilAmendment);
		soilam=getTotal(list);
		
		list=new ArrayList<localCycleUse>();
		DbQuery.getCycleUse(db, dbh, currCycle.getId(), list, DHelper.cat_chemical);
		chem=getTotal(list);
		
		list=new ArrayList<localCycleUse>();
		DbQuery.getCycleUse(db, dbh, currCycle.getId(), list, DHelper.cat_labour);
		labr=getTotal(list);

		list=new ArrayList<localCycleUse>();
		DbQuery.getCycleUse(db, dbh, currCycle.getId(), list, DHelper.cat_other);
		other=getTotal(list);
	}

	private double getTotal(ArrayList<localCycleUse> list) {
		Iterator<localCycleUse> i=list.iterator();
		double total=0;
		while(i.hasNext()){
			total+=i.next().getUseCost();
		}
		return total;
	}

	
}
