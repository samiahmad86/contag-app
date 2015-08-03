package com.contag.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.contag.app.R;
import com.contag.app.util.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/**
 * Created by tanay on 30/7/15.
 */
public class LoginFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = LoginFragment.class.getName();
    private SignInButton btnGooglePlus;

    //////////// static public methods ///////////////////////////////

    /*
        Always use this method to create fragment object.
        Never create object directly
     */
    public static LoginFragment newInstance() {
        LoginFragment lf = new LoginFragment();
        Bundle bundle = new Bundle();
        lf.setArguments(bundle);
        return lf;
    }


    ////////////// Overridden methods /////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        buildGoogleApiClient();
        btnGooglePlus = (SignInButton) view.findViewById(R.id.btn_plus_sign_in);
        btnGooglePlus.setOnClickListener(this);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == Constants.Login.RC_GPLUS_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                mGoogleApiClient.connect();
            }
        }
    }


    ////////////////// ConnectionCallbacks methods //////////////////////////////////////
    @Override
    public void onConnected(Bundle bundle) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if (currentPerson != null) {
                Log.d(TAG, "login success for " + currentPerson.getName().getGivenName());
            } else {
                Log.d(TAG, "login failure");
            }
        } else {
            Log.d(TAG, "API client not connected");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /////////////// on click listener method ////////////////////////////////////////
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_plus_sign_in: {
                Log.d(TAG, "g plus login button");
                mGoogleApiClient.connect();
                break;
            }
        }
    }

    /////////////////////////////// Connection failure methods ////////////////////////////
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "connection failed because " + connectionResult);
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), Constants.Login.RC_GPLUS_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                mGoogleApiClient.connect();
            }
        }
    }


    ////////// private methods ////////////////////////////////////////
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .build();
    }
}
