package com.contag.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.adapter.FeedsAdapter;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.model.ContactResponse;
import com.contag.app.model.ContagContag;
import com.contag.app.model.FeedsResponse;
import com.contag.app.request.ContactRequest;
import com.contag.app.request.FeedsRequest;
import com.contag.app.util.ContactUtils;
import com.contag.app.util.DeviceUtils;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

public class FeedsFragment extends BaseFragment implements AdapterView.OnItemClickListener,
        RequestListener<FeedsResponse.FeedList> {

    private static final String TAG = FeedsFragment.class.getName();

    private ArrayList<FeedsResponse> feeds;
    private ListView lvFeeds;
    private FeedsAdapter feedsAdapter;
    private boolean isLoading = false, isListViewEnabled;
    private View pbFeeds;



    public static FeedsFragment newInstance() {
        FeedsFragment ff = new FeedsFragment();
        Bundle args = new Bundle();
        ff.setArguments(args);
        return ff;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        lvFeeds = (ListView) view.findViewById(R.id.lv_feeds);
        feeds = new ArrayList<>();
        feedsAdapter = new FeedsAdapter(getActivity(), feeds);
        pbFeeds = view.findViewById(R.id.pb_feeds);
        lvFeeds.setAdapter(feedsAdapter);
        isListViewEnabled = true;
        lvFeeds.setOnItemClickListener(this);
        lvFeeds.setOnScrollListener(new AbsListView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }



            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
              /* if (view.getId() == lvFeeds.getId()) {
                    final int currentFirstVisibleItem = lvFeeds.getFirstVisiblePosition();

                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        // getSherlockActivity().getSup getSupportActionBar().show();
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }*/

                if (totalItemCount - (firstVisibleItem + visibleItemCount) <= 3
                        && !isLoading && isListViewEnabled) {
                    Log.d("FeedSizeDebug", "Inside onCreate view going to fetch feeds") ;
                    int start = feeds.size() == 0 ? 0 : feeds.size();
                    int end = start + 10;
                    getFeeds(start, end);

                }
            }
        });
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(brContactsUpdated,
                new IntentFilter(getResources().getString(R.string.intent_filter_contacts_updated)));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(brNonContactContagUserCreated,
                new IntentFilter(getResources().getString(R.string.intent_filter_contag_contact_inserted)));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brNonContactContagUserCreated);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brContactsUpdated);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(feeds.size() != 0) {
            Log.d("FeedSizeDebug", "Feeds size in onResume: " + feeds.size()) ;
            feeds.clear();
            Log.d("FeedSizeDebug", "Feeds size after clearing in onResume: " + feeds.size()) ;
            feedsAdapter.notifyDataSetChanged();
            getFeeds(0,10);
        }

    }

    private void getFeeds(int start, int end){
        Log.d("FeedSizeDebug", "Going to request") ;
        Log.d("FeedSizeDebug", "Start " + start + " End: "+ end ) ;
        pbFeeds.setVisibility(View.VISIBLE);
        FeedsRequest feedsRequest = new FeedsRequest(start, end);
        getSpiceManager().execute(feedsRequest, FeedsFragment.this);
        isLoading = true;
    }

    private BroadcastReceiver brContactsUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (feeds.size() == 0) {
                FeedsRequest fr = new FeedsRequest(0, 10);
                getSpiceManager().execute(fr, FeedsFragment.this);
                isLoading = true;
            }
        }
    };


    private BroadcastReceiver brNonContactContagUserCreated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long userID = intent.getLongExtra(Constants.Keys.KEY_USER_ID, 0l);
            Router.startUserActivity(getActivity(), TAG, userID);
            pbFeeds.setVisibility(View.GONE);
        }
    };


    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Log.d("FeedSizeDebug", "Request failed") ;
        pbFeeds.setVisibility(View.GONE);
    }

    @Override
    public void onRequestSuccess(FeedsResponse.FeedList feedsResponses) {
        Log.d("FeedSizeDebug", "Request was a success") ;
        Log.d("FeedSizeDebug", "Feeds size in onRequestSuccess: " + feeds.size()) ;
        if (feedsResponses.size() != 0) {
            feeds.addAll(feedsResponses);
            Log.d("FeedSizeDebug", "Feeds size in onRequestSuccess: " + feeds.size()) ;
            feedsAdapter.notifyDataSetChanged();

            isLoading = false;
        } else {
            isLoading = true;
        }
        log(TAG, "hiding the progress bar after the feeds are fetched");
        pbFeeds.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (DeviceUtils.isWifiConnected(getActivity())) {
            log(TAG, "progress bar made visible on feeds item click and requesting user profile");
            pbFeeds.setVisibility(View.VISIBLE);
            isListViewEnabled = false;
            new GetUserAndShowProfile().execute(feeds.get(position).fromUser);
        } else {
            Router.startUserActivity(getBaseActivity(), TAG, feeds.get(position).fromUser);
        }
    }

    private class GetUserAndShowProfile extends AsyncTask<Long, Void, ContagContag> {
        private long userID;
        @Override
        protected ContagContag doInBackground(Long... params) {
            userID = params[0];
            return ((BaseActivity) FeedsFragment.this.getActivity()).getUser(userID);
        }

        @Override
        protected void onPostExecute(ContagContag mContagContag) {
            if (mContagContag != null) {
                ContactRequest contactUserRequest = new ContactRequest(Constants.Types.REQUEST_GET_USER_BY_CONTAG_ID, mContagContag.getContag());
                final boolean isContact = mContagContag.getIs_contact();
                final long id = mContagContag.getId();
                log(TAG, "making progress bar visible inside post execute");
                pbFeeds.setVisibility(View.VISIBLE);
                getSpiceManager().execute(contactUserRequest, new RequestListener<ContactResponse.ContactList>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        log(TAG, "hiding progress bar in request failure while fetching user");
                        pbFeeds.setVisibility(View.GONE);
                        isListViewEnabled = true;
                        Router.startUserActivity(FeedsFragment.this.getActivity(), TAG, id);
                    }

                    @Override
                    public void onRequestSuccess(ContactResponse.ContactList contactResponses) {
                        if (contactResponses.size() == 1) {
                            ContactUtils.insertAndReturnContagContag(getActivity().getApplicationContext(), ContactUtils.getContact(contactResponses.get(0)),
                                    contactResponses.get(0).contagContactResponse, isContact);
                        }
                        log(TAG, "hiding progress bar afer user fetched");
                        pbFeeds.setVisibility(View.GONE);
                        isListViewEnabled = true;
                        Router.startUserActivity(FeedsFragment.this.getActivity(), TAG, id);
                    }
                });

            } else {
                Router.startServiceToGetUserByUserID(getActivity(), userID, false);
                showToast("Please wait while we sync your contacts");
            }
        }
    }
}
