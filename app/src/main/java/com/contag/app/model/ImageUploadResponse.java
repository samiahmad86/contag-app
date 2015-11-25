package com.contag.app.model;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tanaytandon on 24/11/15.
 */
public class ImageUploadResponse {
    @Expose
    public boolean result;
    @SerializedName(Constants.Keys.KEY_USER_AVATAR_URL)
    @Expose
    public String avatarUrl;
}
