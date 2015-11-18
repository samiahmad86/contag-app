package com.contag.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.model.BlockContact;

import java.util.ArrayList;

/**
 * Created by tanay on 12/10/15.
 */
public class BlockedListAdapter extends BaseAdapter {

    private ArrayList<BlockContact> items;
    private Context mContext;
    private View.OnClickListener mListener;

    public BlockedListAdapter(Context mContext, ArrayList<BlockContact> items, View.OnClickListener mListener) {
        this.mContext = mContext;
        this.items = items;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        BlockContact bm = (BlockContact) items.get(position);
        if(convertView == null) {
            vh = new ViewHolder();
            convertView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                    inflate(R.layout.item_block_list, parent, false);
            vh.btnBlock = (Button) convertView.findViewById(R.id.btn_unblock);
            vh.tvName = (TextView) convertView.findViewById(R.id.tv_blocked_name);
            vh.btnBlock.setOnClickListener(mListener);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.btnBlock.setTag(bm.userID);
        vh.tvName.setText(bm.userName);
        return convertView;
    }

    public static class ViewHolder {
        private Button btnBlock;
        private TextView tvName;
    }
}
