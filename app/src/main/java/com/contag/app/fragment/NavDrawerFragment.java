package com.contag.app.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.config.Constants;
import com.contag.app.model.Contact;
import com.contag.app.model.ContagContag;
import com.contag.app.util.ImageUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

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
    private View llBlockedUsr1, llBlockedUsr2, llBlockedUsr3;
    private View llMutedUsr1, llMutedUsr2, llMutedUsr3;
    private TextView tvUsrName, tvUsrCuntId;
    private Button btnSeeMoreBlockedUsers, btnSeeMoreMutedUsers;

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
        llBlockedUsr1 = view.findViewById(R.id.ll_blocked_usr_1);
        llBlockedUsr2 = view.findViewById(R.id.ll_blocked_usr_2);
        llBlockedUsr3 = view.findViewById(R.id.ll_blocked_usr_3);
        llMutedUsr1 = view.findViewById(R.id.ll_muted_usr_1);
        llMutedUsr2 = view.findViewById(R.id.ll_muted_usr_2);
        llMutedUsr3 = view.findViewById(R.id.ll_muted_usr_3);
        tvUsrCuntId = (TextView) view.findViewById(R.id.tv_usr_cunt_id);
        tvUsrName = (TextView) view.findViewById(R.id.tv_usr_name);
        btnSeeMoreBlockedUsers = (Button) view.findViewById(R.id.btn_see_blocked_usr);
        btnSeeMoreMutedUsers = (Button) view.findViewById(R.id.btn_see_muted_usr);
        new LoadUser().execute();
        new BlockedList().execute(Constants.Types.LIST_BLOCKED_USERS);
        new BlockedList().execute(Constants.Types.LIST_MUTED_USERS);
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
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_unblock: {

            }
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
            Bitmap blurredBitmap = ImageUtils.fastblur(bitmap, 3);
//            ivHeader.setImageBitmap(blurredBitmap);
            Drawable bg = new BitmapDrawable(getActivity().getResources(), blurredBitmap);
            ivHeader.setBackgroundDrawable(bg);
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
                    placeholder(R.drawable.default_profile_pic_small).into(ivHeader);
            tvUsrName.setText(ccUser.getName());
            tvUsrCuntId.setText(ccUser.getContag());
        }
    }

    private class BlockedList extends AsyncTask<Integer, Void, ArrayList<Contact>> {
        private int listType;

        @Override
        protected ArrayList<Contact> doInBackground(Integer... params) {
            if (params.length > 0) {
                listType = params[0];
                if (listType == Constants.Types.LIST_BLOCKED_USERS) {
                    return ((BaseActivity) getActivity()).getBlockedUsers();
                } else {
                    return ((BaseActivity) getActivity()).getMutedUsers();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Contact> contacts) {
            View view1, view2, view3;
            if (contacts != null) {
                if (listType == Constants.Types.LIST_BLOCKED_USERS) {
                    view1 = llBlockedUsr1;
                    view2 = llBlockedUsr2;
                    view3 = llBlockedUsr3;
                    btnSeeMoreBlockedUsers.setVisibility(View.GONE);
                } else {
                    view1 = llMutedUsr1;
                    view2 = llMutedUsr2;
                    view3 = llMutedUsr3;
                    btnSeeMoreMutedUsers.setVisibility(View.GONE);
                }
                int listSize = contacts.size();
                if (listSize >= 3) {
                    if (listSize != 3) {
                        if (listType == Constants.Types.LIST_BLOCKED_USERS) {
                            btnSeeMoreBlockedUsers.setVisibility(View.VISIBLE);
                        } else {
                            btnSeeMoreMutedUsers.setVisibility(View.VISIBLE);
                        }
                    }
                    ((TextView) view1.findViewById(R.id.tv_blocked_name)).setText(contacts.get(0).getContactName());
                    view1.findViewById(R.id.btn_unblock).setTag(contacts.get(0).getId());
                    view1.findViewById(R.id.btn_unblock).setOnClickListener(NavDrawerFragment.this);
                    ((TextView) view2.findViewById(R.id.tv_blocked_name)).setText(contacts.get(1).getContactName());
                    view2.findViewById(R.id.btn_unblock).setTag(contacts.get(1).getId());
                    view2.findViewById(R.id.btn_unblock).setOnClickListener(NavDrawerFragment.this);
                    ((TextView) view3.findViewById(R.id.tv_blocked_name)).setText(contacts.get(2).getContactName());
                    view3.findViewById(R.id.btn_unblock).setTag(contacts.get(2).getId());
                    view3.findViewById(R.id.btn_unblock).setOnClickListener(NavDrawerFragment.this);
                } else if (listSize == 2) {
                    ((TextView) view1.findViewById(R.id.tv_blocked_name)).setText(contacts.get(0).getContactName());
                    view1.findViewById(R.id.btn_unblock).setTag(contacts.get(0).getId());
                    view1.findViewById(R.id.btn_unblock).setOnClickListener(NavDrawerFragment.this);
                    ((TextView) view2.findViewById(R.id.tv_blocked_name)).setText(contacts.get(1).getContactName());
                    view2.findViewById(R.id.btn_unblock).setTag(contacts.get(1).getId());
                    view2.findViewById(R.id.btn_unblock).setOnClickListener(NavDrawerFragment.this);
                    view3.setVisibility(View.GONE);
                } else if (listSize == 1) {
                    ((TextView) view1.findViewById(R.id.tv_blocked_name)).setText(contacts.get(0).getContactName());
                    view1.findViewById(R.id.btn_unblock).setTag(contacts.get(0).getId());
                    view1.findViewById(R.id.btn_unblock).setOnClickListener(NavDrawerFragment.this);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.GONE);
                } else {
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.GONE);
                }
            }
        }
    }
}
