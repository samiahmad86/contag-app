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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

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
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount - (firstVisibleItem + visibleItemCount) <= 3 && !isLoading && isListViewEnabled) {
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

    }

    @Override
    public void onResume() {
        super.onResume();
        isLoading = true;
        feeds.clear();
        feedsAdapter.notifyDataSetChanged();
        if(feeds.size()!= 0) {
            getFeeds(0,10);
        }

    }

    private void getFeeds(int start, int end){
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


    @Override
    public void onRequestFailure(SpiceException spiceException) {
        pbFeeds.setVisibility(View.GONE);
    }

    @Override
    public void onRequestSuccess(FeedsResponse.FeedList feedsResponses) {
        if (feedsResponses.size() != 0) {
            feeds.addAll(feedsResponses);
            feedsAdapter.notifyDataSetChanged();
            log(TAG, "is loading is set to false");
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
        @Override
        protected ContagContag doInBackground(Long... params) {

            return ((BaseActivity) FeedsFragment.this.getActivity()).getUser(params[0]);
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
                                    contactResponses.get(0).contagContactUser, isContact);
                        }
                        log(TAG, "hiding progress bar afer user fetched");
                        pbFeeds.setVisibility(View.GONE);
                        isListViewEnabled = true;
                        Router.startUserActivity(FeedsFragment.this.getActivity(), TAG, id);
                    }
                });

            } else {
                pbFeeds.setVisibility(View.GONE);
                showToast("Please wait while we sync your contacts");
            }
        }
    }
}
