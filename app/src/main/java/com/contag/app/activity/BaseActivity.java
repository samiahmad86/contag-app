package com.contag.app.activity;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.contag.app.BuildConfig;
import com.contag.app.util.PrefUtils;
import com.contag.app.service.APIService;
import com.octo.android.robospice.SpiceManager;

/**
 * Created by tanay on 30/7/15.
 */
public class BaseActivity extends AppCompatActivity {

    private SpiceManager mSpiceManager = new SpiceManager(APIService.class);

    @Override
    protected void onStart() {
        mSpiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        mSpiceManager.shouldStop();
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
        Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
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
