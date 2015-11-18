package com.contag.app.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.contag.app.R;
import com.contag.app.activity.NotificationsActivity;
import com.contag.app.activity.UserActivity;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by tanay on 21/8/15.
 */
public class GcmService extends GcmListenerService {
    final private String TAG = "CONTAG-GCM";

    public void onMessageReceived(String from, Bundle data) {

        Log.d(TAG, "GCM received");
        String notification_type = data.getString("notification_type");
        Log.d(TAG, notification_type);
        Intent intent;

        switch (notification_type) {
            case "new_user": {
                //Start contact service to fetch records from the server and update the same on local
                Router.startContactService(this, false);
                intent = null;
                break;
            }
            case "request_granted": {
                intent = new Intent(this, UserActivity.class);
                intent.putExtra(Constants.Keys.KEY_USER_ID, data.getString("object_id"));
                break;
            }
            default: {
                // Takes care of profile request add/share cases
                intent = new Intent(this, NotificationsActivity.class);
                break;
            }
        }

        if (intent != null) {
            PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

            // build notification
            // the addAction re-use the same intent to keep the example short
            NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(GcmService.this);

            Uri notifTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Log.d(TAG, "Building notification");
            notifBuilder.setContentTitle(data.getString("heading"))
                    .setContentText(data.getString("text"))
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setSound(notifTone)
                    .setSmallIcon(R.drawable.contag_logo);

            Log.d(TAG, data.getString("text"));

            Log.d(TAG, "Notification manager got");
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            Log.d(TAG, "notification sent");
            notificationManager.notify(0, notifBuilder.build());
        }

    }

}
