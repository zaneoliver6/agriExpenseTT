package uwi.dcit.AgriExpenseTT.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import uwi.dcit.AgriExpenseTT.HireLabour;
import uwi.dcit.AgriExpenseTT.NewCycle;
import uwi.dcit.AgriExpenseTT.NewPurchase;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DHelper;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class FragmentEmpty extends Fragment{
	View view;
    protected boolean isLabour = false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		String type = getArguments().getString("type");
		String category = getArguments().getString("category");
		
		view = inflater.inflate(R.layout.fragment_empty_resourcelist, container, false);
		TextView desc = (TextView)view.findViewById(R.id.tv_empty_desc);
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

        if(type.equals("purchase")){
			if(category == null){
				desc.setText("Tap here to create a new purchase");
			}else{
				desc.setText("Sorry you have not purchased any "+category+" as yet");
			}
		}else if(type.equals("cycle")){
			desc.setText("Tap here to create a new cycle");
		}else if(type.equals("purchase")){
			if(category == null){
				desc.setText("Sorry you haven't purchased any of this to use as yet");
			}else{desc.setText("Sorry you haven't purchased any of this to use as yet");
				desc.setText("Sorry you haven't purhased any "+category+", so there's nothing to use");
			}
		}else if(type.equals("select")){
            desc.setText("Select something to begin operations");
        }else if (type.equals("labour")){
            desc.setText("Tap here to add a new labourer");
            this.isLabour = true;
        }
        // Google Analytics
        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("Empty Screen Loaded");
		return view;
	}

    private void setupButton(String type) {
        ImageView v=(ImageView)view.findViewById(R.id.img_empty_frag);
        if(type.equals("purchase")){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Empty Fragment"," creating a new purchase ");
                    createPurchase();
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
    }
    public void createPurchase(){
        getActivity().startActivityForResult(new Intent(getActivity().getApplicationContext(), NewPurchase.class), DHelper.PURCHASE_REQUEST_CODE);
    }
}
