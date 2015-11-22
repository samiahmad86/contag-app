package com.contag.app.model;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by varunj on 22/11/15.
 */
public class ContagIntroductionRequestModel {


    @Expose
    @SerializedName(Constants.Keys.KEY_INTRODUCED_USER)
    public long introducedUser;

    @Expose
    @SerializedName(Constants.Keys.KEY_INTRODUCED_TO_USERS)
    public String introducedToUsers;

    @Expose
    @SerializedName(Constants.Keys.KEY_INTRODUCTION_MESSAGE)
    public String message ;


    public ContagIntroductionRequestModel(long introducedUser, String introducedToUsers, String message) {
        this.introducedUser = introducedUser;
        this.introducedToUsers = introducedToUsers;
        this.message = message;
    }
}
