package com.contag.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.adapter.ContactAdapter;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.config.Router;
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
public class ContactListFragment extends BaseFragment implements TextWatcher, TextView.OnEditorActionListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = ContactListFragment.class.getName();
    private View pbContacts;
    private ArrayList<ContactListItem> contacts = new ArrayList<>();
    private ContactAdapter contactAdapter;
    private SearchContacts mSearchContacts;
    private String searchFilter;
    private EditText etSearchBox;

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
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactListItem cli = contacts.get(position);
                if (cli.type == Constants.Types.ITEM_CONTAG) {
                    Router.startUserActivity(ContactListFragment.this.getActivity(), TAG, cli.mContagContag.getId());
                }
            }
        });
        Spinner spFilters = (Spinner) view.findViewById(R.id.sp_search_filter);
        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, Constants.Arrays.SEARCH_FILTER);
        spFilters.setAdapter(spAdapter);
        spFilters.setOnItemSelectedListener(this);
        etSearchBox = (EditText) view.findViewById(R.id.et_contact_search);
        etSearchBox.setOnEditorActionListener(this);
        etSearchBox.addTextChangedListener(this);
        searchFilter = Constants.Arrays.SEARCH_FILTER[0];
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.toString().length() != 0) {
            doSearch(s.toString());
        } else {
            new LoadContacts().execute();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH && v.getId() == R.id.et_contact_search) {
            doSearch(v.getText().toString());
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        searchFilter = Constants.Arrays.SEARCH_FILTER[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void doSearch(String query) {
        if (mSearchContacts != null) {
            mSearchContacts.cancel(true);
        }
        mSearchContacts = new SearchContacts();
        mSearchContacts.execute(query, searchFilter);

    }

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

            for (Contact contact : contacts) {
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

    private class SearchContacts extends AsyncTask<String, Void, ArrayList<ContactListItem>> {
        @Override
        protected void onPreExecute() {
            pbContacts.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<ContactListItem> doInBackground(String... params) {
            if (params.length > 0) {
                DaoSession session = ((ContagApplication) ContactListFragment.this.getActivity().
                        getApplicationContext()).getDaoSession();

                ArrayList<ContactListItem> items = new ArrayList<>();

                String query = "%" + params[0] + "%";
                String filter = params[1];
                switch (filter) {
                    case Constants.Values.FILTER_NAME: {
                        query = query + "%";
                        ContagContagDao mContagContagDao = session.getContagContagDao();
                        List<ContagContag> contagContacts = mContagContagDao.queryBuilder().
                                where(ContagContagDao.Properties.Id.notEq(PrefUtils.getCurrentUserID())
                                        , ContagContagDao.Properties.Name.like(query)).
                                orderAsc(ContagContagDao.Properties.Name).list();

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
                        List<Contact> contacts = mContactDao.queryBuilder().where(ContactDao.Properties.IsOnContag.eq(false)
                                , ContactDao.Properties.ContactName.like(query)).orderAsc(ContactDao.Properties.ContactName).list();

                        for (Contact contact : contacts) {
                            items.add(new ContactListItem(contact, Constants.Types.ITEM_NON_CONTAG));
                        }

                        break;
                    }
                    case Constants.Values.FILTER_BLOOD_GROUP: {
                        ContagContagDao mContagContagDao = session.getContagContagDao();
                        List<ContagContag> contagContacts = mContagContagDao.queryBuilder().
                                where(ContagContagDao.Properties.Id.notEq(PrefUtils.getCurrentUserID())
                                        , ContagContagDao.Properties.BloodGroup.like(query)).
                                orderAsc(ContagContagDao.Properties.Name).list();

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
                        break;
                    }
                    case Constants.Values.FILTER_DATE_OF_BIRTH: {
                        query = "%" + query + "%";
                        ContagContagDao mContagContagDao = session.getContagContagDao();
                        List<ContagContag> contagContacts = mContagContagDao.queryBuilder().
                                where(ContagContagDao.Properties.Id.notEq(PrefUtils.getCurrentUserID())
                                        , ContagContagDao.Properties.DateOfBirth.like(query)).
                                orderAsc(ContagContagDao.Properties.Name).list();

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
                        break;
                    }
                }
                return items;
            }
            return null;
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
