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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestSuggestion;
import com.contag.app.util.PrefUtils;
import com.contag.app.view.SlidingTabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tanay on 28/9/15.
 */
public class CurrentUserProfileFragment extends BaseFragment implements View.OnTouchListener, View.OnClickListener {

    public static final String TAG = CurrentUserProfileFragment.class.getName();

    private static final int[] INTEREST_TV_IDS = {R.id.tv_user_interest_1, R.id.tv_user_interest_2,
            R.id.tv_user_interest_3, R.id.tv_user_interest_4};
    private static final int[] INTEREST_ET_IDS = {R.id.actv_user_interest_1, R.id.actv_user_interest_2,
            R.id.actv_user_interest_3, R.id.actv_user_interest_4};

    private TextView[] tvInterests = new TextView[4];
    private AutoCompleteTextView[] actvInterests = new AutoCompleteTextView[4];
    private ArrayAdapter<String> actvAdapter1, actvAdapter2, actvAdapter3;
    private ArrayList<String> actvDataset1, actvDataset2, actvDataset3;
    private HashMap<String, Long> hmNameToID1, hmNameToID2, hmNameToID3;

    private BroadcastReceiver brSuggestions = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log(TAG, "fuck");
            int position = intent.getIntExtra(Constants.Keys.KEY_VIEW_POSITION, -1);
            if (position != -1) {
                String list = intent.getStringExtra(Constants.Keys.KEY_INTEREST_SUGGESTION_LIST);
                Gson gson = new Gson();
                ArrayList<InterestSuggestion> suggestionArrayList = gson.fromJson(list, InterestSuggestion.List.class);
                if (position == 0) {
                    log(TAG, "here");
                    actvDataset1= new ArrayList<>();
                    hmNameToID1.clear();
                    for (InterestSuggestion is : suggestionArrayList) {
                        actvDataset1.add(is.name);
                        hmNameToID1.put(is.name, is.id);
                    }
                    log(TAG, actvDataset1.size() + "");
                    actvAdapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, actvDataset1);
                    actvInterests[0].setAdapter(actvAdapter1);
                    actvAdapter1.notifyDataSetChanged();
                } else if (position == 1) {
                    actvDataset2.clear();
                    hmNameToID2.clear();
                    for (InterestSuggestion is : suggestionArrayList) {
                        actvDataset2.add(is.name);
                        hmNameToID2.put(is.name, is.id);
                    }
                    actvAdapter2.notifyDataSetChanged();
                } else {
                    actvDataset3.clear();
                    hmNameToID3.clear();
                    for (InterestSuggestion is : suggestionArrayList) {
                        actvDataset3.add(is.name);
                        hmNameToID3.put(is.name, is.id);
                    }
                    actvAdapter3.notifyDataSetChanged();
                }
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

        for (int i = 0; i < 4; i++) {
            tvInterests[i] = (TextView) view.findViewById(INTEREST_TV_IDS[i]);
            tvInterests[i].setOnClickListener(this);
            actvInterests[i] = (AutoCompleteTextView) view.findViewById(INTEREST_ET_IDS[i]);
//            actvInterests[i].setOnTouchListener(this);
        }

        actvDataset1 = new ArrayList<>();
        actvDataset2 = new ArrayList<>();
        actvDataset3 = new ArrayList<>();

        hmNameToID1 = new HashMap<>();
        hmNameToID2 = new HashMap<>();
        hmNameToID3 = new HashMap<>();

        actvAdapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, actvDataset1);
        actvAdapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, actvDataset2);
        actvAdapter3 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, actvDataset3);

        actvInterests[0].setThreshold(2);
        actvInterests[1].setThreshold(2);
        actvInterests[2].setThreshold(2);

        actvInterests[0].setAdapter(actvAdapter1);
        actvInterests[1].setAdapter(actvAdapter2);
        actvInterests[2].setAdapter(actvAdapter3);

        actvInterests[0].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getSuggestionList(0, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        actvInterests[1].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getSuggestionList(1, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        actvInterests[2].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getSuggestionList(2, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        SlidingTabLayout stl = (SlidingTabLayout) view.findViewById(R.id.stl_current_user);
        stl.setDistributeEvenly(true);
        stl.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.parseColor("#ffffff");
            }
        });
        stl.setViewPager(pager);

        new LoadInterests().execute();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        log(TAG, "fuck bro");

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
        int id = v.getId();
        switch (id) {
            case R.id.tv_user_interest_1: {
                actvInterests[0].setVisibility(View.VISIBLE);
                v.setVisibility(View.GONE);
                break;
            }
            case R.id.tv_user_interest_2: {
                actvInterests[1].setVisibility(View.VISIBLE);
                v.setVisibility(View.GONE);
                break;
            }
            case R.id.tv_user_interest_3: {
                actvInterests[2].setVisibility(View.VISIBLE);
                v.setVisibility(View.GONE);
                break;
            }
        }
    }

    private void getSuggestionList(int postion, String slug) {
        Router.getInterestSuggestions(getActivity(), slug, postion,
                Constants.Types.SERVICE_GET_INTEREST_SUGGESTIONS);
    }

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

    private class LoadInterests extends AsyncTask<Void, Void, ArrayList<Interest>> {
        @Override
        protected ArrayList<Interest> doInBackground(Void... params) {
            return ((BaseActivity) CurrentUserProfileFragment.this.getActivity()).getUserInterests(PrefUtils.getCurrentUserID());
        }

        @Override
        protected void onPostExecute(ArrayList<Interest> userInterests) {
            int size = userInterests.size();
            if (size == 0) {
                tvInterests[0].setVisibility(View.VISIBLE);
                tvInterests[1].setVisibility(View.VISIBLE);
                tvInterests[2].setVisibility(View.VISIBLE);
                tvInterests[3].setVisibility(View.INVISIBLE);
                return;
            }
            for (int i = 0; i < size; i++) {
                tvInterests[i].setVisibility(View.VISIBLE);
                if (i % 2 == 0) {
                    tvInterests[i + 1].setVisibility(View.INVISIBLE);
                }
                tvInterests[i].setBackgroundResource(R.drawable.bg_white_border_transparent_rect);
                tvInterests[i].setTextColor(getResources().getColor(R.color.white));
                tvInterests[i].setText(userInterests.get(i).getName());
            }
        }
    }


}
