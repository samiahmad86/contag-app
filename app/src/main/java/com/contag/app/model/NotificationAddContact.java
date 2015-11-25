package com.contag.app.model;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by varunj on 25/11/15.
 */
public class NotificationAddContact {

        @SerializedName(Constants.Keys.KEY_NOTIFICATION_ID)
        @Expose
        public long notificationID;

        public NotificationAddContact(long notificationID) {
            this.notificationID = notificationID;
        }


}
