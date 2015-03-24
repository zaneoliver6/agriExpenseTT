package uwi.dcit.AgriExpenseTT.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import uwi.dcit.AgriExpenseTT.NewCycle;
import uwi.dcit.AgriExpenseTT.NewPurchase;
import uwi.dcit.AgriExpenseTT.R;

import static android.app.AlertDialog.Builder;

public class FragmentCreateDialogue extends DialogFragment {

    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {

        Builder builder = new Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogue_create, null);

        view.findViewById(R.id.btn_dialogue_createCycle).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NewCycle.class));
                dismiss();
            }
        });
        view.findViewById(R.id.btn_dialogue_createPurchase).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NewPurchase.class));
                dismiss();
            }
        });


        builder
            .setView(view)
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FragmentCreateDialogue.this.getDialog().cancel();
                }
            });

        return builder.create();
    }



}
