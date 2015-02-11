package uwi.dcit.AgriExpenseTT.helpers;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import uwi.dcit.AgriExpenseTT.AboutScreen;
import uwi.dcit.AgriExpenseTT.HelpScreen;
import uwi.dcit.AgriExpenseTT.HireLabour;
import uwi.dcit.AgriExpenseTT.Main;
import uwi.dcit.AgriExpenseTT.ManageData;
import uwi.dcit.AgriExpenseTT.NewCycle;
import uwi.dcit.AgriExpenseTT.NewPurchase;
import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.fragments.FragmentCreateDialogue;

public class MenuHelper {
    public static void handleClick(String item,Activity activity){
        if (item.equals(R.string.menu_item_home)){
            activity.startActivity(new Intent(activity.getApplicationContext(), Main.class));
        }else if(item.equals(activity.getString(R.string.menu_item_newCycle))){
            activity.startActivity(new Intent(activity.getApplicationContext(), NewCycle.class));
        }else if(item.equals(activity.getString(R.string.menu_item_newPurchase))){
            activity.startActivity(new Intent(activity.getApplicationContext(), NewPurchase.class));
        }else if(item.equals(activity.getString(R.string.menu_item_hireLabour))){
            activity.startActivity(new Intent(activity.getApplicationContext(), HireLabour.class));
        }else if(item.equals(activity.getString(R.string.menu_item_manageData))){
            activity.startActivity(new Intent(activity.getApplicationContext(), ManageData.class));
        }else if(item.equals(activity.getString(R.string.menu_item_about))){
            activity.startActivity(new Intent(activity.getApplicationContext(), AboutScreen.class));
        }else if(item.equals(activity.getString(R.string.menu_help))){
            activity.startActivity(new Intent(activity.getApplicationContext(), HelpScreen.class));
        }else if(item.equals(activity.getString(R.string.menu_setting))){
            activity.startActivity(new Intent(activity.getApplicationContext(), ManageData.class));
        }else if(item.equals(activity.getString(R.string.menu_item_createNew))){
            FragmentCreateDialogue p = new FragmentCreateDialogue();
            p.show(activity.getFragmentManager(),"fm");
        }else{
            if (item != null)Toast.makeText(activity.getApplicationContext(), "Unable to find " + item, Toast.LENGTH_SHORT).show();
            activity.startActivity(new Intent(activity.getApplicationContext(), Main.class));
        }
    }
}
