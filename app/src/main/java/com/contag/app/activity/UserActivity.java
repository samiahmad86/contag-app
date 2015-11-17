package com.contag.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.config.Router;
import com.contag.app.fragment.CurrentUserProfileFragment;
import com.contag.app.fragment.UserProfileFragment;
import com.contag.app.model.ContagContag;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestSuggestion;
import com.contag.app.model.User;
import com.contag.app.request.InterestSuggestionRequest;
import com.contag.app.util.ImageUtils;
import com.contag.app.util.PrefUtils;
import com.google.gson.Gson;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserActivity extends BaseActivity {

    private static final String TAG = UserActivity.class.getName();
    private FlowLayout interestsBoxFlowLayout;
    private ArrayList<Interest> interests;
    private ImageView ivEditIcon;
    private static int[] interestContainer = {R.id.rl_interest_one, R.id.rl_interest_two,
            R.id.rl_interest_three};
    private static int[] interestText = {R.id.tv_user_interest_one, R.id.tv_user_interest_two,
            R.id.tv_user_interest_three};
    private static int[] rmInterest = {R.id.btn_rm_interest_one, R.id.btn_rm_interest_two,
            R.id.btn_rm_interest_three};

    private boolean isEditModeOn = false;
    private long userID ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // adding @UserProfileFragment

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        setUpActionBar(R.id.tb_user);

        View toolbar = findViewById(R.id.tb_user);
        toolbar.setBackgroundResource(R.color.bg_tb_home);

        Intent intent = getIntent();
        userID = intent.getLongExtra(Constants.Keys.KEY_USER_ID, 0);

        if (isUserOnLocal(userID)) {
            Log.d("myuser", "User is in db") ;
            new LoadUser().execute(userID);
        }
        else
            Router.startUserService(this, Constants.Types.REQUEST_GET_USER_BY_ID, userID);

        hideInterest();
        setUpInterests();

        if (userID != PrefUtils.getCurrentUserID()) {
            UserProfileFragment userFragment = UserProfileFragment.newInstance(userID);
            transaction.add(R.id.root_user_fragment, userFragment);
            transaction.commit();
        } else {

            final EditText etUserName = (EditText) findViewById(R.id.et_user_name);
            final EditText etUserStatus = (EditText) findViewById(R.id.et_user_status);
            final TextView tvUserName = (TextView) findViewById(R.id.tv_user_name);
            final TextView tvUserStatus = (TextView) findViewById(R.id.tv_user_status);
            ivEditIcon = (ImageView) findViewById(R.id.iv_edit_profile);
            ivEditIcon.setVisibility(View.VISIBLE);
            ivEditIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEditModeOn) {
                        setupEditableInterests();
                        ivEditIcon.setImageResource(R.drawable.btn_add);
                        etUserName.setText(tvUserName.getText().toString());
                        etUserStatus.setText(tvUserStatus.getText().toString());
                        etUserName.setVisibility(View.VISIBLE);
                        etUserStatus.setVisibility(View.VISIBLE);
                        tvUserName.setVisibility(View.GONE);
                        tvUserStatus.setVisibility(View.GONE);
                        isEditModeOn = true;
                    } else {
                        Log.d(TAG, "sending name");
                        sendNameAndStatus(etUserName.getText().toString(), etUserStatus.getText().toString());
                    }
                }
            });

            boolean isComingFromNotification = intent.getBooleanExtra(Constants.Keys.KEY_COMING_FROM_NOTIFICATION, false);
            CurrentUserProfileFragment currentUserProfileFragment;
            if(isComingFromNotification) {
                currentUserProfileFragment = CurrentUserProfileFragment.newInstance(true, intent.getIntExtra(Constants.Keys.KEY_FRAGMENT_TYPE, 0),
                        intent.getBundleExtra(Constants.Keys.KEY_DATA), intent.getStringExtra(Constants.Keys.KEY_FIELD_NAME));
            } else {
                currentUserProfileFragment = CurrentUserProfileFragment.newInstance();

            } transaction.add(R.id.root_user_fragment, currentUserProfileFragment, CurrentUserProfileFragment.TAG).commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).
                registerReceiver(brGetUser, new IntentFilter("com.contag.app.user.id"));
        LocalBroadcastManager.getInstance(this).
                registerReceiver(brSuggestions, new IntentFilter(getResources().getString(R.string.intent_filter_interest_suggestion)));
        LocalBroadcastManager.getInstance(this).
                registerReceiver(brUserUpdated, new IntentFilter(getResources().getString(R.string.intent_filter_user_received)));
        LocalBroadcastManager.getInstance(this).
                registerReceiver(brInterestUpdated, new IntentFilter(getResources().getString(R.string.intent_filter_interest_updated)));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(brSuggestions);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(brInterestUpdated);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(brUserUpdated);
    }


    private void sendNameAndStatus(String name, String status) {
        try {
            JSONArray aUser = new JSONArray();
            JSONObject oUser = new JSONObject();
            oUser.put(Constants.Keys.KEY_USER_STATUS_UPDATE, status);
            aUser.put(oUser);
            oUser = new JSONObject();
            oUser.put(Constants.Keys.KEY_USER_NAME, name);
            aUser.put(oUser);
            Router.startUserService(this, Constants.Types.REQUEST_PUT, aUser.toString(), Constants.Types.PROFILE_STATUS);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /////////////////////////


    // Show a user's interests
    private void setUpInterests() {
        new AsyncTask<Void, Void, ArrayList<Interest>>() {
            @Override
            protected ArrayList<Interest> doInBackground(Void... params) {
                interests = getUserInterests(PrefUtils.getCurrentUserID());
                return interests;
            }

            @Override
            protected void onPostExecute(ArrayList<Interest> userInterests) {
                showInterests(userInterests);
            }
        }.execute();

    }

    private void hideInterest() {

        for (int rlView : interestContainer) {
            (findViewById(rlView)).setVisibility(View.GONE);
        }
    }

    private void showInterests(ArrayList<Interest> userInterests) {
        int i = 0; //hideInterest();
        for (Interest userInterest : userInterests) {
            if (i >= 3)
                return;
            // Set interest on the text views
            ((TextView) findViewById(interestText[i])).setText(userInterest.getName());
            Log.d("interest", userInterest.getName());
            // make the interest boxes visible
            (findViewById(interestContainer[i])).setVisibility(View.VISIBLE);
            i++;
        }
        Log.d("iList", "Number of interestes: " + i);
    }

    private void setupInterestRemoveButton(ArrayList<Interest> userInterests) {
        int i = 0;

        for (Interest userInterest : userInterests) {
            if (i >= 3)
                return;
            (findViewById(rmInterest[i])).setVisibility(View.VISIBLE);
            (findViewById(rmInterest[i])).setTag(R.id.INTEREST_POSITION, i);
            (findViewById(rmInterest[i])).setTag(R.id.INTEREST_OBJECT, userInterest);
            (findViewById(rmInterest[i])).setOnClickListener(removeInterestListener);
            i++;
        }
    }

    private void setupEditableInterests() {
        new AsyncTask<Void, Void, ArrayList<Interest>>() {
            @Override
            protected ArrayList<Interest> doInBackground(Void... params) {
                return getUserInterests(PrefUtils.getCurrentUserID());
            }

            @Override
            protected void onPostExecute(ArrayList<Interest> userInterests) {
                setupInterestRemoveButton(userInterests);
                if (userInterests.size() < 3)
                    setupNewInterestView();

            }
        }.execute();
    }

    private View.OnClickListener removeInterestListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.id.INTEREST_POSITION);
            Interest interestObject = (Interest) v.getTag(R.id.INTEREST_OBJECT);

            ((TextView) findViewById(interestText[position])).setText("");
            (findViewById(interestContainer[position])).setVisibility(View.GONE);
            try {
                interests.remove(interestObject);
            } catch (Exception e) {
                Log.d("iList", "Exception occured while removing: " + interestObject.getName());
            }
            setupNewInterestView();
            saveInterests();

        }
    };

    private void saveInterests() {
        Log.d("iList", "Going to save the interest list");
        String interestList = "";
        int i = 0;
        for (Interest interest : interests) {
            if (i == 0)
                interestList += String.valueOf(interest.getId());
            else
                interestList += "," + String.valueOf(interest.getId());

            i++;
        }
        Log.d("iList", "Interests List: " + interestList);

        Router.startInterestUpdateService(this, interestList);
    }


    private class Data<T> {
        private T val;

        public void set(T newVal) {
            val = newVal;
        }

        public T get() {
            return val;
        }

        public void clear() {
            val = null;
        }
    }

    private BroadcastReceiver brSuggestions = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra(Constants.Keys.KEY_VIEW_POSITION, -1);
            if (position != -1) {
                String list = intent.getStringExtra(Constants.Keys.KEY_INTEREST_SUGGESTION_LIST);
                Gson gson = new Gson();
                ArrayList<InterestSuggestion> suggestionArrayList = gson.fromJson(list, InterestSuggestion.List.class);
            }
        }
    };


    private BroadcastReceiver brUserUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            findViewById(R.id.tv_user_name).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_user_status).setVisibility(View.VISIBLE);
            findViewById(R.id.et_user_name).setVisibility(View.GONE);
            findViewById(R.id.et_user_status).setVisibility(View.GONE);
            ivEditIcon.setImageResource(R.drawable.edit_pencil_contag);
            new LoadUser().execute(PrefUtils.getCurrentUserID());
        }
    };

    private BroadcastReceiver brInterestUpdated = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            showToast("Interests updated!");
            User.saveUserInterest(((ContagApplication) getApplicationContext()).getDaoSession(), interests);
        }
    };

    BroadcastReceiver brGetUser = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new LoadUser().execute(userID);
        }
    };


    private void setupNewInterestView() {
        (findViewById(R.id.add_new_interest)).setVisibility(View.VISIBLE);
        final EditText interestHint = (EditText) findViewById(R.id.interest_hint);
        final EditText interestText = (EditText) findViewById(R.id.interest_text);
        final ImageView addNewInterestBtn = (ImageView) findViewById(R.id.btn_add);


        final Data<InterestSuggestion> interestSuggestion = new Data<>();

        interestText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("coninterest", "before text: " + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                final String interestTextStr = interestText.getText().toString();

                if (interestTextStr.length() >= 2) {
                    Log.d("coninterest", "Going to search for: " + interestTextStr);
                    getSpiceManager().execute(
                            new InterestSuggestionRequest(interestTextStr),
                            new RequestListener<InterestSuggestion.List>() {
                                @Override
                                public void onRequestSuccess(InterestSuggestion.List suggestions) {

                                    if (suggestions.size() > 0 && interestText.getText().length() > 0) {
                                        InterestSuggestion suggestion = suggestions.get(0);
                                        interestSuggestion.set(suggestion);
                                        Log.d("coninterest", "Current top suggestion: " + suggestions.get(0).name);
                                        interestText.setTag(suggestion);
                                        interestHint.setText(suggestion.name.toLowerCase());

                                    } else {
                                        interestSuggestion.clear();
                                        interestHint.setText("");
                                    }
                                }

                                @Override
                                public void onRequestFailure(SpiceException spiceException) {
                                }
                            }
                    );
                } else {
                    Log.d("coninterest", "no text here");
                    interestHint.setText("");
                }
            }
        });

        addNewInterestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((EditText) findViewById(R.id.interest_hint)).setText("");
                interestHint.setText("");
                interestText.setText("");
                InterestSuggestion suggestion = (InterestSuggestion) interestText.getTag();
                Interest newInterest = new Interest(suggestion.id);
                newInterest.setName(suggestion.name);
                newInterest.setContagUserId(PrefUtils.getCurrentUserID());
                newInterest.setContagContag(getCurrentUser());
                interests.add(newInterest);

                showInterests(interests);
                setupInterestRemoveButton(interests);

                if (interests.size() >= 3) {
                    (findViewById(R.id.add_new_interest)).setVisibility(View.GONE);
                }
                saveInterests();

            }
        });
    }

    //////////////////////////



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CurrentUserProfileFragment lf = (CurrentUserProfileFragment) getSupportFragmentManager().
                findFragmentByTag(CurrentUserProfileFragment.TAG);
        if(lf != null) {
            log(TAG, "fuck bro");
            lf.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class LoadUser extends AsyncTask<Long, Void, ContagContag> {
        @Override
        protected ContagContag doInBackground(Long... params) {
            long id = params[0];
            return UserActivity.this.getUser(id);
        }

        @Override
        protected void onPostExecute(ContagContag ccUser) {
            Toolbar tbHome = (Toolbar) UserActivity.this.findViewById(R.id.tb_user);
            ((TextView) tbHome.findViewById(R.id.tv_user_name)).setText(ccUser.getName());
            ((TextView) tbHome.findViewById(R.id.tv_user_contag_id)).setText(ccUser.getContag());
            ((TextView) tbHome.findViewById(R.id.tv_user_status)).setText(ccUser.getStatus_update());
            Picasso.with(UserActivity.this).load(ccUser.getAvatarUrl()).placeholder(R.drawable.default_profile_pic_small).
                    into(((ImageView) tbHome.findViewById(R.id.iv_user_photo)));
            Picasso.with(UserActivity.this).load(ccUser.getAvatarUrl()).placeholder(R.drawable.default_profile_pic_small).
                    into(picaasoTarget);
            isEditModeOn = false;
        }
    }

    private Target picaasoTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Bitmap blurredBitmap = ImageUtils.fastblur(bitmap, 3);
            ((ImageView) UserActivity.this.findViewById(R.id.iv_bg_usr_img)).setImageBitmap(blurredBitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

}
