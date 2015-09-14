package com.contag.app.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.contag.app.BuildConfig;
import com.contag.app.service.APIService;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.SpiceManager;

/**
 * An abstract {@link Fragment} subclass that is the parent of all other fragments.
 * Activities that contain this fragment must implement the
 * {@link BaseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */


public abstract class BaseFragment extends Fragment {

    private SpiceManager mSpiceManager = new SpiceManager(APIService.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
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
     *
     * @param message the message of the toast.
     */
    protected void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * prints a message if logcat if {@link com.contag.app.BuildConfig#DEBUG} is true
     *
     * @param tag     the tag associated with the log message
     * @param message the message
     */
    protected void log(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    /**
     * @return an instance of {@link Resources}
     */
    protected Resources resources() {
        return getActivity().getResources();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        /**
         * used for interaction with attached activity
         *
         * @param fragmentType
         * @param args         data to pass to other components.
         */
        void onFragmentInteraction(int fragmentType, Bundle args);
    }


}
