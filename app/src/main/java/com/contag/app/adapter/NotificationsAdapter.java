package com.contag.app.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.activity.NotificationsActivity;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.model.ContactResponse;
import com.contag.app.model.ContagContag;
import com.contag.app.model.NotificationsResponse;
import com.contag.app.model.User;
import com.contag.app.request.ContactRequest;
import com.contag.app.util.ContactUtils;
import com.contag.app.util.DeviceUtils;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class NotificationsAdapter extends BaseAdapter implements View.OnClickListener {


    public static final String TAG = NotificationsAdapter.class.getName();
    private Context mCtxt;
    private ArrayList<NotificationsResponse> notifications;
    private SpiceManager mSpiceManager;

    public NotificationsAdapter(Context mContext, ArrayList<NotificationsResponse> notifications, SpiceManager mSpiceManager) {
        this.mCtxt = mContext;
        this.notifications = notifications;
        this.mSpiceManager = mSpiceManager;
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return notifications.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        NotificationsResponse notification = (NotificationsResponse) getItem(position);

        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = ((LayoutInflater) mCtxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                    inflate(R.layout.items_notifications, parent, false);
            mViewHolder.ivUsrProfilePic = (ImageView) convertView.findViewById(R.id.iv_notifications_usr_img);
            mViewHolder.tvNotificationsTxt = (TextView) convertView.findViewById(R.id.tv_notifications_txt);
            mViewHolder.btnShare = (Button) convertView.findViewById(R.id.btn_share);
            mViewHolder.btnReject = (Button) convertView.findViewById(R.id.btn_notif_reject);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(mCtxt).load(Constants.Urls.BASE_URL + notification.avatarUrl).error(R.drawable.default_profile_pic_small).into(mViewHolder.ivUsrProfilePic);
        mViewHolder.tvNotificationsTxt.setText(notification.text);

        mViewHolder.ivUsrProfilePic.setTag(position);
        mViewHolder.ivUsrProfilePic.setOnClickListener(this);

        mViewHolder.btnReject.setTag(position);
        mViewHolder.btnReject.setOnClickListener(this);

        if (notification.notificationType.equals(Constants.Keys.KEY_PROFILE_REQUEST_ADD) ||
                notification.notificationType.equals(Constants.Keys.KEY_PROFILE_REQUEST_SHARE)) {

            mViewHolder.btnShare.setVisibility(View.VISIBLE);
            mViewHolder.btnShare.setTag(position);
            mViewHolder.btnShare.setOnClickListener(this);

            if (notification.notificationType.equals(Constants.Keys.KEY_PROFILE_REQUEST_ADD)) {
                mViewHolder.btnShare.setText("Add");
            } else {
                mViewHolder.btnShare.setText("Share");
            }

            mViewHolder.btnShare.setOnClickListener(this);

        } else {
            mViewHolder.btnShare.setVisibility(View.INVISIBLE);

        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_notifications_usr_img: {
                int position = (int) v.getTag();
                NotificationsResponse notificationsResponse = notifications.get(position);
                if (DeviceUtils.isWifiConnected(mCtxt)) {
                    new GetUserAndShowProfile().execute(notificationsResponse.fromUser);
                } else {
                    Router.startUserActivity(mCtxt, NotificationsActivity.TAG, notificationsResponse.fromUser);
                }
                break;
            }
            case R.id.btn_share: {
                int position = (int) v.getTag();
                NotificationsResponse notificationsResponse = notifications.get(position);
                if (notificationsResponse.notificationType.equalsIgnoreCase(Constants.Keys.KEY_PROFILE_REQUEST_ADD)) {
                    Bundle requestBundle = new Bundle();
                    requestBundle.putLong(Constants.Keys.KEY_REQUEST_ID, notificationsResponse.request);
                    requestBundle.putLong(Constants.Keys.KEY_REQUEST_FROM_USER_ID, notificationsResponse.fromUser);
                    requestBundle.putString(Constants.Keys.KEY_REQUEST_FROM_USER_NAME, notificationsResponse.requesterName);
                    Router.startEditUserActivity(mCtxt, NotificationsActivity.TAG, PrefUtils.getCurrentUserID(), requestBundle,
                            Integer.parseInt(notificationsResponse.fieldCategory), notificationsResponse.fieldName, true);
                } else if (notificationsResponse.notificationType.equalsIgnoreCase(Constants.Keys.KEY_PROFILE_REQUEST_SHARE)) {
                    String userIDS = User.getSharesAsString(notificationsResponse.fieldName, String.valueOf(notificationsResponse.fromUser), mCtxt);
                    Log.d(TAG, userIDS);
                    Router.startUserServiceForPrivacy(mCtxt, notificationsResponse.fieldName, false, userIDS);
                    Router.sendFieldRequestNotificationResponse(mCtxt, notificationsResponse.request,
                            Constants.Types.SERVICE_ALLOW_FIELD_REQUEST);
                    notifications.remove(notificationsResponse);
                    notifyDataSetChanged();
                }
                break;
            }
            case R.id.btn_notif_reject: {
                int position = (int) v.getTag();
                NotificationsResponse notificationsResponse = notifications.get(position);
                Router.sendFieldRequestNotificationResponse(mCtxt, notificationsResponse.request,
                        Constants.Types.SERVICE_REJECT_FIELD_REQUEST);
                notifications.remove(notificationsResponse);
                notifyDataSetChanged();
                break;
            }
        }
    }


    private class GetUserAndShowProfile extends AsyncTask<Long, Void, ContagContag> {
        @Override
        protected ContagContag doInBackground(Long... params) {

            return ((BaseActivity) mCtxt).getUser(params[0]);
        }

        @Override
        protected void onPostExecute(ContagContag mContagContag) {
            if (mContagContag != null) {
                ContactRequest contactUserRequest = new ContactRequest(Constants.Types.REQUEST_GET_USER_BY_CONTAG_ID, mContagContag.getContag());
                final boolean isContact = mContagContag.getIs_contact();
                final long id = mContagContag.getId();
                mSpiceManager.execute(contactUserRequest, new RequestListener<ContactResponse.ContactList>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Router.startUserActivity(mCtxt, TAG, id);
                    }

                    @Override
                    public void onRequestSuccess(ContactResponse.ContactList contactResponses) {
                        if (contactResponses.size() == 1) {
                            ContactUtils.insertAndReturnContagContag(mCtxt.getApplicationContext(), ContactUtils.getContact(contactResponses.get(0)),
                                    contactResponses.get(0).contagContactResponse, isContact);
                        }
                        Router.startUserActivity(mCtxt, TAG, id);
                    }
                });
            }
        }
    }

    public static class ViewHolder {
        //protected TextView tvUsrName;
        protected TextView tvNotificationsTxt;
        protected ImageView ivUsrProfilePic;
        protected Button btnShare;
        protected Button btnReject;
    }
}
