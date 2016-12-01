package uwi.dcit.AgriExpenseTT.helpers;

import android.support.v4.app.Fragment;

public interface NavigationControl {
    void navigate(Fragment oldFrag, Fragment newFrag);

    Fragment getLeftFrag();

    Fragment getRightFrag();

    String[] getMenuOptions();

    int[] getMenuImages();
}
