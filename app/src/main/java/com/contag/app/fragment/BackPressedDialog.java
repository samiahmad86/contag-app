package com.contag.app.fragment;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.activity.UserActivity;
import com.contag.app.config.Constants;

/**
 * Created by tanaytandon on 23/12/15.
 */
public class BackPressedDialog extends DialogFragment  implements View.OnClickListener{

    public static String TAG="For User Status";
    public boolean checkForStatus=false;
    public static BackPressedDialog newInstance() {
        BackPressedDialog mBackPressedDialog = new BackPressedDialog();
        return mBackPressedDialog;
    }
    public static BackPressedDialog newInstance(boolean forUserStatus) {
        BackPressedDialog mBackPressedDialog = new BackPressedDialog();
        Bundle args= new Bundle();
        args.putBoolean(TAG,forUserStatus);
        mBackPressedDialog.setArguments(args);
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


        Bundle args=getArguments();
        if(args!=null)
             checkForStatus=args.getBoolean(TAG);


        Button btnCancel = (Button) view.findViewById(R.id.btn_field_request_reject);
        Button btnOkay = (Button) view.findViewById(R.id.btn_field_request_allow);
        btnOkay.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnCancel.setText("Don't Save");
        btnOkay.setTextColor(getResources().getColor(R.color.light_blue));
        btnOkay.setText("Save");
      //  btnCancel.setTextColor(getResources().getColor(R.color.light_blue));
        return view;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_field_request_reject: {

                if(checkForStatus)
                    ((UserActivity)getActivity()).rejectUserStatus();
                else {
                   // ((BaseActivity) getActivity()).setEditMode(false);
                    ((UserActivity) getActivity()).rejectUserData();

                }


                 getDialog().cancel();
               // getActivity().onBackPressed();
                break;
            }

            case R.id.btn_field_request_allow: {
              //  getDialog().cancel();
                if(checkForStatus)
                    ((UserActivity)getActivity()).saveUserStatus();
                else {
                  //  ((BaseActivity) getActivity()).setEditMode(false);
                    ((UserActivity) getActivity()).saveUserData();

                }
                getDialog().cancel();

                break;
            }

        }
    }
}
