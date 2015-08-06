package uwi.dcit.AgriExpenseTT.fragments;

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

import uwi.dcit.AgriExpenseTT.HireLabour;
import uwi.dcit.AgriExpenseTT.NewCycle;
import uwi.dcit.AgriExpenseTT.NewPurchase;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;

public class FragmentEmpty extends Fragment{
	View view;
    private String type;
    protected boolean isLabour = false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        type = getArguments().getString("type");
		String category = getArguments().getString("category");
		
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
			}else{
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


    private void addResource(){
        if(type.equals("cycle")){
            Intent intent = new Intent(getActivity().getApplicationContext(), NewCycle.class);
            startActivity(intent);
        }
        else if(type.equals("purchase")) {
            Intent intent = new Intent(getActivity().getApplicationContext(), NewPurchase.class);
            startActivity(intent);
        }
    }

    private void setupButton(String type) {
        ImageView v=(ImageView)view.findViewById(R.id.img_empty_frag);
        if(type.equals("purchase")){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                 public void onClick(View v) {
                    Log.d("Empty Fragment"," creating a new purchase ");
//                    createPurchase();
                }
            });
        }else if(type.equals("cycle")){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Empty Fragment"," creating a new cycle");
                    createCycle();
                }
            });
        }else if (type.equals("labour")){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Empty Fragment"," creating a new labourer");
                    createLabourer();
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
            v.setImageResource(R.drawable.icon_touch);
        }
    }

    private void createLabourer() {
        getActivity().startActivityForResult(new Intent(getActivity(), HireLabour.class), DHelper.PURCHASE_REQUEST_CODE);
    }

    public void createCycle(){
        getActivity().startActivityForResult(new Intent(getActivity().getApplicationContext(), NewCycle.class), DHelper.CYCLE_REQUEST_CODE);
        getActivity().finish();
    }
    public void createPurchase(){
        getActivity().startActivityForResult(new Intent(getActivity().getApplicationContext(), NewPurchase.class), DHelper.PURCHASE_REQUEST_CODE);
    }
}
