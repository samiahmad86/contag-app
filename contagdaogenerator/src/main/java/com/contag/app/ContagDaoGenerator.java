package com.contag.app;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class ContagDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.contag.app.model");

        Entity contact = schema.addEntity("Contact");
        contact.addIdProperty();
        contact.addStringProperty("createdOn");
        contact.addStringProperty("updatedOn");
        contact.addStringProperty("contactName");
        contact.addStringProperty("contactNumber");
        contact.addStringProperty("invitedOn");
        contact.addBooleanProperty("isOnContag");
        contact.addBooleanProperty("isMuted");
        contact.addBooleanProperty("isBlocked");
        contact.addBooleanProperty("isInvited");

        Entity contagContact = schema.addEntity("ContagContag");
        contagContact.addIdProperty();
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
        contagContact.addStringProperty("maritalStatus");
        contagContact.addStringProperty("marriageAnniversary");
        contagContact.addStringProperty("status_update");

        Property cuntagToContact = contagContact.addLongProperty("contactId").getProperty();
        contagContact.addToOne(contact, cuntagToContact);

        Entity interest = schema.addEntity("Interest");
        interest.addIdProperty();
        interest.addStringProperty("name");

        Property interestToCuntag = interest.addLongProperty("contagUserId").getProperty();
        interest.addToOne(contagContact, interestToCuntag);

        Entity socialProfile = schema.addEntity("SocialProfile");
        socialProfile.addIdProperty();
        socialProfile.addStringProperty("social_platform");
        socialProfile.addStringProperty("platform_id");

        Property socialProfileToCuntag = socialProfile.addLongProperty("contagUserId").getProperty();
        socialProfile.addToOne(contagContact, socialProfileToCuntag);


        Entity socialPlatform = schema.addEntity("SocialPlatform");
        socialPlatform.addIdProperty();
        socialPlatform.addStringProperty("platformName");
        socialPlatform.addStringProperty("platformBaseUrl");

        new DaoGenerator().generateAll(schema, "../app/src/main/java");
    }
 }
