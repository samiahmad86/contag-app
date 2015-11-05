package com.contag.app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanay on 22/9/15.
 */
public class ContactListItem {

    public ContagContag mContagContag;
    public List<Interest> interests;
    public Contact mContact;
    public List<SocialProfile> profiles ;
    public int type;
    public boolean isSharedWith ;


    public ContactListItem(Contact contact, int type) {
        this.mContact = contact;
        this.type = type;
    }

    public ContactListItem(List<Interest> interests, ContagContag contagContag, int type) {
        this.type = type;
        this.interests = interests;
        this.mContagContag = contagContag;
    }

    public ContactListItem(ArrayList<Interest> interests, ContagContag contagContag, Contact contact, ArrayList<SocialProfile> profiles, int type) {
        this.type = type;
        this.interests = interests;
        this.profiles = profiles ;
        this.mContagContag = contagContag;
        this.mContact = contact ;
    }

    public ContactListItem(ContagContag contagContag, boolean isSharedWith, int type){
        this.type = type ;
        this.isSharedWith = isSharedWith ;
        this.mContagContag = contagContag ;
    }
}
