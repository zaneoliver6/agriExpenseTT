package uwi.dcit.AgriExpenseTT.helpers;

import android.support.v4.app.Fragment;

/**
 * Created by Steffan on 12/01/2015.
 */
public interface NavigationControl {
    //protected Activity activity;
    public void navigate(Fragment oldFrag,Fragment newFrag);
    public Fragment getLeftFrag();
    public Fragment getRightFrag();
    public String[] getMenuOptions();
    public int[] getMenuImages();
}
