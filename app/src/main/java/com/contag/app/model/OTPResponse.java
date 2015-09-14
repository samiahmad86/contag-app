package com.contag.app.model;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tanay on 14/9/15.
 */
public class OTPResponse {

    @Expose
    @SerializedName(Constants.Keys.KEY_NEW_USER)
    public boolean isNewUser;

    @Expose
    @SerializedName(Constants.Keys.KEY_AUTH_TOKEN)
    public String authToken;

    @Expose
    public boolean success;
}
