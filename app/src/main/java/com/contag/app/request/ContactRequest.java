package com.contag.app.request;

import com.contag.app.config.Constants;
import com.contag.app.model.APIInterface;
import com.contag.app.model.ContactResponse;
import com.contag.app.model.RawContacts;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashSet;

/**
 * Created by tanay on 15/9/15.
 */
public class ContactRequest extends RetrofitSpiceRequest<ContactResponse.ContactList, APIInterface> {

    private HashSet<RawContacts> rawContact;
    private int type;

    public ContactRequest(int type) {
        super(ContactResponse.ContactList.class, APIInterface.class);
        this.type = type;
    }

    public ContactRequest(int type, HashSet<RawContacts> mRawContactsArrayList) {
        super(ContactResponse.ContactList.class, APIInterface.class);
        this.type = type;
        this.rawContact = mRawContactsArrayList;
    }

    @Override
    public ContactResponse.ContactList loadDataFromNetwork() throws Exception {
        if (type == Constants.Types.REQUEST_GET) {
            return getService().getContacts(PrefUtils.getAuthToken());
        } else if(type == Constants.Types.REQUEST_POST) {
            return getService().sendContacts(PrefUtils.getAuthToken(), rawContact);
        }
        return null;
    }
}
