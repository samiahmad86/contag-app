package com.contag.app.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.contag.app.BuildConfig;
import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.model.Login;
import com.contag.app.model.OTP;
import com.contag.app.model.OTPResponse;
import com.contag.app.model.Response;
import com.contag.app.request.LoginRequest;
import com.contag.app.request.OTPRequest;
import com.contag.app.util.DeviceUtils;
import com.contag.app.util.PrefUtils;
import com.contag.app.util.RegexUtils;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.HashMap;

/**
 * A simple {@link BaseFragment} subclass for login.
 * Activities that contain this fragment must implement the
 * {@link BaseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class LoginFragment extends BaseFragment implements View.OnClickListener,
        RequestListener<Response>, TextView.OnEditorActionListener {

    private OnFragmentInteractionListener mListener;
    public final static String TAG = LoginFragment.class.getName();
    private int mFragmentType;
    private long phoneNum;
    private Button btnLogin;

    public static LoginFragment newInstance(int code, long number) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.Keys.KEY_FRAGMENT_TYPE, code);
        if (code == Constants.Types.FRAG_OTP) {
            args.putLong(Constants.Keys.KEY_NUMBER, number);
        }
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
            if (mFragmentType == Constants.Types.FRAG_OTP) {
                phoneNum = args.getLong(Constants.Keys.KEY_NUMBER);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        btnLogin = (Button) view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        if (mFragmentType == Constants.Types.FRAG_OTP) {
            ((EditText) view.findViewById(R.id.et_phone_num)).setHint
                    (resources().getString(R.string.enter_otp));
            View btnResendOtp = view.findViewById(R.id.btn_resend_otp);
            btnResendOtp.setVisibility(View.VISIBLE);
            btnResendOtp.setOnClickListener(this);
            view.findViewById(R.id.tv_otp_msg).setVisibility(View.VISIBLE);
            btnLogin.setText("SUBMIT");
        } else {
            TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();
            ((EditText) view.findViewById(R.id.et_phone_num)).setText(mPhoneNumber);
        }
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mFragmentType == Constants.Types.FRAG_OTP) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(brSms,
                    new IntentFilter(getActivity().getResources().getString(R.string.intent_filter_sms)));
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(brUser,
                    new IntentFilter(getActivity().getResources().getString(R.string.intent_filter_user_received)));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mFragmentType == Constants.Types.FRAG_OTP) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brSms);
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brUser);
        }
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

                        case Constants.Types.FRAG_LOGIN: {
                            String phNum = ((EditText) getView().findViewById(R.id.et_phone_num)).getText().toString();
                            if (!RegexUtils.isPhoneNumber(phNum)) {
                                showToast("Enter a valid phone number.");
                                return;
                            }
                            getActivity().findViewById(R.id.pb_login).setVisibility(View.VISIBLE);
                            phoneNum = Long.parseLong(phNum);
                            Login mLogin = new Login(phoneNum);
                            LoginRequest lr = new LoginRequest(mLogin);
                            getSpiceManager().execute(lr, this);
                            break;
                        }

                        case Constants.Types.FRAG_OTP: {
                            String otp = ((EditText) getView().findViewById(R.id.et_phone_num)).getText().toString();
                            int num;
                            try {
                                num = Integer.parseInt(otp);
                            } catch (NumberFormatException ne) {
                                showToast("Enter valid otp");
                                return;
                            }
                            HashMap<String, String> hmHeaders = new HashMap<>();
                            hmHeaders.put(Constants.Headers.HEADER_APP_VERSION_ID, "" + BuildConfig.VERSION_CODE);
                            hmHeaders.put(Constants.Headers.HEADER_DEVICE_TYPE, "android");
                            hmHeaders.put(Constants.Headers.HEADER_PUSH_ID, PrefUtils.getGcmToken());
                            hmHeaders.put(Constants.Headers.HEADER_DEVICE_ID, DeviceUtils.getmDeviceId(getActivity()));
                            OTP objOTP = new OTP(phoneNum, num);
                            OTPRequest or = new OTPRequest(objOTP, hmHeaders);
                            getSpiceManager().execute(or, new RequestListener<OTPResponse>() {
                                @Override
                                public void onRequestFailure(SpiceException spiceException) {
                                    log(TAG, spiceException.getMessage());
                                }

                                @Override
                                public void onRequestSuccess(OTPResponse otpResponse) {
                                    if (otpResponse.success) {
                                        if (otpResponse.isNewUser) {
                                            Router.startNewUserActivity(getActivity(), TAG, phoneNum);
                                            getActivity().finish();
                                        } else {
                                            PrefUtils.setAuthToken(otpResponse.authToken);
                                            Router.startUserService(getActivity(),
                                                    Constants.Types.REQUEST_GET, null);
                                        }
                                    } else {
                                        showToast("OTP is incorrect");
                                    }
                                }
                            });
                            break;
                        }
                    }
                }
            }

            case R.id.btn_resend_otp: {
                Login mLogin = new Login(phoneNum);
                LoginRequest lr = new LoginRequest(mLogin);
                getSpiceManager().execute(lr, this);
                break;
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
    public void onRequestSuccess(Response response) {
        if (response.result && mFragmentType == Constants.Types.FRAG_LOGIN) {
            Bundle bundle = new Bundle();
            bundle.putLong(Constants.Keys.KEY_NUMBER, phoneNum);
            mListener.onFragmentInteraction(Constants.Types.FRAG_OTP, bundle);
        }
    }

    private BroadcastReceiver brSms = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String otp = intent.getStringExtra(Constants.Keys.KEY_OTP);
            View view = getView();
            if (otp != null && view != null) {
                ((EditText) view.findViewById(R.id.et_phone_num)).setText(otp);
            }
        }
    };

    private BroadcastReceiver brUser = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Router.startHomeActivity(getActivity(), TAG);
            getActivity().finish();
        }
    };

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onClick(btnLogin);
        }
        return false;
    }
}
