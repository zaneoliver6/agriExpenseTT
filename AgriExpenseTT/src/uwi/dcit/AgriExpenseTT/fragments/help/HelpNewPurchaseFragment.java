package uwi.dcit.AgriExpenseTT.fragments.help;

import uwi.dcit.AgriExpenseTT.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class HelpNewPurchaseFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_help_article_view_layout4, container, false);
		
		TextView txtHeading = (TextView)view.findViewById(R.id.article_heading);
		txtHeading.setText("New Purchase");
		
		ImageView imgView1 =(ImageView)view.findViewById(R.id.article_image);
		imgView1.setImageDrawable(getResources().getDrawable(R.drawable.help_new_purchase_category));
		
		TextView txt1 = (TextView)view.findViewById(R.id.article_text);
		txt1.setText(getResources().getString(R.string.help_purchase_new_resource_1));
		
		ImageView imgView2 =(ImageView)view.findViewById(R.id.article_image_2);
		imgView2.setImageDrawable(getResources().getDrawable(R.drawable.help_new_purchase_type));
		
		TextView txt2 = (TextView)view.findViewById(R.id.article_text_2);
		txt2.setText(getResources().getString(R.string.help_purchase_new_resource_2));
		
		ImageView imgView3 =(ImageView)view.findViewById(R.id.article_image_3);
		imgView3.setImageDrawable(getResources().getDrawable(R.drawable.help_new_purchase_quantity));
		
		TextView txt3 = (TextView)view.findViewById(R.id.article_text_3);
		txt3.setText(getResources().getString(R.string.help_purchase_new_resource_3));
		
		ImageView imgView4 =(ImageView)view.findViewById(R.id.article_image_4);
		imgView4.setImageDrawable(getResources().getDrawable(R.drawable.help_expense_categories));
		
		TextView txt4 = (TextView)view.findViewById(R.id.article_text_4);
		txt4.setText(getResources().getString(R.string.help_purchase_new_resource_4));
		
		return view;
	}
}
