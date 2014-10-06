package uwi.dcit.AgriExpenseTT.fragments.help;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.HelpTopics;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class HelpListFragment extends ListFragment{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // We need to use a different list item layout for devices older than Honeycomb
        int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;
        // Create an array adapter for the list view, using the Topics array
        setListAdapter(new ArrayAdapter<String>(getActivity(), layout, HelpTopics.Topics));
    }
	
	@Override
    public void onStart() {
        super.onStart();
        
        this.getListView().setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
//				Toast.makeText(getActivity(), position + " "+ HelpTopics.Topics[position], Toast.LENGTH_SHORT).show();
				
				Fragment frag = null;
				
				switch (position){
					case 0:
						frag = new HelpIntroFragment();
						break;
					case 1:
						frag = new HelpNewPurchaseFragment();
						break;
					case 2:
						frag = new HelpNewCropCycleFragment();
						break;
					case 3:
						frag = new HelpManageResourceFragment();
						break;
					case 4:
						frag = new HelpHiringLabourFragment();
						break;
					case 5:
						frag = new HelpManageDataFragment();
						break;
					case 6:
						frag = new HelpCalculateSalesFragment();
						break;
					case 7:
						frag = new HelpGenerateReportFragment();
						break;
					default:
						Toast.makeText(getActivity(), "Help Topic not found", Toast.LENGTH_SHORT).show();
						break;
				}
				
				
				if (frag != null)
					getFragmentManager()
						.beginTransaction()
						.replace(R.id.help_lists, frag)
						.addToBackStack(null)
						.commit();
				
			}
        });
	}
}
