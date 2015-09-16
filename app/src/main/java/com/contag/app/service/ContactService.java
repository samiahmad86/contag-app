package com.contag.app.service;

import android.app.Service;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import com.contag.app.model.Contact;
import com.contag.app.model.ContactResponse;
import com.contag.app.model.ContagContact;
import com.contag.app.model.ContagContactResponse;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestResponse;
import com.contag.app.model.RawContacts;
import com.contag.app.request.ContactRequest;
import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class ContactService extends Service implements Loader.OnLoadCompleteListener<Cursor> {

    private CursorLoader clContact;
    private SpiceManager mSpiceManager = new SpiceManager(APIService.class);
    private static final String TAG = ContactService.class.getName();

    public ContactService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, coContact);
        mSpiceManager.start(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        clContact = new CursorLoader(ContactService.this, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        clContact.registerListener(1, this);
        clContact.startLoading();
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
            clContact.startLoading();
        }
    };


    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<RawContacts> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            contacts.add(new RawContacts(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));
        }
        cursor.close();
        Gson gson = new Gson();
        Log.d(TAG, gson.toJson(contacts).toString());
        ContactRequest cr = new ContactRequest(contacts);
        mSpiceManager.execute(cr, new RequestListener<ContactResponse.ContactList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(ContactResponse.ContactList contactResponses) {
//                Log.d(TAG, "" + contactResponses.size());
//                for (ContactResponse response : contactResponses) {
//                    Realm realmInstance = Realm.getInstance(getApplicationContext());
//                    realmInstance.beginTransaction();
//                    Contact contact = realmInstance.createObject(Contact.class);
//                    copyResponse(realmInstance, contact, response);
//                    realmInstance.commitTransaction();
//                }
//                Realm test = Realm.getInstance(getApplicationContext());
//                RealmResults<Contact> responses = test.where(Contact.class).findAll();
//                Log.d(TAG, responses.get(0).getContactName());
            }
        });
    }

    private void copyResponse(Realm instance, Contact contact, ContactResponse response) {
        contact.setId(response.id);
        contact.setContactName(response.contactName);
        contact.setContactNumber(response.contactNumber);
        contact.setCreatedOn(response.createdOn);
        contact.setInvitedOn(response.invitedOn);

        contact.setBlocked(response.isBlocked);
        contact.setInvited(response.isInvited);
        contact.setMuted(response.isMuted);
        contact.setUpdatedOn(response.updatedOn);
        contact.setOnContag(response.isOnContag);
        if (contact.isOnContag()) {
            ContagContact contagContact = instance.createObject(ContagContact.class);
            copyContag(instance, contagContact, response.contactContagUser);
            contact.setContactContagUser(contagContact);
        } else {
            contact.setContactContagUser(null);
        }

    }

    private void copyContag(Realm instance, ContagContact contact, ContagContactResponse response) {
        contact.setId(response.id);
        contact.setUpdatedOn(response.updatedOn);
        contact.setAddress(response.address);
        contact.setAndroidAppLink(response.androidAppLink);
        contact.setAvatarUrl(response.avatarUrl);
        contact.setBloodGroup(response.bloodGroup);
        contact.setContag(response.contag);
        contact.setCreatedOn(response.createdOn);
        contact.setDateOfBirth(response.dateOfBirth);
        contact.setDesignation(response.designation);
        contact.setEmergencyContactNumber(response.emergencyContactNumber);
        contact.setWorkMobileNumber(response.workMobileNumber);
        contact.setMobileNumber(response.mobileNumber);
        contact.setWorkFacebookPage(response.workFacebookPage);
        contact.setAndroidAppLink(response.androidAppLink);
        contact.setIosAppLink(response.iosAppLink);
        contact.setRegisteredWith(response.registeredWith);
        contact.setLandlineNumber(response.landlineNumber);
        contact.setWorkLandlineNumber(response.workLandlineNumber);
        contact.setGender(response.gender);
        contact.setMobileVerified(response.isMobileVerified);
        contact.setPersonalEmail(response.personalEmail);
        contact.setWorkEmail(response.workEmail);
        contact.setWorkAddress(response.workAddress);
        contact.setMarriageAnniversary(response.marriageAnniversary);

        RealmList<Interest> interests = new RealmList<>();

        for (InterestResponse ir : response.userInterest) {
            Interest interest = instance.createObject(Interest.class);
            interest.setId(ir.id);
            interest.setName(ir.name);
            interests.add(interest);
        }

        contact.setUserInterest(interests);
    }

}
