package com.contag.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.model.Login;
import com.contag.app.model.LoginResponse;
import com.contag.app.request.LoginRequest;
import com.contag.app.util.DeviceUtils;
import com.contag.app.util.RegexUtils;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * A simple {@link BaseFragment} subclass for login.
 * Activities that contain this fragment must implement the
 * {@link BaseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class LoginFragment extends BaseFragment implements View.OnClickListener, RequestListener<LoginResponse> {

    private OnFragmentInteractionListener mListener;
    private final static String TAG = LoginFragment.class.getName();
    private int mFragmentType;
    private boolean isNewUser;

    public static LoginFragment newInstance(int code, boolean isNew) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.Keys.KEY_FRAGMENT_TYPE, code);
        args.putBoolean(Constants.Keys.KEY_NEW_USER, isNew);
        fragment.setArguments(args);
        return fragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mFragmentType = args.getInt(Constants.Keys.KEY_FRAGMENT_TYPE);
            if (mFragmentType == Constants.Values.FRAG_OTP) {
                isNewUser = args.getBoolean(Constants.Keys.KEY_NEW_USER, false);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Button btnLogin = (Button) view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        switch (mFragmentType) {
            case Constants.Values.FRAG_OTP: {
                ((EditText) view.findViewById(R.id.et_phone_num)).setHint
                        (resources().getString(R.string.enter_otp));
                view.findViewById(R.id.tv_otp_msg).setVisibility(View.VISIBLE);
                btnLogin.setText("SUBMIT");
                break;
            }
        }
        return view;
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {

            case R.id.btn_login: {

                if (getView() != null) {

                    switch (mFragmentType) {

                        case Constants.Values.FRAG_LOGIN: {
                            String phNum = ((EditText) getView().findViewById(R.id.et_phone_num)).getText().toString();
                            if (!RegexUtils.isPhoneNumber(phNum)) {
                                showToast("Enter a valid phone number.");
                                return;
                            }
                            getActivity().findViewById(R.id.pb_login).setVisibility(View.VISIBLE);
                            LoginRequest lr = new LoginRequest(DeviceUtils.getmDeviceId(getActivity()), new Login(phNum));
                            getSpiceManager().execute(lr, this);
                            break;
                        }

                        case Constants.Values.FRAG_OTP: {
                            String otp = ((EditText) getView().findViewById(R.id.et_phone_num)).getText().toString();
                            try {
                                Double.parseDouble(otp);
                            } catch (NumberFormatException ne) {
                                showToast("Enter valid otp");
                                return;
                            }
                            // TODO: check if the otp matches with the response
                            if (isNewUser) {
                                Router.startEditUserActivity(getActivity(), TAG);
                            } else {
                                Router.startHomeActivity(getActivity(), TAG);
                            }
                            break;
                        }
                    }
                }
            }
        }

    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        if (getView() != null) {
            getView().findViewById(R.id.pb_login).setVisibility(View.GONE);
        }
        log(TAG, spiceException.getMessage());
    }

    @Override
    public void onRequestSuccess(LoginResponse loginResponse) {
        // TODO: save login response
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.Keys.KEY_NEW_USER, loginResponse.isNewUser);
        mListener.onFragmentInteraction(Constants.Values.FRAG_OTP, bundle);
    }
}
