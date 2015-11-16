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
    public Long id;
    @SerializedName("avatar_url")
    @Expose
    public String avatarUrl;
    @SerializedName("requester_name")
    @Expose
    public String requesterName;
    @SerializedName("field_category")
    @Expose
    public String fieldCategory;
    @SerializedName("created_on")
    @Expose
    public String createdOn;
    @SerializedName("updated_on")
    @Expose
    public String updatedOn;
    @SerializedName("notification_type")
    @Expose
    public String notificationType;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("seen_at")
    @Expose
    public Object seenAt;
    @SerializedName("is_shown")
    @Expose
    public Boolean isShown;
    @SerializedName("user")
    @Expose
    public Long user;
    @SerializedName("from_user")
    @Expose
    public Long fromUser;
    @SerializedName("request")
    @Expose
    public Long request;
    @SerializedName("field_name")
    @Expose
    public String fieldName;

    public static class NotificationList extends ArrayList<NotificationsResponse> {

    }
}
