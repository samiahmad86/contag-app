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
import android.widget.Toast;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.model.Contact;
import com.contag.app.model.ContactListItem;
import com.contag.app.model.ContagContag;
import com.contag.app.model.Interest;
import com.contag.app.util.ContactUtils;
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
        int type = getItemViewType(position) ;
        if (type == Constants.Types.ITEM_CONTAG || type == Constants.Types.ITEM_ADD_CONTAG) {
            return getCuntagView(position, convertView, parent, type);
        } else {
            return getContactView(position, convertView, parent);
        }
    }

    private View getCuntagView(int position, View convertView, ViewGroup parent, int type) {
        ContagViewHolder vhCont;
        if (convertView == null || (convertView.getTag() instanceof ContactViewHolder)) {
            vhCont = new ContagViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_contag_contacts, parent, false);
            vhCont.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_user_photo);
            vhCont.tvContactId = (TextView) convertView.findViewById(R.id.tv_contact_id);
            vhCont.tvContactName = (TextView) convertView.findViewById(R.id.tv_contact_name);
            vhCont.tvInterest1 = (TextView) convertView.findViewById(R.id.tv_user_interest_1);
            vhCont.tvInterest2 = (TextView) convertView.findViewById(R.id.tv_user_interest_2);
            vhCont.tvInterest3 = (TextView) convertView.findViewById(R.id.tv_user_interest_3);
            vhCont.tvInterest4 = (TextView) convertView.findViewById(R.id.tv_user_interest_4);
            vhCont.btnAdd = (Button) convertView.findViewById(R.id.btn_add_contag) ;
            convertView.setTag(vhCont);
        } else {
            vhCont = (ContagViewHolder) convertView.getTag();
        }

        ContagContag contObject = ((ContactListItem) getItem(position)).mContagContag;
        Picasso.with(mContext).load(contObject.getAvatarUrl()).placeholder(R.drawable.default_profile_pic_small)
                .into(vhCont.ivPhoto);
        vhCont.tvContactId.setText(contObject.getContag());
        vhCont.tvContactName.setText(contObject.getName());

        List<Interest> interests = ((ContactListItem) getItem(position)).interests;
        if (interests != null && interests.size() > 0) {
            try {
                setInterestElseHide(interests.get(0), vhCont.tvInterest1);
                setInterestElseHide(interests.get(1), vhCont.tvInterest2);
                setInterestElseHide(interests.get(2), vhCont.tvInterest3);
                setInterestElseHide(interests.get(3), vhCont.tvInterest4);
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        } else {
            vhCont.tvInterest1.setVisibility(View.GONE);
            vhCont.tvInterest2.setVisibility(View.GONE);
            vhCont.tvInterest3.setVisibility(View.GONE);
            vhCont.tvInterest4.setVisibility(View.GONE);

        }


        if(type == Constants.Types.ITEM_ADD_CONTAG) {
            final ContactListItem item = (ContactListItem) getItem(position) ;
            if(ContactUtils.isExistingContact(contObject.getMobileNumber(), mContext.getApplicationContext())) {
                vhCont.btnAdd.setVisibility(View.VISIBLE);
                vhCont.btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContactUtils.addContag(mContext.getApplicationContext(), item);
                        Toast.makeText(mContext, "Adding this user!", Toast.LENGTH_LONG).show();
                    }
                });
            }
            vhCont.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Router.startUserActivity(mContext, TAG, item.mContagContag.getId());
                }
            });
        }else {
            vhCont.btnAdd.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private View getContactView(int position, View convertView, ViewGroup parent) {
        ContactViewHolder vhContact;
        if (convertView == null || (convertView.getTag() instanceof ContagViewHolder)) {
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
            Log.d("ConAdap", "Interest is not null") ;
            tv.setText(interest.getName());
            tv.setVisibility(View.VISIBLE);
        } else
        {
            Log.d("ConAdap", "Interest is null") ;
            tv.setVisibility(View.GONE);
        }
    }

    public static class ContactViewHolder {

        public TextView tvContactName;
        public TextView tvContactNumber;
        public Button btnInvite;

        public ContactViewHolder() {

        }
    }

    public static class ContagViewHolder {

        public TextView tvContactName;
        public TextView tvContactId;
        public TextView tvInterest1;
        public TextView tvInterest2;
        public TextView tvInterest3;
        public TextView tvInterest4;
        public ImageView ivPhoto;
        public Button btnAdd ;

        public ContagViewHolder() {

        }

    }
}
