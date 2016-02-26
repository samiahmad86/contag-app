package com.contag.app.activity;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.model.AddContact;
import com.contag.app.model.ContactResponse;
import com.contag.app.request.ContactRequest;
import com.contag.app.service.APIService;
import com.contag.app.util.ContactUtils;
import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

/**
 * Created by Adi on 2/7/2016.
 */
public class NfcRecieveActivity extends BaseActivity {

    private SpiceManager mSpiceManager = new SpiceManager(APIService.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSpiceManager.start(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {

            Log.e("NFC intent received","Yes");
            Parcelable[] parcelables =
                    intent.getParcelableArrayExtra(
                            NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage inNdefMessage = (NdefMessage) parcelables[0];
            NdefRecord[] inNdefRecords = inNdefMessage.getRecords();
            NdefRecord NdefRecord_0 = inNdefRecords[0];
            String inContact = new String(NdefRecord_0.getPayload());
            Log.e("NFC Data in intent",inContact);
            AsyncSave asyncSave = new AsyncSave();
            asyncSave.execute(inContact);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSpiceManager.isStarted())
            mSpiceManager.shouldStop();
    }

    @Override
    protected void onNewIntent(Intent newIntent) {
        setIntent(newIntent);
    }

    private class AsyncSave extends AsyncTask<String,Void,Boolean> {

        ContactResponse contactResponse;

        @Override
        protected Boolean doInBackground(String... strings) {
            String inContact = strings[0];
            Gson gson = new Gson();
            Log.e("NFC Data in backgroun",inContact);
            contactResponse = gson.fromJson(inContact, ContactResponse.class);
            ContactResponse.ContactList contactList = new ContactResponse.ContactList();
            contactList.add(contactResponse);
            Log.d("newcontact", gson.toJson(contactList));
            if(ContactUtils.saveSingleContact(NfcRecieveActivity.this, contactList, true)) {
                AddContact addContactNfc = new AddContact(contactResponse.contagContactResponse.id);
                ContactRequest cr = new ContactRequest(addContactNfc,Constants.Types.REQUEST_POST_NFC_ADD_USER);
                mSpiceManager.execute(cr, new RequestListener<ContactResponse.ContactList>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Log.d("conadd", "request failed"+spiceException.toString()) ;
                    }

                    @Override
                    public void onRequestSuccess(ContactResponse.ContactList contactResponses) {
                        Log.d("conadd", "request completed"+contactResponses.toString()) ;
                    }
                }) ;
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean isAdded) {
            super.onPostExecute(isAdded);
            if(isFinishing())
                return;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(isAdded)
                        Router.startHomeActivity(NfcRecieveActivity.this,"NfcRecieveActivity", Constants.Types.NFC_OPEN_PROFILE,contactResponse.contagContactResponse.id);
                    else
                        Router.startHomeActivity(NfcRecieveActivity.this,"NfcRecieveActivity", Constants.Types.NFC_OPEN_PROFILE,contactResponse.contagContactResponse.id);

                    // Router.startHomeActivity(NfcRecieveActivity.this, "NfcRecieveActivity");
                    finish();
                }
            });
        }
    }
}
