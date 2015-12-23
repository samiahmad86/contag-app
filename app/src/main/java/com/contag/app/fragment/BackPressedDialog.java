package com.contag.app.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.config.Constants;

/**
 * Created by tanaytandon on 23/12/15.
 */
public class BackPressedDialog extends DialogFragment  implements View.OnClickListener{

    public static BackPressedDialog newInstance() {
        BackPressedDialog mBackPressedDialog = new BackPressedDialog();
        return mBackPressedDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        setCancelable(false);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_field_request_response, container, false);
        TextView tvFieldRequestMessage = (TextView) view.findViewById(R.id.tv_request_message);
        tvFieldRequestMessage.setText("Are you sure you want to exit before saving details?");
        Button btnOkay = (Button) view.findViewById(R.id.btn_field_request_allow);
        btnOkay.setOnClickListener(this);
        btnOkay.setText("Okay");
        Button btnCancel = (Button) view.findViewById(R.id.btn_field_request_reject);
        btnCancel.setOnClickListener(this);
        btnCancel.setText("Cancel");
        return view;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_field_request_allow: {
                ((BaseActivity) getActivity()).setEditMode(false);
                getActivity().onBackPressed();
                break;
            }

            case R.id.btn_field_request_reject: {
                getDialog().cancel();
                break;
            }

        }
    }
}
