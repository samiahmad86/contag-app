package com.contag.app.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.config.Router;
import com.contag.app.model.ContactResponse;
import com.contag.app.model.ContagContactResponse;
import com.contag.app.model.ContagContag;
import com.contag.app.model.CustomShare;
import com.contag.app.model.CustomShareDao;
import com.contag.app.model.DaoSession;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestDao;
import com.contag.app.model.InterestResponse;
import com.contag.app.util.ImageUtils;
import com.contag.app.util.PrefUtils;
import com.contag.app.util.ShareUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NavDrawerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NavDrawerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavDrawerFragment extends Fragment implements View.OnClickListener, NfcAdapter.OnNdefPushCompleteCallback {
    private OnFragmentInteractionListener mListener;
    private ImageView ivHeader;
    private TextView tvUsrName, tvUsrCuntId, notificationTxt, feedbackTxt, rateTxt, logoutTxt;
    private Boolean isLoading;
    private TextView tvNotificationCount,tvVisitingCard;
    private RadioGroup radioSexGroup;
    private RadioButton radioButton;
    private TextView share_contag;
    public static final String TAG = NavDrawerFragment.class.getName();
    private ContagContag mCcUser;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NavDrawerFragment.
     */
    public static NavDrawerFragment newInstance() {
        NavDrawerFragment fragment = new NavDrawerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NavDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nav_drawer, container, false);
        ivHeader = (ImageView) view.findViewById(R.id.iv_nav_drawer_header);

        notificationTxt = (TextView) view.findViewById(R.id.tv_notification_txt);
        tvNotificationCount = (TextView) view.findViewById(R.id.tv_notification_count);
        feedbackTxt = (TextView) view.findViewById(R.id.tv_feedback_txt);
        rateTxt = (TextView) view.findViewById(R.id.tv_rate_txt);
        logoutTxt = (TextView) view.findViewById(R.id.tv_logout_txt);
        tvUsrCuntId = (TextView) view.findViewById(R.id.tv_usr_cunt_id);
        tvUsrName = (TextView) view.findViewById(R.id.tv_usr_name);
        radioSexGroup = (RadioGroup) view.findViewById(R.id.radioSex);
        share_contag = (TextView) view.findViewById(R.id.share_contag);
        tvVisitingCard=(TextView) view.findViewById(R.id.tv_visiting_text);
        tvUsrName.setOnClickListener(this);
        tvUsrCuntId.setOnClickListener(this);

        share_contag.setOnClickListener(this);
        notificationTxt.setOnClickListener(this);
        feedbackTxt.setOnClickListener(this);
        rateTxt.setOnClickListener(this);
        logoutTxt.setOnClickListener(this);
        tvVisitingCard.setOnClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("GCM", "Notification count: " + PrefUtils.getNewNotificationCount());
        new LoadUser().execute();

        tvNotificationCount.setText(String.valueOf(PrefUtils.getNewNotificationCount()));
        if(PrefUtils.getNewNotificationCount()==0)
            tvNotificationCount.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(brNotificationCountUpdate,
                new IntentFilter(getActivity().getResources().getString(R.string.intent_filter_notification_count)));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brNotificationCountUpdate);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.tv_notification_txt: {
                //Log.d("NavDrawer", "Notification") ;

                Router.startNotificationsActivity(getActivity(), "navDrawer");
                break;
            }
            case R.id.tv_feedback_txt: {
                Router.startGmailApp(getActivity());
                break;
            }
            case R.id.tv_rate_txt: {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.contag.app")));
                break;
            }
            case R.id.tv_usr_name: {
                Router.startUserActivity(getActivity(), TAG, PrefUtils.getCurrentUserID());

                break;
            }
            case R.id.tv_visiting_text: {
                Router. startVisitingCardActivity(getActivity(), TAG, PrefUtils.getCurrentUserID());

                break;
            }
            case R.id.tv_usr_cunt_id: {
                Router.startUserActivity(getActivity(), TAG, PrefUtils.getCurrentUserID());
                break;
            }
            case R.id.tv_logout_txt: {
                Log.d("logout", "Logout called");
                logout();
                break;
            }
            case R.id.share_contag: {
                /*int selectedId = radioSexGroup.getCheckedRadioButtonId();
                String category="";
                if(selectedId==R.id.radio_personal)
                    category=" Personal";
                if(selectedId==R.id.radio_social)
                    category=" Social";
                if(selectedId==R.id.radio_professional)
                    category=" Professional";
                ShareUtils.shareText(getActivity(), tvUsrName.getText() + " has shared his" + category + " information with you on Contag. \n Download link:https://play.google.com/store/apps/details?id=com.contag.app");*/

               /* if (mCcUser != null) {
                    ShareUtils.sendViaNFC(getActivity(), getShareableData(mCcUser), this);
                } else {
                    Toast.makeText(getActivity(), "user was null", Toast.LENGTH_SHORT).show();
                }*/
            }

        }
    }

   /* private String getShareableData(ContagContag pUser) {

        DaoSession session = ((ContagApplication) getActivity().getApplicationContext()).getDaoSession();
        CustomShareDao customShareDao = session.getCustomShareDao();
        List<CustomShare> customShareList = new ArrayList<CustomShare>();
        ArrayList<Interest> userInterest=new ArrayList<Interest>();
        InterestDao interestDao = session.getInterestDao();
        userInterest=(ArrayList<Interest>) interestDao.queryBuilder().
                where(InterestDao.Properties.ContagUserId.eq(pUser.getId())).list();




     //  ArrayList<InterestResponse> userInterest=new ArrayList<InterestResponse>();

      //  Log.d("iList", "In base activity: " + id) ;



        try {
            customShareList = customShareDao.queryBuilder().where(customShareDao.queryBuilder().and(CustomShareDao.Properties.UserID.eq(pUser.getId()), CustomShareDao.Properties.Is_public.eq(true))).list();
        } catch (Exception ex) {
            return null;
        }
        List<String> publicFields = new ArrayList<>();
        for (CustomShare customShare : customShareList)
            publicFields.add(customShare.getField_name());



        ContactResponse contactResponse = new ContactResponse();
        contactResponse.contactName = pUser.getName();
        contactResponse.contactNumber = pUser.getMobileNumber();
        contactResponse.isOnContag = true;
        contactResponse.createdOn = pUser.getCreatedOn();
        contactResponse.updatedOn = pUser.getUpdatedOn();

        ContagContactResponse contagContactResponse = new ContagContactResponse();
        ArrayList<InterestResponse> interestResponse=new ArrayList<InterestResponse>();

        for(Interest i: userInterest)
        {
            InterestResponse ir=new InterestResponse();
            ir.id= i.getId();
            ir.interest_id= i.getInterest_id();
            ir.name= i.getName();
            interestResponse.add(ir);
            Log.e(TAG+" User interest",ir.name+ir.toString());

        }


        //public params
        contagContactResponse.name = pUser.getName();
        contagContactResponse.mobileNumber = pUser.getMobileNumber();
        contagContactResponse.statusUpdate = pUser.getStatus_update();
        contagContactResponse.avatarUrl = pUser.getAvatarUrl();
        contagContactResponse.contag = pUser.getContag();


        contagContactResponse.userInterest.addAll(interestResponse);
        Log.e(TAG+" User interest",contagContactResponse.userInterest.toString());
        //others

        if (publicFields.contains("address"))
            contagContactResponse.address = pUser.getAddress();
        if (publicFields.contains("android_app_link"))
            contagContactResponse.androidAppLink = pUser.getAndroidAppLink();
        if (publicFields.contains("blood_group"))
            contagContactResponse.bloodGroup = pUser.getBloodGroup();
        if (publicFields.contains("company_name"))
            contagContactResponse.companyName = pUser.getCompanyName();
        if (publicFields.contains("created_on"))
            contagContactResponse.createdOn = pUser.getCreatedOn();
        if (publicFields.contains("date_of_birth"))
            contagContactResponse.dateOfBirth = pUser.getDateOfBirth();
        if (publicFields.contains("designation"))
            contagContactResponse.designation = pUser.getDesignation();
        if (publicFields.contains("emergency_contact_number"))
            contagContactResponse.emergencyContactNumber = pUser.getEmergencyContactNumber();
        if (publicFields.contains("gender"))
            contagContactResponse.gender = pUser.getGender();
        if (publicFields.contains("id"))
            contagContactResponse.id = pUser.getId();
        if (publicFields.contains("ios_app_link"))
            contagContactResponse.iosAppLink = pUser.getIosAppLink();
        if (publicFields.contains("is_mobile_verified"))
            contagContactResponse.isMobileVerified = pUser.getIsMobileVerified();
        if (publicFields.contains("landline_number"))
            contagContactResponse.landlineNumber = pUser.getMobileNumber();
        if (publicFields.contains("marital_status"))
            contagContactResponse.maritalStatus = pUser.getMaritalStatus();
        if (publicFields.contains("marriage_anniversary"))
            contagContactResponse.marriageAnniversary = pUser.getMarriageAnniversary();
        if (publicFields.contains("personal_email"))
            contagContactResponse.personalEmail = pUser.getPersonalEmail();
        if (publicFields.contains("registered_with"))
            contagContactResponse.registeredWith = pUser.getRegisteredWith();
        if (publicFields.contains("updated_on"))
            contagContactResponse.updatedOn = pUser.getUpdatedOn();
        if (publicFields.contains("website"))
            contagContactResponse.website = pUser.getWebsite();
        if (publicFields.contains("work_address"))
            contagContactResponse.workAddress = pUser.getWorkAddress();
        if (publicFields.contains("work_email"))
            contagContactResponse.workEmail = pUser.getWorkEmail();
        if (publicFields.contains("work_facebook_page"))
            contagContactResponse.workFacebookPage = pUser.getWorkFacebookPage();
        if (publicFields.contains("work_landline_number"))
            contagContactResponse.workLandlineNumber = pUser.getWorkMobileNumber();
        if (publicFields.contains("work_mobile_number"))
            contagContactResponse.workMobileNumber = pUser.getWorkMobileNumber();



        contactResponse.contagContactResponse=contagContactResponse;

        Gson gson = new Gson();
        String userData = gson.toJson(contactResponse);

        Log.e(TAG+" : NFC Data sending",userData);
        return userData;
    }
*/
    private void logout() {
        Log.d("logout", "going to clear pref utils");
        PrefUtils.clearForLogout();
        DaoSession session = ((ContagApplication) getActivity().getApplicationContext()).getDaoSession();
        session.getContactDao().deleteAll();
        session.getContagContagDao().deleteAll();
        session.getCustomShareDao().deleteAll();
        session.getSocialProfileDao().deleteAll();
        session.getInterestDao().deleteAll();
        PrefUtils.clearData();
        getActivity().finish();
        Router.startSplashActivity(getActivity());
        isLoading = true;
    }

    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        if (isAdded()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Profile Sent", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(int value);
    }

    Target headerTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (bitmap == null)
                Log.d(TAG, "bimap null");
            else {
                Log.d(TAG, "bitmap not null");

             /* ByteArrayOutputStream out = new ByteArrayOutputStream();
              bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
              Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));*/
                Bitmap blurredBitmap = ImageUtils.fastblur(bitmap, 3);
                if (blurredBitmap == null)
                    Log.d(TAG, "blurred bitmap null");
                else {
                    Drawable bg = new BitmapDrawable(getActivity().getResources(), blurredBitmap);
                    ivHeader.setImageDrawable(bg);
                }
            }


        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


    private class LoadUser extends AsyncTask<Void, Void, ContagContag> {
        @Override
        protected ContagContag doInBackground(Void... params) {
            return ((BaseActivity) getActivity()).getCurrentUser();
        }

        @Override
        protected void onPostExecute(ContagContag ccUser) {
            mCcUser = ccUser;

            Picasso.with(getActivity()).load(ccUser.getAvatarUrl()).
                    placeholder(R.drawable.img_back).into(headerTarget);
            tvUsrName.setText(ccUser.getName());
            tvUsrCuntId.setText(ccUser.getContag());
        }
    }

    private BroadcastReceiver brNotificationCountUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int notificationCount = intent.getIntExtra(Constants.Keys.KEY_NOTIFICATION_COUNT, 0);
                tvNotificationCount.setText(notificationCount + "");
            }
        }
    };
}
