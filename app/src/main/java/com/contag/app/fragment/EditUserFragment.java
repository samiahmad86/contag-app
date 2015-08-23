package com.contag.app.fragment;

import android.os.Bundle;

/**
 * Created by tanay on 20/8/15.
 */
public class EditUserFragment extends BaseFragment {

    public static EditUserFragment newInstance(int code) {
        EditUserFragment euf = new EditUserFragment();
        Bundle bundle = new Bundle();
        euf.setArguments(bundle);
        return euf;
    }
}
