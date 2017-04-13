package com.lynn9388.irss.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.lynn9388.irss.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFeedDialogFragment extends DialogFragment {
    private AddFeedDialogListener listener;

    public interface AddFeedDialogListener {
        public void onAddFeedDialogPositiveClick(DialogFragment dialogFragment);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (AddFeedDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AddFeedDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder .setTitle(getString(R.string.text_add_feed_title))
                .setView(inflater.inflate(R.layout.fragment_add_feed_dialog, null))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onAddFeedDialogPositiveClick(AddFeedDialogFragment.this);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}
