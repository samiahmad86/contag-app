package com.contag.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.model.ContactListItem;
import com.contag.app.model.ContagContag;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanay on 22/9/15.
 */
public class ShareListAdapter extends BaseAdapter {

    private ArrayList<ContactListItem> items=null;
    private ArrayList<ContactListItem> items_1=null;
    private ArrayList<ContactListItem> items_2;
    private int shareCount ;
    private Context mContext;
    private static final String TAG = ContactAdapter.class.getName();

    public ShareListAdapter(ArrayList<ContactListItem> contactListItems,  Context context) {
        this.mContext = context;
        this.items = contactListItems;
        items_1=new ArrayList<ContactListItem>();

    }

    public void setShareCount(int shareCount){
        this.shareCount = shareCount ;
    }
    public int getShareCount(){
        return shareCount ;
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
    public int getItemViewType(int position) {
        return ((ContactListItem) getItem(position)).type;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContagViewHolder vhCont;

        final ContactListItem  contact = ((ContactListItem) getItem(position))  ;
        if (convertView == null || (convertView.getTag() instanceof ContagViewHolder)) {
            vhCont = new ContagViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_contag_share, parent, false);
            vhCont.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_user_photo);
            vhCont.tvContagID = (TextView) convertView.findViewById(R.id.tv_contact_id);
            vhCont.tvContactName = (TextView) convertView.findViewById(R.id.tv_contact_name);
            vhCont.ckSharedWith = (CheckBox) convertView.findViewById(R.id.checkbox_shared) ;

            convertView.setTag(vhCont);
        } else {
            vhCont = (ContagViewHolder) convertView.getTag();
        }

        ContagContag contag = contact.mContagContag;

        Picasso.with(mContext).load(contag.getAvatarUrl()).placeholder(R.drawable.default_profile_pic_small)
                .fit()
                .centerCrop()
                .into(vhCont.ivPhoto);
        vhCont.tvContagID.setText(contag.getContag());
        vhCont.tvContactName.setText(contag.getName());

        final int pos = position;

        if(contact.isSharedWith)
        {
         convertView.setBackgroundColor(mContext.getResources().getColor(R.color.c99D758));

        }
        else
        {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contact.isSharedWith)
                {
                    items.get(pos).isSharedWith=false;
                    shareCount -= 1 ;
                    notifyDataSetChanged();
                }
                else
                {
                    items.get(pos).isSharedWith=true;
                    shareCount+=1;
                    notifyDataSetChanged();
                }
                Log.d("share", "in adapter: " + shareCount) ;
                Intent sharedIntent = new Intent("com.contag.app.profile.sharecount") ;
                sharedIntent.putExtra("shareCount", shareCount) ;
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(sharedIntent);
            }
        });




     /* vhCont.ckSharedWith.setChecked(contact.isSharedWith);




        vhCont.ckSharedWith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                items.get(pos).isSharedWith = isChecked ;
                if(isChecked)
                    shareCount += 1 ;
                else
                    shareCount -= 1 ;
                Log.d("share", "in adapter: " + shareCount) ;
                Intent sharedIntent = new Intent("com.contag.app.profile.sharecount") ;
                sharedIntent.putExtra("shareCount", shareCount) ;
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(sharedIntent);
            }
        });
*/
        return convertView;
    }


    public void filter(String charText) {
       // charText = charText.toLowerCase(Locale.getDefault());
        items.clear();
        Log.e("search2",charText);
        if (charText.length() == 0) {
            items.addAll(items_1);
            Log.e("search4",charText);
        } else {


            for (ContactListItem wp : items_1) {
                Log.e("search3"+wp.mContagContag.getName().toLowerCase(),charText);
                if (wp.mContagContag.getName().toLowerCase()
                        .startsWith(charText)) {
                    items.add(wp);
                    Log.e("search5",charText);
                }
            }
            Log.e("search6",items.size()+"");
        }
        notifyDataSetChanged();
    }


    public static class ContagViewHolder {

        public ImageView ivPhoto;
        public TextView tvContactName;
        public TextView tvContagID ;
        public CheckBox ckSharedWith ;

    }
    public void setArray(ArrayList<ContactListItem> data)
    {
     items_1.addAll(data);
    }
}
