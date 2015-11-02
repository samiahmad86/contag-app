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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.fragment.CurrentUserProfileFragment;
import com.contag.app.fragment.UserProfileFragment;
import com.contag.app.model.ContagContag;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestSuggestion;
import com.contag.app.request.InterestSuggestionRequest;
import com.contag.app.util.ImageUtils;
import com.contag.app.util.PrefUtils;
import com.google.gson.Gson;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

public class UserActivity extends BaseActivity {

    private static final String TAG = UserActivity.class.getName();
    private FlowLayout interestsBoxFlowLayout ;
    private ImageView ivEditIcon ;
    private static int[] interestContainer = {R.id.rl_interest_one, R.id.rl_interest_two,
            R.id.rl_interest_three} ;
    private static int[] interestText = {R.id.tv_user_interest_one, R.id.tv_user_interest_two,
            R.id.tv_user_interest_three} ;
    private static int[] rmInterest = {R.id.btn_rm_interest_one, R.id.btn_rm_interest_two,
            R.id.btn_rm_interest_three}  ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // adding @UserProfileFragment

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        setUpActionBar(R.id.tb_user);

        View toolbar =  findViewById(R.id.tb_user) ;
        toolbar.setBackgroundResource(R.color.bg_tb_home);

        Intent intent = getIntent();
        long userID = intent.getLongExtra(Constants.Keys.KEY_USER_ID, 0);

        new LoadUser().execute(userID);

        setUpInterests();

        if(userID != PrefUtils.getCurrentUserID()) {
            UserProfileFragment userFragment = UserProfileFragment.newInstance(userID);
            transaction.add(R.id.root_user_fragment, userFragment);
            transaction.commit();
        } else {
            // interests specific setup
            //interestsBoxFlowLayout = (FlowLayout) findViewById(R.id.interests_box);
            //RelativeLayout newInterestView = (RelativeLayout) findViewById(R.id.new_interest);

            //setupNewInterestView(newInterestView);
            //setupEditableInterests();
            ivEditIcon = (ImageView) findViewById(R.id.iv_edit_profile) ;
            ivEditIcon.setVisibility(View.VISIBLE);
            ivEditIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupEditableInterests() ;

                }
            });
            CurrentUserProfileFragment cupf = CurrentUserProfileFragment.newInstance();
            transaction.add(R.id.root_user_fragment, cupf, CurrentUserProfileFragment.TAG).commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).
                registerReceiver(brSuggestions, new IntentFilter(getResources().getString(R.string.intent_filter_interest_suggestion)));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(brSuggestions);
    }


    /////////////////////////


    // Show a user's interests
    private void setUpInterests() {
        new AsyncTask<Void, Void, ArrayList<Interest>>() {
            @Override
            protected ArrayList<Interest> doInBackground(Void... params) {
                return getUserInterests(PrefUtils.getCurrentUserID());
            }

            @Override
            protected void onPostExecute(ArrayList<Interest> userInterests) {
                int i = 0 ;
                for(Interest userInterest : userInterests) {
                    // Set interest on the text views
                    ((TextView) findViewById(interestText[i])).setText(userInterest.getName()) ;
                    Log.d("interest", userInterest.getName());
                    // make the interest boxes visible
                    (findViewById(interestContainer[i])).setVisibility(View.VISIBLE);
                    i++ ;
                }

            }
        }.execute();

    }

    private void setupEditableInterests() {
        new AsyncTask<Void, Void, ArrayList<Interest>>() {
            @Override
            protected ArrayList<Interest> doInBackground(Void... params) {
                return getUserInterests(PrefUtils.getCurrentUserID());
            }

            @Override
            protected void onPostExecute(ArrayList<Interest> userInterests) {
                int i = 0 ;

                for(Interest userInterest : userInterests) {
                    (findViewById(rmInterest[i])).setVisibility(View.VISIBLE);
                    (findViewById(rmInterest[i])).setTag(i) ;
                    //(findViewById(rmInterest[i])).setTag(i) ;
                    i++ ;
                }
                if(userInterests.size() < 5){
                    (findViewById(R.id.add_new_interest)).setVisibility(View.VISIBLE);
                    setupNewInterestView();
                }
            }
        }.execute();
    }

    private class Data<T> {
        private T val;
        public void set(T newVal) { val = newVal; }
        public T get() { return val; }
        public void clear() { val = null; }
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

    private void setupNewInterestView() {
        final EditText interestHint = (EditText) findViewById(R.id.interest_hint);
        final EditText interestText = (EditText) findViewById(R.id.interest_text);
        final ImageView addNewInterestBtn = (ImageView) findViewById(R.id.btn_add);

        final Data<InterestSuggestion> interestSuggestion = new Data<>();

        interestText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("interest", "before text: "+  s)  ;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                final String interestTextStr = interestText.getText().toString();


                    getSpiceManager().execute(
                            new InterestSuggestionRequest(interestTextStr),
                            new RequestListener<InterestSuggestion.List>() {
                                @Override
                                public void onRequestSuccess(InterestSuggestion.List suggestions) {

                                    if (suggestions.size() > 0) {
                                        InterestSuggestion suggestion = suggestions.get(0);
                                        interestSuggestion.set(suggestion);
                                        interestText.setTag(R.id.btn_add, suggestion.id) ;
                                        interestHint.setText(suggestion.name);

                                    } else {
                                        interestSuggestion.clear();
                                        interestText.setText("");
                                    }
                                }

                                @Override
                                public void onRequestFailure(SpiceException spiceException) {}
                            }
                    );
            }
        });

        addNewInterestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("ID of interest is: " + interestText.getTag(R.id.btn_add));
//                if (interestSuggestion.get() != null) {
//                    // here, send the new interest suggestion to the endpoint
//
//                } else {
//                    String newInterestName = interestText.getText().toString();
//                    // do things with newInterestName here
//                }

                // whichever way, you need to get a new interest object, and then call setupEditableInterestView(interest)
            }
        });
    }



    private void setupEditableInterestView(Interest interest) {
        final RelativeLayout editInterestView = (RelativeLayout) View.inflate(this, R.layout.item_interest_edit, null);

        final EditText interestHint = (EditText) editInterestView.findViewById(R.id.interest_hint);
        final EditText interestText = (EditText) editInterestView.findViewById(R.id.interest_text);
        final ImageView updateBtn = (ImageView) editInterestView.findViewById(R.id.btn_update);
        final ImageView removeBtn = (ImageView) editInterestView.findViewById(R.id.remove_interest_btn);

        final Data<InterestSuggestion> interestSuggestion = new Data<>();
        final String hintPlaceholder = interest.getName();

        interestHint.setText(hintPlaceholder);
        interestText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                final String interestTextStr = interestText.getText().toString();
                getSpiceManager().execute(
                        new InterestSuggestionRequest(interestTextStr),
                        new RequestListener<InterestSuggestion.List>() {
                            @Override
                            public void onRequestSuccess(InterestSuggestion.List suggestions) {
                                if (suggestions.size() > 0) {
                                    InterestSuggestion suggestion = suggestions.get(0);
                                    interestSuggestion.set(suggestion);
                                    interestHint.setText(suggestion.name);
                                } else {
                                    interestSuggestion.clear();
                                    if (interestTextStr.length() == 0) {
                                        interestHint.setText(hintPlaceholder);
                                    } else {
                                        interestHint.setText("");
                                    }
                                }
                            }

                            @Override
                            public void onRequestFailure(SpiceException spiceException) {
                            }
                        }
                );
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interestSuggestion.get() != null) {
                    // here, send the new interest suggestion to the endpoint
                } else {
                    String newInterestName = interestText.getText().toString();
                    // do things with newInterestName here
                }
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make the api call to detach the interest here
                // and remove the view from interestsBox
            }
        });

        interestsBoxFlowLayout.addView(editInterestView);
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
