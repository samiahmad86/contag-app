package com.contag.app.service;

import android.app.IntentService;
import android.app.Service;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import com.contag.app.config.ContagApplication;
import com.contag.app.model.Contact;
import com.contag.app.model.ContactDao;
import com.contag.app.model.ContactResponse;
import com.contag.app.model.ContagContactResponse;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.DaoSession;
import com.contag.app.model.RawContacts;
import com.contag.app.request.ContactRequest;
import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class ContactService extends Service implements Loader.OnLoadCompleteListener<Cursor> {

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
        HashSet<RawContacts> contacts = new HashSet<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//            boolean flag = true;
//            for (RawContacts fuckBhai : contacts) {
//                if(fuckBhai.contact_number.equalsIgnoreCase(phoneNumber) && fuckBhai.)
//            }
//            if (flag) {
            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replace("+91", "");
            if (phoneNumber.indexOf("0") == 0) {
                phoneNumber = phoneNumber.substring(1);
            }
            contacts.add(new RawContacts(name, phoneNumber));
//            }
        }
        cursor.close();
        Gson gson = new Gson();
        Log.d(TAG, "" + contacts.size());
        Log.d(TAG, gson.toJson(contacts).toString());
        ContactRequest cr = new ContactRequest(contacts);
        mSpiceManager.execute(cr, new RequestListener<ContactResponse.ContactList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(ContactResponse.ContactList contactResponses) {
                Gson gson = new Gson();
                Log.d(TAG, "fuck you" + contactResponses.size());
                Log.d(TAG, "fuck" + gson.toJson(contactResponses.get(28)) + gson.toJson(contactResponses.get(29)));
                DaoSession session = ((ContagApplication) getApplicationContext()).getDaoSession();
                ContactDao mContactDao = session.getContactDao();
                Log.d(TAG, "fuck" + mContactDao.loadAll().size());
                for (ContactResponse response : contactResponses) {
                    Contact mContact = new Contact(response.id);
                    mContact.setUpdatedOn(response.updatedOn);
                    mContact.setInvitedOn(response.invitedOn);
                    mContact.setContactNumber(response.contactNumber);
                    mContact.setContactName(response.contactName);
                    mContact.setIsBlocked(response.isBlocked);
                    mContact.setIsMuted(response.isMuted);
                    mContact.setIsOnContag(response.isOnContag);
                    mContact.setIsInvited(response.isInvited);
                    if (mContact.getIsOnContag()) {
                        ContagContactResponse ccResponse = response.contactContagUser;
                        ContagContagDao ccDao = session.getContagContagDao();
                        ContagContag cc = new ContagContag(ccResponse.id);
                        cc.setUpdatedOn(ccResponse.updatedOn);
                        cc.setAddress(ccResponse.address);
                        cc.setWorkAddress(ccResponse.workAddress);
                        cc.setAndroidAppLink(ccResponse.androidAppLink);
                        cc.setContactId(response.id);
                        cc.setContact(mContact);
                        ccDao.insert(cc);
                        Log.d(TAG, ccDao.loadAll().size() + "");
                    }
                    Log.d(TAG, "" + mContact.getId() + " " + response.id);
                    Log.d(TAG, "" + mContact.getContactName() + " " + response.contactName);
                    try {
                        mContactDao.insert(mContact);
                    } catch (Exception ex) {
                        // TODO : handle this
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

}
