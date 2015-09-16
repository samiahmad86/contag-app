package com.contag.app.model;

import android.util.Log;

import io.realm.RealmObject;

public class Contact extends RealmObject {

    private long id;
    private ContagContact contactContagUser;
    private String createdOn;
    private String updatedOn;
    private String contactName;
    private String contactNumber;
    private boolean isOnContag;
    private boolean isInvited;
    private String invitedOn;
    private boolean isMuted;
    private boolean isBlocked;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setContactContagUser(ContagContact mContactContagUser) {
        this.contactContagUser = mContactContagUser;
    }

    public ContagContact getContactContagUser() {
        return this.contactContagUser;
    }

    public void setUpdatedOn(String updatedOn) {
        Log.d("Contact", "fuck");
        if (updatedOn != null) {
            this.updatedOn = updatedOn;
        } else {
            this.updatedOn = "";
        }
    }

    public String getUpdatedOn() {
        return this.updatedOn;
    }

    public void setCreatedOn(String createdOn) {
        Log.d("Contact", "fuck");
        if(createdOn != null) {
        this.createdOn = createdOn;
    } else {
            this.createdOn = "";
        }
    }

    public String getCreatedOn() {
        return this.createdOn;
    }

    public void setContactName(String name) {
        this.contactName = name;
    }

    public String getContactName() {
        return this.contactName;
    }

    public void setContactNumber(String number) {
        this.contactNumber = number;
    }

    public String getContactNumber() {
        return this.contactNumber;
    }

    public void setOnContag(boolean val) {
        this.isOnContag = val;
    }

    public boolean isOnContag() {
        return this.isOnContag;
    }

    public void setInvited(boolean val) {
        this.isInvited = val;
    }

    public boolean isInvited() {
        return this.isInvited;
    }

    public void setInvitedOn(String invitedOn) {
        if(invitedOn != null) {
        this.invitedOn = invitedOn;
    } else  {
            this.invitedOn = "a";
        }
    }

    public String getInvitedOn() {
        return this.invitedOn;
    }

    public void setMuted(boolean val) {
        this.isMuted = val;
    }

    public boolean isMuted() {
        return this.isMuted;
    }

    public void setBlocked(boolean val) {
        this.isBlocked = val;
    }

    public boolean isBlocked() {
        return this.isBlocked;
    }
}