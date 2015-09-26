package com.contag.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tanay on 27/9/15.
 */
public class SocialProfileResponse {
    @SerializedName("social_platform")
    @Expose
    public String socialPlatform;
    @SerializedName("platform_id")
    @Expose
    public String platformId;

}
