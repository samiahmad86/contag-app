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
import com.contag.app.model.RawContacts;
import com.contag.app.request.ContactRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

import io.realm.Realm;
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
        clContact = new CursorLoader(ContactService.this, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null , null, null);
        clContact.registerListener(1, this);
        mSpiceManager.start(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        clContact.startLoading();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(clContact != null) {
            clContact.unregisterListener(this);
            clContact.cancelLoad();
            clContact.stopLoading();
        }
        if(mSpiceManager.isStarted()) {
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
        while(cursor.moveToNext()) {
            contacts.add(new RawContacts(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));
        }
        cursor.close();
        ContactRequest cr = new ContactRequest(contacts);
        mSpiceManager.execute(cr, new RequestListener<ContactResponse.ContactList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(ContactResponse.ContactList contactResponses) {
                Log.d(TAG, "" + contactResponses.size());
                for(ContactResponse response: contactResponses) {
                    Realm realmInstance = Realm.getInstance(getApplicationContext());
                    realmInstance.beginTransaction();
                    Contact contact = realmInstance.createObject(Contact.class);
                    copyResponse(contact, response);
                    realmInstance.commitTransaction();
                }
                Realm test = Realm.getInstance(getApplicationContext());
                RealmResults<Contact> responses = test.where(Contact.class).findAll();
                Log.d(TAG,  "" + responses.size() + " " + responses.get(0).contactName);
            }
        });
    }

    private void copyResponse(Contact contact, ContactResponse response) {
        contact.id = response.id;
        contact.contactName = response.contactName;
        contact.contactNumber = response.contactNumber;
        contact.createdOn = response.createdOn;
        contact.invitedOn = response.invitedOn;
        contact.isBlocked = response.isBlocked;
        contact.isInvited = response.isInvited;
        contact.isMuted = response.isMuted;
        contact.updatedOn = response.updatedOn;
        contact.isOnContag = response.isOnContag;
        if(contact.isOnContag) {
            copyContag(contact.contactContagUser, response.contactContagUser);
        } else {
            contact.contactContagUser = null;
        }

    }

    private void copyContag(ContagContact contact, ContagContactResponse response) {

    }

}
