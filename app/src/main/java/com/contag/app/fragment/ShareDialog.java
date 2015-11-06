package com.contag.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.contag.app.R;
import com.contag.app.adapter.ShareListAdapter;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.config.Router;
import com.contag.app.model.ContactListItem;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.CustomShare;
import com.contag.app.model.CustomShareDao;
import com.contag.app.model.DaoSession;
import com.contag.app.util.PrefUtils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by varunj on 04/11/15.
 */
public class ShareDialog extends DialogFragment implements View.OnClickListener{
    private  CustomShare mCustomShare ;
    private ListView lvContags;
    private ArrayList<ContactListItem> shareList = new ArrayList<>();
    private ShareListAdapter shareListAdapter;
    private Button sharePublic ;
    private Button shareCustom ;
    private int shareCount = 0;
    private TextView shareText ;
    private String fieldName ;

    public static ShareDialog newInstance(String fieldName) {

        ShareDialog share = new ShareDialog();
        Bundle args = new Bundle();
        args.putString(Constants.Keys.KEY_FIELD_NAME, fieldName) ;
        share.setArguments(args);
        return share ;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_share_dialog, container, false);


        fieldName = getArguments().getString(Constants.Keys.KEY_FIELD_NAME) ;
        new LoadContags().execute(fieldName) ;
        shareListAdapter= new ShareListAdapter(shareList, getActivity());
        lvContags = (ListView) view.findViewById(R.id.lv_contag_share);
        lvContags.setAdapter(shareListAdapter);

        sharePublic = (Button) view.findViewById(R.id.btn_share_public) ;
        shareCustom = (Button) view.findViewById(R.id.btn_share_custom) ;
        shareText = (TextView) view.findViewById(R.id.tv_share_text) ;
        shareText.setText("Share your " + fieldName + " with: ") ;
        Button shareDone = (Button) view.findViewById(R.id.btn_share_done) ;

        sharePublic.setOnClickListener(this);
        shareCustom.setOnClickListener(this);
        shareDone.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(privacySettingsUpdated,
                new IntentFilter("com.contag.app.profile.privacy"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(shareCountUpdated,
                new IntentFilter("com.contag.app.profile.sharecount")) ;
    }

    @Override
    public void onStop(){
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(privacySettingsUpdated);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(shareCountUpdated);
    }
    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id) {
            case R.id.btn_share_public:{
                mCustomShare.setIs_public(!mCustomShare.getIs_public());
                setPublicButton() ;
                break ;
            }
            case R.id.btn_share_custom: {
                setContagList() ;
                break ;
            }
            case R.id.btn_share_done:{
                savePrivacySettings() ;
                break ;

            }
        }
    }

    private void savePrivacySettings(){

        if(shareCount > 0)
            mCustomShare.setIs_public(false);

        Log.d("shave", "Is Public: " + mCustomShare.getIs_public()) ;
        Log.d("shave", "Share Count: " + shareCount) ;

        Router.startUserServiceForPrivacy(getActivity(), mCustomShare.getField_name(), mCustomShare.getIs_public(),
                getSharesAsString());

    }

    private String getSharesAsString(){
        ArrayList<String> userIDS = new ArrayList<>() ;
        for(ContactListItem item: shareList){
            if (item.isSharedWith)
                userIDS.add(String.valueOf(item.mContagContag.getId())) ;
        }
        return TextUtils.join(",",userIDS) ;
    }

    private BroadcastReceiver privacySettingsUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(getActivity(), "Shared successfully!", Toast.LENGTH_LONG).show() ;
            mCustomShare.setUser_ids(getSharesAsString());
            Log.d("shave", "Broadcast, isPublic: " + mCustomShare.getIs_public()) ;
            Log.d("shave", "Going to save custom share string: " + mCustomShare.getUser_ids()) ;
            Log.d("shave", "Field name: " + mCustomShare.getField_name()) ;

            mCustomShare.update();
        }
    } ;

    private BroadcastReceiver shareCountUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            shareCount = intent.getIntExtra("shareCount", 0) ;
            setCustomShareCount();
        }
    };

    private void setPublicButton(){



        if(mCustomShare.getIs_public() || lvContags.getVisibility() == View.VISIBLE) {
            //Hide contag list
            lvContags.setVisibility(View.GONE);

            // Toggle Color
            sharePublic.setTextColor(getResources().getColor(R.color.light_blue));
            shareCustom.setTextColor(getResources().getColor(R.color.black));
        }
        else
            sharePublic.setTextColor(getResources().getColor(R.color.black));

    }

    private void setContagList(){

        if(lvContags.getVisibility() == View.VISIBLE) {
            // Remove contact list
            lvContags.setVisibility(View.GONE);
            // Set color to unselected
            shareCustom.setTextColor(getResources().getColor(R.color.black));
        } else {
            // Set is_public as false when showing list
            mCustomShare.setIs_public(false);
            //Show the list
            lvContags.setVisibility(View.VISIBLE);
            // Toggle color of buttons
            shareCustom.setTextColor(getResources().getColor(R.color.light_blue));
            sharePublic.setTextColor(getResources().getColor(R.color.black));

        }
    }

    private void setCustomShareCount(){
        shareCustom.setText("Custom(" +  shareCount + ")") ;
        shareCustom.setTextColor(getResources().getColor(R.color.light_blue));
    }


    private class LoadContags extends AsyncTask<String, Void, ArrayList<ContactListItem>> {

        @Override
        protected void onPreExecute() {
        }


        @Override
        protected ArrayList<ContactListItem> doInBackground(String... params) {
            String fieldName = params[0] ;
            DaoSession session = ((ContagApplication) ShareDialog.this.getActivity().
                    getApplicationContext()).getDaoSession();

            ArrayList<ContactListItem> items = new ArrayList<>();

            ContagContagDao mContagContagDao = session.getContagContagDao();
            List<ContagContag> contagContacts = mContagContagDao.queryBuilder().
                    where(ContagContagDao.Properties.Id.notEq(PrefUtils.getCurrentUserID()),
                            ContagContagDao.Properties.Is_contact.eq(true)).list();

            CustomShareDao mCustomDao = session.getCustomShareDao() ;

            mCustomShare = mCustomDao.queryBuilder().where(
                    CustomShareDao.Properties.Field_name.eq(fieldName)
            ).list().get(0) ;


            String[] sharedWith = mCustomShare.getUser_ids().split(",") ;
            Log.d("share", "Length of user ids: " + mCustomShare.getUser_ids().split(",").length) ;

            for(ContagContag cc: contagContacts){
                Log.d("share","Status with: " + cc.getName() + " :"+ ArrayUtils.contains(sharedWith,cc.getId().toString())) ;
                items.add(new ContactListItem(cc, ArrayUtils.contains(sharedWith, cc.getId().toString()),
                        Constants.Types.ITEM_SHARE_CONTAG)) ;
            }

            return items;
        }

        @Override
        protected void onPostExecute(ArrayList<ContactListItem> contactListItems) {

            setPublicButton();

            if(mCustomShare.getUser_ids().length() > 0)
                shareCount = mCustomShare.getUser_ids().split(",").length ;

            setCustomShareCount();
            shareList.clear();
            shareList.addAll(contactListItems);
            shareListAdapter.notifyDataSetChanged();
            shareListAdapter.setShareCount(shareCount) ;

        }
    }

}
