package com.contag.app.model;

import io.realm.RealmObject;

public class Contact extends RealmObject {

    public long id;
    public ContagContact contactContagUser;
    public String createdOn;
    public String updatedOn;
    public String contactName;
    public String contactNumber;
    public boolean isOnContag;
    public boolean isInvited;
    public String invitedOn;
    public boolean isMuted;
    public boolean isBlocked;

}