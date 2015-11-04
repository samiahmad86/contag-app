package com.contag.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by varunj on 04/11/15.
 */
public class CustomShareResponse {
    @SerializedName("field_name")
    @Expose
    public String fieldName;

    @SerializedName("user_ids")
    @Expose
    public String userIDS;

    @SerializedName("is_public")
    @Expose
    public Boolean isPublic;

    @SerializedName("is_private")
    public Boolean isPrivate ;

}
