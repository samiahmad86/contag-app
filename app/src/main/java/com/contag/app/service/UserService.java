package com.contag.app.service;

import android.app.Service;
import android.content.CursorLoader;
import android.content.Intent;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.model.User;
import com.contag.app.request.UserRequest;
import com.contag.app.util.PrefUtils;
import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.json.JSONArray;
import org.json.JSONException;

public class UserService extends Service implements RequestListener<User> {
    private SpiceManager mSpiceManager = new SpiceManager(APIService.class);
    private static final String TAG = UserService.class.getName();


    public UserService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int type = intent.getIntExtra(Constants.Keys.KEY_REQUEST_TYPE, 0);
            switch (type) {
                case Constants.Types.REQUEST_GET: {
                    UserRequest mUserRequest = new UserRequest(type);
                    mSpiceManager.execute(mUserRequest, this);
                    break;
                }
                case Constants.Types.REQUEST_PUT: {
                    String userArrayStr = intent.getStringExtra(Constants.Keys.KEY_USER_ARRAY);
                    UserRequest mUserRequest = new UserRequest(type, userArrayStr);
                    mSpiceManager.execute(mUserRequest, this);
                    break;
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSpiceManager.start(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
    }


    @Override
    public void onRequestFailure(SpiceException spiceException) {

    }

    @Override
    public void onRequestSuccess(User user) {
        Gson gson = new Gson();
        PrefUtils.setCurrentUser(gson.toJson(user).toString());
        PrefUtils.setCurrentUserID(user.id);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent
                (getResources().getString(R.string.intent_filter_user_received)));
        this.stopSelf();
    }
}
