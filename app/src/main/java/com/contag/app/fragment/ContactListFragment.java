package com.contag.app.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.model.Contact;
import com.contag.app.model.ContactDao;
import com.contag.app.model.ContactListItem;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.DaoSession;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanay on 20/9/15.
 */
public class ContactListFragment extends BaseFragment {

    private static final String TAG = ContactListFragment.class.getName();

    public static ContactListFragment newInstance() {
        ContactListFragment clf = new ContactListFragment();
        Bundle args = new Bundle();
        clf.setArguments(args);
        return clf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new LoadContacts().execute();
        return null;
    }

    private class LoadContacts extends AsyncTask<Void, Void, ArrayList<ContactListItem>> {
        @Override
        protected ArrayList<ContactListItem> doInBackground(Void... params) {
            DaoSession session = ((ContagApplication) ContactListFragment.this.getActivity().
                    getApplicationContext()).getDaoSession();

            ArrayList<ContactListItem> items = new ArrayList<>();

            ContagContagDao mContagContagDao = session.getContagContagDao();
            List<ContagContag> contagContacts = mContagContagDao.loadAll();

            if (contagContacts != null) {
                for (ContagContag cuntag : contagContacts) {
                    InterestDao mInterestDao = session.getInterestDao();
                    List<Interest> interests = mInterestDao.queryBuilder().
                            where(InterestDao.Properties.ContagUserId.eq(cuntag.getId())).
                            orderAsc(InterestDao.Properties.Name).list();
                    ContactListItem item = new ContactListItem(interests, cuntag, Constants.Types.ITEM_CONTAG);
                    items.add(item);
                }
            }

            ContactDao mContactDao = session.getContactDao();
            List<Contact> contacts = mContactDao.queryBuilder().where(ContactDao.Properties.IsOnContag.eq(false)).
                    orderAsc(ContactDao.Properties.ContactName).list();

            for(Contact contact: contacts) {
                items.add(new ContactListItem(contact, Constants.Types.ITEM_NON_CONTAG));
            }

            log(TAG, contacts.size() + " contacts");
            return items;
        }
    }

}
