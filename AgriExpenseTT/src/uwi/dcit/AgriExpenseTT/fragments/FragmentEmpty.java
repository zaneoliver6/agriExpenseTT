package uwi.dcit.AgriExpenseTT.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import uwi.dcit.AgriExpenseTT.NewCycle;
import uwi.dcit.AgriExpenseTT.NewPurchase;
import uwi.dcit.AgriExpenseTT.R;

public class FragmentEmpty extends Fragment{
	View view;
    private String type;
    private String category;
    protected boolean isLabour = false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        type = getArguments().getString("type");
        category = getArguments().getString("category");
		
		view = inflater.inflate(R.layout.fragment_empty_resourcelist, container, false);
		TextView desc = (TextView)view.findViewById(R.id.tv_empty_desc);
        final Button button = (Button) view.findViewById(R.id.AddResButton);
        setupButton(type);

        switch (type){
            case "purchase":
                break;
            case "cycle":
                break;
            case "select":
                break;
            case "labour":
                break;
            default:
                break;
        }

        switch (type) { // Setting up the text for the page depending on the type
            case "purchase":
                if (category == null) {
                    desc.setText("Sorry you haven't purchased any of this to use as yet");
                    button.setText("Add Purchase");
                } else {
                    desc.setText("Sorry you haven't purchased any of this to use as yet");
                    desc.setText("Sorry you haven't purhased any " + category + ", so there's nothing to use");
                    button.setText("Add Purchase");
                }
                break;
            case "cycle":
                desc.setText("Tap here to create a new cycle");
                button.setText("Add Cycle");
                break;

            case "select":
                desc.setText("Select something to begin operations");
                button.setText("Add");
                break;
            case "labour":
                desc.setText("Tap here to add a new labourer");
                button.setText("Add Labour");
                this.isLabour = true;
                break;
            default:
                button.setText("Add Purchase");
                break;
        }



        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                addResource();
                }
            });

        // Google Analytics
//        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("Empty Screen Loaded");
		return view;
	}


    private void addResource() {// Adds functionality to the add cycle or Add purchase button
        if (type.equals("cycle")) {
            Intent intent = new Intent(getActivity().getApplicationContext(), NewCycle.class);
            startActivity(intent);
        } else if (type.equals("purchase")) {
            Intent intent = new Intent(getActivity().getApplicationContext(), NewPurchase.class);
            startActivity(intent);
        }
    }
    

    private void setupButton(String type) {// Sets the text for the page
        ImageView v=(ImageView)view.findViewById(R.id.Empty_Agri_Logo);
        switch (type) {
            case "purchase":
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Empty Fragment", " creating a new purchase ");
//                    createPurchase();
                    }
                });
                break;
            case "cycle":
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Empty Fragment", " creating a new cycle");
                        //createCycle();
                    }
                });
                break;
            case "labour":
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Empty Fragment", " creating a new labourer");
                        //createLabourer();
                    }
                });
                break;
            case "cycleuse":
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    Log.d()
                    }
                });
                break;
            case "select":
                //v.setImageResource(R.drawable.icon_touch);
                break;
        }
    }

//    private void createLabourer() {
//        getActivity().startActivityForResult(new Intent(getActivity(), HireLabour.class), DHelper.PURCHASE_REQUEST_CODE);
//    }
//
//    public void createCycle(){
//        getActivity().startActivityForResult(new Intent(getActivity().getApplicationContext(), NewCycle.class), DHelper.CYCLE_REQUEST_CODE);
//    }
//    public void createPurchase(){
//        getActivity().startActivityForResult(new Intent(getActivity().getApplicationContext(), NewPurchase.class), DHelper.PURCHASE_REQUEST_CODE);
//    }


}

//if (category.equals("Planting material")) {
//Intent intent = new Intent(getActivity().getApplicationContext(), NewPurchase.class);
//Bundle b = new Bundle();
//b.putString("category", "Planting material");
//b.putString("action", "Planting material");
//intent.putExtras(b);
//startActivity(intent);
//} else {
//    Intent intent = new Intent(getActivity().getApplicationContext(), NewPurchase.class);
//    startActivity(intent);
//}

