package com.contag.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tanay on 25/9/15.
 */
public class UserRequestModel {

    @Expose
    public String user;

    @SerializedName("profile_category")
    @Expose
    public int profileCategory;

    public UserRequestModel(String user, int profileCategory) {
        this.user = user;
        this.profileCategory = profileCategory;
    }
}
