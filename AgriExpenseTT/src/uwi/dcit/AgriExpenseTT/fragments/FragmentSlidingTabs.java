package uwi.dcit.AgriExpenseTT.fragments;

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
import uwi.dcit.AgriExpenseTT.widgets.SlidingTabLayout;


public abstract class FragmentSlidingTabs extends Fragment {
    protected ArrayList<FragItem> fragments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragments=new ArrayList<>();
//        populateList();
    }

    @Override
    public void onResume(){
        super.onResume();
        populateList();
    }

    public abstract void populateList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_resources, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewpager_manage_resources);
        mViewPager.setAdapter(new ResourcePageAdapter(getChildFragmentManager()));

        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs_manage_resources);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) { return fragments.get(position).getColour(); }
            @Override
            public int getDividerColor(int position) { return fragments.get(position).getColour(); }
        });
    }


    protected class ResourcePageAdapter extends FragmentPagerAdapter {
        public ResourcePageAdapter(FragmentManager fm) {
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
        public CharSequence getPageTitle(int position) {return fragments.get(position).getTitle(); }
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
