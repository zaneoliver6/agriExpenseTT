package fragments;

import helper.DataManager;
import helper.DbHelper;
import helper.DbQuery;

import java.util.ArrayList;
import java.util.Collections;

import uwi.dcit.AgriExpenseTT.R;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FragmentViewResources extends ListFragment{
	SQLiteDatabase db;
	DbHelper dbh;
	ArrayList<String> rList;
	DataManager dm;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh=new DbHelper(this.getActivity().getBaseContext());
		db=dbh.getReadableDatabase();
		dm = new DataManager(getActivity(), db, dbh);
		populateList();
		Collections.sort(rList);
		ArrayAdapter<String> listAdapt=new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, rList);
		setListAdapter(listAdapt);
	}
	
	private void populateList() {
		rList=new ArrayList<String>();
		DbQuery.getResources(db, dbh, null, rList);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		//returns the inflated layout which contains the listview
		return inflater.inflate(R.layout.fragment_choose_purchase, container, false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String type=getArguments().getString("type");
		if(type==null){
			
		}else if(type.equals("delete")){
			AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage("Are you sure you want to delete");
            builder1.setCancelable(true);
            System.out.println(position);
            @SuppressWarnings("unchecked")
			Confirm c=new Confirm(position,(ArrayAdapter<String>) l.getAdapter());
            builder1.setPositiveButton("Yes",c);
            builder1.setNegativeButton("Nope",c);
            AlertDialog alert1 = builder1.create();
            alert1.show();
		}
	}
	private class Confirm implements DialogInterface.OnClickListener{
		int position;
		int id;
		ArrayAdapter<String> adpt;
		public Confirm(int position,ArrayAdapter<String> adpt){
			this.id=DbQuery.getNameResourceId(db, dbh, rList.get(position));
			this.adpt=adpt;
			this.position=position;
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(which==DialogInterface.BUTTON_POSITIVE){
				dm.deleteResource(id);
				rList.remove(position);
				adpt.notifyDataSetChanged();
				 System.out.println(position);
				Toast.makeText(getActivity(),"Resource deleted", Toast.LENGTH_SHORT).show();			
				dialog.cancel();
				//DeleteExpenseList.this.finish();
			}else if(which==DialogInterface.BUTTON_NEGATIVE){
				dialog.cancel();
			}
		}
	}
	
	
}
