package uwi.dcit.AgriExpenseTT.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.helpers.DbHelper;
import uwi.dcit.AgriExpenseTT.helpers.DbQuery;
import uwi.dcit.AgriExpenseTT.models.SlidingTabLayout;

/**
 * Created by Steffan on 12/01/2015.
 */
public class FragmentSlidingDelete extends Fragment {
    protected ArrayList<FragItem> fragments;

    @Override
        public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
        fragments=new ArrayList<FragItem>();
        populateList();
    }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_resources, container, false);
    }
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewpager_manage_resources);
        mViewPager.setAdapter(new PageAdapter(getChildFragmentManager()));
            SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs_manage_resources);
        mSlidingTabLayout.setViewPager(mViewPager);

        // BEGIN_INCLUDE (tab_colorizer)
        // Set a TabColorizer to customize the indicator and divider colors. Here we just retrieve
        // the tab at the position, and return it's set color
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return fragments.get(position).getColour();
            }

            @Override
            public int getDividerColor(int position) {
                return fragments.get(position).getColour();
            }

        });
    }
    private void populateList(){
        SQLiteDatabase db;
        DbHelper dbh=new DbHelper(getActivity().getApplicationContext());
        db=dbh.getReadableDatabase();
        Fragment cycleFrag,resFrag;
        Bundle arguments	= new Bundle();

        if(DbQuery.cyclesExist(db)){
            cycleFrag=new FragmentViewCycles();
            arguments.putString("type", "delete");
            cycleFrag.setArguments(arguments);
        }else{
            cycleFrag = new FragmentEmpty();
            arguments.putString("type", "cycle");
            cycleFrag.setArguments(arguments);
        }
        if(DbQuery.resourceExist(db)){
            resFrag =new FragmentChoosePurchase();
            arguments.putString("det", "delete");
            resFrag.setArguments(arguments);
        }else{
            resFrag=new FragmentEmpty();
            arguments.putString("type", "purchase");
            resFrag.setArguments(arguments);
        }
        Fragment viewRes=new FragmentViewResources();
        arguments.putString("type", "delete");
        viewRes.setArguments(arguments);
        fragments.add(new FragItem(cycleFrag, Color.BLUE,"Cycles"));
        fragments.add(new FragItem(resFrag,Color.GREEN,"Purchases"));
        fragments.add(new FragItem(viewRes,Color.RED,"Resources"));
    }

    protected class PageAdapter extends FragmentPagerAdapter {
        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position).getFrag();
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).getTitle();
        }
    }
    protected class FragItem{
        private Fragment frag;
        private int colour;
        private String title;
        public FragItem(Fragment frag,int colour,String title){
            this.frag=frag;
            this.colour=colour;
            this.title=title;
        }
        public Fragment getFrag() {
            return frag;
        }
        public int getColour() {
            return colour;
        }
        public String getTitle(){
            return title;
        }
    }
}
