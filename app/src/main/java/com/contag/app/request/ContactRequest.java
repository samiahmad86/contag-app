package com.contag.app.request;

import com.contag.app.config.Constants;
import com.contag.app.model.APIInterface;
import com.contag.app.model.AddContact;
import com.contag.app.model.ContactResponse;
import com.contag.app.model.NotificationAddContact;
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
    private String contagID ;
    private AddContact contact ;
    private long userID;

    private long notificationID ;

    public ContactRequest(int type) {
        super(ContactResponse.ContactList.class, APIInterface.class);
        this.type = type;
    }

    public ContactRequest(long userID, int type) {
        super(ContactResponse.ContactList.class, APIInterface.class);
        this.type = type;
        this.userID = userID;
    }

    public ContactRequest(int type, HashSet<RawContacts> mRawContactsArrayList) {
        super(ContactResponse.ContactList.class, APIInterface.class);
        this.type = type;
        this.rawContact = mRawContactsArrayList;
    }

    public ContactRequest(int type, String contagID) {
        super(ContactResponse.ContactList.class, APIInterface.class);
        this.type = type;
        this.contagID = contagID ;
    }

    public ContactRequest(int type, AddContact contact){
        super(ContactResponse.ContactList.class, APIInterface.class);
        this.type = type;
        this.contact = contact ;
    }


    public ContactRequest(int type, long notificationID){
        super(ContactResponse.ContactList.class, APIInterface.class) ;
        this.type = type ;
        this.notificationID = notificationID ;
    }

    @Override
    public ContactResponse.ContactList loadDataFromNetwork() throws Exception {
        if (type == Constants.Types.REQUEST_GET_CURRENT_USER) {

            return getService().getContacts(PrefUtils.getAuthToken());

        } else if (type == Constants.Types.REQUEST_PUT_ADD_USER){

            return getService().addContagUser(PrefUtils.getAuthToken(), contact) ;
        }
        else if(type == Constants.Types.REQUEST_POST) {

            return getService().sendContacts(PrefUtils.getAuthToken(), rawContact);

        } else if (Constants.Types.REQUEST_GET_USER_BY_CONTAG_ID == type){

            return getService().getUserByContagID(PrefUtils.getAuthToken(), contagID);

        } else if(Constants.Types.REQUEST_GET_USER_BY_USER_ID == type) {
            return getService().getUserByUserID(PrefUtils.getAuthToken(),userID);
        } else if (Constants.Types.REQUEST_ADD_CONTAG_NOTIFICATION == type){
            NotificationAddContact nac = new NotificationAddContact(notificationID) ;
            return getService().addContagUserFromNotification(PrefUtils.getAuthToken(), nac) ;
        }

        return null;
    }
}
