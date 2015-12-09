package com.contag.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.contag.app.R;
import com.contag.app.config.Router;
import com.contag.app.util.PrefUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;


/**
 * An {@link IntentService} subclass for getting the GCM id and storing it
 * in Shared Preferences using {@link PrefUtils#setGcmToken(String)}
 */
public class GcmRegisterIntentService extends IntentService {

    private static final String TAG = GcmRegisterIntentService.class.getName();

    public GcmRegisterIntentService() {
        super("GcmRegisterIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                InstanceID id = InstanceID.getInstance(this);
                String token = id.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.d(TAG, "fucking token " + token);
                PrefUtils.setGcmToken(token);
                Router.startLoginActivity(this, TAG, Intent.FLAG_ACTIVITY_NEW_TASK);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

}
