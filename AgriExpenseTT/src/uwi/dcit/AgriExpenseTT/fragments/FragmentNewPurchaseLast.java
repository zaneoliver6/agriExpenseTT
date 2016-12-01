package uwi.dcit.AgriExpenseTT.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import uwi.dcit.AgriExpenseTT.Main;
import uwi.dcit.AgriExpenseTT.NewPurchase;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DataManager;
import uwi.dcit.AgriExpenseTT.helpers.DateFormatHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;
import uwi.dcit.AgriExpenseTT.helpers.TextHelper;
import uwi.dcit.AgriExpenseTT.models.CycleContract.CycleEntry;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;
import uwi.dcit.AgriExpenseTT.models.ResourcePurchaseContract.ResourcePurchaseEntry;
import uwi.dcit.agriexpensesvr.resourcePurchaseApi.model.ResourcePurchase;

public class FragmentNewPurchaseLast extends Fragment implements DatePickerDialog.OnDateSetListener{
    private EditText et_qty;
	private EditText et_cost;
	private TextView helper_qty;
    private Button btnDate;

	private String category;
	private String resource;
	private String quantifier;
	private LocalCycle currC=null;
	private int resId;
	private SQLiteDatabase db;
	private DbHelper dbh;
    private long unixDate;
    private TextView helper_cost;
    private int res;
    private double qty;
    private double cost;
    private ProgressDialog progressDialog;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newpurchase_last, container, false);

        category = getArguments().getString("category");
        resource = getArguments().getString("resource");
        quantifier = getArguments().getString("quantifier");

        dbh = new DbHelper(getActivity().getBaseContext());
        db = dbh.getWritableDatabase();
        resId = DbQuery.getNameResourceId(db, dbh, resource);

        setDetails(view);
        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("New Purchase");

        view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!(v instanceof EditText))((NewPurchase) getActivity()).hideSoftKeyboard();
                    return false;
                }
        });
        return view;
    }

    private void setDetails(View view){
        et_qty  = (EditText) view.findViewById(R.id.et_newPurchaselast_qty);
        et_cost = (EditText) view.findViewById(R.id.et_newPurchaselast_cost);
        helper_qty = (TextView) view.findViewById(R.id.tv_mainNew_header);
        helper_cost = (TextView) view.findViewById(R.id.tv_cycUseItem_sub1_2);

        if(category.equals(DHelper.cat_labour)){
            helper_qty.setText(String.format("Number of %s's %s is going to work", quantifier, resource)); //TODO Review wording for labour
            helper_cost.setText(String.format("Cost of all %s's %s will work for", quantifier, resource));
        }else{
            helper_qty.setText(String.format("Number/quantity of %s %ss", resource, quantifier));
            helper_cost.setText(String.format("Cost of all %s %ss", resource, quantifier));
        }

        NewPurchaseClickListener c = new NewPurchaseClickListener(this);
        view.findViewById(R.id.btn_newpurchaselast_done).setOnClickListener(c);
        btnDate = (Button)view.findViewById(R.id.btn_newPurchaseLast_date);
        btnDate.setOnClickListener(c);

        String format = DateFormatHelper.formatDisplayDate(null);
        btnDate.setText(format);
        unixDate = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar cal = Calendar.getInstance();
        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        String format = DateFormatHelper.formatDisplayDate(cal);
        btnDate.setText(format);
        unixDate = cal.getTimeInMillis();
    }

    private void displayDateDialog() {
        final Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);
        (new DatePickerDialog(getActivity(), this, year, month, day)).show();
    }

    private boolean isInputValid() {
        // Validate Quantity Input
        if ((et_qty.getText().toString()).equals("")) {
            helper_qty.setText(R.string.enter_purchase_quantity);
            helper_qty.setTextColor(ContextCompat.getColor(getActivity(), R.color.helper_text_error));
            et_qty.getBackground().setColorFilter(ContextCompat.getColor(getActivity(), R.color.helper_text_error), PorterDuff.Mode.SRC_ATOP);
            return false;
        } else { // Set the Quantity Field
            qty = Double.parseDouble(et_qty.getText().toString());
            et_qty.getBackground().setColorFilter(ContextCompat.getColor(getActivity(), R.color.helper_text_color), PorterDuff.Mode.SRC_ATOP);
        }

        // Validate Cost Input
        if ((et_cost.getText().toString()).equals("")) {
            helper_cost.setText(R.string.enter_cost);
            helper_cost.setTextColor(ContextCompat.getColor(getActivity(), R.color.helper_text_error));
            et_cost.getBackground().setColorFilter(ContextCompat.getColor(getActivity(), R.color.helper_text_error), PorterDuff.Mode.SRC_ATOP);
            return false;
        } else { // Set the Cost Field
            cost = Double.parseDouble(et_cost.getText().toString());
            et_cost.getBackground().setColorFilter(ContextCompat.getColor(getActivity(), R.color.helper_text_color), PorterDuff.Mode.SRC_ATOP);
        }

        return true;
    }

    private void notifyUserResult() {
        if (progressDialog != null) progressDialog.dismiss();
        if (res != -1) {
            Toast.makeText(getContext(), R.string.purchase_save_success, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), R.string.purchase_save_error, Toast.LENGTH_LONG).show();
        }

        Bundle bundle = new Bundle();
        bundle.putString("type", "purchase");
        bundle.putInt("id", res);
        Intent i = new Intent(getContext(), Main.class);
        i.putExtras(bundle);
        startActivity(i);
    }

    private int savePurchaseRecord() {
        DataManager dm = new DataManager(getActivity().getBaseContext(), db, dbh);
        res = -1;
        try {
            currC = getArguments().getParcelable("cycle");
            //this is for when labour is 'purchased'/hired for a single cycle
            if (category.equals(DHelper.cat_labour) && currC != null) {
                //insert purchase
                res = dm.insertPurchase(resId, quantifier, qty, category, cost, unixDate);
                int pId = DbQuery.getLast(db, dbh, ResourcePurchaseEntry.TABLE_NAME);
                ResourcePurchase p = DbQuery.getARPurchase(db, dbh, pId);

                //use all of the qty of that purchase in the given cycle
                dm.insertCycleUse(currC.getId(), p.getPId(), qty, p.getType(), quantifier, p.getCost());

                //update purchase
                p.setQtyRemaining(p.getQtyRemaining() - qty);
                ContentValues cv = new ContentValues();
                cv.put(ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING, p.getQtyRemaining());
                dm.updatePurchase(p, cv);

                //update cycle
                currC.setTotalSpent(currC.getTotalSpent() + cost);
                cv = new ContentValues();
                cv.put(CycleEntry.CROPCYCLE_TOTALSPENT, currC.getTotalSpent());
                dm.updateCycle(currC, cv);
            } else {
                if (category.equals(DHelper.cat_other))//if its the other category
                    if (resId == -1)//and the resource does not exist
                        resId = DbQuery.insertResource(db, dbh, DHelper.cat_other, TextHelper.formatUserText(resource));//then insert it !
                if (resId != -1)
                    res = dm.insertPurchase(resId, quantifier, qty, category, cost, unixDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            res = -1;
        }

        return res;
    }

    private class NewPurchaseClickListener implements OnClickListener {

        FragmentActivity activity;
        FragmentNewPurchaseLast fragment;

        public NewPurchaseClickListener(FragmentNewPurchaseLast fragment) {
            this.fragment = fragment;
            this.activity = fragment.getActivity();
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_newPurchaseLast_date) {
                fragment.displayDateDialog();
            } else if (v.getId() == R.id.btn_newpurchaselast_done) {
                if (fragment.isInputValid()) { // Check input to ensure information entered
                    // If we get to this point that fields have passed validation test
                    progressDialog = ProgressDialog.show(getActivity(), "Resources", "Retrieving Purchases", true);
                    progressDialog.show();
                    // Run Operation to store the Purchase in a background thread
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            fragment.savePurchaseRecord();
                            // When completed run in UI thread
                            fragment.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fragment.notifyUserResult();
                                }
                            });
                        }
                    }).start();
                }
            }
        }
	}
}
