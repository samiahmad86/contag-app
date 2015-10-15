package com.contag.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.contag.app.model.ContagContag;

import java.util.ArrayList;

/**
 * Created by varunj on 15/10/15.
 */
public class CustomShareAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ContagContag> fuckingCunts;

    public CustomShareAdapter(Context context, ArrayList<ContagContag> cunts) {
        this.mContext = context;
        this.fuckingCunts = cunts;
    }

    @Override
    public int getCount() {
        return fuckingCunts.size();
    }

    @Override
    public Object getItem(int position) {
        return fuckingCunts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
