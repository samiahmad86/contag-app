package com.contag.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
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
import com.contag.app.fragment.DialogProfilePicture;
import com.contag.app.fragment.IntroduceContagDialog;
import com.contag.app.listener.DatabaseRequestListener;
import com.contag.app.model.Contact;
import com.contag.app.model.ContactListItem;
import com.contag.app.model.ContagContag;
import com.contag.app.model.Interest;
import com.contag.app.tasks.DatabaseOperationTask;
import com.contag.app.util.ContactUtils;
import com.contag.app.util.DeviceUtils;
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
    private ContactListItem newContactItem  ;
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
            return getContagView(position, convertView, parent, type);
        } else {
            return getContactView(position, convertView, parent);
        }
    }


    private View getContagView(int position, View convertView, ViewGroup parent, int type) {
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
            vhCont.btnMsg = (Button) convertView.findViewById(R.id.btn_msg);
            vhCont.btnCall = (Button) convertView.findViewById(R.id.btn_call);
            vhCont.btnWhatsapp = (Button) convertView.findViewById(R.id.btn_whatsapp);


            vhCont.btnIntroduceContag = (TextView) convertView.findViewById(R.id.tv_share_contag) ;
            convertView.setTag(vhCont);
        } else {
            vhCont = (ContagViewHolder) convertView.getTag();
        }

        final ContagContag contObject = ((ContactListItem) getItem(position)).mContagContag;

        Picasso.with(mContext).load(contObject.getAvatarUrl()).placeholder(R.drawable.default_profile_pic_small)
                . fit()
                .centerCrop()
                .into(vhCont.ivPhoto);
        vhCont.tvContactId.setText(contObject.getContag().toLowerCase());
        vhCont.tvContactName.setText(contObject.getName());
        vhCont.ivPhoto.setTag(contObject.getAvatarUrl());
        vhCont.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = (String) view.getTag();
                Log.d(TAG, url);
                DialogProfilePicture df=DialogProfilePicture.newInstance(url);
               df.show(((AppCompatActivity) mContext).getSupportFragmentManager(),TAG);

            }
        });


        vhCont.tvContactName.setClickable(false);
        vhCont.tvContactId.setClickable(false);
       /* vhCont.tvContactName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ShareUtils.shareText(mContext,"Hi");



            }
        });
*/
        vhCont.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeviceUtils.dialNumber(mContext, contObject.getMobileNumber());
            }
        });
        vhCont.btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ShareUtils.shareText(mContext,"Hi");
                DeviceUtils.sendSms(mContext, contObject.getMobileNumber(), null);
            }
        });
        vhCont.btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ShareUtils.shareText(mContext,"Hi");
                DeviceUtils.openConversationWithWhatsapp(mContext, contObject.getMobileNumber());
            }
        });

        List<Interest> interests = ((ContactListItem) getItem(position)).interests;
        Log.d("ConAdap", "Size of intersts is: " + interests.size()) ;
        if (interests != null && interests.size() > 0) {
            try {
               final TextView interest[]=new TextView[]{vhCont.tvInterest1,vhCont.tvInterest2,vhCont.tvInterest3,vhCont.tvInterest4};
                for(int i=0;i<interests.size();i++) {
                    setInterestElseHide(interests.get(i),interest[i]);

                }
               /* setInterestElseHide(interests.get(1), vhCont.tvInterest2);
                setInterestElseHide(interests.get(2), vhCont.tvInterest3);
                setInterestElseHide(interests.get(3), vhCont.tvInterest4);*/
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
                Log.d("ConAdap", "Exception occurred") ;
            }
        } else {
            vhCont.tvInterest1.setVisibility(View.GONE);
            vhCont.tvInterest2.setVisibility(View.GONE);
            vhCont.tvInterest3.setVisibility(View.GONE);
            vhCont.tvInterest4.setVisibility(View.GONE);

        }


        if(type == Constants.Types.ITEM_ADD_CONTAG) {

            vhCont.btnIntroduceContag.setVisibility(View.GONE);
            newContactItem = (ContactListItem) getItem(position) ;


            final View btnAdd = vhCont.btnAdd;
            final View btnMsg = vhCont.btnMsg;
            final View btnCall = vhCont.btnCall;
            final View ivPhoto = vhCont.ivPhoto;

            DatabaseRequestListener databaseRequestListener = new DatabaseRequestListener() {
                @Override
                public void onPreExecute() {

                }
                @Override
                public Object onRequestExecute() {
                    return ContactUtils.isExistingContact(contObject.getMobileNumber(), mContext.getApplicationContext());
                }

                @Override
                public void onPostExecute(Object responseObject) {
                    Boolean value = (Boolean) responseObject;
                    if(value) {
                        btnAdd.setVisibility(View.VISIBLE);
                        btnMsg.setVisibility(View.INVISIBLE);
                        btnCall.setVisibility(View.INVISIBLE);
                        btnAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Router.addContagUser(mContext, newContactItem.mContagContag.getId());
                                Toast.makeText(mContext, "Add request Sent!", Toast.LENGTH_LONG).show();
                                ((TextView)v).setText("Added");
                            }
                        });
                    }
                    ivPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, "You need to add and get approved by the user to view the profile.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            };

            DatabaseOperationTask databaseOperationTask = new DatabaseOperationTask(databaseRequestListener);
            databaseOperationTask.execute();

           }else {

            vhCont.btnIntroduceContag.setVisibility(View.VISIBLE);
            vhCont.btnAdd.setVisibility(View.GONE);
           /* vhCont.btnCall.setVisibility(View.VISIBLE);
            vhCont.btnMsg.setVisibility(View.VISIBLE);*/

            final String shareContagName = contObject.getName() ;
            final long shareContagID = contObject.getId() ;

            vhCont.btnIntroduceContag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntroduceContagDialog invite = IntroduceContagDialog.newInstance(shareContagName, shareContagID);
                    invite.show(((BaseActivity) mContext).getSupportFragmentManager(), TAG);
                }
            });

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
            vhContact.tvAlpha = (TextView) convertView.findViewById(R.id.tv_user_initial);
            vhContact.btnCallContact = (Button) convertView.findViewById(R.id.btn_call);
            vhContact.btnMsgContact = (Button) convertView.findViewById(R.id.btn_msg);
            vhContact.btnWhatsapp = (Button) convertView.findViewById(R.id.btn_whatsapp);

            vhContact.btnWhatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact contact = (Contact) v.getTag();
                    DeviceUtils.openConversationWithWhatsapp(mContext, contact.getContactNumber());
                }
            });



            vhContact.btnCallContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact contact = (Contact) v.getTag();
                    DeviceUtils.dialNumber(mContext, contact.getContactNumber());
                }
            });
            vhContact.btnMsgContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact contact = (Contact) v.getTag();
                    //ShareUtils.shareText(mContext,"Hi");
                    DeviceUtils.sendSms(mContext, contact.getContactNumber(), null);
                }
            });
            vhContact.btnInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact contact = (Contact) v.getTag();
                    String text = "Hi " + contact.getContactName() + ", I am on Contag. Download contag at http://bit.ly/1HHI6do";
                    ShareUtils.shareText(mContext, text);
                }
            });
            convertView.setTag(vhContact);
        } else {
            vhContact = (ContactViewHolder) convertView.getTag();
        }
        Contact contact = ((ContactListItem) getItem(position)).mContact;
        vhContact.tvContactName.setText(contact.getContactName());
        //Log.e("character", contact.getContactName().substring(0, 1));
        vhContact.tvContactNumber.setText(contact.getContactNumber());
        vhContact.tvAlpha.setText(contact.getContactName().substring(0,1).toUpperCase());
        vhContact.btnInvite.setTag(contact);
        vhContact.btnMsgContact.setTag(contact);
        vhContact.btnCallContact.setTag(contact);
        vhContact.btnWhatsapp.setTag(contact);
        return convertView;
    }

    private void setInterestElseHide(Interest interest, TextView tv) {

        if (interest != null) {
            Log.d("ConAdap", "Interest is not null :" + interest.getName()) ;
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
        public TextView tvAlpha;
        public Button btnMsgContact;
        public Button btnCallContact;
        private Button btnWhatsapp;

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
        private Button btnMsg;
        private Button btnCall;
        private Button btnWhatsapp;
        public TextView btnIntroduceContag;


        public ContagViewHolder() {

        }

    }
}
