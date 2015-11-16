package com.contag.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.activity.NotificationsActivity;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.model.NotificationsResponse;
import com.contag.app.model.User;
import com.contag.app.util.PrefUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class NotificationsAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mCtxt;
    private ArrayList<NotificationsResponse> notifications;

    public NotificationsAdapter(Context mContext, ArrayList<NotificationsResponse> notifications) {
        this.mCtxt = mContext;
        this.notifications = notifications;
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
            mViewHolder.shareButton = (Button) convertView.findViewById(R.id.btn_share);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(mCtxt).load(Constants.Urls.BASE_URL + notification.avatarUrl).error(R.drawable.default_profile_pic_small).into(mViewHolder.ivUsrProfilePic);
        mViewHolder.tvNotificationsTxt.setText(notification.text);

        mViewHolder.ivUsrProfilePic.setTag(position);
        mViewHolder.ivUsrProfilePic.setOnClickListener(this);

        if (notification.notificationType.equals(Constants.Keys.PROFILE_REQUEST_ADD) ||
                notification.notificationType.equals(Constants.Keys.PROFILE_REQUEST_SHARE)) {

            mViewHolder.shareButton.setVisibility(View.VISIBLE);
            mViewHolder.shareButton.setTag(position);
            mViewHolder.shareButton.setOnClickListener(this);

            if (notification.notificationType.equals(Constants.Keys.PROFILE_REQUEST_ADD)) {
                mViewHolder.shareButton.setText("Add");
            } else {
                mViewHolder.shareButton.setText("Share");
            }

            mViewHolder.shareButton.setOnClickListener(this);

        } else {
            mViewHolder.shareButton.setVisibility(View.INVISIBLE);

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
                Router.startUserActivity(mCtxt, NotificationsActivity.TAG, notificationsResponse.fromUser);
                break;
            }
            case R.id.btn_share: {
                int position = (int) v.getTag();
                NotificationsResponse notificationsResponse = notifications.get(position);
                if (notificationsResponse.notificationType.equalsIgnoreCase(Constants.Keys.PROFILE_REQUEST_ADD)) {
                    Router.startUserActivity(mCtxt, NotificationsActivity.TAG, PrefUtils.getCurrentUserID());
                } else if(notificationsResponse.notificationType.equalsIgnoreCase(Constants.Keys.PROFILE_REQUEST_SHARE)) {
                    String userIDS = User.getSharesAsString(notificationsResponse.fieldName, String.valueOf(notificationsResponse.fromUser), mCtxt);
                    Router.startUserServiceForPrivacy(mCtxt, notificationsResponse.fieldName, false, userIDS);
                    notifications.remove(notificationsResponse);
                    notifyDataSetChanged();
                }
                break;
            }
        }
    }

    public static class ViewHolder {
        //protected TextView tvUsrName;
        protected TextView tvNotificationsTxt;
        protected ImageView ivUsrProfilePic;
        protected Button shareButton;
    }
}
