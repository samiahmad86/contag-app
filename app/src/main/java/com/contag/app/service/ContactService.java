package com.contag.app.service;

import android.app.Service;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.model.Contact;
import com.contag.app.model.ContactDao;
import com.contag.app.model.ContactResponse;
import com.contag.app.model.ContagContactResponse;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.DaoSession;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestDao;
import com.contag.app.model.InterestResponse;
import com.contag.app.model.RawContacts;
import com.contag.app.model.SocialProfile;
import com.contag.app.model.SocialProfileDao;
import com.contag.app.model.SocialProfileResponse;
import com.contag.app.request.ContactRequest;
import com.contag.app.util.PrefUtils;
import com.contag.app.util.RegexUtils;
import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.HashSet;


public class ContactService extends Service implements Loader.OnLoadCompleteListener<Cursor>, RequestListener<ContactResponse.ContactList> {

    private CursorLoader clContact;
    private SpiceManager mSpiceManager = new SpiceManager(APIService.class);
    private static final String TAG = ContactService.class.getName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public ContactService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, coContact);
        clContact = new CursorLoader(ContactService.this, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, null);
        clContact.registerListener(1, this);
        mSpiceManager.start(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getBooleanExtra(Constants.Keys.KEY_SEND_CONTACTS, false)) {
                clContact.startLoading();
            } else {
                ContactRequest cr = new ContactRequest(Constants.Types.REQUEST_GET);
                mSpiceManager.execute(cr, this);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (clContact != null) {
            clContact.unregisterListener(this);
            clContact.cancelLoad();
            clContact.stopLoading();
        }
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
        getContentResolver().unregisterContentObserver(coContact);

    }

    private ContentObserver coContact = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            PrefUtils.setContactBookUpdated(true);
        }
    };


    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
        HashSet<RawContacts> contacts = new HashSet<>();
        ArrayList<Integer> contactIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
            if (!contactIds.contains(id)) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNumber = phoneNumber.replace(" ", "");
                phoneNumber = phoneNumber.replace("+91", "");
                if (phoneNumber.indexOf("0") == 0) {
                    phoneNumber = phoneNumber.substring(1);
                }
                char firstCharacter = phoneNumber.charAt(0);
                if (RegexUtils.isPhoneNumber(phoneNumber) && (firstCharacter == '9' ||
                        firstCharacter == '8' || firstCharacter == '7')) {
                    contacts.add(new RawContacts(name, phoneNumber));
                    contactIds.add(id);
                }
            }
        }
        Gson gson = new Gson();
        Log.d(TAG, "" + contacts.size());
        Log.d(TAG, gson.toJson(contacts).toString());
        ContactRequest cr = new ContactRequest(Constants.Types.REQUEST_POST, contacts);
        mSpiceManager.execute(cr, this);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {

    }

    @Override
    public void onRequestSuccess(ContactResponse.ContactList contactResponses) {
        Gson gson = new Gson();
        Log.d(TAG, "fuck you" + contactResponses.size());
        Log.d(TAG, "fuck" + gson.toJson(contactResponses.get(28)) + gson.toJson(contactResponses.get(29)));
        new InsertContact().execute(contactResponses);
    }

    private class InsertContact extends AsyncTask<ContactResponse.ContactList, Void, Boolean> {

        @Override
        protected Boolean doInBackground(ContactResponse.ContactList... params) {
            DaoSession session = ((ContagApplication) getApplicationContext()).getDaoSession();
            ContactDao mContactDao = session.getContactDao();
            Log.d(TAG, "fuck" + mContactDao.loadAll().size());
            for (ContactResponse response : params[0]) {
                Contact mContact = new Contact(response.id, response.createdOn, response.updatedOn, response.contactName,
                        response.contactNumber, response.invitedOn, response.isOnContag, response.isMuted, response.isBlocked,
                        response.isBlocked);

                if (mContact.getIsOnContag()) {
                    ContagContactResponse ccResponse = response.contactContagUser;
                    ContagContag cc = new ContagContag(ccResponse.id);
                    cc.setContact(mContact);
                    cc.setCreatedOn(ccResponse.createdOn);
                    cc.setUpdatedOn(ccResponse.updatedOn);
                    if(ccResponse.name != null) {
                        cc.setName(ccResponse.name);
                    } else {
                        cc.setName(response.contactName);
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

                    ContagContagDao ccDao = session.getContagContagDao();
                    if (ccResponse.userInterest != null && ccResponse.userInterest.size() > 0) {
                        InterestDao interestDao = session.getInterestDao();
                        for (InterestResponse ir : ccResponse.userInterest) {
                            Log.d(TAG, ir.name);
                            Interest interest = new Interest(ir.id);
                            interest.setName(ir.name);
                            interest.setContagUserId(ccResponse.id);
                            interest.setContagContag(cc);
                            interestDao.insertOrReplace(interest);
                        }
                    }

                    if (ccResponse.socialProfile != null && ccResponse.socialProfile.size() > 0) {
                        SocialProfileDao spDao = session.getSocialProfileDao();
                        for (SocialProfileResponse spr : ccResponse.socialProfile) {
                            SocialProfile socialProfile = new SocialProfile();
                            socialProfile.setPlatform_id(spr.platformId);
                            socialProfile.setSocial_platform(spr.socialPlatform);
                            socialProfile.setContagContag(cc);
                            socialProfile.setContagUserId(ccResponse.id);
                            spDao.insertOrReplace(socialProfile);
                        }
                    }

                    ccDao.insertOrReplace(cc);
                }
                Log.d(TAG, "" + mContact.getId() + " " + response.id);
                Log.d(TAG, "" + mContact.getContactName() + " " + response.contactName);
                try {
                    mContactDao.insertOrReplace(mContact);
                    PrefUtils.setContactBookUpdated(false);
                    PrefUtils.setContactUpdatedTimestamp(System.currentTimeMillis());
                } catch (Exception ex) {
                    // TODO : handle this
                    ex.printStackTrace();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean value) {
            Intent iContactUpdated = new Intent(getResources().getString(R.string.intent_filter_contacts_updated));
            LocalBroadcastManager.getInstance(ContactService.this).sendBroadcast(iContactUpdated);
        }
    }
}