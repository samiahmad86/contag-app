package com.contag.app;


import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Created by tanay on 17/9/15.
 */
public class ContagDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.contag.app.model");

        Entity contact = schema.addEntity("Contact");
        contact.addLongProperty("id");
        contact.addStringProperty("createdOn");
        contact.addStringProperty("updatedOn");
        contact.addStringProperty("contactName");
        contact.addStringProperty("contactNumber");
        contact.addStringProperty("invitedOn");
        contact.addBooleanProperty("isOnContag");
        contact.addStringProperty("isMuted");
        contact.addStringProperty("isBlocked");
        contact.addStringProperty("isInvited");

        Entity contagContact = schema.addEntity("ContagContag");
        contagContact.addLongProperty("id");
        contagContact.addStringProperty("createdOn");
        contagContact.addStringProperty("updatedOn");
        contagContact.addStringProperty("name");
        contagContact.addStringProperty("mobileNumber");
        contagContact.addStringProperty("registeredWith");
        contagContact.addStringProperty("contag");
        contagContact.addStringProperty("landLineNumber");
        contagContact.addStringProperty("emergencyContactNumber");
        contagContact.addBooleanProperty("isMobileVerified");
        contagContact.addStringProperty("gender");
        contagContact.addStringProperty("personalEmail");
        contagContact.addStringProperty("address");
        contagContact.addStringProperty("workEmail");
        contagContact.addStringProperty("workMobileNumber");
        contagContact.addStringProperty("workLandLineNumber");
        contagContact.addStringProperty("workAddress");
        contagContact.addStringProperty("website");
        contagContact.addStringProperty("designation");
        contagContact.addStringProperty("workFacebookPage");
        contagContact.addStringProperty("androidAppLink");
        contagContact.addStringProperty("iosAppLink");
        contagContact.addStringProperty("avatarUrl");
        contagContact.addStringProperty("bloodGroup");
        contagContact.addStringProperty("dateOfBirth");
        contagContact.addBooleanProperty("maritalStatus");
        contagContact.addStringProperty("marriageAnniversary");

        Entity interest = schema.addEntity("Interest");
        interest.addLongProperty("id");
        interest.addStringProperty("name");
    }
 }
