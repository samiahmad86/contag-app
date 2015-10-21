package com.contag.app.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.adapter.NotificationsAdapter;
import com.contag.app.config.Router;
import com.contag.app.fragment.NavDrawerFragment;
import com.contag.app.model.ContagContag;
import com.contag.app.model.NotificationsResponse;
import com.contag.app.request.NotificationsRequest;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationsActivity extends BaseActivity  implements AdapterView.OnItemClickListener,
        NavDrawerFragment.OnFragmentInteractionListener, View.OnClickListener,
        RequestListener<NotificationsResponse.NotificationList>{

    public static final String TAG = NotificationsActivity.class.getName();
    private boolean isLoading = false;
    private ArrayList<NotificationsResponse> notifications;
    private ListView lvNotifications;
    private NotificationsAdapter notificationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        setUpActionBar(R.id.tb_home);

        setUpDrawer(R.id.drawer_layout, R.id.tb_home);

        new LoadUser().execute();

        findViewById(R.id.iv_user_photo).setOnClickListener(this);

        lvNotifications = (ListView) findViewById(R.id.lv_notifications);
        notifications = new ArrayList<>();
        notificationsAdapter = new NotificationsAdapter(this, notifications);
        lvNotifications.setAdapter(notificationsAdapter);
        lvNotifications.setOnItemClickListener(this);
        lvNotifications.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount - (firstVisibleItem + visibleItemCount) <= 3 && !isLoading) {
                    int start = notifications.size() == 0 ? 0 : notifications.size();
                    NotificationsRequest fr = new NotificationsRequest(start, 10) ;
                    getSpiceManager().execute(fr, NotificationsActivity.this);
                    isLoading = true;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifications, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {

    }

    @Override
    public void onRequestSuccess(NotificationsResponse.NotificationList notificationsResponses) {
        if(notificationsResponses.size() != 0) {
            notifications.addAll(notificationsResponses);
            notificationsAdapter.notifyDataSetChanged();
        }
        isLoading = false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      // Router.startUserActivity(this, TAG, Long.parseLong(notifications.get(position).objectId, 10));
    }

    @Override
    public void onFragmentInteraction(int value) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_user_photo: {

                Router.startUserActivity(this, TAG, PrefUtils.getCurrentUserID());
                break;
            }
        }
    }

    private class LoadUser extends AsyncTask<Void, Void, ContagContag> {
        @Override
        protected ContagContag doInBackground(Void... params) {
            return NotificationsActivity.this.getCurrentUser();
        }

        @Override
        protected void onPostExecute(ContagContag ccUser) {
            Toolbar tbHome = (Toolbar) NotificationsActivity.this.findViewById(R.id.tb_home);
            ((TextView) tbHome.findViewById(R.id.tv_user_name)).setText(ccUser.getName());
            ((TextView) tbHome.findViewById(R.id.tv_user_contag_id)).setText(ccUser.getContag());
            Picasso.with(NotificationsActivity.this).load(ccUser.getAvatarUrl()).placeholder(R.drawable.default_profile_pic_small).
                    into(((ImageView) tbHome.findViewById(R.id.iv_user_photo)));

        }
    }
}
