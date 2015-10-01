package com.contag.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.config.Router;
import com.contag.app.model.ContagContag;
import com.contag.app.model.DaoSession;
import com.contag.app.model.ProfileModel;
import com.contag.app.model.SocialPlatform;
import com.contag.app.model.SocialPlatformDao;
import com.contag.app.model.SocialProfile;
import com.contag.app.model.SocialProfileDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by tanay on 28/9/15.
 */
public class EditProfileDetailsFragment extends BaseFragment implements View.OnClickListener {

    private HashMap<Integer, ProfileModel> hmProfileModel;
    private int type;
    private boolean isListDrawn = false;
    private ArrayList<ViewHolder> viewHolderArrayList;
    private LinearLayout llViewContainer;
    public static final String TAG = EditProfileDetailsFragment.class.getName();

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
        viewHolderArrayList = new ArrayList<>();
        Bundle args = getArguments();
        llViewContainer = (LinearLayout) view.findViewById(R.id.ll_profile_container);
        type = args.getInt(Constants.Keys.KEY_USER_PROFILE_TYPE);
        TextView tvProfileType = (TextView) view.findViewById(R.id.tv_profile_type);

        switch (type) {
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

        new LoadUser().execute(type);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).
                registerReceiver(brUsr, new IntentFilter(getResources().getString(R.string.intent_filter_user_received)));
        LocalBroadcastManager.getInstance(getActivity()).
                registerReceiver(brDatePicked, new IntentFilter(getResources().getString(R.string.intent_filter_date_set)));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brUsr);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brDatePicked);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_edit: {
                int tag = (int) v.getTag();
                ViewHolder vh = viewHolderArrayList.get(tag);
                if (vh.btnEdit.getText().toString().equalsIgnoreCase("Edit") ||
                        vh.btnEdit.getText().toString().equalsIgnoreCase("Add")) {
                    vh.etFieldValue.setText(vh.tvFieldValue.getText().toString());
                    vh.tvFieldValue.setVisibility(View.GONE);
                    vh.etFieldValue.setEnabled(true);
                    int fieldType = hmProfileModel.get(tag).fieldType;
                    if (fieldType == Constants.Types.FIELD_LIST) {
                        vh.spFieldValue.setVisibility(View.VISIBLE);
                        vh.btnEdit.setText("Update");
                    } else if (fieldType == Constants.Types.FIELD_STRING) {
                        vh.etFieldValue.setVisibility(View.VISIBLE);
                        vh.btnEdit.setText("Update");
                    } else if (fieldType == Constants.Types.FIELD_DATE) {
                        String date = vh.tvFieldValue.getText().toString();
                        if (date == null || date.length() == 0) {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
                            Calendar calendar = Calendar.getInstance();
                            date = dateFormat.format(calendar.getTime()).toString();
                        }
                        vh.tvFieldValue.setVisibility(View.VISIBLE);
                        DateFragment df = DateFragment.newInstance(date, tag, type);
                        df.show(getChildFragmentManager(), "Date");
                    }
                } else {
                    JSONArray arrUsr = new JSONArray();
                    JSONObject oUsr = new JSONObject();
                    try {
                        int fieldType = hmProfileModel.get(tag).fieldType;
                        if (fieldType == Constants.Types.FIELD_LIST) {
                            oUsr.put(hmProfileModel.get(tag).key,
                                    vh.spFieldValue.getSelectedItem().toString());
                            log(TAG, vh.spFieldValue.getSelectedItem().toString());
                        } else if(fieldType == Constants.Types.FIELD_STRING) {
                            oUsr.put(hmProfileModel.get(tag).key, vh.etFieldValue.getText().toString());
                        } else if(fieldType == Constants.Types.FIELD_DATE) {
                            oUsr.put(hmProfileModel.get(tag).key, vh.tvFieldValue.getText().toString());
                        }
                        arrUsr.put(oUsr);
                        Router.startUserService(getActivity(), Constants.Types.REQUEST_PUT,
                                arrUsr.toString(), type);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    vh.btnEdit.setVisibility(View.GONE);
                    vh.pbUpdate.setVisibility(View.VISIBLE);
                    vh.btnEdit.setEnabled(false);
                    vh.etFieldValue.setEnabled(false);
                }
                break;
            }
        }
    }


    private void addViews(int size) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        int initialCount = llViewContainer.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = inflater.inflate(R.layout.item_profile_edit, llViewContainer, false);
            ViewHolder vh = new ViewHolder();
            vh.btnEdit = (Button) view.findViewById(R.id.btn_edit);
            vh.btnEdit.setTag(initialCount + i);
            vh.btnEdit.setOnClickListener(this);
            vh.etFieldValue = (EditText) view.findViewById(R.id.et_field_value);
            vh.pbUpdate = (ProgressBar) view.findViewById(R.id.pb_update);
            vh.tvFieldLabel = (TextView) view.findViewById(R.id.tv_field_label);
            vh.tvFieldValue = (TextView) view.findViewById(R.id.tv_field_value);
            vh.spFieldValue = (Spinner) view.findViewById(R.id.sp_field_value);
            view.setTag(vh);
            viewHolderArrayList.add(vh);
            llViewContainer.addView(view, initialCount + i);
        }
    }

    private void removeViews(int size) {
        int initialSize = llViewContainer.getChildCount();
        for (int i = 1; i <= size; i++) {
            llViewContainer.removeViewAt(initialSize - i);
            viewHolderArrayList.remove(initialSize - i);
        }

    }

    private void setViewContent() {
        for (int i = 0; i < hmProfileModel.size(); i++) {
            ViewHolder vh = viewHolderArrayList.get(i);
            vh.btnEdit.setEnabled(true);
            vh.btnEdit.setTag(i);
            vh.btnEdit.setText("Edit");
            vh.btnEdit.setVisibility(View.VISIBLE);
            vh.tvFieldValue.setVisibility(View.VISIBLE);
            vh.pbUpdate.setVisibility(View.GONE);
            vh.etFieldValue.setVisibility(View.GONE);
            vh.spFieldValue.setVisibility(View.GONE);
            if (hmProfileModel.get(i).fieldType == Constants.Types.FIELD_STRING) {
                vh.etFieldValue.setInputType(hmProfileModel.get(i).inputType);
            } else if (hmProfileModel.get(i).fieldType == Constants.Types.FIELD_LIST) {
                String[] arr = null;
                if (hmProfileModel.get(i).key.equalsIgnoreCase(Constants.Keys.KEY_USER_GENDER)) {
                    arr = Constants.Arrays.USER_GENDER;
                } else if (hmProfileModel.get(i).key.equalsIgnoreCase(Constants.Keys.KEY_USER_MARITAL_STATUS)) {
                    arr = Constants.Arrays.USER_MARITAL_STATUS;
                } else if (hmProfileModel.get(i).key.equalsIgnoreCase(Constants.Keys.KEY_USER_BLOOD_GROUP)) {
                    arr = Constants.Arrays.USER_BLOOD_GROUPS;
                }
                ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this.getActivity(),
                        android.R.layout.simple_spinner_item, arr);
                vh.spFieldValue.setAdapter(spAdapter);
            }
            vh.tvFieldLabel.setText(convertKeytoLabel(hmProfileModel.get(i).key));
            if (hmProfileModel.get(i).value != null && (String.valueOf(hmProfileModel.get(i).value)).length() != 0) {
                vh.tvFieldValue.setText(String.valueOf(hmProfileModel.get(i).value));
            } else {
                vh.tvFieldValue.setVisibility(View.GONE);
                vh.btnEdit.setText("Add");
            }
        }
    }

    private String convertKeytoLabel(String key) {
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

    private BroadcastReceiver brUsr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, 0);
            if (type == EditProfileDetailsFragment.this.type) {
                new LoadUser().execute(type);
            }
        }
    };

    private BroadcastReceiver brDatePicked = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int type = intent.getIntExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, 0);
                if (type == EditProfileDetailsFragment.this.type) {
                    int position = intent.getIntExtra(Constants.Keys.KEY_VIEW_POSITION, 0);
                    viewHolderArrayList.get(position).tvFieldValue.setVisibility(View.VISIBLE);
                    viewHolderArrayList.get(position).tvFieldValue.
                            setText(intent.getStringExtra(Constants.Keys.KEY_DATE_VALUE));
                    viewHolderArrayList.get(position).btnEdit.setText("Update");
                }
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
                    hm.put(1, new ProfileModel(Constants.Keys.KEY_USER_PERSONAL_EMAIL, cc.getPersonalEmail(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS));
                    hm.put(2, new ProfileModel(Constants.Keys.KEY_USER_ADDRESS, cc.getAddress(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS));
                    hm.put(3, new ProfileModel(Constants.Keys.KEY_USER_LANDLINE_NUMBER, cc.getLandLineNumber(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_PHONE));
                    hm.put(4, new ProfileModel(Constants.Keys.KEY_USER_BLOOD_GROUP, cc.getBloodGroup(),
                            Constants.Types.FIELD_LIST));
                    hm.put(5, new ProfileModel(Constants.Keys.KEY_USER_DATE_OF_BIRTH, cc.getDateOfBirth(),
                            Constants.Types.FIELD_DATE));
                    hm.put(6, new ProfileModel(Constants.Keys.KEY_USER_EMERGENCY_CONTACT_NUMBER, cc.getEmergencyContactNumber(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_PHONE));
                    hm.put(7, new ProfileModel(Constants.Keys.KEY_USER_MARRIAGE_ANNIVERSARY, cc.getMarriageAnniversary(),
                            Constants.Types.FIELD_DATE));
                    hm.put(8, new ProfileModel(Constants.Keys.KEY_USER_MARITAL_STATUS, cc.getMaritalStatus(),
                            Constants.Types.FIELD_LIST));
                    hm.put(9, new ProfileModel(Constants.Keys.KEY_USER_GENDER, cc.getGender(),
                            Constants.Types.FIELD_LIST));
                    break;
                }
                case Constants.Types.PROFILE_PROFESSIONAL: {
                    hm.put(0, new ProfileModel(Constants.Keys.KEY_USER_NAME, cc.getName(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));
                    hm.put(1, new ProfileModel(Constants.Keys.KEY_USER_WORK_EMAIL, cc.getWorkEmail(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS));
                    hm.put(2, new ProfileModel(Constants.Keys.KEY_USER_WORK_ADDRESS, cc.getWorkAddress(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS));
                    hm.put(3, new ProfileModel(Constants.Keys.KEY_USER_WORK_MOBILE_NUMBER, cc.getWorkMobileNumber(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_PHONE));
                    hm.put(4, new ProfileModel(Constants.Keys.KEY_USER_WORK_LANDLINE_NUMBER, cc.getWorkLandLineNumber(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_PHONE));
                    hm.put(5, new ProfileModel(Constants.Keys.KEY_USER_DESIGNATION, cc.getDesignation(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));
                    hm.put(6, new ProfileModel(Constants.Keys.KEY_USER_WORK_FACEBOOK_PAGE, cc.getWorkFacebookPage(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));
                    hm.put(7, new ProfileModel(Constants.Keys.KEY_USER_ANDROID_APP_LINK, cc.getAndroidAppLink(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));
                    hm.put(8, new ProfileModel(Constants.Keys.KEY_USER_IOS_APP_LINK, cc.getIosAppLink(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));
                    break;
                }

                case Constants.Types.PROFILE_SOCIAL: {
                    DaoSession session = ((ContagApplication) getActivity().getApplicationContext()).getDaoSession();
                    SocialPlatformDao socialPlatformDao = session.getSocialPlatformDao();
                    SocialProfileDao socialProfileDao = session.getSocialProfileDao();
                    ArrayList<SocialPlatform> socialPlatforms = (ArrayList<SocialPlatform>) socialPlatformDao.loadAll();
                    HashMap<String, String> hmReverse = new HashMap<>();
                    for (SocialPlatform sp : socialPlatforms) {
                        hmReverse.put(sp.getPlatformName(), sp.getPlatformBaseUrl());
                    }
                    ArrayList<SocialProfile> socialProfiles = (ArrayList<SocialProfile>) socialProfileDao.loadAll();
                    int counter = 0;
                    for (SocialProfile sp : socialProfiles) {
                        if (hmReverse.containsKey(sp.getSocial_platform())) {
                            hm.put(counter++, new ProfileModel(sp.getSocial_platform(),
                                    hmReverse.get(sp.getSocial_platform()) + "/" + sp.getPlatform_id(),
                                    Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));
                            hmReverse.remove(sp.getSocial_platform());
                        }
                    }
                    for (SocialPlatform sp : socialPlatforms) {
                        if (hmReverse.containsKey(sp.getPlatformName())) {
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
            if (!isListDrawn) {
                addViews(hmProfileModel.size());
                isListDrawn = true;
            } else if (hmProfileModel.size() > llViewContainer.getChildCount()) {
                addViews(hmProfileModel.size() - llViewContainer.getChildCount());
            } else if (hmProfileModel.size() < llViewContainer.getChildCount()) {
                removeViews(llViewContainer.getChildCount() - hmProfileModel.size());
            }
            setViewContent();
        }
    }

    private class ViewHolder {
        public TextView tvFieldLabel;
        public TextView tvFieldValue;
        public EditText etFieldValue;
        public Button btnEdit;
        public ProgressBar pbUpdate;
        public Spinner spFieldValue;
    }
}
