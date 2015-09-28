package com.contag.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.contag.app.R;
import com.contag.app.adapter.ContactAdapter;
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
import com.contag.app.util.PrefUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanay on 20/9/15.
 */
public class ContactListFragment extends BaseFragment {

    private static final String TAG = ContactListFragment.class.getName();
    private View pbContacts;
    private ArrayList<ContactListItem> contacts = new ArrayList<>();
    private ContactAdapter contactAdapter;

    public static ContactListFragment newInstance() {
        ContactListFragment clf = new ContactListFragment();
        Bundle args = new Bundle();
        clf.setArguments(args);
        return clf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        pbContacts = view.findViewById(R.id.pb_contacts);
        contactAdapter = new ContactAdapter(contacts, getActivity());
        ListView lvContacts = (ListView) view.findViewById(R.id.lv_contact);
        lvContacts.setAdapter(contactAdapter);
        new LoadContacts().execute();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(brContactsUpdated,
                new IntentFilter(getResources().getString(R.string.intent_filter_contacts_updated)));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(brContactRequestMade,
                new IntentFilter(getResources().getString(R.string.intent_filter_contacts_request)));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brContactsUpdated);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brContactRequestMade);
    }


    private BroadcastReceiver brContactsUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new LoadContacts().execute();
        }
    };

    private BroadcastReceiver brContactRequestMade = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };


    private class LoadContacts extends AsyncTask<Void, Void, ArrayList<ContactListItem>> {

        @Override
        protected void onPreExecute() {
            pbContacts.setVisibility(View.VISIBLE);
        }


        @Override
        protected ArrayList<ContactListItem> doInBackground(Void... params) {
            DaoSession session = ((ContagApplication) ContactListFragment.this.getActivity().
                    getApplicationContext()).getDaoSession();

            ArrayList<ContactListItem> items = new ArrayList<>();

            ContagContagDao mContagContagDao = session.getContagContagDao();
            List<ContagContag> contagContacts = mContagContagDao.queryBuilder().
                    where(ContagContagDao.Properties.Id.notEq(PrefUtils.getCurrentUserID())).list();

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

        @Override
        protected void onPostExecute(ArrayList<ContactListItem> contactListItems) {
            contacts.clear();
            contacts.addAll(contactListItems);
            contactAdapter.notifyDataSetChanged();
            pbContacts.setVisibility(View.GONE);
        }
    }

}
