package com.contag.app.model;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tanay on 14/9/15.
 */
public class NewUser {

    @Expose
    public long number;

    @Expose
    @SerializedName(Constants.Keys.KEY_CONTAG_ID)
    public String contagID;

    public NewUser(long number, String contagID) {
        this.number = number;
        this.contagID = contagID;
    }
}
