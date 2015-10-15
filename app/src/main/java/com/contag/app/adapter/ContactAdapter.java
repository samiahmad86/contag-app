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
import com.contag.app.model.Contact;
import com.contag.app.model.ContactListItem;
import com.contag.app.model.ContagContag;
import com.contag.app.model.Interest;
import com.contag.app.util.ShareUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanay on 22/9/15.
 */
public class ContactAdapter extends BaseAdapter {

    private ArrayList<ContactListItem> items;
    private Context mContext;
    private static final String TAG = ContactAdapter.class.getName();

    public ContactAdapter(ArrayList<ContactListItem> contactListItems, Context context) {
        this.mContext = context;
        this.items = contactListItems;
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
        if (getItemViewType(position) == Constants.Types.ITEM_CONTAG) {
            return getCuntagView(position, convertView, parent);
        } else {
            return getContactView(position, convertView, parent);
        }
    }

    private View getCuntagView(int position, View convertView, ViewGroup parent) {
        CuntagViewHolder vhCunt;
        if (convertView == null || (convertView.getTag() instanceof ContactViewHolder)) {
            vhCunt = new CuntagViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_cuntag_contacts, parent, false);
            vhCunt.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_user_photo);
            vhCunt.tvContactId = (TextView) convertView.findViewById(R.id.tv_contact_id);
            vhCunt.tvContactName = (TextView) convertView.findViewById(R.id.tv_contact_name);
            vhCunt.tvInterest1 = (TextView) convertView.findViewById(R.id.tv_user_interest_1);
            vhCunt.tvInterest2 = (TextView) convertView.findViewById(R.id.tv_user_interest_2);
            vhCunt.tvInterest3 = (TextView) convertView.findViewById(R.id.tv_user_interest_3);
            vhCunt.tvInterest4 = (TextView) convertView.findViewById(R.id.tv_user_interest_4);
            convertView.setTag(vhCunt);
        } else {
            vhCunt = (CuntagViewHolder) convertView.getTag();
        }
        ContagContag cuntObject = ((ContactListItem) getItem(position)).mContagContag;
        Picasso.with(mContext).load(cuntObject.getAvatarUrl()).placeholder(R.drawable.default_profile_pic_small)
                .into(vhCunt.ivPhoto);
        vhCunt.tvContactId.setText(cuntObject.getContag());
        vhCunt.tvContactName.setText(cuntObject.getName());
        List<Interest> interests = ((ContactListItem) getItem(position)).interests;
        if (interests != null && interests.size() > 0) {
            Log.d(TAG, "fuck");
            try {
                setInterestElseHide(interests.get(0), vhCunt.tvInterest1);
                setInterestElseHide(interests.get(1), vhCunt.tvInterest2);
                setInterestElseHide(interests.get(2), vhCunt.tvInterest3);
                setInterestElseHide(interests.get(3), vhCunt.tvInterest4);
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        }
        return convertView;
    }

    private View getContactView(int position, View convertView, ViewGroup parent) {
        ContactViewHolder vhContact;
        if (convertView == null || (convertView.getTag() instanceof CuntagViewHolder)) {
            vhContact = new ContactViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_contacts, parent, false);
            vhContact.tvContactName = (TextView) convertView.findViewById(R.id.tv_contact_name);
            vhContact.tvContactNumber = (TextView) convertView.findViewById(R.id.tv_contact_num);
            vhContact.btnInvite = (Button) convertView.findViewById(R.id.btn_cuntag_invite);
            vhContact.btnInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact contact = (Contact) v.getTag();
                    String text = "Hi " + contact.getContactName() + ", I am on Contag. Download contag at http://www.contag.com";
                    ShareUtils.shareText(mContext, text);
                }
            });
            convertView.setTag(vhContact);
        } else {
            vhContact = (ContactViewHolder) convertView.getTag();
        }
        Contact contact = ((ContactListItem) getItem(position)).mContact;
        vhContact.tvContactName.setText(contact.getContactName());
        vhContact.tvContactNumber.setText(contact.getContactNumber());
        vhContact.btnInvite.setTag(contact);
        return convertView;
    }

    private void setInterestElseHide(Interest interest, TextView tv) {

        if (interest != null) {
            tv.setText(interest.getName());
            tv.setVisibility(View.VISIBLE);
        }
    }

    public static class ContactViewHolder {

        public TextView tvContactName;
        public TextView tvContactNumber;
        public Button btnInvite;

        public ContactViewHolder() {

        }
    }

    public static class CuntagViewHolder {

        public TextView tvContactName;
        public TextView tvContactId;
        public TextView tvInterest1;
        public TextView tvInterest2;
        public TextView tvInterest3;
        public TextView tvInterest4;
        public ImageView ivPhoto;

        public CuntagViewHolder() {

        }

    }
}
