package com.contag.app.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.contag.app.BuildConfig;
import com.contag.app.R;
import com.contag.app.model.User;
import com.contag.app.util.PrefUtils;
import com.contag.app.service.APIService;
import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;

/**
 * Created by tanay on 30/7/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private SpiceManager mSpiceManager = new SpiceManager(APIService.class);

    @Override
    protected void onStart() {
        super.onStart();
        mSpiceManager.start(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
    }

    /**
     * Setup {@link Toolbar}
     *
     * @param id of the toolbar in the layout file.
     */
    protected void setUpActionBar(int id) {
        final Toolbar tb = (Toolbar) findViewById(id);
        setSupportActionBar(tb);
    }

    /**
     * @return an instance of {@link SpiceManager} to execute network request.
     */
    protected SpiceManager getSpiceManager() {
        return mSpiceManager;
    }

    /**
     * show a toast of duration {@link Toast#LENGTH_SHORT}
     *
     * @param message the message of the toast.
     */
    protected void showToast(String message) {
        Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
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

    public User getCurrentUser() {
        Gson gson = new Gson();
        String str = PrefUtils.getCurrentUser();
        if (str != null) {
            return gson.fromJson(PrefUtils.getCurrentUser(), User.class);
        }
        return null;
    }

    /**
     * @return boolean denoting if user is logged in.
     */
    public boolean isUserLoggedIn() {
        return PrefUtils.getAuthToken() != null;
    }


}
