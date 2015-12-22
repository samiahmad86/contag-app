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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.contag.app.model.ContagContag;
import com.contag.app.model.DaoSession;
import com.contag.app.util.ImageUtils;
import com.contag.app.util.PrefUtils;
import com.contag.app.util.ShareUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NavDrawerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NavDrawerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavDrawerFragment extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    private ImageView ivHeader;
    private TextView tvUsrName, tvUsrCuntId, notificationTxt, feedbackTxt, rateTxt, logoutTxt;
    private Boolean isLoading ;
    private TextView tvNotificationCount ;
    private RadioGroup radioSexGroup;
    private RadioButton radioButton;
    private TextView share_contag;
    public static final String TAG = NavDrawerFragment.class.getName();

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

        notificationTxt = (TextView) view.findViewById(R.id.tv_notification_txt) ;
        tvNotificationCount = (TextView) view.findViewById(R.id.tv_notification_count) ;
        feedbackTxt = (TextView) view.findViewById(R.id.tv_feedback_txt) ;
        rateTxt = (TextView) view.findViewById(R.id.tv_rate_txt) ;
        logoutTxt = (TextView) view.findViewById(R.id.tv_logout_txt) ;
        tvUsrCuntId = (TextView) view.findViewById(R.id.tv_usr_cunt_id);
        tvUsrName = (TextView) view.findViewById(R.id.tv_usr_name);
        radioSexGroup=(RadioGroup)view.findViewById(R.id.radioSex);
        share_contag=(TextView)view.findViewById(R.id.share_contag);

        tvUsrName.setOnClickListener(this);
        tvUsrCuntId.setOnClickListener(this);

        share_contag.setOnClickListener(this);
        notificationTxt.setOnClickListener(this);
        feedbackTxt.setOnClickListener(this);
        rateTxt.setOnClickListener(this);
        logoutTxt.setOnClickListener(this);
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
    public void onResume(){
        super.onResume();
        Log.d("GCM", "Notification count: " + PrefUtils.getNewNotificationCount()) ;
        new LoadUser().execute();
        tvNotificationCount.setText(String.valueOf(PrefUtils.getNewNotificationCount())) ;
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
                break ;
            }
            case R.id.tv_feedback_txt: {
                Router.startGmailApp(getActivity());
                break ;
            }
            case R.id.tv_rate_txt: {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.contag.app")));
                break ;
            }
            case R.id.tv_usr_name: {
                Router.startUserActivity(getActivity(), TAG, PrefUtils.getCurrentUserID());

                break ;
            }
            case R.id.tv_usr_cunt_id: {
                Router.startUserActivity(getActivity(), TAG, PrefUtils.getCurrentUserID());
                break ;
            }
            case R.id.tv_logout_txt: {
                Log.d("logout", "Logout called") ;
                logout() ;
                break ;
            }
            case R.id.share_contag: {
                int selectedId = radioSexGroup.getCheckedRadioButtonId();
                String category="";
                if(selectedId==R.id.radio_personal)
                    category=" Personal";
                if(selectedId==R.id.radio_social)
                    category=" Social";
                if(selectedId==R.id.radio_professional)
                    category=" Professional";


                ShareUtils.shareText(getActivity(),tvUsrName.getText()+ " has shared his"+ category +" information with you on Contag. \n Download link:https://play.google.com/store/apps/details?id=com.contag.app" );



            }

        }
    }

    private void logout(){
        Log.d("logout", "going to clear pref utils");
        PrefUtils.clearForLogout();
        DaoSession session = ((ContagApplication) getActivity().getApplicationContext()).getDaoSession();
        session.getContactDao().deleteAll();
        session.getContagContagDao().deleteAll();
        session.getCustomShareDao().deleteAll();
        session.getSocialProfileDao().deleteAll();
        session.getInterestDao().deleteAll();
        PrefUtils.clearData();
        Router.startLoginActivity(getActivity(), "NavDrawer", Constants.Types.FRAG_LOGIN);
        isLoading = true ;
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
            Bitmap blurredBitmap = ImageUtils.fastblur(bitmap, 3);
            Drawable bg = new BitmapDrawable(getActivity().getResources(), blurredBitmap);
            ivHeader.setImageDrawable(bg);

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

            Picasso.with(getActivity()).load(ccUser.getAvatarUrl()).
                    placeholder(R.drawable.default_profile_pic_small).into(headerTarget);
            tvUsrName.setText(ccUser.getName());
            tvUsrCuntId.setText(ccUser.getContag());
        }
    }

    private BroadcastReceiver brNotificationCountUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null) {
                int notificationCount = intent.getIntExtra(Constants.Keys.KEY_NOTIFICATION_COUNT, 0);
                tvNotificationCount.setText(notificationCount + "");
            }
        }
    };
}
