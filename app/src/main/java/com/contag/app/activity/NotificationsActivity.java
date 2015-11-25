package com.contag.app.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.adapter.NotificationsAdapter;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.fragment.NavDrawerFragment;
import com.contag.app.model.ContactResponse;
import com.contag.app.model.ContagContag;
import com.contag.app.model.NotificationsResponse;
import com.contag.app.request.ContactRequest;
import com.contag.app.request.NotificationsRequest;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationsActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        NavDrawerFragment.OnFragmentInteractionListener, View.OnClickListener,
        RequestListener<NotificationsResponse.NotificationList> {

    public static final String TAG = NotificationsActivity.class.getName();
    private boolean isLoading = false;
    private ArrayList<NotificationsResponse> notifications;
    private NotificationsAdapter notificationsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        setUpActionBar(R.id.tb_home);

        new LoadUser().execute();

        findViewById(R.id.iv_user_photo).setOnClickListener(this);

        Button btnBack = (Button) findViewById(R.id.btn_back);

        ListView lvNotifications = (ListView) findViewById(R.id.lv_notifications);
        notifications = new ArrayList<>();
        notificationsAdapter = new NotificationsAdapter(this, notifications, getSpiceManager());
        lvNotifications.setAdapter(notificationsAdapter);
        lvNotifications.setOnItemClickListener(this);
        btnBack.setOnClickListener(this);
        lvNotifications.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount - (firstVisibleItem + visibleItemCount) <= 5 && !isLoading) {
                    Log.d("Nofubar", "Making the request now") ;
                    int start = notifications.size() == 0 ? 0 : notifications.size();
                    int end = start + 10 ;
                    getNotifications(start, end);
                }
            }
        });

        //Set new notifications count to 0

        PrefUtils.setNewNotificationCount(0);

     }


    @Override
    public void onRequestFailure(SpiceException spiceException) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if(notifications.size() != 0) {
            notifications.clear();
            notificationsAdapter.notifyDataSetChanged();
            getNotifications(0, 10);
        }
    }

    private void getNotifications(int start, int end){
        Log.d("Nofubar", "Start value is: " + start) ;
        Log.d("Nofubar", "End value is: "+ end) ;
        NotificationsRequest fr = new NotificationsRequest(start, end);
        getSpiceManager().execute(fr, NotificationsActivity.this);
        isLoading = true;
    }

    public void hideNotification(long notificationID){
        Log.d("notifdelete", "Activity method called") ;
        NotificationsRequest fr = new NotificationsRequest(notificationID, "delete");
        getSpiceManager().execute(fr, new RequestListener<NotificationsResponse.NotificationList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(NotificationsResponse.NotificationList notificationsResponses) {
                //showToast("Notification removed!");
            }
        }) ;
    }

    public void addContagUser(long notificationID){
        Log.d("notifadd", "Add contag user called with id: "+ notificationID) ;
        ContactRequest cr = new ContactRequest(Constants.Types.REQUEST_ADD_CONTAG_NOTIFICATION, notificationID) ;
        getSpiceManager().execute(cr, new RequestListener<ContactResponse.ContactList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(ContactResponse.ContactList contactResponses) {
                showToast("Contag user was added to your contact book!");
            }
        });
    }
    @Override
    public void onRequestSuccess(NotificationsResponse.NotificationList notificationsResponses) {
        Log.d("Nofubar", "Current size of notifications: " + notifications.size()) ;
        Log.d("Nofubar", "Response from server is of size: " + String.valueOf(notificationsResponses.size()));

        if (notificationsResponses.size() != 0) {
            notifications.addAll(notificationsResponses);
            notificationsAdapter.notifyDataSetChanged();
            isLoading = false;
        } else {
            isLoading = true;
        }

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
            case R.id.btn_back: {
                this.finish();
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

            Picasso.with(NotificationsActivity.this)
                    .load(ccUser.getAvatarUrl())
                    .placeholder(R.drawable.default_profile_pic_small)
                    .into(((ImageView) tbHome.findViewById(R.id.iv_user_photo)));

            Picasso.with(NotificationsActivity.this)
                    .load(ccUser.getAvatarUrl())
                    .placeholder(R.drawable.default_profile_pic_small)
                    .into((ImageView) findViewById(R.id.iv_header_pic));

        }
    }
}
