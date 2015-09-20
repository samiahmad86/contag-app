package com.contag.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.model.ContagContag;

/**
 * Created by Bedprakash on 9/19/2015.
 */
public class UserProfileFragment extends BaseFragment {

    ContagContag contact;


    public static UserProfileFragment newInstance(long userId) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.Keys.KEY_CONTAG_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        Bundle args = getArguments();

        return view;
    }

}
