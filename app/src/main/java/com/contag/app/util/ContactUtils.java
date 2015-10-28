package com.contag.app.util;

import android.content.Context;
import android.util.Log;

import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.config.Router;
import com.contag.app.model.Contact;
import com.contag.app.model.ContactDao;
import com.contag.app.model.ContactListItem;
import com.contag.app.model.ContactResponse;
import com.contag.app.model.ContagContactResponse;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.DaoSession;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestDao;
import com.contag.app.model.InterestResponse;
import com.contag.app.model.SocialProfile;
import com.contag.app.model.SocialProfileDao;
import com.contag.app.model.SocialProfileResponse;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by varunj on 27/10/15.
 */
public class ContactUtils {
    private static DaoSession session ;

    public static void saveContact(Context mContext, ContactResponse.ContactList contactResponse){

        session = ((ContagApplication) mContext).getDaoSession();
        ContactDao mContactDao = session.getContactDao();

        Log.d("Condev", "Current Dao size: " + mContactDao.loadAll().size());

        for (ContactResponse response : contactResponse) {
            Contact mContact = getContact(response) ;

            if (mContact.getIsOnContag()) {

                ContagContag cc = getContagContact(response.contactContagUser, mContact);
                ContagContagDao ccDao = session.getContagContagDao();

                if (response.contactContagUser.userInterest != null && response.contactContagUser.userInterest.size() > 0) {
                    InterestDao interestDao = session.getInterestDao();

                    List<Interest> interests = getInterestList(response.contactContagUser.userInterest,
                            response.contactContagUser ,cc) ;

                    for(Interest interest: interests){
                        interestDao.insertOrReplace(interest);
                    }
                }

                if (response.contactContagUser.socialProfile != null && response.contactContagUser.socialProfile.size() > 0) {
                    SocialProfileDao spDao = session.getSocialProfileDao();
                    List<SocialProfile> socialProfiles = getSocialProfiles(response.contactContagUser.socialProfile,
                            response.contactContagUser.id ,cc);
                    for(SocialProfile socialProfile: socialProfiles) {
                        spDao.insertOrReplace(socialProfile);
                    }
                }
                ccDao.insertOrReplace(cc);
            }
            Log.d("Condev", "" + mContact.getId() + " " + response.id);
            Log.d("Condevs", "" + mContact.getContactName() + " " + response.contactName);

            try {
                mContactDao.insertOrReplace(mContact);

            } catch (Exception ex) {
                // TODO : handle this
                ex.printStackTrace();
            }
        }
    }

    public static ContactListItem getContactListItem(ContactResponse.ContactList contactResponse){
        ContactListItem listItem ;
        ArrayList<Interest> interests = new ArrayList<>() ;
        ArrayList<SocialProfile> socialProfiles = new ArrayList<>() ;
        Contact mContact = new Contact();
        ContagContag cc = new ContagContag() ;
        for (ContactResponse response : contactResponse) {
             mContact = getContact(response);

            if (mContact.getIsOnContag()) {

                cc = getContagContact(response.contactContagUser, mContact);

                if (response.contactContagUser.userInterest != null && response.contactContagUser.userInterest.size() > 0)
                    interests = getInterestList(response.contactContagUser.userInterest,
                            response.contactContagUser ,cc) ;

                if (response.contactContagUser.socialProfile != null && response.contactContagUser.socialProfile.size() > 0)
                    socialProfiles = getSocialProfiles(response.contactContagUser.socialProfile,
                            response.contactContagUser.id ,cc);
            }

        }
        listItem = new ContactListItem(interests, cc, mContact, socialProfiles, Constants.Types.ITEM_ADD_CONTAG) ;
        return listItem ;

    }

    public static void addContag(Context mContext, ContactListItem contag){
        session = ((ContagApplication) mContext.getApplicationContext()).getDaoSession();
        ContactDao mContactDao = session.getContactDao();
        ContagContagDao ccDao = session.getContagContagDao();
        InterestDao interestDao = session.getInterestDao();
        SocialProfileDao spDao = session.getSocialProfileDao();

        for(Interest interest: contag.interests){
            interestDao.insertOrReplace(interest);
        }

        for(SocialProfile socialProfile: contag.profiles) {
            spDao.insertOrReplace(socialProfile);
        }
        ccDao.insertOrReplace(contag.mContagContag);
        mContactDao.insertOrReplace(contag.mContact);

        Router.addContagUser(mContext, contag.mContagContag.getId());

        Log.d("conadd", "Added the contag user!") ;



    }

    public static Boolean isExistingContact(long userID, Context mContext){
        session = ((ContagApplication) mContext).getDaoSession();
        ContagContagDao ccDao = session.getContagContagDao();

        long count = ccDao.queryBuilder().where(ContagContagDao.Properties.Id.eq(userID)).count() ;

        return (count == 0 && userID != PrefUtils.getCurrentUserID()) ;


    }

    private static ContagContag getContagContact(ContagContactResponse ccResponse, Contact mContact){

        ContagContag cc = new ContagContag(ccResponse.id);
        cc.setContact(mContact);
        cc.setCreatedOn(ccResponse.createdOn);
        cc.setUpdatedOn(ccResponse.updatedOn);
        if(ccResponse.name != null) {
            cc.setName(ccResponse.name);
        } else {
            cc.setName("Contag User");
        }
        cc.setRegisteredWith(ccResponse.registeredWith);
        cc.setMobileNumber(ccResponse.mobileNumber);
        cc.setContag(ccResponse.contag);
        cc.setLandLineNumber(ccResponse.landlineNumber);
        cc.setEmergencyContactNumber(ccResponse.emergencyContactNumber);
        cc.setIsMobileVerified(ccResponse.isMobileVerified);
        cc.setGender(ccResponse.gender);
        cc.setAddress(ccResponse.address);
        cc.setWorkEmail(ccResponse.workEmail);
        cc.setWorkMobileNumber(ccResponse.workMobileNumber);
        cc.setWorkLandLineNumber(ccResponse.workLandlineNumber);
        cc.setWebsite(ccResponse.website);
        cc.setDesignation(ccResponse.designation);
        cc.setWorkFacebookPage(ccResponse.workFacebookPage);
        cc.setAndroidAppLink(ccResponse.androidAppLink);
        cc.setIosAppLink(ccResponse.iosAppLink);
        cc.setAvatarUrl(ccResponse.avatarUrl);
        cc.setBloodGroup(ccResponse.bloodGroup);
        cc.setDateOfBirth(ccResponse.dateOfBirth);
        cc.setIsMobileVerified(ccResponse.isMobileVerified);
        cc.setMaritalStatus(ccResponse.maritalStatus);
        cc.setMarriageAnniversary(ccResponse.marriageAnniversary);
        cc.setPersonalEmail(ccResponse.personalEmail);
        cc.setWorkAddress(ccResponse.workAddress);

        return cc ;

    }

    private static Contact getContact(ContactResponse  response){
        return new Contact(response.id, response.createdOn, response.updatedOn, response.contactName,
                response.contactNumber, response.invitedOn, response.isOnContag, response.isMuted, response.isBlocked,
                response.isBlocked);
    }

    private static ArrayList<Interest> getInterestList(ArrayList<InterestResponse> interests,
                                                      ContagContactResponse ccResponse, ContagContag cc){
        ArrayList<Interest> mInterest = new ArrayList<>() ;
        for (InterestResponse ir : interests) {
            Interest interest = new Interest(ir.id);
            interest.setName(ir.name);
            interest.setContagUserId(ccResponse.id);
            interest.setContagContag(cc);
            mInterest.add(interest) ;
        }
        return mInterest ;
    }

    private static ArrayList<SocialProfile> getSocialProfiles(ArrayList<SocialProfileResponse> profiles, long userID , ContagContag cc){
        ArrayList<SocialProfile> mProfiles = new ArrayList<>() ;
        for (SocialProfileResponse spr : profiles) {
            SocialProfile socialProfile = new SocialProfile();
            socialProfile.setPlatform_id(spr.platformId);
            socialProfile.setSocial_platform(spr.socialPlatform);
            socialProfile.setContagContag(cc);
            socialProfile.setContagUserId(userID);

        }
        return mProfiles ;

    }

}
