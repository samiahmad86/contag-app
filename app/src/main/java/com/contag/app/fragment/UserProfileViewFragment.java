package com.contag.app.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ProfileModel;
import com.contag.app.model.SocialPlatform;
import com.contag.app.model.SocialProfile;

import java.util.ArrayList;
import java.util.HashMap;

public class UserProfileViewFragment extends BaseFragment implements View.OnClickListener {

    private long userId;
    private int profileType;

    private HashMap<Integer, ProfileModel> hmProfileModel;
    private ArrayList<ViewHolder> viewHolderArrayList;
    private LinearLayout llViewContainer;
    public static final String TAG = UserProfileViewFragment.class.getName();

    public static UserProfileViewFragment newInstance(int profileType, long userID) {
        UserProfileViewFragment upvf = new UserProfileViewFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.Keys.KEY_USER_PROFILE_TYPE, profileType);
        args.putLong(Constants.Keys.KEY_USER_ID, userID);
        upvf.setArguments(args);
        return upvf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_details, container, false);
        hmProfileModel = new HashMap<>();
        viewHolderArrayList = new ArrayList<>();
        Bundle args = getArguments();
        llViewContainer = (LinearLayout) view.findViewById(R.id.ll_profile_container);
        profileType = args.getInt(Constants.Keys.KEY_USER_PROFILE_TYPE);
        userId = args.getLong(Constants.Keys.KEY_USER_ID);
        TextView tvProfileType = (TextView) view.findViewById(R.id.tv_profile_type);

        switch (profileType) {
            case Constants.Types.PROFILE_PERSONAL: {
                tvProfileType.setText("Personal Details");
                break;
            }
            case Constants.Types.PROFILE_SOCIAL: {
                tvProfileType.setText("Social Details");
                break;
            }
            case Constants.Types.PROFILE_PROFESSIONAL: {
                tvProfileType.setText("Professional Details");
                break;
            }
        }

        new LoadUser().execute();
        return view;
    }


    private void drawView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        int size = hmProfileModel.size();
        for (int i = 0; i < size; i++) {
            View view = inflater.inflate(R.layout.item_profile_view, llViewContainer, false);
            llViewContainer.addView(view, i);
            ViewHolder vh = new ViewHolder();
            vh.btnRequestField = (Button) view.findViewById(R.id.btn_request);
            vh.tvFieldLabel = (TextView) view.findViewById(R.id.tv_field_label);
            vh.tvFieldValue = (TextView) view.findViewById(R.id.tv_field_value);
            viewHolderArrayList.add(vh);
        }
    }

    private void setViewContent() {
        int size = hmProfileModel.size();
        for (int i = 0; i < size; i++) {
            ViewHolder vh = viewHolderArrayList.get(i);
            ProfileModel pm = hmProfileModel.get(i);
            vh.tvFieldLabel.setText(convertKeyToLabel(pm.key));
            vh.btnRequestField.setTag(pm.key);
            String value = (String) pm.value;
            if (profileType == Constants.Types.PROFILE_SOCIAL) {
                if (value.lastIndexOf("/") != (value.length() - 1)) {
                    vh.tvFieldValue.setText((String) pm.value);
                    vh.tvFieldValue.setOnClickListener(this);
                } else {
                    vh.btnRequestField.setVisibility(View.VISIBLE);
                    vh.btnRequestField.setOnClickListener(this);
                    vh.tvFieldValue.setVisibility(View.GONE);
                }
            } else {
                if (value == null || value.length() == 0) {
                    vh.btnRequestField.setOnClickListener(this);
                    vh.btnRequestField.setVisibility(View.VISIBLE);
                    vh.tvFieldValue.setVisibility(View.GONE);
                } else {
                    vh.tvFieldValue.setText(String.valueOf(pm.value));
                }
            }

        }
    }

    private String convertKeyToLabel(String key) {
        String str = key.replace("_", " ");
        str = str.toLowerCase();
        char ch = str.charAt(0);
        str = ((char) (ch - 32)) + str.substring(1);
        int position = str.indexOf(" ");
        while (position != -1) {
            ch = str.charAt(position + 1);
            str = str.substring(0, position + 1) + (char) (ch - 32) + str.substring(position + 2);
            position = str.indexOf(" ", position + 1);
        }
        return str;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_request: {
                Router.startProfileRequestService(getActivity(), Constants.Types.SERVICE_MAKE_PROFILE_REQUEST,
                        userId, (String) v.getTag());
                break;
            }
            case R.id.tv_field_value: {
                String link = (String) ((TextView) v).getText();
                Router.openSocialProfile(getActivity(), link);
                break;
            }
        }
    }

    private class LoadUser extends AsyncTask<Integer, Void, HashMap<Integer, ProfileModel>> {
        @Override
        protected HashMap<Integer, ProfileModel> doInBackground(Integer... params) {
            ContagContag cc = ((BaseActivity) UserProfileViewFragment.this.getActivity()).getUser(userId);
            HashMap<Integer, ProfileModel> hm = new HashMap<>();
            switch (profileType) {
                case Constants.Types.PROFILE_PERSONAL: {
                    hm.put(0, new ProfileModel(Constants.Keys.KEY_USER_NAME, cc.getName()));
                    hm.put(1, new ProfileModel(Constants.Keys.KEY_USER_MOBILE_NUMBER, cc.getMobileNumber()));
                    hm.put(2, new ProfileModel(Constants.Keys.KEY_USER_PERSONAL_EMAIL, cc.getPersonalEmail()));
                    hm.put(3, new ProfileModel(Constants.Keys.KEY_USER_ADDRESS, cc.getAddress()));
                    hm.put(4, new ProfileModel(Constants.Keys.KEY_USER_LANDLINE_NUMBER, cc.getLandLineNumber()));
                    hm.put(5, new ProfileModel(Constants.Keys.KEY_USER_BLOOD_GROUP, cc.getBloodGroup()));
                    hm.put(6, new ProfileModel(Constants.Keys.KEY_USER_DATE_OF_BIRTH, cc.getDateOfBirth()));
                    hm.put(7, new ProfileModel(Constants.Keys.KEY_USER_EMERGENCY_CONTACT_NUMBER,
                            cc.getEmergencyContactNumber()));
                    hm.put(8, new ProfileModel(Constants.Keys.KEY_USER_MARRIAGE_ANNIVERSARY,
                            cc.getMarriageAnniversary()));
                    hm.put(9, new ProfileModel(Constants.Keys.KEY_USER_MARITAL_STATUS, cc.getMaritalStatus()));
                    hm.put(10, new ProfileModel(Constants.Keys.KEY_USER_GENDER, cc.getGender()));
                    break;
                }
                case Constants.Types.PROFILE_PROFESSIONAL: {
                    hm.put(0, new ProfileModel(Constants.Keys.KEY_USER_WORK_EMAIL, cc.getWorkEmail()));
                    hm.put(1, new ProfileModel(Constants.Keys.KEY_USER_WORK_ADDRESS, cc.getWorkAddress()));
                    hm.put(2, new ProfileModel(Constants.Keys.KEY_USER_WORK_MOBILE_NUMBER,
                            cc.getWorkMobileNumber()));
                    hm.put(3, new ProfileModel(Constants.Keys.KEY_USER_WORK_LANDLINE_NUMBER,
                            cc.getWorkLandLineNumber()));
                    hm.put(4, new ProfileModel(Constants.Keys.KEY_USER_DESIGNATION, cc.getDesignation()));
                    hm.put(5, new ProfileModel(Constants.Keys.KEY_USER_WORK_FACEBOOK_PAGE,
                            cc.getWorkFacebookPage()));
                    hm.put(6, new ProfileModel(Constants.Keys.KEY_USER_ANDROID_APP_LINK, cc.getAndroidAppLink()));
                    hm.put(7, new ProfileModel(Constants.Keys.KEY_USER_IOS_APP_LINK, cc.getIosAppLink()));
                    break;
                }

                case Constants.Types.PROFILE_SOCIAL: {
                    ArrayList<SocialPlatform> socialPlatforms = ((BaseActivity) UserProfileViewFragment.this.getActivity()).
                            getSocialPlatforms();
                    HashMap<String, String> hmNameToUrl = new HashMap<>();
                    for (SocialPlatform sp : socialPlatforms) {
                        hmNameToUrl.put(sp.getPlatformName(), sp.getPlatformBaseUrl());
                    }
                    ArrayList<SocialProfile> socialProfiles = ((BaseActivity) UserProfileViewFragment.this.getActivity()).
                            getSocialProfiles(userId);
                    int counter = 0;
                    for (SocialProfile sp : socialProfiles) {
                        if (hmNameToUrl.containsKey(sp.getSocial_platform())) {
                            hm.put(counter++, new ProfileModel(sp.getSocial_platform(),
                                    hmNameToUrl.get(sp.getSocial_platform()) + "/" + sp.getPlatform_id(),
                                    Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));
                            hmNameToUrl.remove(sp.getSocial_platform());
                        }
                    }
                    for (SocialPlatform sp : socialPlatforms) {
                        if (hmNameToUrl.containsKey(sp.getPlatformName())) {
                            hm.put(counter++, new ProfileModel(sp.getPlatformName(), sp.getPlatformBaseUrl() + "/", Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));
                        }
                    }
                    break;
                }
            }
            return hm;
        }

        @Override
        protected void onPostExecute(HashMap<Integer, ProfileModel> hm) {
            hmProfileModel.clear();
            hmProfileModel.putAll(hm);
            drawView();
            setViewContent();
        }
    }


    public class ViewHolder {
        protected TextView tvFieldValue;
        protected TextView tvFieldLabel;
        protected Button btnRequestField;

        public ViewHolder() {

        }
    }

}
