package com.contag.app.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.service.APIService;
import com.octo.android.robospice.SpiceManager;

/**
 * Created by archit on 17/11/15.
 */
public class ShareFieldDialog extends DialogFragment implements View.OnClickListener{

    private long fromUserID, requestID;
    private String fieldName;
    private SpiceManager mSpiceManager = new SpiceManager(APIService.class);

    public static ShareFieldDialog newInstance(Bundle requestBundle, String fieldName) {
        ShareFieldDialog mShareFieldDialog = new ShareFieldDialog();
        Bundle args = new Bundle();
        args.putBundle(Constants.Keys.KEY_DATA,requestBundle);
        args.putString(Constants.Keys.KEY_FIELD_NAME, fieldName);
        mShareFieldDialog.setArguments(args);
        return mShareFieldDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        getDialog().setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_field_request_response, container, false);
        TextView tvFieldRequestMessage = (TextView) view.findViewById(R.id.tv_request_message);
        Bundle args = getArguments();
        Bundle requestBundle = args.getBundle(Constants.Keys.KEY_DATA);
        String name = requestBundle.getString(Constants.Keys.KEY_REQUEST_FROM_USER_NAME);
        fieldName = args.getString(Constants.Keys.KEY_FIELD_NAME);
        String message = "Do you want to share " +
                convertKeyToLabel(fieldName) + " with " + name;
        tvFieldRequestMessage.setText(message);
        requestID = requestBundle.getLong(Constants.Keys.KEY_REQUEST_ID);
        fromUserID = requestBundle.getLong(Constants.Keys.KEY_REQUEST_FROM_USER_ID);
        view.findViewById(R.id.btn_field_request_allow).setOnClickListener(this);
        view.findViewById(R.id.btn_field_request_reject).setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mSpiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_field_request_reject:{
                Router.sendFieldRequestNotificationResponse(getActivity(), requestID, Constants.Types.SERVICE_REJECT_FIELD_REQUEST);
                getDialog().dismiss();
                break;
            }
            case R.id.btn_field_request_allow: {
                Router.startUserServiceForPrivacy(getActivity(), fieldName, false, fromUserID + "");
                Router.sendFieldRequestNotificationResponse(getActivity(), requestID, Constants.Types.SERVICE_ALLOW_FIELD_REQUEST);
                getDialog().dismiss();
                break;
            }
        }
    }



    private String convertKeyToLabel(String key) {
        String str = key.replace("_", " ");
        str = str.toLowerCase();
        char ch = str.charAt(0);
        str = ((char) (ch - 32)) + str.substring(1);
        int position = str.indexOf(" ");
        while (position != -1) {
            ch = str.charAt(position + 1);
            str = str.substring(0, position + 1) + (char) (ch - 32) + str.substring(position + 2);
            position = str.indexOf(" ", position + 1);
        }
        return str;
    }

}
