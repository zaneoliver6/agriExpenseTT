package uwi.dcit.AgriExpenseTT.helpers;


import android.app.Activity;
import android.widget.Toast;

//import com.cocosw.undobar.UndoBarController;
//import com.cocosw.undobar.UndoBarController.UndoBar;

public class NotifyHelper {

    public static void notify(final Activity activity, String message){
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();

//        new UndoBar(activity).message(message).listener(new UndoBarController.UndoListener() {
//            @Override
//            public void onUndo(@Nullable Parcelable parcelable) {
//                Toast.makeText(activity, "Undo operation not available for this message", Toast.LENGTH_SHORT).show();
//            }
//        }).show();
    }
}

