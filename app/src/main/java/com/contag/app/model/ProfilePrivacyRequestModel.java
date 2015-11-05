package com.contag.app.model;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by varunj on 05/11/15.
 */
public class ProfilePrivacyRequestModel {

    @Expose
    @SerializedName(Constants.Keys.KEY_FIELD_NAME)
    public String fieldName;

    @Expose
    @SerializedName(Constants.Keys.KEY_IS_PUBLIC)
    public Boolean isPublic;

    @Expose
    @SerializedName(Constants.Keys.KEY_USER_IDS)
    public String userIDs;


    public ProfilePrivacyRequestModel(String fieldName, Boolean isPublic, String userIDs) {

        this.fieldName = fieldName ;
        this.isPublic = isPublic ;
        this.userIDs = userIDs ;
    }
}
