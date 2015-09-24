package com.contag.app.model;

import java.util.List;

/**
 * Created by tanay on 22/9/15.
 */
public class ContactListItem {

    public ContagContag mContagContag;
    public List<Interest> interests;
    public Contact mContact;
    public int type;

    public ContactListItem(Contact contact, int type) {
        this.mContact = contact;
        this.type = type;
    }

    public ContactListItem(List<Interest> interests, ContagContag contagContag, int type) {
        this.type = type;
        this.interests = interests;
        this.mContagContag = contagContag;
    }
}
