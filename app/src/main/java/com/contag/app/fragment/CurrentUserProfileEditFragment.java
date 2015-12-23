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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.model.ContagContag;
import com.contag.app.model.P2ProfileModel;
import com.contag.app.util.DeviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CurrentUserProfileEditFragment extends BaseFragment implements View.OnClickListener, View.OnTouchListener {

    public static final String TAG = CurrentUserProfileEditFragment.class.getName();

    private HashMap<Integer, P2ProfileModel> hmP2PProfileModel;
    private ArrayList<ViewHolder> viewHolderArrayList;
    private LinearLayout llViewContainer;
    private boolean isEditModeOn = false;
    private boolean isDateShown;

    private Button btnEditProfile;
    private ProgressBar pbProfileUpdate;
    private ScrollView svProfile;
    private int profileType;
    private boolean isComingFromNotification, cameFromNotification;
    private Bundle requestBundle;
    private String fieldName;
    private float x1, x2, y1, y2;
    public TextView tvTabDetail;


    public static CurrentUserProfileEditFragment newInstance(int type) {
        CurrentUserProfileEditFragment currentUserProfileEditFragment = new CurrentUserProfileEditFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.Keys.KEY_USER_PROFILE_TYPE, type);
        currentUserProfileEditFragment.setArguments(args);
        return currentUserProfileEditFragment;
    }

    public static CurrentUserProfileEditFragment newInstance(int type, boolean isComingFromNotification,
                                                             Bundle requestBundle, String fieldName) {
        CurrentUserProfileEditFragment currentUserProfileEditFragment = new CurrentUserProfileEditFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.Keys.KEY_USER_PROFILE_TYPE, type);
        args.putBundle(Constants.Keys.KEY_DATA, requestBundle);
        args.putBoolean(Constants.Keys.KEY_COMING_FROM_NOTIFICATION, isComingFromNotification);
        args.putString(Constants.Keys.KEY_FIELD_NAME, fieldName);
        currentUserProfileEditFragment.setArguments(args);
        return currentUserProfileEditFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_details, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        hmP2PProfileModel = new HashMap<>();

        viewHolderArrayList = new ArrayList<>();
        Bundle args = getArguments();
        llViewContainer = (LinearLayout) view.findViewById(R.id.ll_profile_container);
        btnEditProfile = (Button) view.findViewById(R.id.btn_edit_profile);
        pbProfileUpdate = (ProgressBar) view.findViewById(R.id.pb_edit_profile);
        svProfile = (ScrollView) view.findViewById(R.id.sv_user_details);
        svProfile.setOnTouchListener(this);

        profileType = args.getInt(Constants.Keys.KEY_USER_PROFILE_TYPE);
        tvTabDetail=(TextView) view.findViewById(R.id.tv_tab_detail);
        tvTabDetail.setText(keyToString(profileType));
        btnEditProfile.setVisibility(View.VISIBLE);
        btnEditProfile.setTag(0);
        btnEditProfile.setOnClickListener(this);
        isDateShown = false;
        isComingFromNotification = args.getBoolean(Constants.Keys.KEY_COMING_FROM_NOTIFICATION);
        if (isComingFromNotification) {
            requestBundle = args.getBundle(Constants.Keys.KEY_DATA);
            fieldName = args.getString(Constants.Keys.KEY_FIELD_NAME);
        }
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
            case R.id.btn_add: {
                if (!DeviceUtils.isInternetConnected(getActivity())) {
                    showToast("Sorry there is no internet.");
                    return;
                }
                openEditMode();
                scrollToPosition((int) v.getTag());
                break;
            }
            case R.id.btn_edit_profile: {
                if (!DeviceUtils.isInternetConnected(getActivity())) {
                    showToast("Sorry there is no internet.");
                    return;
                }
                if (isEditModeOn) {
                    btnEditProfile.setEnabled(false);
                    sendData();
                } else {
                    ((BaseActivity) getActivity()).setEditMode(true);
                    openEditMode();
                }
                break;
            }
            case R.id.btn_share: {
                int position = (int) v.getTag();
                P2ProfileModel mP2ProfileModel = hmP2PProfileModel.get(position);
                ShareDialog share = ShareDialog.newInstance(mP2ProfileModel.key, mP2ProfileModel.value);
                share.show(getChildFragmentManager(), TAG);
                break;

            }
        }
    }


    private void addViews() {
        llViewContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        for (int position = 0; position < hmP2PProfileModel.size(); position++) {
            View view = inflater.inflate(R.layout.item_profile_edit, llViewContainer, false);

            ViewHolder vh = new ViewHolder();
            vh.rlContainer = (RelativeLayout) view.findViewById(R.id.rl_other_container);
            vh.etFieldValue = (EditText) view.findViewById(R.id.et_field_value);
            vh.tvFieldLabel = (TextView) view.findViewById(R.id.tv_field_name);
            vh.tvFieldValue = (TextView) view.findViewById(R.id.tv_field_value);
            vh.spFieldValue = (Spinner) view.findViewById(R.id.sp_field_value);
            vh.btnShare = (Button) view.findViewById(R.id.btn_share);
            vh.view_line=view.findViewById(R.id.view_line);

            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            //vh.btnShare.setTag(hmP2PProfileModel.get(i).key) ;
            vh.btnShare.setTag(position);
            vh.btnAdd = (Button) view.findViewById(R.id.btn_add);
            vh.btnShare.setOnClickListener(this);
            vh.btnAdd.setOnClickListener(this);
            viewHolderArrayList.add(vh);
            llViewContainer.addView(view);
        }
    }

    private void setViewContent() {
        for (int i = 0; i < hmP2PProfileModel.size(); i++) {
            setUpView(i);
        }
    }

    private void setUpView(int position) {
        P2ProfileModel mP2ProfileModel = hmP2PProfileModel.get(position);
        ViewHolder mViewHolder = viewHolderArrayList.get(position);

        mViewHolder.tvFieldValue.setVisibility(View.VISIBLE);
        mViewHolder.etFieldValue.setVisibility(View.GONE);
        mViewHolder.view_line.setVisibility(View.VISIBLE);
        mViewHolder.spFieldValue.setVisibility(View.GONE);
        mViewHolder.btnAdd.setVisibility(View.GONE);

        String message = mP2ProfileModel.key + " is " + mP2ProfileModel.value;
        log(TAG, message);
//        Router.sendLogs(getActivity(), message, System.currentTimeMillis());

        mViewHolder.tvFieldLabel.setText(convertKeyToLabel(mP2ProfileModel.key));

        int viewType = mP2ProfileModel.viewType;
        if (viewType == Constants.Types.FIELD_STRING) {
            mViewHolder.etFieldValue.setInputType(mP2ProfileModel.inputType);
        } else if (viewType == Constants.Types.FIELD_LIST) {
            ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this.getActivity(),
                    R.layout.spinner_item, mP2ProfileModel.values);
            mViewHolder.spFieldValue.setAdapter(spAdapter);
        } else if (viewType == Constants.Types.FIELD_DATE) {
            final int datePosition = position;
            mViewHolder.etFieldValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus && !isDateShown && isEditModeOn) {
                        log(TAG, "show the date focus");
                        showDate(datePosition);
                    }
                }
            });
            mViewHolder.etFieldValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isDateShown && isEditModeOn) {
                        log(TAG, "show the date click");
                        showDate(datePosition);
                    }
                }
            });
        }

        if (mP2ProfileModel.value != null && mP2ProfileModel.value.length() != 0) {
            mViewHolder.tvFieldValue.setText(mP2ProfileModel.value);
            mViewHolder.btnShare.setVisibility(View.VISIBLE);
            // mViewHolder.btnShare.setTag(position);
        } else {
            mViewHolder.tvFieldValue.setVisibility(View.GONE);
            mViewHolder.btnAdd.setVisibility(View.GONE);
            mViewHolder.btnAdd.setTag(position);
        }
    }

    private void showDate(int position) {
        String date = hmP2PProfileModel.get(position).value;
        if (date == null) {
            date = "";
        }
        DateFragment df = DateFragment.newInstance(date, position, profileType);
        df.show(getChildFragmentManager(), "Date");
        isDateShown = true;
    }

    private void openEditMode() {
        for (int i = 0; i < hmP2PProfileModel.size(); i++) {
            ViewHolder mViewHolder = viewHolderArrayList.get(i);
            P2ProfileModel mP2ProfileModel = hmP2PProfileModel.get(i);
            mViewHolder.btnAdd.setVisibility(View.GONE);
            mViewHolder.btnShare.setVisibility(View.GONE);
            if (mP2ProfileModel.viewType == Constants.Types.FIELD_LIST) {
                mViewHolder.spFieldValue.setVisibility(View.VISIBLE);
                mViewHolder.view_line.setVisibility(View.GONE);
                if (mP2ProfileModel.value != null && mP2ProfileModel.value.length() != 0) {
                    mViewHolder.spFieldValue.setSelection(getSelectedPosition(i));
                }
            } else {
                mViewHolder.etFieldValue.setVisibility(View.VISIBLE);
                mViewHolder.view_line.setVisibility(View.GONE);
                if (mP2ProfileModel.value != null && mP2ProfileModel.value.length() != 0) {
                    mViewHolder.etFieldValue.setText(mP2ProfileModel.value);
                }
            }
            mViewHolder.tvFieldValue.setVisibility(View.GONE);
        }
        isEditModeOn = true;
        Intent iDisableSwipe = new Intent(getActivity().getResources().getString(R.string.intent_filter_edit_mode_enabled));
        iDisableSwipe.putExtra(Constants.Keys.KEY_EDIT_MODE_TOGGLE, false);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(iDisableSwipe);
        btnEditProfile.setText("Save");
        btnEditProfile.setTextColor(getResources().getColor(R.color.white));
    }

    private void sendData() {
        JSONArray aUser = new JSONArray();
        JSONObject oUser;
        try {
            for (int i = 0; i < hmP2PProfileModel.size(); i++) {
                oUser = new JSONObject();
                P2ProfileModel p2ProfileModel = hmP2PProfileModel.get(i);
                ViewHolder vh = viewHolderArrayList.get(i);
                int fieldType = p2ProfileModel.viewType;
                if (fieldType == Constants.Types.FIELD_LIST) {
                    String selectionValue = "";
                    if (vh.spFieldValue.getSelectedItemPosition() != 0) {
                        selectionValue = vh.spFieldValue.getSelectedItem().toString();
                    } else if (p2ProfileModel.value != null && p2ProfileModel.value.length() != 0) {
                        selectionValue = p2ProfileModel.value;
                    }
                    oUser.put(p2ProfileModel.key, selectionValue);
                } else {
                    oUser.put(p2ProfileModel.key, vh.etFieldValue.getText().toString());
                }
                aUser.put(oUser);
            }
            pbProfileUpdate.setVisibility(View.VISIBLE);
           // btnEditProfile.setText("Save");
            // pbProfileUpdate.setIndeterminateDrawable(getResources().getDrawable(R.anim.pb_animation));


            Router.startUserService(getActivity(), Constants.Types.REQUEST_PUT, aUser.toString(), profileType);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void scrollToPosition(int position) {
        if (hmP2PProfileModel.get(position).viewType == Constants.Types.FIELD_LIST) {
            viewHolderArrayList.get(position).spFieldValue.requestFocus();
        } else {
            viewHolderArrayList.get(position).etFieldValue.requestFocus();
        }
    }

    public static String convertKeyToLabel(String key) {
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

    private int getSelectedPosition(int pos) {
        int position = 0;
        P2ProfileModel mP2ProfileModel = hmP2PProfileModel.get(pos);
        pos = 0;
        for (String entry : mP2ProfileModel.values) {
            if (mP2ProfileModel.value.equalsIgnoreCase(entry)) {
                position = pos;
            }
            pos++;
        }
        return position;
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
                    log(DateFragment.TAG, "date is " + intent.getStringExtra(Constants.Keys.KEY_DATE_VALUE));
                    vh.etFieldValue.
                            setText(intent.getStringExtra(Constants.Keys.KEY_DATE_VALUE));
                    isDateShown = false;
                    ((BaseActivity) getActivity()).hideKeyboard(vh.etFieldValue.getWindowToken());
                }
            }
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.sv_user_details && isEditModeOn) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    x1 = event.getX();
                    y1 = event.getY();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    x2 = event.getX();
                    y2 = event.getY();
                    float deltaY = Math.abs(y2 - y1);
                    float deltaX = Math.abs(x2 -x1);
                    if(deltaY < deltaX && deltaX > 100) {
                        showToast("You cannnot navigate while in edit mode");
                        return false;
                    }
                    break;
                }
            }
        }
        return v.onTouchEvent(event);
    }

    private class LoadUser extends AsyncTask<Integer, Void, HashMap<Integer, P2ProfileModel>> {
        @Override
        protected HashMap<Integer, P2ProfileModel> doInBackground(Integer... params) {

            ContagContag cc = ((BaseActivity) CurrentUserProfileEditFragment.this.getActivity()).getCurrentUser();
            HashMap<Integer, P2ProfileModel> hmP2ProfileModel = new HashMap<>();

            switch (profileType) {

                case Constants.Types.PROFILE_PERSONAL: {
                    hmP2ProfileModel.put(0, new P2ProfileModel(Constants.Keys.KEY_USER_PERSONAL_EMAIL, cc.getPersonalEmail(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS));

                    hmP2ProfileModel.put(1, new P2ProfileModel(Constants.Keys.KEY_USER_ADDRESS, cc.getAddress(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS));

                    hmP2ProfileModel.put(2, new P2ProfileModel(Constants.Keys.KEY_USER_LANDLINE_NUMBER, cc.getLandLineNumber(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_PHONE));

                    hmP2ProfileModel.put(3, new P2ProfileModel(Constants.Keys.KEY_USER_BLOOD_GROUP, cc.getBloodGroup(),
                            Constants.Types.FIELD_LIST, Constants.Arrays.USER_BLOOD_GROUPS));

                    hmP2ProfileModel.put(4, new P2ProfileModel(Constants.Keys.KEY_USER_DATE_OF_BIRTH, cc.getDateOfBirth(),
                            Constants.Types.FIELD_DATE, InputType.TYPE_DATETIME_VARIATION_DATE));

                    hmP2ProfileModel.put(5, new P2ProfileModel(Constants.Keys.KEY_USER_EMERGENCY_CONTACT_NUMBER, cc.getEmergencyContactNumber(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_PHONE));

                    hmP2ProfileModel.put(6, new P2ProfileModel(Constants.Keys.KEY_USER_MARRIAGE_ANNIVERSARY, cc.getMarriageAnniversary(),
                            Constants.Types.FIELD_DATE, InputType.TYPE_DATETIME_VARIATION_DATE));

                    hmP2ProfileModel.put(7, new P2ProfileModel(Constants.Keys.KEY_USER_MARITAL_STATUS, cc.getMaritalStatus(),
                            Constants.Types.FIELD_LIST, Constants.Arrays.USER_MARITAL_STATUS));


                    break;
                }
                case Constants.Types.PROFILE_PROFESSIONAL: {
                    log(TAG, "company name is  " + cc.getCompanyName());
                    hmP2ProfileModel.put(0, new P2ProfileModel(Constants.Keys.KEY_COMPANY_NAME, cc.getCompanyName(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));

                    hmP2ProfileModel.put(1, new P2ProfileModel(Constants.Keys.KEY_USER_WORK_EMAIL, cc.getWorkEmail(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS));

                    hmP2ProfileModel.put(2, new P2ProfileModel(Constants.Keys.KEY_USER_WORK_ADDRESS, cc.getWorkAddress(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS));

                    hmP2ProfileModel.put(3, new P2ProfileModel(Constants.Keys.KEY_USER_WORK_MOBILE_NUMBER, cc.getWorkMobileNumber(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_PHONE));

                    hmP2ProfileModel.put(4, new P2ProfileModel(Constants.Keys.KEY_USER_WORK_LANDLINE_NUMBER, cc.getWorkLandLineNumber(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_PHONE));

                    hmP2ProfileModel.put(5, new P2ProfileModel(Constants.Keys.KEY_USER_DESIGNATION, cc.getDesignation(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));

                    hmP2ProfileModel.put(6, new P2ProfileModel(Constants.Keys.KEY_USER_WORK_FACEBOOK_PAGE, cc.getWorkFacebookPage(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));

                    hmP2ProfileModel.put(7, new P2ProfileModel(Constants.Keys.KEY_USER_ANDROID_APP_LINK, cc.getAndroidAppLink(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));

                    hmP2ProfileModel.put(8, new P2ProfileModel(Constants.Keys.KEY_USER_IOS_APP_LINK, cc.getIosAppLink(),
                            Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));

                    break;
                }

            }
            return hmP2ProfileModel;
        }

        @Override
        protected void onPostExecute(HashMap<Integer, P2ProfileModel> hm) {
            hmP2PProfileModel.clear();
            hmP2PProfileModel.putAll(hm);
            if (viewHolderArrayList.size() != hmP2PProfileModel.size()) {
                addViews();
            }
            if (isEditModeOn) {
                Intent iEnableSwipe = new Intent(getActivity().getResources().getString(R.string.intent_filter_edit_mode_enabled));
                iEnableSwipe.putExtra(Constants.Keys.KEY_EDIT_MODE_TOGGLE, true);
                log(TAG, "sending broadcast from p2 with true");
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(iEnableSwipe);
            }
            ((BaseActivity) getActivity()).setEditMode(false);
            isEditModeOn = false;
            setViewContent();
            btnEditProfile.setEnabled(true);
            pbProfileUpdate.setVisibility(View.GONE);
            btnEditProfile.setText("Edit");
            if (cameFromNotification) {
                for (int position = 0; position < hmP2PProfileModel.size(); position++) {
                    P2ProfileModel mP2ProfileModel = hmP2PProfileModel.get(position);
                    if (mP2ProfileModel.key.equalsIgnoreCase(fieldName) && mP2ProfileModel.value != null
                            && mP2ProfileModel.value.length() != 0) {
                        ShareFieldDialog mShareFieldDialog = ShareFieldDialog.newInstance(requestBundle, fieldName);
                        mShareFieldDialog.show(getChildFragmentManager(), "share_dialog");
                        break;
                    }
                }
                cameFromNotification = false;
            }
            if (isComingFromNotification) {
                openEditMode();
                for (int position = 0; position < hmP2PProfileModel.size(); position++) {
                    P2ProfileModel mP2ProfileModel = hmP2PProfileModel.get(position);
                    if (mP2ProfileModel.key.equals(fieldName)) {
                        scrollToPosition(position);
                        break;
                    }
                }
                cameFromNotification = true;
                isComingFromNotification = false;
            }
        }
    }
    private String keyToString(int profileType) {
        if(profileType==Constants.Types.PROFILE_PERSONAL)
            return "PERSONAl";
        if(profileType==Constants.Types.PROFILE_SOCIAL)
            return "SOCIAL";
        if(profileType==Constants.Types.PROFILE_PROFESSIONAL)
            return "PROFESSIONAL";
        return "";
    }


    private class ViewHolder {
        public TextView tvFieldLabel;
        public TextView tvFieldValue;
        public EditText etFieldValue;
        public Button btnAdd;
        public Button btnShare;
        public Spinner spFieldValue;
        public RelativeLayout rlContainer;
        public View view_line;
    }
}
