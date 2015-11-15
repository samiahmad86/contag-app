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
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.model.NotificationsResponse;
import com.contag.app.model.User;
import com.contag.app.util.PrefUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class NotificationsAdapter extends BaseAdapter {

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
        Log.d("Nof", "Inside get view of notifications adapter");
        ViewHolder mViewHolder;
        NotificationsResponse notification = (NotificationsResponse) getItem(position);
        final Long objectID = notification.fromUser;

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
        //vh.tvUsrName.setText(notification.name);
        mViewHolder.tvNotificationsTxt.setText(notification.text);

        mViewHolder.ivUsrProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.startUserActivity(mCtxt, "Notifications", objectID);
            }
        });

        if (notification.notificationType.equals("profile_request_add") ||
                notification.notificationType.equals("profile_request_share")) {

            mViewHolder.shareButton.setVisibility(View.VISIBLE);

            if (notification.notificationType.equals("profile_request_add")) {
                mViewHolder.shareButton.setText("Add");
                mViewHolder.shareButton.setTag(0);
            } else {
                mViewHolder.shareButton.setText("Share");
                mViewHolder.shareButton.setTag(1);
            }
            final int requestType = (int) mViewHolder.shareButton.getTag();
            final String requestBy = String.valueOf(notification.user);
            final String fieldName = notification.requesterName;

            mViewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String userIDS = User.getSharesAsString(fieldName, requestBy, mCtxt);
                    Router.startUserServiceForPrivacy(mCtxt, fieldName, false, userIDS);
                    if (requestType == 0) {
                        Router.startUserActivity(mCtxt, "Notifications", PrefUtils.getCurrentUserID());
                    }
                }
            });

        } else {
            mViewHolder.shareButton.setVisibility(View.INVISIBLE);

        }
        return convertView;
    }

    public static class ViewHolder {
        //protected TextView tvUsrName;
        protected TextView tvNotificationsTxt;
        protected ImageView ivUsrProfilePic;
        protected Button shareButton;
    }
}
