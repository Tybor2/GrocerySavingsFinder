package com.example.android.grocerysavingsfinder;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class BarcodeNumFragment extends DialogFragment {
    public static final String ARG_CODE = "code";

    public static final String EXTRA_CODE =
            "com.example.android.grocerysavingsfinder.code";

    private EditText mEditText;

    public static BarcodeNumFragment newInstance(int code) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_CODE, code);

        BarcodeNumFragment fragment = new BarcodeNumFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_barcode, null);
        mEditText = (EditText) v.findViewById(R.id.enter_code);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Enter Barcode Number")
                .setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String code = mEditText.getText().toString();
                                sendResult(Activity.RESULT_OK, code);
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode, String code) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_CODE, code);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }


}
