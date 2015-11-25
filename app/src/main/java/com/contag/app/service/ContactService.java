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
import com.contag.app.model.AddContact;
import com.contag.app.model.ContactResponse;
import com.contag.app.model.RawContacts;
import com.contag.app.request.ContactRequest;
import com.contag.app.util.ContactUtils;
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
                Log.d("Condev", "ContactService: Going to send the contacts to the server") ;
                clContact.startLoading();
            } else if(intent.getBooleanExtra(Constants.Keys.KEY_ADD_CONTACT, false)) {
                Log.d("conadd", "Going to make that call") ;
                long userID = intent.getLongExtra(Constants.Keys.KEY_USER_ID, 0l);
                Log.d("conadd", "" + userID) ;
                AddContact addContact = new AddContact(userID)  ;
                ContactRequest cr = new ContactRequest(Constants.Types.REQUEST_PUT_ADD_USER, addContact);
                mSpiceManager.execute(cr, new RequestListener<ContactResponse.ContactList>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Log.d("conadd", "request failed") ;
                    }

                    @Override
                    public void onRequestSuccess(ContactResponse.ContactList contactResponses) {
                        //ContactUtils.saveContact(ContactService.this, contactResponses);
                    }
                }) ;
            }
            else {
                Log.d("Condev", "ContactService: Going to fetch the contacts from the server") ;
                ContactRequest cr = new ContactRequest(Constants.Types.REQUEST_GET_CURRENT_USER);
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
        Log.d("Condev", "Currently loading your contact book") ;
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

                if (RegexUtils.isPhoneNumber(phoneNumber)) {
                    char firstCharacter = phoneNumber.charAt(0);
                    if (firstCharacter == '9' || firstCharacter == '8' || firstCharacter == '7') {
                        contacts.add(new RawContacts(name, phoneNumber));
                        contactIds.add(id);
                    }
                }
            }
        }
        Log.d("Condev", "Contacts loaded from contact book. Time to make the request");
        Log.d("Condev", "Requesting the server with " + String.valueOf(contacts.size()) + " contacts");
        ContactRequest cr = new ContactRequest(Constants.Types.REQUEST_POST, contacts);
        mSpiceManager.execute(cr, this);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        spiceException.printStackTrace();
        Log.d("Condev", "Contact upload request failed on server side: " + spiceException.getCause()) ;
    }

    @Override
    public void onRequestSuccess(ContactResponse.ContactList contactResponses) {
        Gson gson = new Gson();
        //Log.d("Condev", "Server request successful with a response of " + String.valueOf(contactResponses.size()) + "contacts") ;

        new InsertContact().execute(contactResponses);
    }

    private class InsertContact extends AsyncTask<ContactResponse.ContactList, Void, Boolean> {

        @Override
        protected Boolean doInBackground(ContactResponse.ContactList... params) {
            Log.d("Condev", "Loading the returned contacts into dao") ;
            ContactUtils.saveContact(getApplicationContext(), params[0]) ;
            return true;
        }

        @Override
        protected void onPostExecute(Boolean value) {
            Log.d("Condev", "Local dao updated successfully. Going to broadcast the same") ;
            PrefUtils.setContactBookUpdated(false);
            PrefUtils.setContactUpdatedTimestamp(System.currentTimeMillis());
            Intent iContactUpdated = new Intent(getResources().getString(R.string.intent_filter_contacts_updated));
            LocalBroadcastManager.getInstance(ContactService.this).sendBroadcast(iContactUpdated);
        }
    }
}