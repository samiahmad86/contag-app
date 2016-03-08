

package com.contag.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.config.Router;
import com.contag.app.fragment.BackPressedDialog;
import com.contag.app.fragment.CurrentUserProfileEditFragment;
import com.contag.app.fragment.CurrentUserProfileFragment;
import com.contag.app.fragment.CurrentUserSocialProfileEditFragment;
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

import org.apache.commons.lang3.StringUtils;
import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = UserActivity.class.getName();
    private FlowLayout interestsBoxFlowLayout;
    private ArrayList<Interest> interests;
    private ArrayList<Long> interestIDS = new ArrayList<>();

    private Button btnEdit;
    private static int[] interestContainer = {R.id.rl_interest_one, R.id.rl_interest_two,
            R.id.rl_interest_three};
    private static int[] interestText = {R.id.tv_user_interest_one, R.id.tv_user_interest_two,
            R.id.tv_user_interest_three};
    private static int[] rmInterest = {R.id.btn_rm_interest_one, R.id.btn_rm_interest_two,
            R.id.btn_rm_interest_three};

    private boolean isEditModeOn = false;
    private long userID;
    private InterestSuggestion currentSuggestion = null;
    private View progressBar;

    private View view_1, view_2, view_3, view_4;
    private static final int PERMISSIONS_REQUEST_WRITE_STORAGE = 100;



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
            Log.d("myuser", "User is in db");
            new LoadUser().execute(userID);
        } else
            Router.startUserService(this, Constants.Types.REQUEST_GET_USER_BY_ID, userID);


        log(TAG, "userid " + userID + " stored userid " + PrefUtils.getCurrentUserID());

        if (userID != PrefUtils.getCurrentUserID()) {
            int profileCategory = intent.getIntExtra(Constants.Keys.KEY_PROFILE_CATEGORY, 0);
            Bundle extra=intent.getBundleExtra(Constants.Keys.KEY_BUNDLE);
            if(extra!=null) {
                String i=extra.getString("profile_category");
             //   Log.e("Router in loop", i);
                profileCategory=Integer.parseInt(i);
            }
          //  Log.e("Router",profileCategory+" : in user activity");
            UserProfileFragment userFragment = UserProfileFragment.newInstance(userID, profileCategory);
            transaction.replace(R.id.root_user_fragment, userFragment);
            transaction.commit();
        } else {
            log(TAG, "maaa chudao");
            final EditText etUserName = (EditText) findViewById(R.id.et_user_name);
            final EditText etUserStatus = (EditText) findViewById(R.id.et_user_status);
            final TextView tvUserName = (TextView) findViewById(R.id.tv_user_name);
            final TextView tvUserStatus = (TextView) findViewById(R.id.tv_user_status);
            final View view_1 = findViewById(R.id.view_1);
            final View view_2 = findViewById(R.id.view_2);
            final View view_3 = findViewById(R.id.view_3);
            final View view_4 = findViewById(R.id.view_4);
            final Button btnAddPic = (Button) findViewById(R.id.btn_add_pic);

            final ImageView imgProfile=(ImageView) findViewById(R.id.iv_user_photo);

            progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);



           /* (findViewById(R.id.add_new_interest)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnEdit.performClick();
                }
            });*/
            /*(findViewById(R.id.fl_interests_box)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnEdit.performClick();
                }
            });*/
            tvUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnEdit.performClick();
                }
            });
            tvUserStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnEdit.performClick();
                }
            });

            /*final Button backButton=(Button) findViewById(R.id.btn_back);
            backButton.setVisibility(View.GONE);
            backButton.setOnClickListener(this);*/
            btnAddPic.setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.iv_user_photo).setOnClickListener(this);
            btnEdit = (Button) findViewById(R.id.btn_edit_profile);
            view_1.setVisibility(View.VISIBLE);
            view_2.setVisibility(View.GONE);
            view_3.setVisibility(View.VISIBLE);
            view_4.setVisibility(View.GONE);
          //
            btnEdit.setVisibility(View.GONE);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEditModeOn) {
                        setupEditableInterests();
                        // ivEditIcon.setImageResource(R.drawable.btn_save);
                        btnEdit.setText("Save");
                     //   btnEdit.setTextColor();
                        etUserName.setText(tvUserName.getText().toString());
                        etUserStatus.setText(tvUserStatus.getText().toString());
                        etUserName.setVisibility(View.VISIBLE);
                        etUserStatus.setVisibility(View.VISIBLE);
                        etUserStatus.setVisibility(View.VISIBLE);
                        tvUserName.setVisibility(View.GONE);
                        view_1.setVisibility(View.GONE);
                        view_2.setVisibility(View.VISIBLE);
                        tvUserStatus.setVisibility(View.GONE);
                        view_3.setVisibility(View.GONE);
                        view_4.setVisibility(View.VISIBLE);
                        isEditModeOn = true;
                    } else {

                        String name = etUserName.getText().toString();
                        if (name.length() > 0) {

                            sendNameAndStatus(name, etUserStatus.getText().toString());


                        } else {
                            showToast("Name cannot be blank!");

                        }


                    }
                }
            });

            log(TAG, "uncle fucker");
            boolean isComingFromNotification = intent.getBooleanExtra(Constants.Keys.KEY_COMING_FROM_NOTIFICATION, false);
            log(TAG, "uncle fuicker");

            CurrentUserProfileFragment currentUserProfileFragment;
            if (isComingFromNotification) {

             //   setEditMode(true);
                currentUserProfileFragment = CurrentUserProfileFragment.newInstance(true, intent.getIntExtra(Constants.Keys.KEY_FRAGMENT_TYPE, 0),
                        intent.getBundleExtra(Constants.Keys.KEY_DATA), intent.getStringExtra(Constants.Keys.KEY_FIELD_NAME));
                log(TAG, "Calling set is edit mode on as true");
                setEditMode(true);
            } else {
                currentUserProfileFragment = CurrentUserProfileFragment.newInstance();
                log(TAG, "oh fuck you");

            }
            transaction.replace(R.id.root_user_fragment, currentUserProfileFragment, CurrentUserProfileFragment.TAG).commit();
        }

        setUpInterests();
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
        if (userID == PrefUtils.getCurrentUserID()) {
            LocalBroadcastManager.getInstance(this).registerReceiver(brProfilePictureChanged,
                    new IntentFilter(getResources().getString(R.string.intent_filter_profile_picture_changed)));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(brSuggestions);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(brInterestUpdated);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(brUserUpdated);
        if (userID == PrefUtils.getCurrentUserID()) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(brProfilePictureChanged);
        }
    }


    @Override
    public void onBackPressed() {

        if(this.isEditModeOn==true)
        {
            BackPressedDialog mPressedDialog = BackPressedDialog.newInstance(true);
            mPressedDialog.show(getSupportFragmentManager(), "fuck you");

        }
        else {
            boolean isEditModeOn = false;
            CurrentUserProfileFragment my = (CurrentUserProfileFragment) getSupportFragmentManager().findFragmentByTag(CurrentUserProfileFragment.TAG);
            if (my != null && my.isVisible()) {
                List<Fragment> fragments = my.getChildFragmentManager().getFragments();
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment instanceof CurrentUserProfileEditFragment) {
                            if (((CurrentUserProfileEditFragment) fragment).isEditModeOn()) {
                                isEditModeOn = true;
                            }

                        }
                        if (fragment instanceof CurrentUserSocialProfileEditFragment) {
                            if (((CurrentUserSocialProfileEditFragment) fragment).isEditModeOn()) {
                                isEditModeOn = true;
                            }

                        }
                    }
                }
            }

            Log.e(TAG, isEditModeOn + "");
            if ((userID != PrefUtils.getCurrentUserID())) {
                if (isTaskRoot()) {
                    finish();
                    Router.startHomeActivity(this, TAG);
                } else {
                    super.onBackPressed();
                }


            } else {
                if (isEditModeOn) {

                    BackPressedDialog mPressedDialog = BackPressedDialog.newInstance();
                    mPressedDialog.show(getSupportFragmentManager(), "fuck you");
                } else {
                    if (isTaskRoot()) {
                        Log.e(TAG, "here_3");
                        finish();
                        Router.startHomeActivity(this, TAG);
                    } else {
                        Log.e(TAG, "here_4");
                        super.onBackPressed();
                    }


                }
            }
        }
    }



    public void saveUserStatus()
    {
        if(this.isEditModeOn) {
            EditText etUserName = (EditText) findViewById(R.id.et_user_name);
            EditText etUserStatus = (EditText) findViewById(R.id.et_user_status);
            sendNameAndStatus(etUserName.getText().toString(), etUserStatus.getText().toString());
            this.isEditModeOn = false;
        }
      // super.onBackPressed();


    }
    public void rejectUserStatus()
    {
        if(this.isEditModeOn) {
            findViewById(R.id.tv_user_name).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_user_status).setVisibility(View.VISIBLE);
            findViewById(R.id.et_user_name).setVisibility(View.GONE);
            findViewById(R.id.et_user_status).setVisibility(View.GONE);
            findViewById(R.id.view_1).setVisibility(View.VISIBLE);
            findViewById(R.id.view_2).setVisibility(View.GONE);
            findViewById(R.id.view_3).setVisibility(View.VISIBLE);
            findViewById(R.id.view_4).setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            btnEdit.setText("Edit");
            setUpInterests();
            hideInterestRemoveButton();
        }
        this.isEditModeOn=false;

      //  super.onBackPressed();
    }

    public void saveUserData() {

        CurrentUserProfileFragment my = (CurrentUserProfileFragment) getSupportFragmentManager().findFragmentByTag(CurrentUserProfileFragment.TAG);
        if (my != null && my.isVisible()) {

            List<Fragment> fragments = my.getChildFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof CurrentUserProfileEditFragment) {
                       // ((CurrentUserProfileEditFragment) fragment).sendData();
                        ((CurrentUserProfileEditFragment) fragment).sendDataOnBack();



                    }
                    if (fragment instanceof CurrentUserSocialProfileEditFragment) {
                        ((CurrentUserSocialProfileEditFragment) fragment).sendDataOnBack();

                    }
                }
            }
        }
    }

    public void rejectUserData() {

        CurrentUserProfileFragment my = (CurrentUserProfileFragment) getSupportFragmentManager().findFragmentByTag(CurrentUserProfileFragment.TAG);
        if (my != null && my.isVisible()) {

            List<Fragment> fragments = my.getChildFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof CurrentUserProfileEditFragment) {
                        ((CurrentUserProfileEditFragment) fragment).setViewContent();

                    }
                    if (fragment instanceof CurrentUserSocialProfileEditFragment) {
                        ((CurrentUserSocialProfileEditFragment) fragment).setViewContent();

                    }
                }
            }
        }
    }


    public void reloadProfile() {
        CurrentUserProfileFragment currentUserProfileFragment = CurrentUserProfileFragment.newInstance();
    }


    public void sendNameAndStatus(String name, String status) {
        try {
            JSONArray aUser = new JSONArray();
            JSONObject oUser = new JSONObject();
            oUser.put(Constants.Keys.KEY_USER_STATUS_UPDATE, status);
            aUser.put(oUser);
            oUser = new JSONObject();
            oUser.put(Constants.Keys.KEY_USER_NAME, name);
            aUser.put(oUser);
            progressBar.setVisibility(View.VISIBLE);
            hideKeyboard(getApplicationContext(), this.getCurrentFocus());
            Router.startUserService(this, Constants.Types.REQUEST_PUT, aUser.toString(), Constants.Types.PROFILE_STATUS);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onSaveInstanceState(final Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
    }

    // Show a user's interests
    private void setUpInterests() {
        new AsyncTask<Void, Void, ArrayList<Interest>>() {
            @Override
            protected ArrayList<Interest> doInBackground(Void... params) {

                interests = getUserInterests(userID);
                Log.d("iList", "Size of interests in setUpinterests: " + interests.size());
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

    private void hideInterestRemoveButton() {

        int i = 0;
        do {
            (findViewById(rmInterest[i])).setVisibility(View.GONE);
            i++;
        } while (i < 3);
    }


    private void showInterests(ArrayList<Interest> userInterests) {
        int i = 0;
        interestIDS.clear();
        hideInterest();

        if((userInterests.size()<3)&&(userID ==PrefUtils.getCurrentUserID())) {
           // (findViewById(R.id.add_new_interest)).setVisibility(View.VISIBLE);
            setupNewInterestView();
        }
        for (Interest userInterest : userInterests) {
            if (i >= 3)
                return;

            // Set interest on the text views
            ((TextView) findViewById(interestText[i])).setText(userInterest.getName());
            Log.d("iList", "Showing interest with name" + userInterest.getName());
            Log.d("iList", "Showing interest with interest id: " + userInterest.getInterest_id());

            // make the interest boxes visible
            (findViewById(interestContainer[i])).setVisibility(View.VISIBLE);
            interestIDS.add(userInterest.getInterest_id());
            i++;
        }
        Log.d("iList", "Showed interests with interestids being: " + StringUtils.join(interestIDS, ","));
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

        interestIDS.clear();
        for (Interest interest : interests) {

            interestIDS.add(interest.getInterest_id());
        }
        String interestList = StringUtils.join(interestIDS, ",");
        Log.d("iList", "The request string has this: " + interestList);

        Router.startInterestUpdateService(this, interestList);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_user_photo: {
                Intent intentUploadImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentUploadImage, Constants.Values.REQUEST_CODE_IMAGE_UPLOAD);
                break;
            }
            case R.id.btn_back: {
                this.finish();
                break;
            }
        }
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
            findViewById(R.id.view_1).setVisibility(View.VISIBLE);
            findViewById(R.id.view_2).setVisibility(View.GONE);
            findViewById(R.id.view_3).setVisibility(View.VISIBLE);
            findViewById(R.id.view_4).setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

            //  ivEditIcon.setImageResource(R.drawable.edit_contag_profile);
            btnEdit.setText("Edit");
          //  btnEdit.setTextColor(Color.WHITE);

                    (findViewById(R.id.add_new_interest)).setVisibility(View.GONE);
            setUpInterests();
            hideInterestRemoveButton();
            new LoadUser().execute(PrefUtils.getCurrentUserID());
        }
    };

    private BroadcastReceiver brInterestUpdated = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            User.storeInterests(((ContagApplication) getApplicationContext()).getDaoSession(), interests);
            ((EditText) findViewById(R.id.interest_hint)).setText("");
            ((EditText) findViewById(R.id.interest_text)).setText("");
            showToast("Interests updated!");
        }
    };

    private BroadcastReceiver brGetUser = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new LoadUser().execute(userID);
        }
    };


    private BroadcastReceiver brProfilePictureChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String avatarUrl = intent.getStringExtra(Constants.Keys.KEY_USER_AVATAR_URL);
            Toolbar tbHome = (Toolbar) UserActivity.this.findViewById(R.id.tb_user);

            Picasso.with(UserActivity.this).load(avatarUrl).placeholder(R.drawable.default_profile_pic_small).
                    into(((ImageView) tbHome.findViewById(R.id.iv_user_photo)));
            Picasso.with(UserActivity.this).load(avatarUrl).placeholder(R.drawable.default_profile_pic_small).
                    into(picaasoTarget);
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

                                    if (suggestions.size() > 0 && interestText.getText().length() > 0 &&
                                            !interestIDS.contains(suggestions.get(0).interest_id)) {
                                        InterestSuggestion suggestion = suggestions.get(0);
                                        interestSuggestion.set(suggestion);
                                        Log.d("coninterest", "Current top suggestion: " + suggestions.get(0).name);
                                        currentSuggestion = suggestion;
                                        interestHint.setText(suggestion.name.toLowerCase());

                                    } else {
                                        currentSuggestion = null;
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

                if (currentSuggestion != null) {

                    Interest newInterest = new Interest();
                    newInterest.setInterest_id(currentSuggestion.interest_id);
                    newInterest.setName(currentSuggestion.name);
                    newInterest.setContagUserId(PrefUtils.getCurrentUserID());
                    newInterest.setContagContag(getCurrentUser());
                    interests.add(newInterest);
                    Log.d("iList", "New interest object created:" + newInterest.getInterest_id());


                    showInterests(interests);
                    setupInterestRemoveButton(interests);

                    if (interests.size() >= 3) {
                        (findViewById(R.id.add_new_interest)).setVisibility(View.GONE);
                    }
                    saveInterests();

                    interestHint.setText("");
                    currentSuggestion = null;
                    interestSuggestion.clear();

                } else {
                    showToast("Please enter a valid interest!");
                }

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Constants.Values.REQUEST_CODE_IMAGE_UPLOAD && resultCode == Activity.RESULT_OK) {
            String compressedImagePath = ImageUtils.getCompressedImagePath(data.getData(), this);
            if (compressedImagePath != null) {
                Router.startProfilePicutreUpload(this, compressedImagePath);
            }
        } else {
            CurrentUserProfileFragment lf = (CurrentUserProfileFragment) getSupportFragmentManager().
                    findFragmentByTag(CurrentUserProfileFragment.TAG);
            if (lf != null) {
                log(TAG, "fuck bro");
                lf.onActivityResult(requestCode, resultCode, data);
            }
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
            if (ccUser != null) {
                Toolbar tbHome = (Toolbar) UserActivity.this.findViewById(R.id.tb_user);
                ((TextView) tbHome.findViewById(R.id.tv_user_name)).setText(ccUser.getName());
                ((TextView) tbHome.findViewById(R.id.tv_user_contag_id)).setText(ccUser.getContag().toLowerCase());
                tbHome.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
//                progressBar.setVisibility(View.GONE);
                tbHome.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        onBackPressed();
                        //What to do on back clicked
                        // finish();
                      /*  Log.e(TAG,"backpressed");
                        if (userID != PrefUtils.getCurrentUserID()) {
                            if (isTaskRoot()) {
                                finish();
                                Router.startHomeActivity(UserActivity.this, TAG);
                            } else
                                onBackPressed();


                        } else {
                            if (isEditModeOn()) {
                                BackPressedDialog mPressedDialog = BackPressedDialog.newInstance();
                                mPressedDialog.show(getSupportFragmentManager(), "fuck you");
                            } else {
                                if (isTaskRoot()) {
                                    finish();
                                    Router.startHomeActivity(UserActivity.this, TAG);
                                } else
                                    onBackPressed();

                            }

                        }*/
                    }
                });


                ((TextView) tbHome.findViewById(R.id.tv_user_status)).setText(ccUser.getStatus_update());
              /*  Button backButton=(Button) tbHome.findViewById(R.id.btn_back);
                backButton.setVisibility(View.VISIBLE);
                backButton.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                             public void onClick(View v)

                                                 {
                                                    finish();
                                                 }
                });*/

                Picasso.with(UserActivity.this).load(ccUser.getAvatarUrl()).placeholder(R.drawable.default_profile_pic_small).
                        fit()
                        .centerCrop()
                        .into(((ImageView) tbHome.findViewById(R.id.iv_user_photo)));
                Picasso.with(UserActivity.this).load(ccUser.getAvatarUrl()).placeholder(R.drawable.default_profile_pic_small).
                        into(picaasoTarget);
                isEditModeOn = false;
            }

        }
    }

    private Target picaasoTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

          /*  ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));*/
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
    public boolean checkWritePermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_STORAGE);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            return false;
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted


            } else {
                Toast.makeText(UserActivity.this, "Until you grant the permission, we cannot access your profile", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
