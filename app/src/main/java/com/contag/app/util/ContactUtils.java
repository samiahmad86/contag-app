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


public class ContactUtils {
    private static DaoSession session;

    private static DaoSession getSession(Context context) {
        if(session == null) {
            session = ((ContagApplication) context).getDaoSession();
        }
        return session;
    }

    public static void saveContact(Context mContext, ContactResponse.ContactList contactResponse) {

        ContactDao mContactDao = getSession(mContext).getContactDao();

        Log.d("Condev", "Current Dao size: " + mContactDao.loadAll().size());

        for (ContactResponse response : contactResponse) {
            Contact mContact = getContact(response);

            if (mContact.getIsOnContag()) {
                insertAndReturnContagContag(mContext, mContact, response.contagContactUser, true);
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

    public static ContagContag getContagContagByContagID(Context mContext, String contagID) {
        ContagContagDao mContagContagDao = getSession(mContext).getContagContagDao();
        try {
            return mContagContagDao.queryBuilder().where(ContagContagDao.Properties.Contag.eq(contagID)).list().get(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ContagContag insertAndReturnContagContag(Context mContext, Contact mContact, ContagContactResponse mContagContactRespone,
                                                           boolean isContact) {
        ContagContag mContagContag = getContagContact(mContagContactRespone, mContact, isContact);
        ContagContagDao mContagContagDao = getSession(mContext).getContagContagDao();

        if (mContagContactRespone.userInterest != null && mContagContactRespone.userInterest.size() > 0) {
            InterestDao interestDao = getSession(mContext).getInterestDao();

            List<Interest> interests = getInterestList(mContagContactRespone.userInterest,
                    mContagContactRespone, mContagContag);

            for (Interest interest : interests) {
                interestDao.insertOrReplace(interest);
            }
        }

        if (mContagContactRespone.socialProfile != null && mContagContactRespone.socialProfile.size() > 0) {
            SocialProfileDao spDao = getSession(mContext).getSocialProfileDao();
            List<SocialProfile> socialProfiles = getSocialProfiles(mContagContactRespone.socialProfile,
                    mContagContactRespone.id, mContagContag);
            for (SocialProfile socialProfile : socialProfiles) {
                spDao.insertOrReplace(socialProfile);
            }
        }
        mContagContagDao.insertOrReplace(mContagContag);
        return mContagContag;
    }

    public static ContactListItem getContactListItem(Context mContext, ContagContag mContagContag) {
        InterestDao mInterestDao = getSession(mContext).getInterestDao();
        List<Interest> interests = mInterestDao.queryBuilder().
                where(InterestDao.Properties.ContagUserId.eq(mContagContag.getId())).
                orderAsc(InterestDao.Properties.Name).list();
        ContactListItem mContactListItem;
        if(mContagContag.getIs_contact()) {
            mContactListItem = new ContactListItem(interests, mContagContag, Constants.Types.ITEM_CONTAG);
        } else {
            mContactListItem = new ContactListItem(interests, mContagContag, Constants.Types.ITEM_ADD_CONTAG);
        }
            return mContactListItem;
    }

    public static void addContag(Context mContext, ContactListItem contag) {
        ContactDao mContactDao = getSession(mContext).getContactDao();
        ContagContagDao ccDao = getSession(mContext).getContagContagDao();

        mContactDao.insertOrReplace(contag.mContact);

        contag.mContagContag.setIs_contact(true);
        ccDao.insertOrReplace(contag.mContagContag);

        Router.addContagUser(mContext, contag.mContagContag.getId());

        Log.d("conadd", "Added the contag user!");


    }

    public static Boolean isExistingContact(String contactNumber, Context mContext) {

        ContactDao cDao = getSession(mContext).getContactDao();
        long count = cDao.queryBuilder().where(ContactDao.Properties.ContactNumber.eq(contactNumber)).count();

        return (count == 0);


    }

    private static ContagContag getContagContact(ContagContactResponse ccResponse, Contact mContact, Boolean isOnContag) {

        ContagContag cc = new ContagContag(ccResponse.id);
        cc.setContact(mContact);
        cc.setCreatedOn(ccResponse.createdOn);
        cc.setUpdatedOn(ccResponse.updatedOn);
        if (ccResponse.name != null) {
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
        cc.setAvatarUrl(Constants.Urls.BASE_URL + ccResponse.avatarUrl);
        cc.setBloodGroup(ccResponse.bloodGroup);
        cc.setDateOfBirth(ccResponse.dateOfBirth);
        cc.setIsMobileVerified(ccResponse.isMobileVerified);
        cc.setMaritalStatus(ccResponse.maritalStatus);
        cc.setMarriageAnniversary(ccResponse.marriageAnniversary);
        cc.setPersonalEmail(ccResponse.personalEmail);
        cc.setWorkAddress(ccResponse.workAddress);
        cc.setIs_contact(isOnContag);

        return cc;

    }

    public static Contact getContact(ContactResponse response) {
        return new Contact(response.id, response.createdOn, response.updatedOn, response.contactName,
                response.contactNumber, response.invitedOn, response.isOnContag, response.isMuted, response.isBlocked,
                response.isBlocked);
    }

    private static ArrayList<Interest> getInterestList(ArrayList<InterestResponse> interests,
                                                       ContagContactResponse ccResponse, ContagContag cc) {
        ArrayList<Interest> mInterest = new ArrayList<>();
        for (InterestResponse ir : interests) {
            Interest interest = new Interest(ir.id);
            interest.setName(ir.name);
            interest.setContagUserId(ccResponse.id);
            interest.setContagContag(cc);
            mInterest.add(interest);
        }
        return mInterest;
    }

    private static ArrayList<SocialProfile> getSocialProfiles(ArrayList<SocialProfileResponse> profiles, long userID, ContagContag contagContag) {
        ArrayList<SocialProfile> mProfiles = new ArrayList<>();
        for (SocialProfileResponse socialProfileResponse : profiles) {
            SocialProfile socialProfile = new SocialProfile();
            socialProfile.setPlatform_id(socialProfileResponse.platformId);
            socialProfile.setSocial_platform(socialProfileResponse.socialPlatform);
            socialProfile.setContagContag(contagContag);
            socialProfile.setPlatform_username(socialProfileResponse.platformUsername);
            socialProfile.setContagUserId(userID);
            mProfiles.add(socialProfile);
        }
        return mProfiles;

    }

}
