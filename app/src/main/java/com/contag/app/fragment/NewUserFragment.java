package com.contag.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.contag.app.BuildConfig;
import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.model.NewUser;
import com.contag.app.model.NewUserResponse;
import com.contag.app.request.NewUserRequest;
import com.contag.app.util.DeviceUtils;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.HashMap;

/**
 * Created by tanay on 30/7/15.
 */

public class NewUserFragment extends BaseFragment implements View.OnClickListener, RequestListener<NewUserResponse> {
    private static final String TAG = NewUserFragment.class.getName();
    private long phoneNumber;
    private OnFragmentInteractionListener mListener;

    /**
     * Factory method to create an instance of fragment
     *
     * @return new instance of {@link NewUserFragment}
     */
    public static NewUserFragment newInstance(long phoneNumber) {
        NewUserFragment nuf = new NewUserFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.Keys.KEY_NUMBER, phoneNumber);
        nuf.setArguments(bundle);
        return nuf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            this.phoneNumber = args.getLong(Constants.Keys.KEY_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_user, container, false);
        view.findViewById(R.id.btn_contag_id).setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_contag_id: {
                View v = getView();
                if (v != null) {
                    String contagID = ((EditText) v.findViewById(R.id.et_contag_id)).getText().toString();
                    if (contagID.length() != 8) {
                        showToast("Your CONTAG ID has to be 8 character in length.");
                        return;
                    } else {
                        for (int i = 0; i < 8; i++) {
                            if (!Character.isLetterOrDigit(contagID.charAt(i))) {
                                showToast("Contag Id can contain only numbers and alphabets");
                                return;
                            }
                        }
                    }
                    NewUser mNewUser = new NewUser(phoneNumber, contagID);
                    HashMap<String, String> hmHeaders = new HashMap<>();
                    hmHeaders.put(Constants.Headers.HEADER_APP_VERSION_ID, "" + BuildConfig.VERSION_CODE);
                    hmHeaders.put(Constants.Headers.HEADER_DEVICE_TYPE, "android");
                    hmHeaders.put(Constants.Headers.HEADER_PUSH_ID, PrefUtils.getGcmToken());
                    hmHeaders.put(Constants.Headers.HEADER_DEVICE_ID, DeviceUtils.getmDeviceId(getActivity()));
                    hmHeaders.put(Constants.Headers.HEADER_TOKEN, "Anon");
                    NewUserRequest nur = new NewUserRequest(hmHeaders, mNewUser);
                    getSpiceManager().execute(nur, this);
                }
                break;
            }
        }
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {

    }

    @Override
    public void onRequestSuccess(NewUserResponse newUserResponse) {
        if(!newUserResponse.success) {
            View v = getView();
            if(v != null) {
                v.findViewById(R.id.tv_error_msg).setVisibility(View.VISIBLE);
            }
        } else {
            PrefUtils.setAuthToken(newUserResponse.authToken);
            mListener.onFragmentInteraction(Constants.Types.FRAG_USER_DETAILS, null);
            log(TAG, PrefUtils.getAuthToken());
        }
    }
}




