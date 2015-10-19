package com.contag.app.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.contag.app.R;
import com.contag.app.adapter.NotificationsAdapter;
import com.contag.app.model.NotificationsResponse;
import com.contag.app.request.NotificationsRequest;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

public class NotificationsActivity extends BaseActivity  implements AdapterView.OnItemClickListener, RequestListener<NotificationsResponse.NotificationList>{

    public static final String TAG = NotificationsActivity.class.getName();
    private boolean isLoading = false;
    private ArrayList<NotificationsResponse> notifications;
    private ListView lvNotifications;
    private NotificationsAdapter notificationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

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
}
