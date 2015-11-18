package com.contag.app.model;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tanay on 27/9/15.
 */
public class SocialProfileResponse {
    @Expose
    public long id;
    @SerializedName("social_platform")
    @Expose
    public String socialPlatform;
    @SerializedName("platform_id")
    @Expose
    public String platformId;
    @SerializedName(Constants.Keys.KEY_USER_PLATFORM_USERNAME)
    @Expose
    public String platformUsername;
}
