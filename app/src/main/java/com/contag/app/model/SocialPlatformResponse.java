package com.contag.app.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SocialPlatformResponse {

    @SerializedName("id")
    @Expose
    public Long id;
    @SerializedName("platform_name")
    @Expose
    public String platformName;
    @SerializedName("is_api_available")
    @Expose
    public Boolean isApiAvailable;
    @SerializedName("sync_type")
    @Expose
    public String syncType;
    @SerializedName("priority")
    @Expose
    public Long priority;
    @SerializedName("is_binary")
    @Expose
    public Boolean isBinary;

    public static class List extends ArrayList<SocialPlatformResponse> {

    }
}