package uwi.dcit.AgriExpenseTT.fragments.slides;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import uwi.dcit.AgriExpenseTT.Main;
import uwi.dcit.AgriExpenseTT.R;

public class NewPurchaseSlides extends FragmentActivity {

    private static final int Num_pages = 3;
    int[] mResources = {
            R.drawable.new_purchase_slide2,
            R.drawable.new_purchase_slide3,
            R.drawable.new_purchase_slide4,
            R.drawable.new_purchase_slide5,
            R.drawable.new_purchase_slide6,
    };
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_introtest);
        mPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(this);
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onBackPressed(){
        if(mPager.getCurrentItem()==0){
            super.onBackPressed();
        }else{
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_introtest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void ReturnToMain(View view) {
        Intent intent = new Intent(NewPurchaseSlides.this, Main.class);
        startActivity(intent);
    }

    private class ScreenSlidePagerAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;
        public ScreenSlidePagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.fragment_screen_slide_page, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.slide_image);
            imageView.setImageResource(mResources[position]);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }
    }
}
