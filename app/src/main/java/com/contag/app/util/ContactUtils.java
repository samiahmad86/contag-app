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

        for (ContactResponse response : contactResponse) {
            Contact mContact = getContact(response);
            if (mContact.getIsOnContag()) {
                insertAndReturnContagContag(mContext, mContact, response.contagContactResponse, true);
            }
            try {
                mContactDao.insertOrReplace(mContact);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void saveSingleContact(Context mContext, ContactResponse.ContactList contactResponse, boolean isContact) {
        ContactDao mContactDao = getSession(mContext).getContactDao();

        for (ContactResponse response : contactResponse) {
            Contact mContact = getContact(response);
            if (mContact.getIsOnContag()) {
                insertAndReturnContagContag(mContext, mContact, response.contagContactResponse, isContact);
            }
            try {
                mContactDao.insertOrReplace(mContact);
            } catch (Exception ex) {
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

        Router.addContagUser(mContext, contag.mContagContag.getId());



    }

    public static Boolean isExistingContact(String contactNumber, Context mContext) {

        ContactDao cDao = getSession(mContext).getContactDao();
        long count = cDao.queryBuilder().where(ContactDao.Properties.ContactNumber.eq(contactNumber)).count();

        return (count == 0);

    }

    private static ContagContag getContagContact(ContagContactResponse contagContactResponse, Contact mContact, Boolean isOnContag) {

        ContagContag contagContag = new ContagContag(contagContactResponse.id);
        contagContag.setContact(mContact);
        contagContag.setCreatedOn(contagContactResponse.createdOn);
        contagContag.setUpdatedOn(contagContactResponse.updatedOn);
        if (contagContactResponse.name != null) {
            contagContag.setName(contagContactResponse.name);
        } else {
            contagContag.setName("Contag User");
        }
        contagContag.setRegisteredWith(contagContactResponse.registeredWith);
        contagContag.setMobileNumber(contagContactResponse.mobileNumber);
        contagContag.setContag(contagContactResponse.contag);
        contagContag.setLandLineNumber(contagContactResponse.landlineNumber);
        contagContag.setEmergencyContactNumber(contagContactResponse.emergencyContactNumber);
        contagContag.setIsMobileVerified(contagContactResponse.isMobileVerified);
        contagContag.setGender(contagContactResponse.gender);
        contagContag.setAddress(contagContactResponse.address);
        contagContag.setWorkEmail(contagContactResponse.workEmail);
        contagContag.setWorkMobileNumber(contagContactResponse.workMobileNumber);
        contagContag.setWorkLandLineNumber(contagContactResponse.workLandlineNumber);
        contagContag.setWebsite(contagContactResponse.website);
        contagContag.setDesignation(contagContactResponse.designation);
        contagContag.setWorkFacebookPage(contagContactResponse.workFacebookPage);
        contagContag.setAndroidAppLink(contagContactResponse.androidAppLink);
        contagContag.setIosAppLink(contagContactResponse.iosAppLink);
        contagContag.setAvatarUrl(Constants.Urls.BASE_URL + contagContactResponse.avatarUrl);
        contagContag.setBloodGroup(contagContactResponse.bloodGroup);
        contagContag.setDateOfBirth(contagContactResponse.dateOfBirth);
        contagContag.setIsMobileVerified(contagContactResponse.isMobileVerified);
        contagContag.setMaritalStatus(contagContactResponse.maritalStatus);
        contagContag.setMarriageAnniversary(contagContactResponse.marriageAnniversary);
        contagContag.setPersonalEmail(contagContactResponse.personalEmail);
        contagContag.setWorkAddress(contagContactResponse.workAddress);
        contagContag.setCompanyName(contagContactResponse.companyName);
        contagContag.setIs_contact(isOnContag);

        return contagContag;

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
            mProfiles.add(socialProfile) ;
        }
        return mProfiles;

    }

}
