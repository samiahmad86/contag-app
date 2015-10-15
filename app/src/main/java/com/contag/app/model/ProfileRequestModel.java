package com.contag.app.model;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tanay on 2/10/15.
 */
public class ProfileRequestModel {

    @Expose
    @SerializedName(Constants.Keys.KEY_PROFILE_REQUEST_FOR_USER)
    public long id;

    @Expose
    @SerializedName(Constants.Keys.KEY_PROFILE_REQUEST_TYPE)
    public String type;

    @Expose
    @SerializedName(Constants.Keys.KEY_FIELD_TYPE)
    public String fieldType;


    public ProfileRequestModel(long id, String type, String fieldType) {
        this.id = id;
        this.type = type;
        this.fieldType = fieldType;
    }
}
