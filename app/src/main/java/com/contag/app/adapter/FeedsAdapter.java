package com.contag.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.model.FeedsResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeedsAdapter extends BaseAdapter {

    private Context mCtxt;
    private ArrayList<FeedsResponse> feeds;

    public FeedsAdapter(Context mContext, ArrayList<FeedsResponse> feeds) {
        this.mCtxt = mContext;
        this.feeds = feeds;
    }

    @Override
    public int getCount() {
        return feeds.size();
    }

    @Override
    public Object getItem(int position) {
        return feeds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return feeds.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        FeedsResponse feed = (FeedsResponse) getItem(position);
        if(convertView == null) {
            vh = new ViewHolder();
            convertView = ((LayoutInflater) mCtxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                    inflate(R.layout.items_feeds, parent, false);
            vh.ivUsrProfilePic = (ImageView) convertView.findViewById(R.id.iv_feeds_usr_img);
            vh.tvFeedsStory = (TextView) convertView.findViewById(R.id.tv_feeds_story);
            vh.tvUsrCuntId = (TextView) convertView.findViewById(R.id.tv_feeds_usr_cunt_id);
            vh.tvUsrName = (TextView) convertView.findViewById(R.id.tv_feeds_usr_name);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Picasso.with(mCtxt).load(feed.storyImage).error(R.drawable.default_profile_pic_small).into(vh.ivUsrProfilePic);
        vh.tvUsrName.setText(feed.name);
        vh.tvUsrCuntId.setText(feed.contagId);
        vh.tvFeedsStory.setText(feed.storyText);
        return convertView;
    }

    public static class ViewHolder {
        protected TextView tvUsrName;
        protected TextView tvUsrCuntId;
        protected TextView tvFeedsStory;
        protected ImageView ivUsrProfilePic;
    }
}
