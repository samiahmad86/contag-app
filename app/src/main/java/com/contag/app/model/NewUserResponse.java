package com.contag.app.model;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tanay on 14/9/15.
 */
public class NewUserResponse {
    @Expose
    public boolean success;

    @Expose
    @SerializedName(Constants.Keys.KEY_AUTH_TOKEN)
    public String authToken;

}
