package com.contag.app.model;

import android.util.Log;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by tanay on 12/10/15.
 */
public class FeedsResponse {
    @SerializedName("id")
    @Expose
    public long id;
    @SerializedName("contag_id")
    @Expose
    public String contagId;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("created_on")
    @Expose
    public String createdOn;
    @SerializedName("updated_on")
    @Expose
    public String updatedOn;
    @SerializedName("story_text")
    @Expose
    public String storyText;
    @SerializedName("story_url")
    @Expose
    public String storyUrl;
    @SerializedName("story_image")
    @Expose
    public String storyImage;
    @SerializedName("story_type")
    @Expose
    public String storyType;
    @SerializedName("from_user")
    @Expose
    public long fromUser;
    @SerializedName("avatar_url")
    @Expose
    public String avatarURL;

    public String getAvatarURL(){
        Log.d("feed", Constants.Urls.BASE_URL + avatarURL) ;
        return Constants.Urls.BASE_URL + avatarURL ;
    }

    @Expose
    @SerializedName(Constants.Keys.KEY_PROFILE_CATEGORY)
    public int profileCategory;

    public static class FeedList extends ArrayList<FeedsResponse> {

    }
}
