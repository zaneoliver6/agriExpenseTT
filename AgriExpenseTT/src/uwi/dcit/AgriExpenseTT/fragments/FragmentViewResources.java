package uwi.dcit.AgriExpenseTT.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.dbstruct.structs.Resource;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class FragmentViewResources extends ListFragment{
	SQLiteDatabase db;
	DbHelper dbh;
	ArrayList<String> rList;
	DataManager dm;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh=new DbHelper(this.getActivity().getBaseContext());
		db=dbh.getWritableDatabase();
		dm = new DataManager(getActivity(), db, dbh);
		populateList();
		Collections.sort(rList);
		ArrayAdapter<String> listAdapt=new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, rList);
		setListAdapter(listAdapt);
        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("View Resources Fragment");
	}
	
	private void populateList() {
		rList=new ArrayList<String>();
		Resource.getResources(db, dbh, null, rList);
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
		if(type.equals("delete")){
            @SuppressWarnings("unchecked")
            Confirm c = new Confirm(position,(ArrayAdapter<String>) l.getAdapter());

			AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage("Are you sure you want to delete")
                    .setCancelable(true)
                    .setPositiveButton("Delete",c)
                    .setNegativeButton("Cancel",c);

            AlertDialog alert1 = builder1.create();
            alert1.show();
		}
	}
	private class Confirm implements DialogInterface.OnClickListener{
		int position;
		int id;
		ArrayAdapter<String> adpt;
		public Confirm(int position,ArrayAdapter<String> adpt){
			this.id=Resource.getNameResourceId(db, dbh, rList.get(position));
			this.adpt=adpt;
			this.position=position;
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(which==DialogInterface.BUTTON_POSITIVE){
				dm.deleteResource(id);
				rList.remove(position);
				adpt.notifyDataSetChanged();
				Toast.makeText(getActivity(),"Resource deleted", Toast.LENGTH_SHORT).show();
				dialog.cancel();
				//DeleteExpenseList.this.finish();
			}else if(which==DialogInterface.BUTTON_NEGATIVE){
				dialog.cancel();
			}
		}
	}
	
	
}
