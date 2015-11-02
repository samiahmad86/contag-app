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
import android.widget.Spinner;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ProfileModel;
import com.contag.app.util.DeviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CurrentUserProfileEditFragment extends BaseFragment implements View.OnClickListener {

    private HashMap<Integer, ProfileModel> hmProfileModel;
    private int profileType;
    private boolean isListDrawn = false;
    private ArrayList<ViewHolder> viewHolderArrayList;
    private LinearLayout llViewContainer;
    public static final String TAG = CurrentUserProfileEditFragment.class.getName();

    public static CurrentUserProfileEditFragment newInstance(int type) {
        CurrentUserProfileEditFragment epdf = new CurrentUserProfileEditFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.Keys.KEY_USER_PROFILE_TYPE, type);
        epdf.setArguments(args);
        return epdf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_details, container, false);
        hmProfileModel = new HashMap<>();
        viewHolderArrayList = new ArrayList<>();
        Bundle args = getArguments();
        llViewContainer = (LinearLayout) view.findViewById(R.id.ll_profile_container);
        profileType = args.getInt(Constants.Keys.KEY_USER_PROFILE_TYPE);
        new LoadUser().execute();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).
                registerReceiver(brUsr, new IntentFilter(getResources().getString(R.string.intent_filter_user_received)));
        LocalBroadcastManager.getInstance(getActivity()).
                registerReceiver(brDatePicked, new IntentFilter(getResources().getString(R.string.intent_filter_date_set)));
        LocalBroadcastManager.getInstance(getActivity()).
                registerReceiver(brEditModeToggle, new IntentFilter(getResources().getString(R.string.intent_filter_edit_mode)));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brUsr);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brDatePicked);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brEditModeToggle);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_add: {
                if (!DeviceUtils.isInternetConnected(getActivity())) {
                    showToast("Sorry there is no internet.");
                    return;
                }
                openEditMode();
                Intent intent = new Intent(getActivity().getResources().getString(R.string.intent_filter_edit_button_toggle));
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                break;
            }
        }
    }


    private void addViews() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        for (int i = 0; i < hmProfileModel.size(); i++) {
            View view = inflater.inflate(R.layout.item_profile_edit, llViewContainer, false);
            ViewHolder vh = new ViewHolder();
            vh.etFieldValue = (EditText) view.findViewById(R.id.et_field_value);
            vh.tvFieldLabel = (TextView) view.findViewById(R.id.tv_field_label);
            vh.tvFieldValue = (TextView) view.findViewById(R.id.tv_field_value);
            vh.spFieldValue = (Spinner) view.findViewById(R.id.sp_field_value);
            vh.btnShare = (Button) view.findViewById(R.id.btn_share);
            vh.btnAdd = (Button) view.findViewById(R.id.btn_add);
            vh.btnAdd.setOnClickListener(this);
            viewHolderArrayList.add(vh);
            llViewContainer.addView(view);
        }
    }

    private void setViewContent() {
        for (int i = 0; i < hmProfileModel.size(); i++) {
            ViewHolder vh = viewHolderArrayList.get(i);
            vh.btnAdd.setTag(i);
            vh.tvFieldValue.setVisibility(View.VISIBLE);
            vh.etFieldValue.setVisibility(View.GONE);
            vh.spFieldValue.setVisibility(View.GONE);
            vh.btnAdd.setVisibility(View.GONE);
            int fieldType = hmProfileModel.get(i).fieldType;
            if (fieldType == Constants.Types.FIELD_STRING) {
                vh.etFieldValue.setInputType(hmProfileModel.get(i).inputType);
            } else if (fieldType == Constants.Types.FIELD_LIST) {
                ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this.getActivity(),
                        android.R.layout.simple_spinner_item, getSpinnerArray(hmProfileModel.get(i).key));
                vh.spFieldValue.setAdapter(spAdapter);
            } else if (fieldType == Constants.Types.FIELD_DATE) {
                final int position = i;
                vh.etFieldValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus) {
                            ViewHolder vh = viewHolderArrayList.get(position);
                            String date = vh.tvFieldValue.getText().toString();
                            if (date == null || date.length() == 0) {
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
                                Calendar calendar = Calendar.getInstance();
                                date = dateFormat.format(calendar.getTime()).toString();
                            }
                            DateFragment df = DateFragment.newInstance(date, position, profileType);
                            df.show(getChildFragmentManager(), "Date");
                        }
                    }
                });
            }
            vh.tvFieldLabel.setText(convertKeytoLabel(hmProfileModel.get(i).key));
            String value = String.valueOf(hmProfileModel.get(i).value);
            if (value != null && value.length() != 0) {
                vh.tvFieldValue.setText(value);
                vh.btnShare.setVisibility(View.VISIBLE);
            } else {
                vh.tvFieldValue.setVisibility(View.GONE);
                vh.btnAdd.setVisibility(View.VISIBLE);
            }
        }
    }

    private void openEditMode() {
        for (int i = 0; i < hmProfileModel.size(); i++) {
            ViewHolder vh = viewHolderArrayList.get(i);
            ProfileModel pm = hmProfileModel.get(i);
            vh.btnAdd.setVisibility(View.GONE);
            vh.btnShare.setVisibility(View.GONE);
            String value = vh.tvFieldValue.getText().toString();
            if (pm.fieldType == Constants.Types.FIELD_LIST) {
                vh.spFieldValue.setVisibility(View.VISIBLE);
                if(value.length() != 0) {
                    int position = -1;
                    String[] arr = getSpinnerArray(pm.key);
                    for(int j = 0; j < arr.length; j ++) {
                        if(arr[j].equalsIgnoreCase(value)) {
                            position = j;
                            break;
                        }
                    }
                    vh.spFieldValue.setSelection(position);
                }
            } else {
                vh.etFieldValue.setVisibility(View.VISIBLE);
                if(value.length() != 0) {
                    vh.etFieldValue.setText(value);
                }
            }
            vh.tvFieldValue.setVisibility(View.GONE);
        }
    }

    private void sendData(String name) {
        JSONArray aUser = new JSONArray();
        JSONObject oUser = new JSONObject();
        try {
            oUser.put(Constants.Keys.KEY_USER_NAME, name);
            aUser.put(oUser);
            for(int i = 0; i < hmProfileModel.size(); i++) {
                oUser = new JSONObject();
                ProfileModel pm  = hmProfileModel.get(i);
                ViewHolder vh = viewHolderArrayList.get(i);
                int fieldType = pm.fieldType;
                if(fieldType == Constants.Types.FIELD_LIST) {
                    oUser.put(pm.key, vh.spFieldValue.getSelectedItem().toString());
                } else {
                    oUser.put(pm.key, vh.etFieldValue.getText().toString());
                }
                aUser.put(oUser);
            }
            log(TAG, aUser.toString());
            Router.startUserService(getActivity(), Constants.Types.REQUEST_PUT, aUser.toString(), profileType);
        } catch (JSONException ex) {
            ex.printStackTrace();
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

    private String[] getSpinnerArray(String key) {
        String[] arr = null;
        if (key.equalsIgnoreCase(Constants.Keys.KEY_USER_GENDER)) {
            arr = Constants.Arrays.USER_GENDER;
        } else if (key.equalsIgnoreCase(Constants.Keys.KEY_USER_MARITAL_STATUS)) {
            arr = Constants.Arrays.USER_MARITAL_STATUS;
        } else if (key.equalsIgnoreCase(Constants.Keys.KEY_USER_BLOOD_GROUP)) {
            arr = Constants.Arrays.USER_BLOOD_GROUPS;
        }
        return arr;
    }

    private BroadcastReceiver brUsr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, 0);
            if (type == CurrentUserProfileEditFragment.this.profileType) {
                new LoadUser().execute(type);
            }
        }
    };

    private BroadcastReceiver brDatePicked = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int type = intent.getIntExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, 0);
                if (type == CurrentUserProfileEditFragment.this.profileType) {
                    int position = intent.getIntExtra(Constants.Keys.KEY_VIEW_POSITION, 0);
                    ViewHolder vh = viewHolderArrayList.get(position);
                    vh.etFieldValue.
                            setText(intent.getStringExtra(Constants.Keys.KEY_DATE_VALUE));

                }
            }
        }
    };

    private BroadcastReceiver brEditModeToggle = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                boolean toggle = intent.getBooleanExtra(Constants.Keys.KEY_EDIT_MODE_TOGGLE, false);
                if (toggle) {
                    openEditMode();
                } else {
                    sendData(intent.getStringExtra(Constants.Keys.KEY_USER_NAME));
                }
            }
        }
    };

    private class LoadUser extends AsyncTask<Integer, Void, HashMap<Integer, ProfileModel>> {
        @Override
        protected HashMap<Integer, ProfileModel> doInBackground(Integer... params) {
            ContagContag cc = ((BaseActivity) CurrentUserProfileEditFragment.this.getActivity()).getCurrentUser();
            HashMap<Integer, ProfileModel> hm = new HashMap<>();
            switch (profileType) {
                case Constants.Types.PROFILE_PERSONAL: {
                    hm.put(0, new ProfileModel(Constants.Keys.KEY_USER_MOBILE_NUMBER, cc.getMobileNumber(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_PHONE));
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
                    hm.put(0, new ProfileModel(Constants.Keys.KEY_USER_WORK_EMAIL, cc.getWorkEmail(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS));
                    hm.put(1, new ProfileModel(Constants.Keys.KEY_USER_WORK_ADDRESS, cc.getWorkAddress(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS));
                    hm.put(2, new ProfileModel(Constants.Keys.KEY_USER_WORK_MOBILE_NUMBER, cc.getWorkMobileNumber(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_PHONE));
                    hm.put(3, new ProfileModel(Constants.Keys.KEY_USER_WORK_LANDLINE_NUMBER, cc.getWorkLandLineNumber(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_PHONE));
                    hm.put(4, new ProfileModel(Constants.Keys.KEY_USER_DESIGNATION, cc.getDesignation(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));
                    hm.put(5, new ProfileModel(Constants.Keys.KEY_USER_WORK_FACEBOOK_PAGE, cc.getWorkFacebookPage(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));
                    hm.put(6, new ProfileModel(Constants.Keys.KEY_USER_ANDROID_APP_LINK, cc.getAndroidAppLink(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));
                    hm.put(7, new ProfileModel(Constants.Keys.KEY_USER_IOS_APP_LINK, cc.getIosAppLink(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));
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
                addViews();
                log(TAG, "drawing list");
                isListDrawn = true;
            } else {
                log(TAG, "list not drawn");
            }
            setViewContent();
        }
    }

    private class ViewHolder {
        public TextView tvFieldLabel;
        public TextView tvFieldValue;
        public EditText etFieldValue;
        public Button btnAdd;
        public Button btnShare;
        public Spinner spFieldValue;
    }
}
