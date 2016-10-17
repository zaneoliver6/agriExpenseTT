package uwi.dcit.AgriExpenseTT.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;

public class FragmentViewResources extends ListFragment{
	private SQLiteDatabase db;
	private DbHelper dbh;
	private ArrayList<String> rList;
	private DataManager dm;
	private View view;
	private ArrayAdapter<String> listAdapt;
	private final String TAG = "FragmentViewResources";
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dbh=new DbHelper(this.getActivity().getBaseContext());
		db=dbh.getWritableDatabase();
		dm = new DataManager(getActivity(), db, dbh);
		Log.d(TAG, "Setup Database was successful");

		rList = new ArrayList<>();

		listAdapt=new ArrayAdapter<>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, rList);
		setListAdapter(listAdapt);
		Log.d(TAG, "Adapter was configured");
	}
	
	private void populateList(final View v) {
		Log.d(TAG, "Populate the List");
		if (rList == null || rList.size() > 0)rList = new ArrayList<>();

		Log.d(TAG, "Creating the Dialog");
		progressDialog = ProgressDialog.show(getActivity(), "Resources", "Retrieving Purchases", true);
		progressDialog.show();

		//  Run Database Operation in thread other than UI
		(new Thread(new Runnable() {
			@Override
			public void run() {
				DbQuery.getResources(db, dbh, null, rList);
				Collections.sort(rList);
				Log.d(TAG, "Retrieved " + rList.size() + " records from the database");

				// Update the UI
				if (v != null) {
					v.post(new Runnable() {
						@Override
						public void run() {
							listAdapt.notifyDataSetChanged();
							progressDialog.dismiss();
							Log.d(TAG, "Retrieved " + rList.size() + " records from the database and updated the UI");
						}
					});
				}else{
					progressDialog.dismiss();
					Log.e(TAG, "Did not have a valid view to update the UI");
				}
			}
		})).start();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view  =  inflater.inflate(R.layout.fragment_choose_purchase, container, false);
		populateList(view);
		return view;
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
		ArrayAdapter<String> adapter;
		public Confirm(int position,ArrayAdapter<String> adapter){
			this.id = DbQuery.getNameResourceId(db, dbh, rList.get(position));
			this.adapter = adapter;
			this.position = position;
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(which==DialogInterface.BUTTON_POSITIVE){
				dm.deleteResource(id);
				rList.remove(position);
				adapter.notifyDataSetChanged();
				Toast.makeText(getActivity(),"Resource deleted", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
//				dialog.cancel();
				//DeleteExpenseList.this.finish();
			}else if(which==DialogInterface.BUTTON_NEGATIVE){
				dialog.cancel();
			}
		}
	}
	
	
}
