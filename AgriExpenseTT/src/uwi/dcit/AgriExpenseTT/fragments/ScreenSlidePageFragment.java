package uwi.dcit.AgriExpenseTT.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


import uwi.dcit.AgriExpenseTT.R;


public class ScreenSlidePageFragment extends Fragment {

    public static final ScreenSlidePageFragment newInstance(String message){
        ScreenSlidePageFragment f = new ScreenSlidePageFragment();
        Bundle b = new Bundle(1);
        b.putString("EXTRA_MESSAGE", message);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //String message = "EXTRA_MESSAGE";
        View v = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        Button btn = (Button)v.findViewById(R.id.finish_button);
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                getActivity().finish();
            }
        });
        return v;
    }
}
