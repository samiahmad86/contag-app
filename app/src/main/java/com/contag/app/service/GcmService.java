package com.contag.app.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.contag.app.R;
import com.contag.app.activity.NotificationsActivity;
import com.contag.app.activity.UserActivity;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.util.AtomicIntegerUtils;
import com.contag.app.util.PrefUtils;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by tanay on 21/8/15.
 */
public class GcmService extends GcmListenerService {
    final private String TAG = "CONTAG-GCM";

    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "notification got me " + PrefUtils.getCurrentUserID());
        int notificationCount = PrefUtils.getNewNotificationCount();
        if (PrefUtils.getAuthToken() != null) {
            String notification_type = data.getString("notification_type");

            Intent intent;
            Log.d("GCMPUSH", data.toString());

            switch (notification_type) {
                case "update_profile": {
                    Log.d("newprofile", "GCM push received");
                    long userID = Long.parseLong(data.getString("user_id"));
                    Router.startServiceToGetUserByUserID(this, userID, true, null);
                    intent = null;
                    break;
                }
                case "introduction": {
                }
                case "request_granted": {
                    Log.d("GCMPUSH", "GCM push received for introductin");
                    long userID = Long.parseLong(data.getString("user_id"));
                    Router.startServiceToGetUserByUserID(this, userID, true, data);
                    intent = null;
                    Log.d("GCMPUSH", "User id for profile is: " + userID);
                    break;
                }
                default: {

                    if (notification_type.equals("add_request_completed") || notification_type.equals("birthday") ||
                            notification_type.equals("anniversary")) {
                        Log.d("GCMPUSH", "Push received for:  " + notification_type);
                        intent = new Intent(this, UserActivity.class);
                        intent.putExtra(Constants.Keys.KEY_USER_ID, data.getString("user_id"));
                        Log.d("GCMPUSH", "Profile ID is: " + data.getString("user_id"));
                    } else {
                        intent = new Intent(this, NotificationsActivity.class);
                        notificationCount += 1;
                    }

                    break;
                }
            }

            if (intent != null) {
                PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

                // build notification
                // the addAction re-use the same intent to keep the example short
                NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this);

                Uri notifTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                Log.d(TAG, "Building notification");
                notifBuilder.setContentTitle(data.getString("heading"))
                        .setContentText(data.getString("text"))
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setSound(notifTone)
                        .setSmallIcon(R.drawable.contag_logo);


                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(AtomicIntegerUtils.getmNotificationID(), notifBuilder.build());
                PrefUtils.setNewNotificationCount(notificationCount);
                Intent notificationCountUpdatedIntent = new Intent(getResources().getString(R.string.intent_filter_notification_count));
                notificationCountUpdatedIntent.putExtra(Constants.Keys.KEY_NOTIFICATION_COUNT, notificationCount);
                LocalBroadcastManager.getInstance(this).sendBroadcast(notificationCountUpdatedIntent);

            }

        }
    }
}
