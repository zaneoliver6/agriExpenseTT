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

import uwi.dcit.AgriExpenseTT.NewCycle;
import uwi.dcit.AgriExpenseTT.NewPurchase;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class FragmentEmpty extends Fragment{
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		String type = getArguments().getString("type");
		String category = getArguments().getString("category");
		
		view = inflater.inflate(R.layout.fragment_empty_purchaselist, container, false);
		
		TextView desc = (TextView)view.findViewById(R.id.tv_empty_desc);
		setupButton(type);
        Log.d("Fragment Empty type",type);
		if(type.equals("purchase")){
			if(category == null){
				desc.setText("Tap here to create a new purchase");
			}else{
				desc.setText("Sorry you have not purchased any "+category+" as yet");
			}
		}else if(type.equals("cycle")){
			desc.setText("Tap here to create a new cycle");
		}else if(type.equals("purchaseuse")){
			if(category == null){
				desc.setText("Sorry you haven't purchased any of this to use as yet");
			}else{desc.setText("Sorry you haven't purchased any of this to use as yet");
				desc.setText("Sorry you haven't purhased any "+category+", so there's nothing to use");
			}
		}else if(type.equals("select")){
            desc.setText("Select something to begin operations");
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
    public void createCycle(){
        startActivity(new Intent(getActivity().getApplicationContext(), NewCycle.class));
    }
    public void createPurchase(){
        startActivity(new Intent(getActivity().getApplicationContext(), NewPurchase.class));
    }
}
