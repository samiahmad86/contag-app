package com.contag.app.activity;

import android.support.v7.app.AppCompatActivity;

import com.contag.app.service.APIService;
import com.octo.android.robospice.SpiceManager;

/**
 * Created by tanay on 30/7/15.
 */
public class BaseActivity extends AppCompatActivity {
private SpiceManager mSpiceManager=new SpiceManager(APIService.class);

    @Override
    protected void onStart() {
        mSpiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        mSpiceManager.shouldStop();
        super.onStop();
    }

    public SpiceManager getmSpiceManager() {
        return mSpiceManager;
    }
}
