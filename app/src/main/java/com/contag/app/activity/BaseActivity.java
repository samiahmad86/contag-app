package com.contag.app.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.contag.app.BuildConfig;
import com.contag.app.R;
import com.contag.app.config.ContagApplication;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.DaoSession;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestDao;
import com.contag.app.model.SocialPlatform;
import com.contag.app.model.SocialPlatformDao;
import com.contag.app.model.SocialProfile;
import com.contag.app.model.SocialProfileDao;
import com.contag.app.model.User;
import com.contag.app.util.PrefUtils;
import com.contag.app.service.APIService;
import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;

import java.util.ArrayList;

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

    public ContagContag getCurrentUser() {
        return getUser(PrefUtils.getCurrentUserID());
    }

    public ArrayList<Interest> getCurrentUserInterests() {
        return getUserInterests(PrefUtils.getCurrentUserID());
    }

    public ArrayList<SocialProfile> getCurrentUserSocialProfiles() {
        return getSocialProfiles(PrefUtils.getCurrentUserID());
    }

    public ContagContag getUser(long id) {
        DaoSession session = ((ContagApplication) getApplicationContext()).getDaoSession();
        ContagContagDao ccDao = session.getContagContagDao();
        return ccDao.queryBuilder().where(ContagContagDao.Properties.Id.eq(id)).list().get(0);
    }

    public ArrayList<Interest> getUserInterests(long id) {
        DaoSession session = ((ContagApplication) getApplicationContext()).getDaoSession();
        InterestDao interestDao = session.getInterestDao();
       return (ArrayList<Interest>) interestDao.queryBuilder().
                where(InterestDao.Properties.ContagUserId.eq(id)).list();
    }

    public ArrayList<SocialProfile> getSocialProfiles(long id) {
        DaoSession session = ((ContagApplication) getApplicationContext()).getDaoSession();
        SocialProfileDao socialProfileDao = session.getSocialProfileDao();
        return (ArrayList<SocialProfile>) socialProfileDao.queryBuilder().
                where(SocialProfileDao.Properties.ContagUserId.eq(id)).list();
    }

    public ArrayList<SocialPlatform> getSocialPlatforms() {
        DaoSession session = ((ContagApplication) getApplicationContext()).getDaoSession();
        SocialPlatformDao socialPlatformDao = session.getSocialPlatformDao();
        return (ArrayList<SocialPlatform>) socialPlatformDao.loadAll();
    }

    /**
     * @return boolean denoting if user is logged in.
     */
    public boolean isUserLoggedIn() {
        return PrefUtils.getAuthToken() != null;
    }


}
