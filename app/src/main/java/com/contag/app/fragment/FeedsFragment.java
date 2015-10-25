package com.contag.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.contag.app.R;
import com.contag.app.adapter.FeedsAdapter;
import com.contag.app.config.Router;
import com.contag.app.model.FeedsResponse;
import com.contag.app.request.FeedsRequest;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

/**
 * Created by tanay on 23/9/15.
 */
public class FeedsFragment extends BaseFragment implements AdapterView.OnItemClickListener,
        RequestListener<FeedsResponse.FeedList> {

    private static final String TAG = FeedsFragment.class.getName();

    private ArrayList<FeedsResponse> feeds;
    private ListView lvFeeds;
    private FeedsAdapter feedsAdapter;
    private boolean isLoading = false;

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
        lvFeeds.setAdapter(feedsAdapter);
        lvFeeds.setOnItemClickListener(this);
        lvFeeds.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount - (firstVisibleItem + visibleItemCount) <= 3 && !isLoading) {
                    int start = feeds.size() == 0 ? 0 : feeds.size();
                    FeedsRequest fr = new FeedsRequest(start, 10);
                    getSpiceManager().execute(fr, FeedsFragment.this);
                    isLoading = true;
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
    private BroadcastReceiver brContactsUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(feeds.size() == 0) {
                FeedsRequest fr = new FeedsRequest(0, 10);
                getSpiceManager().execute(fr, FeedsFragment.this);
                isLoading = true;
            }
        }
    };


    @Override
    public void onRequestFailure(SpiceException spiceException) {

    }

    @Override
    public void onRequestSuccess(FeedsResponse.FeedList feedsResponses) {
        if(feedsResponses.size() != 0) {
            feeds.addAll(feedsResponses);
            feedsAdapter.notifyDataSetChanged();
        }
        isLoading = false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Router.startUserActivity(getBaseActivity(), TAG, feeds.get(position).fromUser);
    }
}
