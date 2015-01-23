package uwi.dcit.AgriExpenseTT.helpers;

import android.app.Activity;
import android.view.MenuItem;
import android.widget.Toast;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.fragments.FragmentCreateDialogue;

/**
 * Created by Steffan on 16/01/2015.
 */
public class MenuHelper {
    public static void handleClick(String item,Activity activity){
        if(item.equals(activity.getString(R.string.menu_item_newCycle))){

        }else if(item.equals(activity.getString(R.string.menu_item_newPurchase))){

        }else if(item.equals(activity.getString(R.string.menu_item_hireLabour))){

        }else if(item.equals(activity.getString(R.string.menu_item_manageData))){

        }else if(item.equals(activity.getString(R.string.menu_item_genFile))){

        }else if(item.equals(activity.getString(R.string.menu_item_about))){

        }else if(item.equals(activity.getString(R.string.menu_item_createNew))){
            MenuItem m=(MenuItem)activity.findViewById(R.id.action_create);
//            m.get
            FragmentCreateDialogue p = new FragmentCreateDialogue();
            p.show(activity.getFragmentManager(),"fm");
            Toast.makeText(activity.getApplicationContext(),"New thing",Toast.LENGTH_SHORT).show();

        }
    }
}
