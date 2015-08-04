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

/*
    Sticky service that will be started either on device reboot or from the main activity
 */


public class ContactService extends Service implements Loader.OnLoadCompleteListener<Cursor> {

    private CursorLoader clContact;

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
        clContact = new CursorLoader(ContactService.this, ContactsContract.Contacts.CONTENT_URI, null, null , null, null);
        clContact.registerListener(1, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        getContentResolver().unregisterContentObserver(coContact);

    }

    ////////////////////// Content Observer ///////////////////////////////

    ////////////////////// once the contact changes get the cursor /////////////////////////
    private ContentObserver coContact = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            clContact.startLoading();
        }
    };


    ////////////////////////////// Loader function /////////////////////////

    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {

    }
}
