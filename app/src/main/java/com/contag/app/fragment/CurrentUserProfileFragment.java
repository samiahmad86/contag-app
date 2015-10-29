package com.contag.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.model.FeedsResponse;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestSuggestion;
import com.contag.app.request.InterestSuggestionRequest;
import com.contag.app.util.PrefUtils;
import com.contag.app.view.SlidingTabLayout;
import com.google.gson.Gson;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tanay on 28/9/15.
 */
public class CurrentUserProfileFragment extends BaseFragment implements View.OnTouchListener, View.OnClickListener {

    public static final String TAG = CurrentUserProfileFragment.class.getName();

    private FlowLayout interestsBoxFlowLayout;

//    private class InterestsData {
//        public InterestSuggestion newInterestSuggestion;
//
//        public void setNewInterestSuggestion(InterestSuggestion suggestion) {
//            newInterestSuggestion = suggestion;
//        }
//
//        public void clear() {
//            newInterestSuggestion = null;
//        }
//    };
//
//    private final InterestsData interestsData = new InterestsData();

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

    public static CurrentUserProfileFragment newInstance() {
        CurrentUserProfileFragment cupf = new CurrentUserProfileFragment();
        Bundle args = new Bundle();
        cupf.setArguments(args);
        return cupf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_user_profile, container, false);

        ViewPager pager = (ViewPager) view.findViewById(R.id.root_pager);

        PersonalDetailsTabsAdapter homeTabsAdapter = new
                PersonalDetailsTabsAdapter(getChildFragmentManager());
        pager.setAdapter(homeTabsAdapter);

        SlidingTabLayout stl = (SlidingTabLayout) view.findViewById(R.id.stl_current_user);
        stl.setDistributeEvenly(true);
        stl.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.parseColor("#ffffff");
            }
        });
        stl.setViewPager(pager);

        // interests specific setup
        interestsBoxFlowLayout = (FlowLayout) view.findViewById(R.id.interests_box);
        RelativeLayout newInterestView = (RelativeLayout) view.findViewById(R.id.new_interest);

        setupNewInterestView(newInterestView);
        setupEditableInterests();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof CurrentUserSocialProfileEditFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).
                registerReceiver(brSuggestions, new IntentFilter(getResources().getString(R.string.intent_filter_interest_suggestion)));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brSuggestions);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        EditText edit = (EditText) v;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (edit.getRight() - edit.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                showToast("yes");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    /////////////////////////

    private void setupNewInterestView(RelativeLayout newInterestView) {
        final EditText interestHint = (EditText) newInterestView.findViewById(R.id.interest_hint);
        final EditText interestText = (EditText) newInterestView.findViewById(R.id.interest_text);
        final ImageView addNewInterestBtn = (ImageView) newInterestView.findViewById(R.id.btn_add);

        final Data<InterestSuggestion> interestSuggestion = new Data<>();
        final String hintPlaceholder = "New Interest";

        interestHint.setText(hintPlaceholder);
        interestText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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
                            public void onRequestFailure(SpiceException spiceException) {}
                        }
                );
            }
        });

        addNewInterestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interestSuggestion.get() != null) {
                    // here, send the new interest suggestion to the endpoint
                } else {
                    String newInterestName = interestText.getText().toString();
                    // do things with newInterestName here
                }

                // whichever way, you need to get a new interest object, and then call setupEditableInterestView(interest)
            }
        });
    }

    private void setupEditableInterests() {
        new AsyncTask<Void, Void, ArrayList<Interest>>() {
            @Override
            protected ArrayList<Interest> doInBackground(Void... params) {
                return ((BaseActivity) CurrentUserProfileFragment.this.getActivity()).getUserInterests(PrefUtils.getCurrentUserID());
            }

            @Override
            protected void onPostExecute(ArrayList<Interest> userInterests) {
                for(Interest userInterest : userInterests) {
                    CurrentUserProfileFragment.this.setupEditableInterestView(userInterest);
                }
            }
        }.execute();
    }

    private void setupEditableInterestView(Interest interest) {
        final RelativeLayout editInterestView = (RelativeLayout) View.inflate(getActivity(), R.layout.item_interest_edit, null);

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

    private class PersonalDetailsTabsAdapter extends FragmentPagerAdapter {

        public PersonalDetailsTabsAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case UserProfileFragment.ViewMode.PERSONAL_DETAILS: {
                    fragment = CurrentUserProfileEditFragment.newInstance(Constants.Types.PROFILE_PERSONAL);
                    break;
                }
                case UserProfileFragment.ViewMode.SOCIAL_DETAILS: {
                    fragment = CurrentUserSocialProfileEditFragment.newInstance();
                    break;
                }
                case UserProfileFragment.ViewMode.PROFESSIONAL_DETAILS: {
                    fragment = CurrentUserProfileEditFragment.newInstance(Constants.Types.PROFILE_PROFESSIONAL);
                    break;
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return UserProfileFragment.ViewMode.TABS_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: {
                    return "Personal";
                }
                case 1: {
                    return "Social";
                }
                case 2: {
                    return "Professional";
                }
            }
            return null;
        }

    }


}
