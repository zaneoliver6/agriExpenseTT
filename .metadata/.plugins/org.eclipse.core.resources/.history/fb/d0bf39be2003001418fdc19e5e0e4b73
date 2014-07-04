package com.example.agriexpensett;

import helper.DHelper;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;

public class NewCycleRedesigned extends ActionBarActivity {
	TextView sub_head;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_cycle_redesigned);
		setupInitialFrag();
	}
	public void appendSub(String extras){
		sub_head=(TextView)findViewById(R.id.tv_mainNew_subheader);
		String s=sub_head.getText().toString();
		s=s+" "+extras;
		sub_head.setText(s);
	}
	private void setupInitialFrag() {
		Bundle pass=new Bundle();
		pass.putString("type",DHelper.cat_plantingMaterial);
		FragmentManager fm=getFragmentManager();
		FragmentTransaction ft=fm.beginTransaction();
		ListFragment listfrag=new NewCycleLists();
		listfrag.setArguments(pass);
		ft.add(R.id.NewCycleListContainer,listfrag);
		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_cycle_redesigned, menu);
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

}
