package com.contag.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.model.ContagContag;
import com.contag.app.util.JsonUtils;

/**
 * Created by Bedprakash on 9/20/2015.
 */
public class PersonalDetailsFragment extends BaseFragment {

    public static PersonalDetailsFragment getInstance(ContagContag contagUser) {
        PersonalDetailsFragment fragment = new PersonalDetailsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Keys.KEY_CONTAG_USER, JsonUtils.jsonify(contagUser));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_personal_details,container,false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
