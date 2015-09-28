package com.contag.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.adapter.ProfileListAdapter;
import com.contag.app.config.Constants;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ProfileModel;

import java.util.HashMap;

/**
 * Created by tanay on 28/9/15.
 */
public class EditProfileDetailsFragment extends BaseFragment {

    private HashMap<Integer, ProfileModel> hmProfileModel;
    private ProfileListAdapter pla;
    private int type;

    public static EditProfileDetailsFragment newInstance(int type) {
        EditProfileDetailsFragment epdf = new EditProfileDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.Keys.KEY_USER_PROFILE_TYPE, type);
        epdf.setArguments(args);
        return epdf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_user, container, false);
        hmProfileModel = new HashMap<>();
        Bundle args = getArguments();
        type = args.getInt(Constants.Keys.KEY_USER_PROFILE_TYPE);
        new LoadUser().execute(type);
        pla = new ProfileListAdapter(type, hmProfileModel, getActivity());
        ListView lvProfileDetails = (ListView) view.findViewById(R.id.lv_user_details);
        lvProfileDetails.setAdapter(pla);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).
                registerReceiver(brUsr, new IntentFilter(getResources().getString(R.string.intent_filter_user_received)));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brUsr);
    }

    private BroadcastReceiver brUsr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, 0);
            if(type == EditProfileDetailsFragment.this.type) {
                new LoadUser().execute(type);
            }
        }
    };

    private class LoadUser extends AsyncTask<Integer, Void, HashMap<Integer, ProfileModel>> {
        @Override
        protected HashMap<Integer, ProfileModel> doInBackground(Integer... params) {
            int type = params[0];
            ContagContag cc = ((BaseActivity) EditProfileDetailsFragment.this.getActivity()).getCurrentUser();
            HashMap<Integer, ProfileModel> hm = new HashMap<>();
            switch (type) {
                case Constants.Types.PROFILE_PERSONAL: {
                    hm.put(0, new ProfileModel(Constants.Keys.KEY_USER_NAME, cc.getName(), Constants.Types.FIELD_STRING));
                    hm.put(1, new ProfileModel(Constants.Keys.KEY_USER_PERSONAL_EMAIL, cc.getPersonalEmail(), Constants.Types.FIELD_STRING));
                    hm.put(2, new ProfileModel(Constants.Keys.KEY_USER_ADDRESS, cc.getAddress(), Constants.Types.FIELD_STRING));
                    hm.put(3, new ProfileModel(Constants.Keys.KEY_USER_LANDLINE_NUMBER, cc.getLandLineNumber(), Constants.Types.FIELD_STRING));
                    hm.put(4, new ProfileModel(Constants.Keys.KEY_USER_BLOOD_GROUP, cc.getBloodGroup(), Constants.Types.FIELD_STRING));
                    hm.put(5, new ProfileModel(Constants.Keys.KEY_USER_DATE_OF_BIRTH, cc.getDateOfBirth(), Constants.Types.FIELD_STRING));
                    hm.put(6, new ProfileModel(Constants.Keys.KEY_USER_EMERGENCY_CONTACT_NUMBER, cc.getEmergencyContactNumber(), Constants.Types.FIELD_STRING));
                    hm.put(7, new ProfileModel(Constants.Keys.KEY_USER_MARRIAGE_ANNIVERSARY, cc.getMarriageAnniversary(), Constants.Types.FIELD_STRING));
                    hm.put(8, new ProfileModel(Constants.Keys.KEY_USER_MARITAL_STATUS, cc.getMaritalStatus(), Constants.Types.FIELD_BOOLEAN));
                    break;
                }
                case Constants.Types.PROFILE_PROFESSIONAL : {
                    hm.put(0, new ProfileModel(Constants.Keys.KEY_USER_NAME, cc.getName(), Constants.Types.FIELD_STRING));
                    hm.put(1, new ProfileModel(Constants.Keys.KEY_USER_WORK_EMAIL, cc.getWorkEmail(), Constants.Types.FIELD_STRING));
                    hm.put(2, new ProfileModel(Constants.Keys.KEY_USER_WORK_ADDRESS, cc.getWorkAddress(), Constants.Types.FIELD_STRING));
                    hm.put(3, new ProfileModel(Constants.Keys.KEY_USER_WORK_MOBILE_NUMBER, cc.getWorkMobileNumber(), Constants.Types.FIELD_STRING));
                    hm.put(4, new ProfileModel(Constants.Keys.KEY_USER_WORK_LANDLINE_NUMBER, cc.getWorkLandLineNumber(), Constants.Types.FIELD_STRING));
                    hm.put(5, new ProfileModel(Constants.Keys.KEY_USER_DESIGNATION, cc.getDesignation(), Constants.Types.FIELD_STRING));
                    hm.put(6, new ProfileModel(Constants.Keys.KEY_USER_WORK_FACEBOOK_PAGE, cc.getWorkFacebookPage(), Constants.Types.FIELD_STRING));
                    hm.put(7, new ProfileModel(Constants.Keys.KEY_USER_ANDROID_APP_LINK, cc.getAndroidAppLink(), Constants.Types.FIELD_STRING));
                    hm.put(8, new ProfileModel(Constants.Keys.KEY_USER_IOS_APP_LINK, cc.getIosAppLink(), Constants.Types.FIELD_STRING));
                    break;
                }

                case Constants.Types.PROFILE_SOCIAL: {

                    break;
                }
            }
            return hm;
        }

        @Override
        protected void onPostExecute(HashMap<Integer, ProfileModel> hm) {
            hmProfileModel.clear();
            hmProfileModel.putAll(hm);
            pla.notifyDataSetChanged();
        }
    }
}
