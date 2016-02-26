package com.contag.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.contag.app.BuildConfig;
import com.contag.app.activity.BaseActivity;
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
    protected BaseActivity mBaseActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            mBaseActivity = (BaseActivity) context;
        }
        mSpiceManager.start(getActivity());
    }



    /**
     * @return the mBaseActivity
     */
    public BaseActivity getBaseActivity() {
        return mBaseActivity;
    }

    @Override
    public void onStart() {
        super.onStart();
    }



    public void onDetach() {
        super.onDetach();
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * @return an instance of {@link SpiceManager} to execute network request.
     */
    protected SpiceManager getSpiceManager() {
        return ((BaseActivity) getActivity()).getSpiceManager();
    }

    /**
     * @return boolean denoting if user is logged in.
     */
    protected boolean isUserLoggedIn() {
        return PrefUtils.getAuthToken() != null;
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

    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

}
