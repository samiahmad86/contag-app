package com.contag.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.contag.app.BuildConfig;
import com.contag.app.service.APIService;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.SpiceManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class BaseFragment extends Fragment {

    private SpiceManager mSpiceManager = new SpiceManager(APIService.class);

    public static BaseFragment newInstance() {
        BaseFragment fragment = new BaseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onStart() {
        mSpiceManager.start(getActivity());
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * @return an instance of {@link SpiceManager} to execute network request.
     */
    protected SpiceManager getSpiceManager() {
        return mSpiceManager;
    }

    /**
     * @return boolean denoting if user is logged in.
     */
    protected boolean isUserLoggedIn() {
        return PrefUtils.getKeyAccessToken() != null;
    }

    /**
     * show a toast of duration {@link Toast#LENGTH_SHORT}
     * @param message the message of the toast.
     */
    protected void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * prints a message if logcat if {@link com.contag.app.BuildConfig#DEBUG} is true
     * @param tag the tag associated with the log message
     * @param message the message
     */
    protected void log(String tag, String message) {
        if(BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }


}
