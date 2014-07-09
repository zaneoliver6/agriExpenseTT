package fragments;

import helper.DataManager;
import helper.DbHelper;
import helper.DbQuery;

import java.text.DecimalFormat;

import com.example.agriexpensett.CycleUseageRedesign;
import com.example.agriexpensett.R;
import com.example.agriexpensett.UseResource;
import com.example.agriexpensett.localCycle;
import com.example.agriexpensett.R.id;
import com.example.agriexpensett.R.layout;
import com.example.agriexpensett.cycleendpoint.model.Cycle;
import com.example.agriexpensett.rpurchaseendpoint.model.RPurchase;

import android.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentPurchaseUse extends Fragment {
	View view;
	SQLiteDatabase db;
	DbHelper dbh;
	localCycle c=null;
	double useAmount=0;
	double calcost=0.0,TypeSpent=0.0;
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
	EditText et_amt;
	private void setDetails(int pId,int cycleId) {
		RPurchase p=DbQuery.getAPurchase(db, dbh,pId);
		c=getArguments().getParcelable("cycleMain");
		TextView h_main=(TextView)view.findViewById(R.id.tv_usePurchase_header1);
		TextView h_sub=(TextView)view.findViewById(R.id.tv_usePurchase_header2);
		TextView d_top=(TextView)view.findViewById(R.id.tv_usePurchase_top_det1);
		
		d_buttom1=(TextView)view.findViewById(R.id.tv_usePurchase_buttom_det1);
		d_buttom2=(TextView)view.findViewById(R.id.tv_usePurchase_buttom_det2);
		d_buttom3=(TextView)view.findViewById(R.id.tv_usePurchase_buttom_det3);
		et_amt=(EditText)view.findViewById(R.id.et_useAmt);
		
		//Setting Labels
		System.out.println("resId"+p.getResourceId());
		String res=DbQuery.findResourceName(db, dbh,p.getResourceId());
		h_main.setText("Currently");
		d_top.setText("Curently this "+res+" has "+p.getQuantifier()+" "+p.getQtyRemaining()+" remaining");
		h_sub.setText("Use");
		
		d_buttom1.setText("Using "+useAmount+" "+p.getQuantifier()+" adds $"+calcost+" to the current crop cycle");
		d_buttom2.setText("Total spent on "+p.getType()+" becomes $"+TypeSpent);
		d_buttom3.setText("The crop cycle's new total cost becomes $"+c.getTotalSpent());
	
		Button calc=(Button)view.findViewById(R.id.btn_UsePurchase_cal);
		Button dne=(Button)view.findViewById(R.id.btn_usePurchase_done);
		Click clic=new Click(p);
		calc.setOnClickListener(clic);
		dne.setOnClickListener(clic);
		
		
	}
	private class Click implements OnClickListener{
		RPurchase p;
		public Click(RPurchase p){
			this.p=p;
		}
		@Override
		public void onClick(View v) {
			if(et_amt.getText().toString().equals(null)||et_amt.getText().toString().equals("")){
				Toast.makeText(getActivity().getBaseContext(), "Enter Amount", Toast.LENGTH_SHORT).show();
				return;
			}
			DecimalFormat df = new DecimalFormat("#.00");    
			
			if(v.getId()==R.id.btn_UsePurchase_cal){
				
				calcost=Double.parseDouble(et_amt.getText().toString());
				useAmount=calcost;
				calcost=(calcost/p.getQty())*p.getCost();
				calcost=(Double.valueOf(df.format(calcost)));
				d_buttom1.setText("Using "+useAmount+" "+p.getQuantifier()+" adds $"+calcost+" to the current crop cycle");
				d_buttom2.setText("Total spent on "+p.getType()+" becomes $"+(TypeSpent+calcost));
				d_buttom3.setText("The crop cycle's new total cost becomes $"+(c.getTotalSpent()+calcost));
			}else if(v.getId()==R.id.btn_usePurchase_done){
				Toast.makeText(getActivity().getBaseContext(),"yea", Toast.LENGTH_SHORT).show();
				
				System.out.println("cycleId"+c.getId()+" purchaseId"+p.getPId());
				double qty=Double.parseDouble(et_amt.getText().toString());
				System.out.println("qty"+qty+" qty remaining"+p.getQtyRemaining());
				if(qty<=p.getQtyRemaining()){
					DataManager dm=new DataManager(getActivity().getBaseContext());
					dm.insertCycleUse(c.getId(), p.getPId(), qty, p.getType());
					dm.updatePurchase(p.getPId(),(p.getQtyRemaining()-qty));
					
					calcost=(qty/p.getQty())*p.getCost();
					calcost=(Double.valueOf(df.format(calcost)));
					c.setTotalSpent(Double.valueOf(c.getTotalSpent()+calcost));
					dm.updateCycleSpent(c.getId(), c.getTotalSpent());
					
					//getActivity().finish();
					IntentLauncher i=new IntentLauncher();
					i.start();
				}else{
					Toast.makeText(getActivity().getBaseContext(), "Not enough "+p.getQuantifier()+" remaining", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	private class IntentLauncher extends Thread{
		@Override
		public void run(){
			Bundle b=new Bundle();
			//b.putParcelable("cycleMain",c);
			System.out.println("i am here !!!");
			Intent n=new Intent(getActivity(),CycleUseageRedesign.class);
			n.putExtra("cycleMain", c);
			//n.putExtra("cycleMain",b);
			getActivity().startActivity(n);
			getActivity().finish();
		}
	}

	

}

