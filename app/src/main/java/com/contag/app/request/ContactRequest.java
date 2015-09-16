package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.Contact;
import com.contag.app.model.ContactResponse;
import com.contag.app.model.RawContacts;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;

/**
 * Created by tanay on 15/9/15.
 */
public class ContactRequest extends RetrofitSpiceRequest<ContactResponse.ContactList, APIInterface> {

    private ArrayList<RawContacts> rawContact;

    public ContactRequest(ArrayList<RawContacts> mRawContactsArrayList) {
        super(ContactResponse.ContactList.class, APIInterface.class);
        this.rawContact = mRawContactsArrayList;
    }

    @Override
    public ContactResponse.ContactList loadDataFromNetwork() throws Exception {
        return getService().sendContacts(PrefUtils.getAuthToken(), rawContact);
    }
}
