package com.contag.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.config.Router;
import com.contag.app.model.ContagContag;
import com.contag.app.model.DaoSession;
import com.contag.app.util.ImageUtils;
import com.contag.app.util.PrefUtils;
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

        new LoadUser().execute();

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
        tvNotificationCount.setText(String.valueOf(PrefUtils.getNewNotificationCount())) ;
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
                        Uri.parse("market://details?id=com.android.contag")));
                break ;
            }
            case R.id.tv_logout_txt: {
                Log.d("logout", "Logout called") ;
                    logout() ;
                break ;
            }

        }
    }

    private void logout(){
//
        Log.d("logout", "going to clear pref utils");
        PrefUtils.clearForLogout();
        DaoSession session = ((ContagApplication) getActivity().getApplicationContext()).getDaoSession();
        session.clear();
        Router.startLoginActivity(getActivity(), "NavDrawer", Constants.Types.FRAG_LOGIN);
        isLoading = true ;
    }
//
//    RequestListener<MessageResponse> nr = new RequestListener<>({
//
//
//        @Override
//        public void onRequestFailure (SpiceException spiceException){
//
//    }
//
//        @Override
//        public void onRequestSuccess (MessageResponse messageResponse){
//        Log.d("NavDrawer", messageResponse.message);
//        isLoading = false;
//    }
//    })  ;


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

}
