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
import com.contag.app.config.Router;
import com.contag.app.model.NotificationsResponse;
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
        Log.d("Nof", "Inside get view of notifications adapter") ;
        ViewHolder vh;
        NotificationsResponse notification = (NotificationsResponse) getItem(position);
        final Long objectID = Long.parseLong(notification.objectId, 10) ;

        if(convertView == null) {
            vh = new ViewHolder();
            convertView = ((LayoutInflater) mCtxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                    inflate(R.layout.items_notifications, parent, false);
            vh.ivUsrProfilePic = (ImageView) convertView.findViewById(R.id.iv_notifications_usr_img);
            vh.tvNotificationsTxt = (TextView) convertView.findViewById(R.id.tv_notifications_txt);
            vh.shareButton = (Button) convertView.findViewById(R.id.btn_share) ;
            //vh.tvUsrName = (TextView) convertView.findViewById(R.id.tv_notifications_usr_name);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Picasso.with(mCtxt).load(notification.image).error(R.drawable.default_profile_pic_small).into(vh.ivUsrProfilePic);
        //vh.tvUsrName.setText(notification.name);
        vh.tvNotificationsTxt.setText(notification.text);

        vh.ivUsrProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.startUserActivity(mCtxt, "Notifications", objectID);
            }
        });

        if (notification.notificationType.equals("profile_request_add") ||
                notification.notificationType.equals("profile_request_share")){

            vh.shareButton.setVisibility(View.VISIBLE);

            if(notification.notificationType.equals("profile_request_add"))
                vh.shareButton.setText("Add") ;
            else
                vh.shareButton.setText("Share");

            vh.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Router.startUserActivity(mCtxt, "Notifications", PrefUtils.getCurrentUserID());
                }
            });

        } else {
            vh.shareButton.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public static class ViewHolder {
        //protected TextView tvUsrName;
        protected TextView tvNotificationsTxt;
        protected ImageView ivUsrProfilePic;
        protected Button shareButton ;
    }
}
