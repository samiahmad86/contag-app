package com.contag.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.contag.app.config.Constants;
import com.contag.app.model.ProfileModel;

import java.util.HashMap;

/**
 * Created by tanay on 28/9/15.
 */
public class ProfileListAdapter extends BaseAdapter {

    private int profileType;
    private HashMap<Integer, ProfileModel> hmModel;
    private Context mContext;

    public ProfileListAdapter(int profileType, HashMap<Integer, ProfileModel> hmProfileModel, Context context) {
        this.mContext = context;
        this.profileType = profileType;
        this.hmModel = hmProfileModel;
    }

    @Override
    public int getCount() {
        return hmModel.size();
    }

    @Override
    public Object getItem(int position) {
        return hmModel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

}
