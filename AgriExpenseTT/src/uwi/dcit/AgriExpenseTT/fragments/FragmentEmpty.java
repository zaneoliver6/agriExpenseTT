package uwi.dcit.AgriExpenseTT.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import uwi.dcit.AgriExpenseTT.AddData;
import uwi.dcit.AgriExpenseTT.HireLabour;
import uwi.dcit.AgriExpenseTT.NewCycle;
import uwi.dcit.AgriExpenseTT.NewPurchase;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;

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

        if(type.equals("purchase")){ // Setting up the text for the page depending on the type
			if(category == null){
				desc.setText("Tap here to create a new purchase");
                button.setText("Add Purchase");
			}else {
				desc.setText("Sorry you have not purchased any "+category+" as yet");
			}
		}else if(type.equals("cycle")){
			desc.setText("Tap here to create a new cycle");
            button.setText("Add Cycle");
		}else if(type.equals("purchase")){
			if(category == null){
				desc.setText("Sorry you haven't purchased any of this to use as yet");
                button.setText("Add Purchase");
			}else{desc.setText("Sorry you haven't purchased any of this to use as yet");
				desc.setText("Sorry you haven't purhased any "+category+", so there's nothing to use");
                button.setText("Add Purchase");
			}
		}else if(type.equals("select")){
            desc.setText("Select something to begin operations");
            button.setText("Add");
        }else if (type.equals("labour")){
            desc.setText("Tap here to add a new labourer");
            button.setText("Add Labour");
            this.isLabour = true;
        }else{
            button.setText("Add Purchase");
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

    private void setupButton(String type) {// functionality kinda deleted atm.. dont know how it will affect if deleted atm but will test soon
        ImageView v=(ImageView)view.findViewById(R.id.Empty_Agri_Logo);
        if(type.equals("purchase")){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                 public void onClick(View v) {
                    Log.d("Empty Fragment", " creating a new purchase ");
                    //createPurchase();
                }
            });
        }else if(type.equals("cycle")){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Empty Fragment", " creating a new cycle");
                    //createCycle();
                }
            });
        }else if (type.equals("labour")){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Empty Fragment", " creating a new labourer");
                    //createLabourer();
                }
            });
        }else if(type.equals("cycleuse")){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.d()
                }
            });
        }else if(type.equals("select")){
            //v.setImageResource(R.drawable.icon_touch);
        }
    }

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

