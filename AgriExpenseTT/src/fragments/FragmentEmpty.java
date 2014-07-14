package fragments;

import uwi.dcit.AgriExpenseTT.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentEmpty extends Fragment{
	
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String type=getArguments().getString("type");
		view=inflater.inflate(R.layout.fragment_empty_purchaselist, container, false);
		TextView desc=(TextView)view.findViewById(R.id.tv_empty_desc);
		String category=getArguments().getString("category");
			if(type.equals("purchase")){
				if(category==null){
					desc.setText("Sorry you have not made any purchases as yet");
				}else{
					desc.setText("Sorry you have not purchased any "+category+" as yet");
				}
			}else if(type.equals("cycle")){
				desc.setText("Sorry you have not created any crop cycles as yet");
			}else if(type.equals("purchaseuse")){
				if(category==null){
					desc.setText("Sorry you haven't purchased any of this to use as yet");
				}else{
					desc.setText("Sorry you haven't purhased any "+category+", so there's nothing to use");
				}
			}
		return view;
	}
	
}
