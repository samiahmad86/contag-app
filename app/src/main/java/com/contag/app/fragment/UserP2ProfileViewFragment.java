package com.contag.app.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
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
import com.contag.app.model.ProfileViewModel;
import com.contag.app.model.SocialPlatform;
import com.contag.app.model.SocialProfile;
import com.contag.app.model.SocialProfileModel;
import com.contag.app.util.DeviceUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class UserP2ProfileViewFragment extends BaseFragment implements View.OnClickListener {

    private long userId;
    private int profileType;

    private HashMap<Integer, ProfileViewModel> hmP2ProfileView;
    private ArrayList<ViewHolder> viewHolderArrayList;
    private LinearLayout llViewContainer;
    public static final String TAG = UserP2ProfileViewFragment.class.getName();

    public static UserP2ProfileViewFragment newInstance(int profileType, long userID) {
        UserP2ProfileViewFragment mUserP2ProfileViewFragment = new UserP2ProfileViewFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.Keys.KEY_USER_PROFILE_TYPE, profileType);
        args.putLong(Constants.Keys.KEY_USER_ID, userID);
        mUserP2ProfileViewFragment.setArguments(args);
        return mUserP2ProfileViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_details, container, false);
        hmP2ProfileView = new HashMap<>();
        viewHolderArrayList = new ArrayList<>();
        Bundle args = getArguments();
        llViewContainer = (LinearLayout) view.findViewById(R.id.ll_profile_container);
        profileType = args.getInt(Constants.Keys.KEY_USER_PROFILE_TYPE);
        userId = args.getLong(Constants.Keys.KEY_USER_ID);
        new LoadUser().execute();
        return view;
    }


    private void drawView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        llViewContainer.removeAllViews();
        int size = hmP2ProfileView.size();
        for (int i = 0; i < size; i++) {
            View view = inflater.inflate(R.layout.item_profile_view, llViewContainer, false);
            llViewContainer.addView(view);
            ViewHolder mViewHolder = new ViewHolder();
            mViewHolder.btnAction = (Button) view.findViewById(R.id.btn_action);
            mViewHolder.btnRequestField = (Button) view.findViewById(R.id.btn_request);
            mViewHolder.tvFieldLabel = (TextView) view.findViewById(R.id.tv_field_name);
            mViewHolder.tvFieldValue = (TextView) view.findViewById(R.id.tv_field_value);
            mViewHolder.tvFieldValue.setOnClickListener(this);
            mViewHolder.btnAction.setOnClickListener(this);
            mViewHolder.btnRequestField.setOnClickListener(this);
            viewHolderArrayList.add(mViewHolder);
        }
    }

    private void setViewContent() {
        int size = hmP2ProfileView.size();
        for (int position = 0; position < size; position++) {
            ViewHolder mViewHolder = viewHolderArrayList.get(position);
            ProfileViewModel mProfileViewModel = hmP2ProfileView.get(position);
            mViewHolder.tvFieldLabel.setText(convertKeyToLabel(mProfileViewModel.key));
            if (mProfileViewModel.isAdded) {
                mViewHolder.btnAction.setTag(position);
                mViewHolder.btnAction.setVisibility(View.VISIBLE);
                mViewHolder.btnRequestField.setVisibility(View.GONE);
                mViewHolder.tvFieldValue.setText(mProfileViewModel.value);
            } else {
                mViewHolder.btnRequestField.setTag(position);
                mViewHolder.btnAction.setVisibility(View.GONE);
                mViewHolder.btnRequestField.setVisibility(View.VISIBLE);
                mViewHolder.tvFieldValue.setVisibility(View.GONE);
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
                int positon = (int) v.getTag();
                Router.startProfileRequestService(getActivity(), Constants.Types.SERVICE_MAKE_PROFILE_REQUEST,
                        userId, hmP2ProfileView.get(positon).key, profileType + "");
                break;
            }
            case R.id.tv_field_value: {
                String text = (String) ((TextView) v).getText();
                DeviceUtils.copyToClipboard(getActivity(), text);
                showToast("copied to clipboard");
                break;
            }
            case R.id.btn_action: {
                int position = (Integer) v.getTag();
                ProfileViewModel mProfileViewModel = hmP2ProfileView.get(position);
                switch (mProfileViewModel.fieldType) {
                    case Constants.Types.FIELD_EMAIL: {
                        Router.openGmailForEmailID(getActivity(), mProfileViewModel.value);
                        break;
                    }
                    case Constants.Types.FIELD_ADDRESS: {
                        Router.openGoogleMapsWithAddress(getActivity(), mProfileViewModel.value);
                        break;
                    }
                    case Constants.Types.FIELD_NUMBER: {
                        DeviceUtils.dialNumber(getActivity(), mProfileViewModel.value);
                        break;
                    }
                    case Constants.Types.FIELD_GOOGLE: {
                        Router.openGooglePlusProfile(getActivity(), mProfileViewModel.socialProfile.getPlatform_id());
                        break;
                    }
                    case Constants.Types.FIELD_INSTAGRAM: {
                        Router.openInstagramProfile(getActivity(), mProfileViewModel.socialProfile.getPlatform_id());
                        break;
                    }
                    case Constants.Types.FIELD_FACEBOOK: {
                        Router.openFacebookProfile(getActivity(), mProfileViewModel.socialProfile.getPlatform_id());
                        break;
                    }
                    case Constants.Types.FIELD_LINKEDIN: {
                        Router.openLinkedInProfile(getActivity(), mProfileViewModel.socialProfile.getPlatform_id());
                        break;
                    }
                    case Constants.Types.FIELD_TWITTER: {
                        Router.openTwitterProfile(getActivity(), mProfileViewModel.socialProfile.getPlatform_id());
                        break;
                    }
                    case Constants.Types.FIELD_SOCIAL: {
                        Router.openSocialProfile(getActivity(), viewHolderArrayList.get(position).tvFieldValue.toString());
                        break;
                    }
                    default: {
                        DeviceUtils.copyToClipboard(getActivity(), viewHolderArrayList.get(position).tvFieldValue.getText().toString());
                        showToast(convertKeyToLabel(mProfileViewModel.key) + " was copied to clipboard");
                    }
                }
                break;
            }
        }
    }

    private class LoadUser extends AsyncTask<Integer, Void, HashMap<Integer, ProfileViewModel>> {
        @Override
        protected HashMap<Integer, ProfileViewModel> doInBackground(Integer... params) {
            ContagContag mContagContag = ((BaseActivity) UserP2ProfileViewFragment.this.getActivity()).getUser(userId);
            if (mContagContag != null) {
                HashMap<Integer, ProfileViewModel> hmProfileViewModel = new HashMap<>();
                switch (profileType) {
                    case Constants.Types.PROFILE_PERSONAL: {

                        hmProfileViewModel.put(0, new ProfileViewModel(Constants.Keys.KEY_USER_MOBILE_NUMBER,
                                mContagContag.getMobileNumber(), Constants.Types.FIELD_NUMBER));

                        hmProfileViewModel.put(1, new ProfileViewModel(Constants.Keys.KEY_USER_PERSONAL_EMAIL,
                                mContagContag.getPersonalEmail(), Constants.Types.FIELD_EMAIL));

                        hmProfileViewModel.put(2, new ProfileViewModel(Constants.Keys.KEY_USER_ADDRESS,
                                mContagContag.getAddress(), Constants.Types.FIELD_ADDRESS));

                        hmProfileViewModel.put(3, new ProfileViewModel(Constants.Keys.KEY_USER_LANDLINE_NUMBER,
                                mContagContag.getLandLineNumber(), Constants.Types.FIELD_NUMBER));

                        hmProfileViewModel.put(4, new ProfileViewModel(Constants.Keys.KEY_USER_BLOOD_GROUP,
                                mContagContag.getBloodGroup(), Constants.Types.FIELD_LIST));

                        hmProfileViewModel.put(5, new ProfileViewModel(Constants.Keys.KEY_USER_DATE_OF_BIRTH,
                                mContagContag.getDateOfBirth(), Constants.Types.FIELD_DATE));

                        hmProfileViewModel.put(6, new ProfileViewModel(Constants.Keys.KEY_USER_EMERGENCY_CONTACT_NUMBER,
                                mContagContag.getEmergencyContactNumber(), Constants.Types.FIELD_NUMBER));

                        hmProfileViewModel.put(7, new ProfileViewModel(Constants.Keys.KEY_USER_MARITAL_STATUS,
                                mContagContag.getMaritalStatus(), Constants.Types.FIELD_LIST));

                        hmProfileViewModel.put(8, new ProfileViewModel(Constants.Keys.KEY_USER_MARRIAGE_ANNIVERSARY,
                                mContagContag.getMarriageAnniversary(), Constants.Types.FIELD_DATE));

                        hmProfileViewModel.put(9, new ProfileViewModel(Constants.Keys.KEY_USER_GENDER,
                                mContagContag.getGender(), Constants.Types.FIELD_LIST));

                        break;
                    }
                    case Constants.Types.PROFILE_PROFESSIONAL: {

                        hmProfileViewModel.put(0, new ProfileViewModel(Constants.Keys.KEY_USER_WORK_EMAIL,
                                mContagContag.getWorkEmail(), Constants.Types.FIELD_EMAIL));

                        hmProfileViewModel.put(1, new ProfileViewModel(Constants.Keys.KEY_USER_WORK_ADDRESS,
                                mContagContag.getWorkAddress(), Constants.Types.FIELD_ADDRESS));

                        hmProfileViewModel.put(2, new ProfileViewModel(Constants.Keys.KEY_USER_WORK_MOBILE_NUMBER,
                                mContagContag.getWorkMobileNumber(), Constants.Types.FIELD_NUMBER));

                        hmProfileViewModel.put(3, new ProfileViewModel(Constants.Keys.KEY_USER_LANDLINE_NUMBER,
                                mContagContag.getWorkLandLineNumber(), Constants.Types.FIELD_NUMBER));

                        hmProfileViewModel.put(4, new ProfileViewModel(Constants.Keys.KEY_USER_DESIGNATION,
                                mContagContag.getDesignation(), Constants.Types.FIELD_STRING));

                        hmProfileViewModel.put(5, new ProfileViewModel(Constants.Keys.KEY_USER_WORK_FACEBOOK_PAGE,
                                mContagContag.getWorkFacebookPage(), Constants.Types.FIELD_SOCIAL));

                        hmProfileViewModel.put(6, new ProfileViewModel(Constants.Keys.KEY_USER_ANDROID_APP_LINK,
                                mContagContag.getAndroidAppLink(), Constants.Types.FIELD_SOCIAL));

                        hmProfileViewModel.put(7, new ProfileViewModel(Constants.Keys.KEY_USER_IOS_APP_LINK,
                                mContagContag.getIosAppLink(), Constants.Types.FIELD_SOCIAL));

                        break;
                    }

                    case Constants.Types.PROFILE_SOCIAL: {
                        ArrayList<SocialPlatform> socialPlatforms = ((BaseActivity) getActivity()).getSocialPlatforms();
                        ArrayList<SocialProfile> socialProfiles = ((BaseActivity) getActivity()).getSocialProfiles(userId);
                        HashMap<String, SocialPlatform> hmNameToSocialPlatform = new HashMap<>();
                        for (SocialPlatform socialPlatform : socialPlatforms) {
                            hmNameToSocialPlatform.put(socialPlatform.getPlatformName(), socialPlatform);
                        }
                        int counter = 0;
                        for (SocialProfile socialProfile : socialProfiles) {
                            if (hmNameToSocialPlatform.containsKey(socialProfile.getSocial_platform())) {
                                String keyLowerCase = socialProfile.getSocial_platform().toLowerCase();
                                if (keyLowerCase.contains("google")) {
                                    hmProfileViewModel.put(counter++, new ProfileViewModel(keyLowerCase, socialProfile,
                                            Constants.Types.FIELD_GOOGLE));
                                } else if (keyLowerCase.contains("linkedin")) {
                                    hmProfileViewModel.put(counter++, new ProfileViewModel(keyLowerCase, socialProfile,
                                            Constants.Types.FIELD_LINKEDIN));
                                } else if (keyLowerCase.contains("facebook")) {
                                    hmProfileViewModel.put(counter++, new ProfileViewModel(keyLowerCase, socialProfile,
                                            Constants.Types.FIELD_FACEBOOK));
                                } else if (keyLowerCase.contains("twitter")) {
                                    hmProfileViewModel.put(counter++, new ProfileViewModel(keyLowerCase, socialProfile,
                                            Constants.Types.FIELD_TWITTER));
                                } else if (keyLowerCase.contains("instagram")) {
                                    hmProfileViewModel.put(counter++, new ProfileViewModel(keyLowerCase, socialProfile,
                                            Constants.Types.FIELD_INSTAGRAM));
                                } else {
                                    hmProfileViewModel.put(counter++, new ProfileViewModel(keyLowerCase,
                                            hmNameToSocialPlatform.get(socialProfile.getSocial_platform()).getPlatformBaseUrl()
                                                    + "/" + socialProfile.getPlatform_id(),
                                            Constants.Types.FIELD_SOCIAL));
                                }
                                hmNameToSocialPlatform.remove(socialProfile.getSocial_platform());
                            }
                        }

                        for (SocialPlatform socialPlatform : socialPlatforms) {
                            if (hmNameToSocialPlatform.containsKey(socialPlatform.getPlatformName())) {
                                String keyLowerCase = socialPlatform.getPlatformName().toLowerCase();
                                if (keyLowerCase.contains("facebook")) {
                                    hmProfileViewModel.put(counter++, new ProfileViewModel(keyLowerCase, Constants.Types.FIELD_FACEBOOK));
                                } else if (keyLowerCase.contains("google")) {
                                    hmProfileViewModel.put(counter++, new ProfileViewModel(keyLowerCase, Constants.Types.FIELD_GOOGLE));
                                } else if (keyLowerCase.contains("twitter")) {
                                    hmProfileViewModel.put(counter++, new ProfileViewModel(keyLowerCase, Constants.Types.FIELD_TWITTER));
                                } else if (keyLowerCase.contains("instagram")) {
                                    hmProfileViewModel.put(counter++, new ProfileViewModel(keyLowerCase, Constants.Types.FIELD_INSTAGRAM));
                                } else if (keyLowerCase.contains("linkedin")) {
                                    hmProfileViewModel.put(counter++, new ProfileViewModel(keyLowerCase, Constants.Types.FIELD_LINKEDIN));
                                } else {
                                    hmProfileViewModel.put(counter++, new ProfileViewModel(keyLowerCase, Constants.Types.FIELD_SOCIAL));
                                }
                            }
                        }

                    }

                }

                return hmProfileViewModel;
            }
            return null;
        }

        @Override
        protected void onPostExecute(HashMap<Integer, ProfileViewModel> integerP2ProfileViewModelHashMap) {
            hmP2ProfileView.clear();
            hmP2ProfileView.putAll(integerP2ProfileViewModelHashMap);
            drawView();
            setViewContent();
        }
    }


    public class ViewHolder {
        protected TextView tvFieldValue;
        protected TextView tvFieldLabel;
        protected Button btnRequestField;
        protected Button btnAction;

        public ViewHolder() {

        }
    }

}
