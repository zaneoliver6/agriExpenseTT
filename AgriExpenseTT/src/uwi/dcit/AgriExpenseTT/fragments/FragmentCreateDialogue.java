package uwi.dcit.AgriExpenseTT.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import uwi.dcit.AgriExpenseTT.R;

/**
 * Created by Steffan on 23/01/2015.
 */
public class FragmentCreateDialogue extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialogue_create,null))
                .setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentCreateDialogue.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
