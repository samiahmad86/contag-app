package com.contag.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tanay on 23/9/15.
 */
public class FeedsFragment extends BaseFragment {

    public static FeedsFragment newInstance() {
        FeedsFragment ff = new FeedsFragment();
        Bundle args = new Bundle();
        ff.setArguments(args);
        return ff;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return null;
    }

}
