package com.example.agriexpensett;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.example.agriexpensett.translogendpoint.Translogendpoint;
import com.example.agriexpensett.translogendpoint.model.TransLog;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class NewCycle extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new__cycle);
		test();
		
		
	}

	private void test() {
		
		System.out.println();
		//NamespaceManager 
		/*
		ArrayList<localCycle> list=new ArrayList<localCycle>();
		DbQuery.getCycles(db, dbh, list);
		Iterator<localCycle> i=list.iterator();
		localCycle curr;
		while(i.hasNext()){
			curr=i.next();
			System.out.println(curr.toString());
		}
		
		ArrayList<String> crops=new ArrayList<String>();
		DbQuery.getResources(db, dbh,"crop", crops);
		Iterator<String> ci=crops.iterator();
		while(ci.hasNext()){
			String k=ci.next();
			System.out.println(k);
		}
		System.out.println("yaw");
		
		ArrayList<localResourcePurchase> purchases=new ArrayList<localResourcePurchase>();
		DbQuery.getResourcePurchases(db, dbh, purchases,null, null);
		Iterator<localResourcePurchase> pi=purchases.iterator();
		while(pi.hasNext()){
			System.out.println("-------");
			localResourcePurchase m=pi.next();
			System.out.println(m.toString());
		}*/
		
		DataManager dm=new DataManager(this);
		/*dm.insertCycle(3,"beds", 1);
		dm.insertCycleUse( 2, 1, 2,"crop");
		dm.insertPurchase(1, 2, "seeds", 100, "crop");*/
		SQLiteDatabase db;
		DbHelper dbh=new DbHelper(this);
		db=dbh.getReadableDatabase();
		Cursor cursor=db.rawQuery("select * from "+DbHelper.TABLE_TRANSACTION_LOG,null);
		while(cursor.moveToNext()){
			String meh=cursor.getString(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_TABLE));
			meh=meh+" "+cursor.getInt(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_ROWID));
			meh=meh+" "+cursor.getString(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_OPERATION));
			meh=meh+" "+cursor.getLong(cursor.getColumnIndex(DbHelper.TRANSACTION_LOG_TRANSTIME));
			System.out.println(meh+"\n");
		}
		
		cursor=db.rawQuery("select * from "+DbHelper.TABLE_CLOUD_KEY, null);
		while(cursor.moveToNext()){
			String meh=cursor.getString(cursor.getColumnIndex(DbHelper.CLOUD_KEY_TABLE));
			meh=meh+" "+cursor.getInt(cursor.getColumnIndex(DbHelper.CLOUD_KEY_ROWID));
			meh=meh+" "+cursor.getString(cursor.getColumnIndex(DbHelper.CLOUD_KEY));
			System.out.println(meh+"\n");
		}
		
		cursor=db.rawQuery("select * from "+dbh.TABLE_REDO_LOG, null);
		while(cursor.moveToNext()){
			String meh=cursor.getString(cursor.getColumnIndex(dbh.REDO_LOG_TABLE));
			meh=meh+" "+cursor.getInt(cursor.getColumnIndex(dbh.REDO_LOG_ROW_ID));
			meh=meh+" "+cursor.getString(cursor.getColumnIndex(dbh.REDO_LOG_OPERATION));
			System.out.println(meh+"\n");
		}
		
		//dm.deleteCycle(7);
		//dm.deleteCycleUse(7);
		//dm.deletePurchase(8);
		//db.close();
		CloudInterface c=new CloudInterface(this,db,dbh);
		c.insertLog();
		//dm.update();
		
		/*Translogendpoint.Builder builder = new Translogendpoint.Builder(
		         AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
		         null);         
		builder = CloudEndpointUtils.updateBuilder(builder);
		Translogendpoint endpoint = builder.build();
		TransLog t=new TransLog();
		t.setId("mouse973");
		t.setOperation("ins");
		t.setTableKind("hisone");
		t.setTransTime((long) 299221212);
		t.setAccount("meh");
		if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
		try {
			endpoint.insertTransLog(t).execute();
		} catch (IOException e) {
			System.out.println("could not insert Log");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		TransactionLog tL=new TransactionLog(dbh,db);
		//tL.updateLocal(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_cycle, menu);
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
