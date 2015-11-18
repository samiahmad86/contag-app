package com.contag.app.model;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tanaytandon on 18/11/15.
 */
public class FieldRequestNotificationResponse {

    @SerializedName(Constants.Keys.KEY_NOTIF_STATUS)
    @Expose
    public String status;

    @SerializedName(Constants.Keys.KEY_REQUEST_ID)
    @Expose
    public long requestID;

    public FieldRequestNotificationResponse(String status, long requestID) {
        this.requestID = requestID;
        this.status = status;
    }

}
