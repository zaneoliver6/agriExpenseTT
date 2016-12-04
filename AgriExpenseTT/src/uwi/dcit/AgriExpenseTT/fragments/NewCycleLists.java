package uwi.dcit.AgriExpenseTT.fragments;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import uwi.dcit.AgriExpenseTT.NewCycle;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;

public class NewCycleLists extends ListFragment {
    private String type;
    private ArrayList<String> list;
    private SQLiteDatabase db;
    private DbHelper dbh;
    private TextView et_main;
    private TextView et_search;
    private ArrayAdapter<String> listAdapt;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbh = new DbHelper(this.getActivity().getBaseContext());
        db = dbh.getReadableDatabase();
        list = new ArrayList<>();
        type = getArguments().getString("type");
        // Create the List adapter
        listAdapt = new ArrayAdapter<>(this.getActivity().getBaseContext(), android.R.layout.simple_list_item_1, list);
        setListAdapter(listAdapt);
    }

    private void populateList() {
        if (list == null || list.size() > 0) list = new ArrayList<>();
        (new Thread(new Runnable() {
            @Override
            public void run() {
                // Perform DB Operation
                if (type.equals(DHelper.cat_plantingMaterial)) {
                    DbQuery.getResources(db, dbh, DHelper.cat_plantingMaterial, list);
                } else if (type.equals("land")) {
                    list.add("Acre");
                    list.add("Hectare");
                    list.add("Bed");
                }

                for(String val : list){
                    System.out.println(val);
                }

                Collections.sort(list);
                // Update the UI on the Main Thread
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("NewCycleList", "Retrieved: " + list.size() + " values");
                        listAdapt.notifyDataSetChanged();
                    }
                });
            }
        })).start();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_reuse, container, false);

        populateList();

        et_main = (TextView) view.findViewById(R.id.tv_frag_mainHead_new);
        et_search = (TextView) view.findViewById(R.id.et_listReuse_search);
        et_search.setVisibility(View.INVISIBLE); //TODO Remove the search bar until more reliable performance is achieved

        if (type.equals("land")) {
            et_search.setVisibility(View.GONE);
        } else {
            TWatch tw = new TWatch(listAdapt);
            et_search.addTextChangedListener(tw);
        }
        if (type.equals(DHelper.cat_plantingMaterial)) {
            et_main.setText("Select the crop to plant for this cycle");
        } else if (type.equals("land")) {
            et_main.setText("Select the type of land you are using");
        }
        view.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        if (v.getId() != (R.id.et_newCycleLast_landqty))
                            ((NewCycle) getActivity()).hideSoftKeyboard();
                        return false;
                    }
                }
        );

        return view;
    }

    @Override
    public void onDestroy() {
        db.close();
        super.onDestroy();
    }

    public void updateSub(String message) {
        if (this.getActivity() instanceof NewCycle) {
            ((NewCycle) getActivity()).replaceSub(message);
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Fragment nextFragment = null;
        Bundle arguments = new Bundle();

        if (type.equals(DHelper.cat_plantingMaterial)) {
            arguments.putString("type", "land");                                        //passes the type of the data we want in the new list fragment
            arguments.putString(DHelper.cat_plantingMaterial, list.get(position));    //passes the crop chosen to the land list fragment
            updateSub("Details: " + listAdapt.getItem(position) + ", ");                                //Change the details section of the fragment
            nextFragment = new NewCycleLists();                                        //Launch a new instance of the class to deal with the land type selection
        } else if (type.equals("land")) {
            //Pass the crop specified in previous fragment on to the next action
            arguments.putString(DHelper.cat_plantingMaterial, getArguments().getString(DHelper.cat_plantingMaterial));
            arguments.putString("land", listAdapt.getItem(position));                        //Pass on the land type selected to the next fragment

            StringBuilder stb = new StringBuilder();                                //Using String builder to get details rather than concatenation
            stb.append("Details: ")
                    .append(getArguments().getString(DHelper.cat_plantingMaterial))
                    .append(", ")
                    .append(listAdapt.getItem(position));
            updateSub(stb.toString());                                                //Change the details section to reflect user choice

            nextFragment = new FragmentNewCycleLast();
        }

        if (nextFragment != null) {
            nextFragment.setArguments(arguments);                                        //Add Arguments to the next fragment to be loaded

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.NewCycleListContainer, nextFragment)                        //Load the New Fragment
                    .addToBackStack(type)                                                    //add the transaction to the back stack
                    .commit();
        }

    }

    public class TWatch implements TextWatcher {
        ArrayAdapter<String> adapter;

        public TWatch(ArrayAdapter<String> adapter) {
            super();
            this.adapter = adapter;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            adapter.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }
} 
