package com.example.agriexpensett;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;

public class MainMenu extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		setupButtons();
	}

	private void setupButtons() {
		Button btn_newCycle=(Button)findViewById(R.id.newCycle);
		Button btn_purchase=(Button)findViewById(R.id.NewPurchase);
		
		Button btn_CycleDet=(Button)findViewById(R.id.ResDetail);
		Button btn_SignIn=(Button)findViewById(R.id.btn_SignIn);
		Button btn_HireLabour=(Button)findViewById(R.id.HireLabour);
		click c=new click();
		btn_newCycle.setOnClickListener(c);
		btn_purchase.setOnClickListener(c);
		
		btn_CycleDet.setOnClickListener(c);
		btn_SignIn.setOnClickListener(c);
		btn_HireLabour.setOnClickListener(c);
		
		Button btn_manageD=(Button)findViewById(R.id.manageData);
		btn_manageD.setOnClickListener(c);
	}
	public class click implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent nextActivity = null;
			if(v.getId()==R.id.newCycle){
				System.out.println("Select new Cycle");
				nextActivity=new Intent(MainMenu.this,NewCycleRedesigned.class);
			}else if(v.getId()==R.id.NewPurchase){
				System.out.println("New Purchase");
				nextActivity=new Intent(MainMenu.this,NewPurchaseRedesign.class);
			}else if(v.getId()==R.id.ResDetail){
				nextActivity=new Intent(MainMenu.this,ViewNavigation.class);
			}else if(v.getId()==R.id.btn_SignIn){
				SignIn s=new SignIn(MainMenu.this);
				s.signIn();
				System.out.println("sigh");
				return;
			}else if(v.getId()==R.id.HireLabour){
				nextActivity=new Intent(MainMenu.this,HireLabour.class);
			}else if(v.getId()==R.id.manageData){
				nextActivity=new Intent(MainMenu.this,ManageData.class);
			}
			startActivity(nextActivity);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
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
	

}
