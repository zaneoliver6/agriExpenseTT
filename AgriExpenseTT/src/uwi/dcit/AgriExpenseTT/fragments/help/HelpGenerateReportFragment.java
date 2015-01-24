package uwi.dcit.AgriExpenseTT.fragments.help;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.GAnalyticsHelper;

public class HelpGenerateReportFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_help_article_view, container, false);
		
		TextView txtHeading = (TextView)view.findViewById(R.id.article_heading);
		txtHeading.setText("Introduction");
		
		ImageView img = (ImageView)view.findViewById(R.id.article_image);
		img.setImageDrawable(getResources().getDrawable(R.drawable.help_homescreen));
		
		
		TextView txtContent = (TextView)view.findViewById(R.id.article_text);
		txtContent.setText(getResources().getString(R.string.help_intro));

        GAnalyticsHelper.getInstance(this.getActivity()).sendScreenView("Help Generate Report Fragment");
		return view;
	}
}
