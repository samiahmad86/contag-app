package com.contag.app.model;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SocialRequestModel {
    @SerializedName(Constants.Keys.KEY_SOCIAL_PLATFORM_ID)
    @Expose
    public long socialPlatformId;
    @SerializedName(Constants.Keys.KEY_PLATFORM_ID)
    @Expose
    public String platformId;
    @SerializedName(Constants.Keys.KEY_PLATFORM_TOKEN)
    @Expose
    public String platformToken;
    @SerializedName(Constants.Keys.KEY_PLATFORM_SECRET)
    @Expose
    public String platformSecret;
    @SerializedName(Constants.Keys.KEY_PLATFORM_PERMISSION)
    @Expose
    public String platformPermissions;
    @SerializedName(Constants.Keys.KEY_PLATFORM_EMAIL_ID)
    @Expose
    public String platformEmail;
    @SerializedName(Constants.Keys.KEY_USER_FIELD_VISIBILITY)
    @Expose
    public String visibility;

    public SocialRequestModel(long socialPlatformId, String platformId, String platformToken, String platformPermissions,
                              String platformSecret, String platformEmail, String visibility) {
        this.socialPlatformId = socialPlatformId;
        this.visibility = visibility;
        this.platformId = platformId;
        this.platformToken = platformToken;
        this.platformEmail = platformEmail;
        this.platformPermissions = platformPermissions;
        this.platformSecret = platformSecret;
    }

}
