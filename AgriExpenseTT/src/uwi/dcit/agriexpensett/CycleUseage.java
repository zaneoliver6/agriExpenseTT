package uwi.dcit.AgriExpenseTT;

import helper.DHelper;
import helper.DbHelper;
import helper.DbQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;







import com.example.agriexpensett.cycleendpoint.model.Cycle;
import com.example.agriexpensett.rpurchaseendpoint.model.RPurchase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import dataObjects.localCycle;
import dataObjects.localCycleUse;

public class CycleUseage extends ActionBarActivity {
	private TextView tv_plantMaterial_main;
	private TextView tv_plantMaterial_det1;
	private TextView tv_plantMaterial_det2;
	
	private TextView tv_fertilizer_main;
	private TextView tv_fertilizer_det1;
	private TextView tv_fertilizer_det2;
	
	private TextView tv_chemical_main;
	private TextView tv_chemical_det1;
	private TextView tv_chemical_det2;
	
	private TextView tv_soilAmendment_main;
	private TextView tv_soilAmendment_det1;
	private TextView tv_soilAmendment_det2;
	
	Double totalCrop,totalChemical,totalFertilizer,totalSoilAmendment;
	private DbHelper dbh;
	private SQLiteDatabase db;
	ArrayList<localCycleUse> cUseList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cycle_useage);
		dbh=new DbHelper(this);
		db=dbh.getReadableDatabase();
		//printUses();
		setup();
		
	}
	
	private void clicks(int id){
		Button btn_CropUse=(Button)findViewById(R.id.btn_Cycle_plantMaterial1);
		Button btn_UseCrop=(Button)findViewById(R.id.btn_Cycle_plantMaterial2);
		Button btn_Chemical=(Button)findViewById(R.id.btn_Cycle_chemical1);
		Button btn_UseChemical=(Button)findViewById(R.id.btn_Cycle_chemical2);
		Button btn_Fertilizer=(Button)findViewById(R.id.btn_Cycle_fertilizer1);
		Button btn_UseFertilizer=(Button)findViewById(R.id.btn_Cycle_fertilizer2);
		Button btn_SoilAmendment=(Button)findViewById(R.id.btn_Cycle_SoilAmendment1);
		Button btn_UseSoilAmendment=(Button)findViewById(R.id.btn_Cycle_SoilAmendment2);
		Click c=new Click(id);
		btn_CropUse.setOnClickListener(c);
		btn_Chemical.setOnClickListener(c);
		btn_Fertilizer.setOnClickListener(c);
		btn_SoilAmendment.setOnClickListener(c);
		btn_UseCrop.setOnClickListener(c);
		btn_UseChemical.setOnClickListener(c);
		btn_UseFertilizer.setOnClickListener(c);
		btn_UseSoilAmendment.setOnClickListener(c);
	}
	public class Click implements OnClickListener{
		private int cycleId;
		public Click(int id){
			cycleId=id;
		}
		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_Cycle_plantMaterial1){
				populateArrayList(cycleId, DHelper.cat_plantingMaterial);
				showPopupList(CycleUseage.this);
			}else if(v.getId()==R.id.btn_Cycle_chemical1){
				populateArrayList(cycleId, DHelper.cat_chemical);
				showPopupList(CycleUseage.this);
			}else if(v.getId()==R.id.btn_Cycle_fertilizer1){
				populateArrayList(cycleId, DHelper.cat_fertilizer);
				showPopupList(CycleUseage.this);
			}else if(v.getId()==R.id.btn_Cycle_SoilAmendment1){
				populateArrayList(cycleId, DHelper.cat_soilAmendment);
				showPopupList(CycleUseage.this);
			}else if(v.getId()==R.id.btn_Cycle_plantMaterial2){
				Cycle c=DbQuery.getCycle(db, dbh, cycleId);
				localCycle cyc=new localCycle();
				cyc.setCropId(c.getCropId());
				cyc.setId(c.getId());
				Bundle b=new Bundle();
				b.putParcelable("cyc",cyc);
				Intent n=new Intent(CycleUseage.this,UseResource.class);
				n.putExtra("cyc",b);
				n.putExtra("type",DHelper.cat_plantingMaterial);
				n.putExtra("total",""+totalCrop);
				startActivity(n);
			}else if(v.getId()==R.id.btn_Cycle_chemical2){
				Cycle c=DbQuery.getCycle(db, dbh, cycleId);
				localCycle cyc=new localCycle();
				cyc.setCropId(c.getCropId());
				cyc.setId(c.getId());
				Bundle b=new Bundle();
				b.putParcelable("cyc",cyc);
				Intent n=new Intent(CycleUseage.this,UseResource.class);
				n.putExtra("cyc",b);
				n.putExtra("type",DHelper.cat_chemical);
				n.putExtra("total",""+totalChemical);
				startActivity(n);
			}else if(v.getId()==R.id.btn_Cycle_fertilizer2){
				Cycle c=DbQuery.getCycle(db, dbh, cycleId);
				localCycle cyc=new localCycle();
				cyc.setCropId(c.getCropId());
				cyc.setId(c.getId());
				Bundle b=new Bundle();
				b.putParcelable("cyc",cyc);
				Intent n=new Intent(CycleUseage.this,UseResource.class);
				n.putExtra("cyc",b);
				n.putExtra("type",DHelper.cat_fertilizer);
				n.putExtra("total",""+totalFertilizer);
				startActivity(n);
			}else if(v.getId()==R.id.btn_Cycle_SoilAmendment2){
				Cycle c=DbQuery.getCycle(db, dbh, cycleId);
				localCycle cyc=new localCycle();
				cyc.setCropId(c.getCropId());
				cyc.setId(c.getId());
				Bundle b=new Bundle();
				b.putParcelable("cyc",cyc);
				Intent n=new Intent(CycleUseage.this,UseResource.class);
				n.putExtra("cyc",b);
				n.putExtra("type",DHelper.cat_soilAmendment);
				n.putExtra("total",""+totalSoilAmendment);
				startActivity(n);
			}
		}
		
	}
	private void setup() {
		//getting parecled data and converting the data
		Bundle data = getIntent().getExtras();
		localCycle curr = (localCycle) data.getParcelable("cycleMain");
		clicks(curr.getId());
		String plantMaterialName=DbQuery.findResourceName(db, dbh, curr.getCropId());
		Calendar calendar=Calendar.getInstance();//creates an instance of a local calendar
		calendar.setTimeInMillis(curr.getTime());//uses the time(milisecs) stored in the obj to set the time
				
		//getting text views
		tv_plantMaterial_main=(TextView)findViewById(R.id.tv_cycle_menuplantMaterial);
		tv_plantMaterial_det1=(TextView)findViewById(R.id.tv_cycle_mplantMaterial_det1);
		tv_plantMaterial_det2=(TextView)findViewById(R.id.tv_cycle_mplantMaterial_det2);
		
		tv_chemical_main=(TextView)findViewById(R.id.tv_cycle_menuChemical);
		tv_chemical_det1=(TextView)findViewById(R.id.tv_cycle_mChemical_det1);
		tv_chemical_det2=(TextView)findViewById(R.id.tv_cycle_mChemical_det2);
		
		tv_fertilizer_main=(TextView)findViewById(R.id.tv_cycle_menuFertilizer);
		tv_fertilizer_det1=(TextView)findViewById(R.id.tv_cycle_mFertilizer_det1);
		tv_fertilizer_det2=(TextView)findViewById(R.id.tv_cycle_mFertilizer_det2);
		
		tv_soilAmendment_main=(TextView)findViewById(R.id.tv_cycle_menuSoilAmendment);
		tv_soilAmendment_det1=(TextView)findViewById(R.id.tv_cycle_mSoilAmendment_det1);
		tv_soilAmendment_det2=(TextView)findViewById(R.id.tv_cycle_mSoilAmendment_det2);
		
		ImageView imageView=(ImageView)findViewById(R.id.icon_purchaseType_main);
		imageView.setImageResource(R.drawable.money_doller1);
		//TODO
		//getting aggregate and complex data 
		ArrayList<localCycleUse> useList=new ArrayList<localCycleUse>();
		//--------------------------------------------------------PLANT MATERIAL
		DbQuery.getCycleUse(db, dbh, curr.getId(), useList, DHelper.cat_plantingMaterial);//fills the list with plantMaterial usage for THIS cycle
		//DbQuery.getCycleUse(db, dbh, cycleid, list, type);
		Double plantMaterialTotal=0.0;
		ArrayList<String> cNames=null;
		double[] cTotals=null;
		if(useList.isEmpty()){
			System.out.println(curr.getId()+" :plantMaterial empty");
		}else{
			cNames=new ArrayList<String>();
			cTotals=new double[useList.size()];
			Iterator<localCycleUse> itr=useList.iterator();
			while(itr.hasNext()){
				localCycleUse lcu=itr.next();
				plantMaterialTotal+=lcu.getUseCost();//stores the total amount of money spent on plantMaterials
				
				RPurchase purchaseUse=DbQuery.getARPurchase(db, dbh,lcu.getPurchaseId());
				String name=DbQuery.findResourceName(db, dbh, purchaseUse.getResourceId());
				
				//calculates the total spent on each plantMaterial
				Iterator<String> i=cNames.iterator();//list of plantMaterial names' iterator
				int pos=0;//start position for totals corresponding to each name
				boolean found=false;
				while(i.hasNext()){//goes through the names of the plantMaterials
					if(name.equals(i.next())){
						cTotals[pos]+=lcu.getUseCost();
						found=true;
					}else{
						pos++;
					}
				}
				if(found==false){//if we didnt find the name in the list
					cNames.add(name);//add the name to the list
					cTotals[pos]=lcu.getUseCost();//set the corresponding cost
				}
			}
		}
		//----------------------------------------------------FERTILIZER
		useList=new ArrayList<localCycleUse>();
		DbQuery.getCycleUse(db, dbh, curr.getId(), useList, DHelper.cat_fertilizer);//fills the list with plantMaterial usage for THIS cycle
		//DbQuery.getCycleUse(db, dbh, cycleid, list, type);
		Double fertilizerTotal=0.0;
		ArrayList<String> fNames=null;
		double[] fTotals=null;
		if(useList.isEmpty()){
			System.out.println(curr.getId()+" :fertilizer empty");
		}else{
			fNames=new ArrayList<String>();
			fTotals=new double[useList.size()];
			Iterator<localCycleUse> itr=useList.iterator();
			while(itr.hasNext()){
				localCycleUse lcu=itr.next();
				fertilizerTotal+=lcu.getUseCost();//stores the total amount of money spent on fertilizers
				
				RPurchase purchaseUse=DbQuery.getARPurchase(db, dbh,lcu.getPurchaseId());
				String name=DbQuery.findResourceName(db, dbh, purchaseUse.getResourceId());
				
				//calculates the total spent on each fertilizer
				Iterator<String> i=fNames.iterator();//list of fertilizer names' iterator
				int pos=0;//start position for totals corresponding to each name
				boolean found=false;
				while(i.hasNext()){//goes through the names of the fertilizers
					if(name.equals(i.next())){
						fTotals[pos]+=lcu.getUseCost();
						found=true;
					}else{
						pos++;
					}
				}
				if(found==false){//if we didnt find the name in the list
					fNames.add(name);//add the name to the list
					fTotals[pos]=lcu.getUseCost();//set the corresponding cost
				}
			}
		}
		
		//------------------------------------------------CHEMICAL
		useList=new ArrayList<localCycleUse>();
		DbQuery.getCycleUse(db, dbh, curr.getId(), useList, DHelper.cat_chemical);//fills the list with plantMaterial usage for THIS cycle
		//DbQuery.getCycleUse(db, dbh, cycleid, list, type);
		Double chemicalTotal=0.0;
		ArrayList<String> chNames=null;
		double[] chTotals=null;
		if(useList.isEmpty()){
			System.out.println(curr.getId()+" :chemical empty");
		}else{
			chNames=new ArrayList<String>();
			chTotals=new double[useList.size()];
			Iterator<localCycleUse> itr=useList.iterator();
			while(itr.hasNext()){
				localCycleUse lcu=itr.next();
				chemicalTotal+=lcu.getUseCost();//stores the total amount of money spent on fertilizers
				
				RPurchase purchaseUse=DbQuery.getARPurchase(db, dbh,lcu.getPurchaseId());
				String name=DbQuery.findResourceName(db, dbh, purchaseUse.getResourceId());
				
				//calculates the total spent on each fertilizer
				Iterator<String> i=chNames.iterator();//list of fertilizer names' iterator
				int pos=0;//start position for totals corresponding to each name
				boolean found=false;
				while(i.hasNext()){//goes through the names of the fertilizers
					if(name.equals(i.next())){
						chTotals[pos]+=lcu.getUseCost();
						found=true;
					}else{
						pos++;
					}
				}
				if(found==false){//if we didnt find the name in the list
					chNames.add(name);//add the name to the list
					chTotals[pos]=lcu.getUseCost();//set the corresponding cost
				}
			}
		}
		//----------------------------SOIL AMENDMENT
		useList=new ArrayList<localCycleUse>();
		DbQuery.getCycleUse(db, dbh, curr.getId(), useList, DHelper.cat_soilAmendment);//fills the list with soil amendment usage for THIS cycle
		//DbQuery.getCycleUse(db, dbh, cycleid, list, type);
		Double soilAmendmentTotal=0.0;
		ArrayList<String> saNames=null;
		double[] saTotals=null;
		if(useList.isEmpty()){
			System.out.println(curr.getId()+" :soilAmendments empty");
		}else{
			saNames=new ArrayList<String>();
			saTotals=new double[useList.size()];
			Iterator<localCycleUse> itr=useList.iterator();
			while(itr.hasNext()){
				localCycleUse lcu=itr.next();
				soilAmendmentTotal+=lcu.getUseCost();//stores the total amount of money spent on fertilizers
				
				RPurchase purchaseUse=DbQuery.getARPurchase(db, dbh,lcu.getPurchaseId());
				String name=DbQuery.findResourceName(db, dbh, purchaseUse.getResourceId());
				
				//calculates the total spent on each soil amendment
				Iterator<String> i=saNames.iterator();//list of soil Amdendment names' iterator
				int pos=0;//start position for totals corresponding to each name
				boolean found=false;
				while(i.hasNext()){//goes through the names of the fertilizers
					if(name.equals(i.next())){
						saTotals[pos]+=lcu.getUseCost();
						found=true;
					}else{
						pos++;
					}
				}
				if(found==false){//if we didnt find the name in the list
					saNames.add(name);//add the name to the list
					saTotals[pos]=lcu.getUseCost();//set the corresponding cost
				}
			}
		}
		
		//----------------------------LABOUR
		/*useList=new ArrayList<localCycleUse>();
		DbQuery.getCycleUse(db, dbh, curr.getId(), useList, DHelper.cat_soilAmendment);//fills the list with soil amendment usage for THIS cycle
		//DbQuery.getCycleUse(db, dbh, cycleid, list, type);
		Double labourTotal=0.0;
		ArrayList<String> lNames=null;
		double[] lTotals=null;
		if(useList.isEmpty()){
			System.out.println(curr.getId()+" :labour empty");
		}else{
			lNames=new ArrayList<String>();
			lTotals=new double[useList.size()];
			Iterator<localCycleUse> itr=useList.iterator();
			while(itr.hasNext()){
				localCycleUse lcu=itr.next();
				soilAmendmentTotal+=lcu.getUseCost();//stores the total amount of money spent on fertilizers
						
				RPurchase purchaseUse=DbQuery.getAPurchase(db, dbh,lcu.getPurchaseId());
				String name=DbQuery.findResourceName(db, dbh, purchaseUse.getResourceId());
						
				//calculates the total spent on each soil amendment
				Iterator<String> i=lNames.iterator();//list of soil Amdendment names' iterator
				int pos=0;//start position for totals corresponding to each name
				boolean found=false;
				while(i.hasNext()){//goes through the names of the fertilizers
					if(name.equals(i.next())){
						lTotals[pos]+=lcu.getUseCost();
						found=true;
					}else{
						pos++;
					}
				}
				if(found==false){//if we didnt find the name in the list
					lNames.add(name);//add the name to the list
					lTotals[pos]=lcu.getUseCost();//set the corresponding cost
				}
			}
		}
		
		totalCrop=plantMaterialTotal;totalChemical=chemicalTotal;
		totalFertilizer=fertilizerTotal;totalSoilAmendment=soilAmendmentTotal;*/
		//setting up textviews
		
		//---------CROP
		tv_plantMaterial_main.setText("Planting Material");
		tv_plantMaterial_det1.setText("$"+plantMaterialTotal+" has been spent on "+plantMaterialName+" for this cycle so far");
		if(cNames!=null){
			int x=0,maxPos=0;
			Iterator<String> namesItr=cNames.iterator();
			while(namesItr.hasNext()){
				if(cTotals[x]>cTotals[maxPos]){
					maxPos=x;
				}
				System.out.println("plantMaterials "+namesItr.next());
				x++;
			}
			tv_plantMaterial_det2.setText("The most amount of money was spent on "+cNames.get(maxPos)+" which costed $"+cTotals[maxPos]);
		}else{
			tv_plantMaterial_det2.setText("No main expense yet");
		}
		//---------CHEMICAL
		tv_chemical_main.setText("Chemicals");
		tv_chemical_det1.setText("$"+chemicalTotal+" has been spent on chemicals for this cycle so far");
		if(chNames!=null){
			int x=0,maxPos=0;
			Iterator<String> namesItr=chNames.iterator();
			while(namesItr.hasNext()){
				if(chTotals[x]>chTotals[maxPos]){
					maxPos=x;
				}
				System.out.println("chemicals "+namesItr.next());
				x++;
			}
			tv_chemical_det2.setText("The most amount of money was spent on "+chNames.get(maxPos)+" which costed $"+chTotals[maxPos]);
		}else{
			tv_chemical_det2.setText("No main expense yet");
		}
		//---------FERILIZER
		tv_fertilizer_main.setText("Fertilizer");
		tv_fertilizer_det1.setText("$"+fertilizerTotal+" has been spent on fertilizers for this cycle so far");
		if(fNames!=null){
			int x=0,maxPos=0;
			Iterator<String> namesItr=fNames.iterator();
			while(namesItr.hasNext()){
				if(fTotals[x]>fTotals[maxPos]){
					maxPos=x;
				}
				System.out.println("fertilizers "+namesItr.next());
				x++;
			}
			tv_fertilizer_det2.setText("The most amount of money was spent on "+fNames.get(maxPos)+" which costed $"+fTotals[maxPos]);
		}else{
			tv_fertilizer_det2.setText("No main expense yet");
		}
		
		//---------SOIL AMENDMENT
		tv_soilAmendment_main.setText("Soil Amendment");
		tv_soilAmendment_det1.setText("$"+soilAmendmentTotal+" has been spent on soil amendments for this cycle so far");
		if(saNames!=null){
			int x=0,maxPos=0;
			Iterator<String> namesItr=saNames.iterator();
			while(namesItr.hasNext()){
				if(saTotals[x]>saTotals[maxPos]){
					maxPos=x;
				}
					System.out.println("soil Amendment "+namesItr.next());
					x++;
			}
			tv_soilAmendment_det2.setText("The most amount of money was spent on "+saNames.get(maxPos)+" which costed $"+saTotals[maxPos]);
		}else{
			tv_soilAmendment_det2.setText("No main expense yet");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cycle_useage, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	


//----------------------------------------------------LIST VIEW
	public void showPopupList(final Activity context){
		int pWidth=600;
		int pHeight=550;
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		View simpList = inflater.inflate(R.layout.simple_plist, null);
		populateList(simpList);
		
		//registerListClick(simpList,flag);
		
		// Creating the PopupWindow
		   final PopupWindow popup = new PopupWindow(context);
		   //curr=popup;
		   popup.setContentView(simpList);
		   popup.setWidth(pWidth);
		   popup.setHeight(pHeight);
		   popup.setFocusable(true);
		   // Displaying the popup at the specified location, + offsets.
		   if(cUseList.isEmpty()){
			   Toast.makeText(context,"This cycle hasn't used any of this", Toast.LENGTH_SHORT).show();
		   }else{
			   popup.showAtLocation(simpList, Gravity.CENTER_HORIZONTAL,0, 0);
		   }
	}
	
	private void populateArrayList(int cycleid,String type) {
		cUseList=new ArrayList<localCycleUse>();
		DbQuery.getCycleUse(db, dbh, cycleid, cUseList, type);
	}
	
	@SuppressWarnings("unchecked")
	private void populateList(View v) {
		ArrayAdapter<localCycle> cycleAdptr=new cycleAdapter();
		ListView list=(ListView)v.findViewById(R.id.simpleListText);
		list.setAdapter(cycleAdptr);
	}
	@SuppressWarnings("rawtypes")
	public class cycleAdapter extends ArrayAdapter{
	
		@SuppressWarnings("unchecked")
		public cycleAdapter() {
			super(CycleUseage.this,R.layout.cycle_list_item,cUseList);
			// TODO Auto-generated constructor stub
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView=convertView;
			if(itemView==null){//ensures view is not null
				itemView=getLayoutInflater().inflate(R.layout.cycle_list_item,parent,false);
			}
			//find the expense to work with
			localCycleUse currUse=cUseList.get(position);
			
			//fill the layout's views with the relevant information
			
			
			//make:
			TextView Crop=(TextView)itemView.findViewById(R.id.tv_cycleList_crop);
			int id=currUse.getPurchaseId();
			RPurchase p=DbQuery.getARPurchase(db, dbh, id);
			String txt=DbQuery.findResourceName(db, dbh, p.getResourceId());
			Crop.setText(txt);
			
			TextView Land=(TextView)itemView.findViewById(R.id.tv_cycleList_Land);
			double qty=currUse.getAmount();
			double cost=currUse.getUseCost();
			txt="USED FROM:"+p.getQty()+" "+p.getQuantifier()+"s $"+p.getCost()+"\n"
			+"USED ON CYCLE:"+qty+" "+p.getQuantifier()+"s \n"
			+"COST OF USE:$"+cost;
			Land.setText(txt);
			
			/*TextView DateR=(TextView)itemView.findViewById(R.id.tv_cycleList_date);
			TextView DayL=(TextView)itemView.findViewById(R.id.tv_cycleList_day);
			Long dateMils=currCycle.getTime();
			Calendar calender=Calendar.getInstance();
			calender.setTimeInMillis(dateMils);
			
			cid=calender.get(Calendar.DAY_OF_WEEK);
			String[] days={"Sun","Mon","Tue","Wed","Thur","Fri","Sat"};
			
			if(cid==7){
				DayL.setText(days[6]);
			}else{
				DayL.setText(days[cid]);
			}
			Date d=calender.getTime();
			DateR.setText(d.toLocaleString());
			//ImageView imageView=(ImageView)itemView.findViewById(R.id.cat_Icon);
			int i=0;
			return itemView;
			*/
			return itemView;
		}
	}
	private void registerClick() {
		ListView list=(ListView)findViewById(R.id.listView_cycles);
		AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener(){
	
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				//Intent nextActivity=new Intent(ViewCycles.this,CycleUseage.class);
				//nextActivity.putExtra("cycleMain", cycleList.get(position));
				//startActivity(nextActivity);
				//Toast.makeText(Expense_day.this,dExpenses.get(position).getCategory(), Toast.LENGTH_SHORT).show();				
			}
		};
		list.setOnItemClickListener(itemListener);
		
	}
}
