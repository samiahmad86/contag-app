package com.contag.app.model;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by varunj on 27/10/15.
 */
public class AddContactRequest {

    @SerializedName(Constants.Keys.KEY_USER_ID)
    @Expose
    public long userID;

    public AddContactRequest(long userID) {
        this.userID = userID;
    }
}
