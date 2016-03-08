package com.contag.app.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.adapter.ContactAdapter;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.config.Router;
import com.contag.app.listener.DatabaseRequestListener;
import com.contag.app.model.Contact;
import com.contag.app.model.ContactDao;
import com.contag.app.model.ContactListItem;
import com.contag.app.model.ContactResponse;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.DaoSession;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestDao;
import com.contag.app.model.SocialProfile;
import com.contag.app.model.SocialProfileDao;
import com.contag.app.request.ContactRequest;
import com.contag.app.tasks.DatabaseOperationTask;
import com.contag.app.util.ContactUtils;
import com.contag.app.util.DeviceUtils;
import com.contag.app.util.PrefUtils;
import com.contag.app.util.RegexUtils;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ContactListFragment extends BaseFragment implements TextWatcher, TextView.OnEditorActionListener,
        AdapterView.OnItemSelectedListener, RequestListener<ContactResponse.ContactList> {

    public static final String TAG = ContactListFragment.class.getName();
    private View pbContacts;
    private ArrayList<ContactListItem> contacts = new ArrayList<>();
    private ContactAdapter contactAdapter;
    private SearchContacts mSearchContacts;
    private ListView lvContacts;
    private String searchFilter;
    private static final Integer[] filterIDS = {R.id.tv_filter_name, R.id.tv_filter_platform, R.id.tv_filter_blood};
    PopupWindow filterDropDown;
    private View filterView;
    private boolean isListViewEnabled;
    ImageView btnFilter;
    EditText etSearchBox;



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
        lvContacts = (ListView) view.findViewById(R.id.lv_contact);

        long userId=PrefUtils.getCurrentUserID();

        lvContacts.setAdapter(contactAdapter);

        if(savedInstanceState==null) {
            new LoadContacts().execute();
            Log.d("Contactlist", "saved instance null");
        }
        else
            Log.d("Contactlist", "saved instance not null");


        //  final Bundle savedInstance=savedInstanceState;
        isListViewEnabled = true;


        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long viewID) {
                ContactListItem contactListItem = contacts.get(position);
                      if (DeviceUtils.isWifiConnected(getActivity()) && (contactListItem.type == Constants.Types.ITEM_ADD_CONTAG
                        || contactListItem.type == Constants.Types.ITEM_CONTAG) && isListViewEnabled) {
                    ContagContag mContagContag = contactListItem.mContagContag;
                    ContactRequest contactUserRequest = new ContactRequest(Constants.Types.REQUEST_GET_USER_BY_CONTAG_ID, mContagContag.getContag());
                    final boolean isContact = mContagContag.getIs_contact();
                    final long id = mContagContag.getId();
                    pbContacts.setVisibility(View.VISIBLE);
                    log(TAG, System.currentTimeMillis() + " making request");
                    isListViewEnabled = false;

                    log(TAG, "pending request count"+getSpiceManager().getPendingRequestCount());
                    getSpiceManager().removeAllDataFromCache();

                    getSpiceManager().execute(contactUserRequest, new RequestListener<ContactResponse.ContactList>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            log(TAG, System.currentTimeMillis() + " error time");
                            pbContacts.setVisibility(View.GONE);
                            Router.startUserActivity(ContactListFragment.this.getActivity(), TAG, id);
                            isListViewEnabled = true;
                        }

                        @Override
                        public void onRequestSuccess(final ContactResponse.ContactList contactResponses) {
                            log(TAG, System.currentTimeMillis() + " success time");
                            DatabaseRequestListener mDatabaseRequestListener = new DatabaseRequestListener() {
                                @Override
                                public void onPreExecute() {

                                }

                                @Override
                                public Object onRequestExecute() {
                                    if (contactResponses.size() == 1) {
                                        ContactUtils.insertAndReturnContagContag(getActivity().getApplicationContext(), ContactUtils.getContact(contactResponses.get(0)),
                                                contactResponses.get(0).contagContactResponse, isContact);
                                    }
                                    return null;
                                }

                                @Override
                                public void onPostExecute(Object responseObject) {
                                    pbContacts.setVisibility(View.GONE);
                                    log(TAG, System.currentTimeMillis() + " success time opening user profile");
                                    Router.startUserActivity(ContactListFragment.this.getActivity(), TAG, id);
                                    isListViewEnabled = true;
                                }
                            };
                            DatabaseOperationTask databaseOperationTask = new DatabaseOperationTask(mDatabaseRequestListener);
                            databaseOperationTask.execute();
                        }
                    });
                } else if (contactListItem.type != Constants.Types.ITEM_NON_CONTAG && isListViewEnabled) {

                    log(TAG, "No wifi");
                    Router.startUserActivity(ContactListFragment.this.getActivity(), TAG, contactListItem.mContagContag.getId());
                }
            }
        });

        lvContacts.setOnScrollListener(new AbsListView.OnScrollListener() {
            /* @Override
             public void onScrollStateChanged(AbsListView view, int scrollState) {
                 if (scrollState != 0) {
                     hideKeyboard();
                 }
             }*/
            int mLastFirstVisibleItem = 0;
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

                //  toolbarHeight= getActivity().findViewById(R.id.rl_tb_container).getHeight();
                final ListView lw = lvContacts;
                if (scrollState != 0) {
                    hideKeyboard();

                }

                if(scrollState == 0) {
                    Log.i("a", "scrolling stopped...");

                }

/*
                if (view.getId() == lw.getId()) {
                    final int currentFirstVisibleItem = lw.getFirstVisiblePosition();
                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {

                        if(!toolbarHidden) {
                            getActivity().findViewById(R.id.rl_tb_container).animate().translationYBy(-toolbarHeight);
                            toolbarHidden=true;
                        }
                        Log.i("a", "scrolling down...");
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                       if(toolbarHidden) {
                           getActivity().findViewById(R.id.rl_tb_container).animate().translationYBy(toolbarHeight);
                           toolbarHidden=false;
                       }
                        Log.i("a", "scrolling up...");
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }*/
            }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

      /*  btnFilter = (ImageView) view.findViewById(R.id.btn_filter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleDropDown(true, v);

            }
        });*/
      /*  filterView = inflater.inflate(R.layout.popup_filter, container, false);
        filterView.findViewById(R.id.tv_filter_name).setOnClickListener(filterSelection);
        filterView.findViewById(R.id.tv_filter_blood).setOnClickListener(filterSelection);
        filterView.findViewById(R.id.tv_filter_platform).setOnClickListener(filterSelection);
        filterView.findViewById(R.id.tv_filter_interests).setOnClickListener(filterSelection);

        filterDropDown = new PopupWindow(filterView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
     *//*   filterDropDown.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        filterDropDown.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);*//*
        // popupWindow.setContentView(popupView);
     *//*   filterDropDown.setWindowLayoutMode(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        filterDropDown.setHeight(1);
        filterDropDown.setWidth(1);*//*
        // filterDropDown.setBackgroundDrawable(new BitmapDrawable());
        filterDropDown.setBackgroundDrawable(getResources().getDrawable(R.color.light_blue));
        // filterDropDown.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
      *//*  Rect location=locateView(filterV*/
        //   filterDropDown.showAtLocation(filterView, Gravity.TOP | Gravity.LEFT, location.top, location.bottom);*/

        etSearchBox = (EditText) view.findViewById(R.id.et_contact_search);
        etSearchBox.setOnEditorActionListener(this);
        etSearchBox.addTextChangedListener(this);
        pbContacts.setVisibility(View.VISIBLE);
        searchFilter = Constants.Arrays.SEARCH_FILTER[0];

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
    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        isListViewEnabled = true;

        Log.d("Contactlist","OnResume");

    }

    private void toggleDropDown(Boolean show, View v) {
        if (show)
        // filterDropD

        {
            Rect location = locateView(v);

            filterDropDown.showAtLocation(v, Gravity.TOP | Gravity.LEFT, location.right, location.bottom);
        } else
            filterDropDown.dismiss();
    }


    public View.OnClickListener filterSelection = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            TextView selectedFilter = (TextView) v;
            int position = -1;
            switch (id) {
                case R.id.tv_filter_name: {
                    position = 0;
                    break;
                }
                case R.id.tv_filter_platform: {
                    position = 1;
                    break;
                }
                case R.id.tv_filter_blood: {
                    position = 2;
                    break;
                }
                case R.id.tv_filter_interests: {
                    position = 3;
                    break;
                }
            }
            searchFilter = Constants.Arrays.SEARCH_FILTER[position];
            toggleFilterSelection(selectedFilter, position);
            new LoadContacts().execute();
            filterDropDown.dismiss();

        }
    };

    private void toggleFilterSelection(TextView selectedFilter, Integer position) {

        for (Integer filterID : filterIDS) {
            if (filterID != selectedFilter.getId())
                (filterView.findViewById(filterID)).setBackgroundColor(getResources().getColor(R.color.light_blue));
        }
        selectedFilter.setBackgroundColor(getResources().getColor(R.color.filter_selection));

    }

    private BroadcastReceiver brContactsUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Condev", "Broadcast received for contacts updated");
            new LoadContacts().execute();
        }
    };

    private BroadcastReceiver brContactRequestMade = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Condev", "Contact request made broadcast receiver");

        }
    };


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().length() != 0) {
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
        new LoadContacts().execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Log.d("SearchFilter", "Request failed");

    }

    @Override
    public void onRequestSuccess(ContactResponse.ContactList contactResponses) {
        pbContacts.setVisibility(View.GONE);
        if (contactResponses.size() == 1) {

            final ContactResponse mContactResponse = contactResponses.get(0);

            DatabaseRequestListener databaseRequestListener = new DatabaseRequestListener() {
                @Override
                public void onPreExecute() {
                }

                @Override
                public Object onRequestExecute() {
                    Contact mContact = ContactUtils.getContact(mContactResponse);
                    ContagContag mContagContag = ContactUtils.insertAndReturnContagContag(getActivity().getApplicationContext(), mContact,
                            mContactResponse.contagContactResponse, false);
                    return mContagContag;
                }

                @Override
                public void onPostExecute(Object responseObject) {
                    ContagContag mContagContag = (ContagContag) responseObject;
                    addContagContagToList(mContagContag);
                }
            };
            DatabaseOperationTask databaseOperationTask = new DatabaseOperationTask(databaseRequestListener);
            databaseOperationTask.execute();

        } else {
            showToast("No users found with that Contag id!");
        }

    }

    private void addContagContagToList(final ContagContag mContagContag) {
        DatabaseRequestListener mDatabaseRequestListener = new DatabaseRequestListener() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public Object onRequestExecute() {
                return ContactUtils.getContactListItem(getActivity().getApplicationContext(), mContagContag);
            }

            @Override
            public void onPostExecute(Object responseObject) {
                ContactListItem listItem = (ContactListItem) responseObject;
                contacts.clear();
                ArrayList<ContactListItem> contactListItems = new ArrayList<>();
                contactListItems.add(listItem);
                contacts.addAll(contactListItems);
                contactAdapter.notifyDataSetChanged();
            }
        };
        DatabaseOperationTask databaseOperationTask = new DatabaseOperationTask(mDatabaseRequestListener);
        databaseOperationTask.execute();
    }

    private void doSearch(final String query) {
        if (mSearchContacts != null) {
            mSearchContacts.cancel(true);
        }

        if (query.length() == 8 && RegexUtils.isContagId(query)) {
            DatabaseRequestListener databaseRequestListener = new DatabaseRequestListener() {
                @Override
                public void onPreExecute() {

                }

                @Override
                public Object onRequestExecute() {
                    ContagContag mContagContag = ContactUtils.getContagContagByContagID(getActivity().getApplicationContext(), query);
                    return null;
                }

                @Override
                public void onPostExecute(Object responseObject) {
                    ContagContag mContagContag = (ContagContag) responseObject;
                    if (mContagContag != null) {
                        addContagContagToList(mContagContag);
                    } else {
                        pbContacts.setVisibility(View.VISIBLE);
                        ContactRequest contactUserRequest = new ContactRequest(Constants.Types.REQUEST_GET_USER_BY_CONTAG_ID, query);
                        getSpiceManager().execute(contactUserRequest, ContactListFragment.this);
                    }
                }
            };
            DatabaseOperationTask databaseOperationTask = new DatabaseOperationTask(databaseRequestListener);
            databaseOperationTask.execute(databaseOperationTask);
        } else {
            mSearchContacts = new SearchContacts();
            mSearchContacts.execute(query, searchFilter);
        }

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




            ArrayList<ContactListItem> contactListItems = new ArrayList<>();
            ContagContagDao mContagContagDao = session.getContagContagDao();
            List<ContagContag> contagContacts = mContagContagDao.queryBuilder().
                    where(ContagContagDao.Properties.Id.notEq(PrefUtils.getCurrentUserID()),
                            ContagContagDao.Properties.Is_contact.eq(true)).orderAsc(ContagContagDao.Properties.Name).list();

            if (contagContacts != null) {
                for (ContagContag mContagContag : contagContacts) {

                    ContactListItem mContactListItem = ContactUtils.getContactListItem(getActivity().getApplicationContext(), mContagContag);
                    contactListItems.add(mContactListItem);
                }
            }

             /* InterestDao mInterestDao = session.getInterestDao();
            List<Interest> interests = mInterestDao.queryBuilder().
                    orderAsc(InterestDao.Properties.ContagUserId).list();

            for (Interest i : interests) {
                Log.d("ConList", "The interest user id is: " + i.getContagUserId());
                Log.d("ConList", "The interest is: " + i.getName());
            }
*/
            ContactDao mContactDao = session.getContactDao();
            List<Contact> contacts = mContactDao.queryBuilder().where(ContactDao.Properties.IsOnContag.eq(false)).
                    orderAsc(ContactDao.Properties.ContactName).list();

            for (Contact contact : contacts) {
                contactListItems.add(new ContactListItem(contact, Constants.Types.ITEM_NON_CONTAG));
            }

            return contactListItems;
        }

        @Override
        protected void onPostExecute(ArrayList<ContactListItem> contactListItems) {
            if (contactListItems.size() != 0) {
                contacts.clear();
                contacts.addAll(contactListItems);
                pbContacts.setVisibility(View.GONE);
                contactAdapter.notifyDataSetChanged();

            }
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

                ArrayList<ContactListItem> contactListItems = new ArrayList<>();

                String query = params[0];
                String filter = params[1];

                switch (filter) {
                    case Constants.Values.FILTER_NAME: {
                        query = query + "%";
                        ContagContagDao mContagContagDao = session.getContagContagDao();
                        List<ContagContag> contagContacts = mContagContagDao.queryBuilder().
                                where(ContagContagDao.Properties.Id.notEq(PrefUtils.getCurrentUserID())
                                        , ContagContagDao.Properties.Name.like(query),
                                        ContagContagDao.Properties.Is_contact.eq(true)).
                                orderAsc(ContagContagDao.Properties.Name).list();

                        if (contagContacts != null) {
                            for (ContagContag mContagContag : contagContacts) {
                                ContactListItem item = ContactUtils.getContactListItem(getActivity().getApplicationContext(), mContagContag);
                                contactListItems.add(item);
                            }
                        }

                        ContactDao mContactDao = session.getContactDao();
                        List<Contact> contacts = mContactDao.queryBuilder().where(ContactDao.Properties.IsOnContag.eq(false)
                                , ContactDao.Properties.ContactName.like(query)).orderAsc(ContactDao.Properties.ContactName).list();

                        for (Contact contact : contacts) {
                            contactListItems.add(new ContactListItem(contact, Constants.Types.ITEM_NON_CONTAG));
                        }
                        if (contactListItems.size() != 0) {
                            break;
                        }
                    }
//                    case Constants.Values.FILTER_BLOOD_GROUP: {
//                        query = query + "%";
//                        ContagContagDao mContagContagDao = session.getContagContagDao();
//                        List<ContagContag> contagContacts = mContagContagDao.queryBuilder().
//                                where(ContagContagDao.Properties.Id.notEq(PrefUtils.getCurrentUserID())
//                                        , ContagContagDao.Properties.BloodGroup.like(query)).
//                                orderAsc(ContagContagDao.Properties.Name).list();
//
//                        if (contagContacts != null) {
//                            for (ContagContag mContagContag : contagContacts) {
//                                ContactListItem item = ContactUtils.getContactListItem(getActivity().getApplicationContext(), mContagContag);
//                                contactListItems.add(item);
//                            }
//                        }
//                        break;
//                    }
//                    case Constants.Values.FILTER_PLATFORM: {
//                        query = "%" + query + "%";
//                        SocialProfileDao mSocialProfileDao = session.getSocialProfileDao();
//                        List<SocialProfile> socialProfiles =
//                                mSocialProfileDao.queryBuilder().where(
//                                        SocialProfileDao.Properties.ContagUserId.notEq(PrefUtils.getCurrentUserID()),
//                                        SocialProfileDao.Properties.Social_platform.like(query)).list();
//                        List<Long> userIds = new ArrayList<>();
//
//                        for (SocialProfile profile : socialProfiles) {
//                            userIds.add(profile.getContagUserId());
//                        }
//
//                        ContagContagDao mContagContagDao = session.getContagContagDao();
//                        List<ContagContag> contagContacts = mContagContagDao.queryBuilder().
//                                where(ContagContagDao.Properties.Id.in(userIds)).
//                                orderAsc(ContagContagDao.Properties.Name).list();
//
//                        if (contagContacts != null) {
//                            for (ContagContag mContagContag : contagContacts) {
//                                ContactListItem item = ContactUtils.getContactListItem(getActivity().getApplicationContext(), mContagContag);
//                                contactListItems.add(item);
//                            }
//                        }
//                        break;
//                    }
                    case Constants.Values.FILTER_INTERESTS: {
                        query = query + "%";
                        InterestDao mInterestDao = session.getInterestDao();
                        List<Interest> interests = mInterestDao.queryBuilder().
                                where(InterestDao.Properties.Name.like(query),
                                        InterestDao.Properties.ContagUserId.notEq(PrefUtils.getCurrentUserID())).list();
                        List<Long> userIds = new ArrayList<>();
                        for (Interest interest : interests) {
                            userIds.add(interest.getContagUserId());
                        }
                        ContagContagDao mContagContagDao = session.getContagContagDao();
                        List<ContagContag> contagContacts = mContagContagDao.queryBuilder().
                                where(ContagContagDao.Properties.Id.in(userIds)).
                                orderAsc(ContagContagDao.Properties.Name).list();

                        if (contagContacts != null) {
                            for (ContagContag mContagContag : contagContacts) {
                                ContactListItem item = ContactUtils.getContactListItem(getActivity().getApplicationContext(), mContagContag);
                                contactListItems.add(item);
                            }
                        }
                        break;
                    }
                }

                return contactListItems;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ContactListItem> contactListItems) {
            contacts.clear();
            contacts.addAll(contactListItems);
            contactAdapter.notifyDataSetChanged();
            pbContacts.setVisibility(View.INVISIBLE);
        }
    }


    public static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

}
