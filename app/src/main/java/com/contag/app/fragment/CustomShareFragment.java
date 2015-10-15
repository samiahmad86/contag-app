package com.contag.app.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.contag.app.R;
import com.contag.app.config.Constants;

/**
 * Created by varunj on 15/10/15.
 */
public class CustomShareFragment extends DialogFragment {

    public static CustomShareFragment newInstance(int position) {
        CustomShareFragment mCustomShareFragment = new CustomShareFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.Keys.KEY_VIEW_POSITION, position);
        mCustomShareFragment.setArguments(args);
        return mCustomShareFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_share, container, false);
        return view;
    }
}
