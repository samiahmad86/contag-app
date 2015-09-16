package com.contag.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by tanay on 15/9/15.
 */
public class ContactResponse {
    @Expose
    public long id;
    @SerializedName("contact_contag_user")
    @Expose
    public ContagContactResponse contactContagUser;
    @SerializedName("created_on")
    @Expose
    public String createdOn;
    @SerializedName("updated_on")
    @Expose
    public String updatedOn;
    @SerializedName("contact_name")
    @Expose
    public String contactName;
    @SerializedName("contact_number")
    @Expose
    public String contactNumber;
    @SerializedName("is_on_contag")
    @Expose
    public boolean isOnContag;
    @SerializedName("is_invited")
    @Expose
    public boolean isInvited;
    @SerializedName("invited_on")
    @Expose
    public String invitedOn;
    @SerializedName("is_muted")
    @Expose
    public boolean isMuted;
    @SerializedName("is_blocked")
    @Expose
    public boolean isBlocked;

    public static class ContactList extends ArrayList<ContactResponse> {

    }
}
