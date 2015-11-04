package com.contag.app.model;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by tanay on 12/10/15.
 */
public class NotificationsResponse {

    @SerializedName("id")
    @Expose
    public long id;
//
//    @SerializedName("name")
//    @Expose
//    public String name;

    @SerializedName("user_id")
    @Expose
    public String userId;

    @SerializedName("notification_type")
    @Expose
    public String notificationType;

    @SerializedName("request_type")
    @Expose
    public String requestType ;

    @SerializedName("text")
    @Expose
    public String text;

    @SerializedName("avatar_url")
    @Expose
    public String avatarURL;

    @SerializedName("object_id")
    @Expose
    public String objectId;

    @SerializedName("seen_at")
    @Expose
    public String seenAt;


    @SerializedName("created_on")
    @Expose
    public String createdOn;
    @SerializedName("updated_on")
    @Expose
    public String updatedOn;

    public String getAvatarURL(){
        return Constants.Urls.BASE_URL + avatarURL ;
    }

    public static class NotificationList extends ArrayList<NotificationsResponse> {

    }
}
