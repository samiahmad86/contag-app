package com.contag.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.adapter.ShareListAdapter;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.config.Router;
import com.contag.app.model.ContactListItem;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.CustomShare;
import com.contag.app.model.CustomShareDao;
import com.contag.app.model.DaoSession;
import com.contag.app.model.Response;
import com.contag.app.util.ContactUtils;
import com.contag.app.util.PrefUtils;
import com.contag.app.util.ShareUtils;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShareDialog extends DialogFragment implements View.OnClickListener,RequestListener<Response>,TextWatcher, TextView.OnEditorActionListener {
    private CustomShare mCustomShare;
    private ListView lvContags;
    private ArrayList<ContactListItem> shareList = new ArrayList<>();
    private ShareListAdapter shareListAdapter;
  /*  private Button sharePublic;
    private Button shareCustom;*/
    private int shareCount = 0;
    private TextView shareText;
    private TextView shareTextIntent;
    private String fieldName;
    private LinearLayout ll_share;
    private static String contact_for_share;
    private RadioGroup radioSexGroup;
    private RadioButton radioButtonPublic,radioButtonCustom;
    private EditText etSearchBox;
    private SearchContacts mSearchContacts;


    public static ShareDialog newInstance(String fieldName, String value) {

        ShareDialog share = new ShareDialog();
        Bundle args = new Bundle();

        contact_for_share = getLabel(fieldName) + " : " + value;
      Log.e("share Dialog", fieldName);
     Log.e("Share Dialog", value);

        args.putString(Constants.Keys.KEY_FIELD_NAME, fieldName);
        share.setArguments(args);
        return share;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_share_field_1, container, false);
        fieldName = getArguments().getString(Constants.Keys.KEY_FIELD_NAME);
        shareListAdapter = new ShareListAdapter(shareList, getActivity());
        new LoadContags().execute(fieldName);

        lvContags = (ListView) view.findViewById(R.id.lv_contag_share);
        lvContags.setVisibility(View.GONE);
        lvContags.setTextFilterEnabled(true);

        ll_share = (LinearLayout) view.findViewById(R.id.ll_share);
        View footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_footer, null, false);
        lvContags.addFooterView(footerView);
        lvContags.setAdapter(shareListAdapter);

        radioSexGroup=(RadioGroup)view.findViewById(R.id.radioSex);
        radioButtonPublic=(RadioButton) view.findViewById(R.id.radio_public);
        radioButtonCustom=(RadioButton) view.findViewById(R.id.radio_custom);
        etSearchBox = (EditText) view.findViewById(R.id.et_contact_search);
        etSearchBox.setOnEditorActionListener(this);
        etSearchBox.addTextChangedListener(this);
        etSearchBox.setVisibility(View.GONE);


       /* sharePublic = (Button) view.findViewById(R.id.btn_share_public);
        shareCustom = (Button) view.findViewById(R.id.btn_share_custom);*/
        shareText = (TextView) view.findViewById(R.id.tv_share_text);
        shareTextIntent = (TextView) view.findViewById(R.id.tv_share_intent);


        shareText.setText("Share your " + getLabel(fieldName) + "");
        Button shareDone = (Button) view.findViewById(R.id.btn_share_done);
        shareTextIntent.setOnClickListener(this);
       /* sharePublic.setOnClickListener(this);
        shareCustom.setOnClickListener(this);*/
        shareDone.setOnClickListener(this);



        radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.radio_public){
                    Log.e("radio","public");
                   // mCustomShare.setIs_public(!mCustomShare.getIs_public());
                    mCustomShare.setIs_public(true);
                    setPublicButton();
                }
                if(checkedId==R.id.radio_custom) {
                    setContagList();
                    mCustomShare.setIs_public(false);
                    Log.e("radio","custom");
                }


            }
        });

        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        shareListAdapter.filter(s.toString().toLowerCase());
        Log.e("search1",s.toString().toLowerCase());
        /*if (s.toString().length() != 0) {
          //  doSearch(s.toString());
            Log.e("search1",s.toString());


        } else {
           // new LoadContags().execute(fieldName);
        }*/
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH && v.getId() == R.id.et_contact_search) {
          //  doSearch(v.getText().toString());

            shareListAdapter.filter(v.getText().toString().toLowerCase());
        }
        return false;
    }

    @Override
    public void afterTextChanged(Editable s) {


    }

    @Override
    public void onResume()
    {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(shareCountUpdated,
                new IntentFilter("com.contag.app.profile.sharecount"));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(shareCountUpdated);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_share_public: {

                 mCustomShare.setIs_public(!mCustomShare.getIs_public());
                setPublicButton();
                break;
            }
            case R.id.btn_share_custom: {
                setContagList();
                break;
            }
            case R.id.btn_share_done: {
                savePrivacySettings();
                getDialog().dismiss();
                break;

            }
            case R.id.tv_share_intent: {
                ShareUtils.shareText(getActivity(), contact_for_share+"\n\nShared via Contag:\nhttp://bit.ly/1HHI6do");
                getDialog().dismiss();
                break;

            }
        }
    }

    private void savePrivacySettings() {

        mCustomShare.setUser_ids(getSharesAsString());

//        Log.d("ShareFubar", "Is Public: " + mCustomShare.getIs_public());
//        Log.d("ShareFubar", "Share Count: " + shareCount);
//        Log.d("ShareFubar", "Share user id string: " + mCustomShare.getUser_ids());


        Router.startUserServiceForPrivacy(getActivity(), mCustomShare.getField_name(), mCustomShare.getIs_public(),
                mCustomShare.getUser_ids());


    }

    private String getSharesAsString() {
        ArrayList<String> userIDS = new ArrayList<>();
        for (ContactListItem item : shareList) {
            if (item.isSharedWith)
                userIDS.add(String.valueOf(item.mContagContag.getId()));
        }
        return TextUtils.join(",", userIDS);
    }


    private BroadcastReceiver shareCountUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            shareCount = intent.getIntExtra("shareCount", 0);
            setCustomShareCount();
        }
    };

    private void setPublicButton() {


        if (mCustomShare.getIs_public() || lvContags.getVisibility() == View.VISIBLE) {
            //Hide contag list
            lvContags.setVisibility(View.GONE);
            etSearchBox.setVisibility(View.GONE);
            // Toggle Color
          /*  sharePublic.setTextColor(getResources().getColor(R.color.light_blue));
            shareCustom.setTextColor(getResources().getColor(R.color.black));*/
            radioButtonPublic.setTextColor(getResources().getColor(R.color.light_blue));
            radioButtonCustom.setTextColor(getResources().getColor(R.color.black));

        } else
            radioButtonPublic.setTextColor(getResources().getColor(R.color.black));
           /* sharePublic.setTextColor(getResources().getColor(R.color.black));

*/
    }

    private void setContagList() {

        if (lvContags.getVisibility() == View.VISIBLE) {
            // Remove fieldLabel list
            lvContags.setVisibility(View.GONE);
            etSearchBox.setVisibility(View.GONE);
            //ll_share.setVisibility(View.GONE);
            // Set color to unselected
            radioButtonCustom.setTextColor(getResources().getColor(R.color.black));
           // shareCustom.setTextColor(getResources().getColor(R.color.black));
        } else {
            // Set is_public as false when showing list
            mCustomShare.setIs_public(false);

            //Show the list
            etSearchBox.setVisibility(View.VISIBLE);
            lvContags.setVisibility(View.VISIBLE);
            //  lvContags.addFooterView(lvContags);
            //ll_share.setVisibility(View.VISIBLE);
            // Toggle color of buttons
            radioButtonCustom.setTextColor(getResources().getColor(R.color.light_blue));
            radioButtonPublic.setTextColor(getResources().getColor(R.color.black));
           /* shareCustom.setTextColor(getResources().getColor(R.color.light_blue));
            sharePublic.setTextColor(getResources().getColor(R.color.black));
                        */

        }
    }

    private void setCustomShareCount() {
        radioButtonCustom.setText("Custom(" + shareCount + ")");
      //  shareCustom.setText("Custom(" + shareCount + ")");
        if(mCustomShare.getIs_public()){
            radioButtonPublic.setTextColor(getResources().getColor(R.color.light_blue));
            radioButtonPublic.setChecked(true);

          //  sharePublic.setTextColor(getResources().getColor(R.color.light_blue));
        } //else if(shareCount > 0) {
        else{
            radioButtonCustom.setTextColor(getResources().getColor(R.color.light_blue));
            radioButtonCustom.setChecked(true);
        }
        // shareCustom.setTextColor(getResources().getColor(R.color.light_blue));

    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {

    }

    @Override
    public void onRequestSuccess(Response response) {

    }


    private class LoadContags extends AsyncTask<String, Void, ArrayList<ContactListItem>> {

        @Override
        protected void onPreExecute() {
        }


        @Override
        protected ArrayList<ContactListItem> doInBackground(String... params) {
            String fieldName = params[0];
            DaoSession session = ((ContagApplication) ShareDialog.this.getActivity().
                    getApplicationContext()).getDaoSession();

            ArrayList<ContactListItem> items = new ArrayList<>();

            ContagContagDao mContagContagDao = session.getContagContagDao();
            List<ContagContag> contagContacts = mContagContagDao.queryBuilder().
                    where(ContagContagDao.Properties.Id.notEq(PrefUtils.getCurrentUserID()),
                            ContagContagDao.Properties.Is_contact.eq(true)).list();

            CustomShareDao mCustomDao = session.getCustomShareDao();
            Log.d("ShareFubar", "Trying to open this up for: " + fieldName);
            try {
                mCustomShare = mCustomDao.queryBuilder().where(
                        CustomShareDao.Properties.Field_name.eq(fieldName)
                ).list().get(0);
            } catch (Exception e) {
                Log.d("ShareFubar", "Did not find the platform: " + fieldName);
            }
            String[] sharedWith;
            try {
                sharedWith = mCustomShare.getUser_ids().split(",");

            } catch (Exception e) {
                sharedWith = new String[1];
                sharedWith[0] = "";
                Log.d("ShareFubar", "Exception occurred") ;
            }

            Collections.sort(contagContacts, new Comparator<ContagContag>() {
                @Override
                public int compare(ContagContag lhs, ContagContag rhs) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            });


            for (ContagContag cc : contagContacts) {
                Log.d("ShareFubar", "Status with: " + cc.getName() + " :" + ArrayUtils.contains(sharedWith, cc.getId().toString()));
                if(ArrayUtils.contains(sharedWith, cc.getId().toString()))
                    shareCount++;
                items.add(new ContactListItem(cc, ArrayUtils.contains(sharedWith, cc.getId().toString()),
                        Constants.Types.ITEM_SHARE_CONTAG));
            }

            return items;
        }

        @Override
        protected void onPostExecute(ArrayList<ContactListItem> contactListItems) {

           if(mCustomShare.getIs_private())
            {

              /*  radioButtonCustom.setTextColor(getResources().getColor(R.color.black));
                radioButtonPublic.setTextColor(getResources().getColor(R.color.black));
                radioButtonCustom.setChecked(true);*/




            }

                if (mCustomShare.getIs_public()) {
                    radioButtonPublic.setChecked(true);
                    radioButtonPublic.setTextColor(getResources().getColor(R.color.light_blue));
                    radioButtonCustom.setTextColor(getResources().getColor(R.color.black));

                    Log.e("Debug", "here-1");
                }
         else {
                    if (shareCount > 0) {
                        radioButtonCustom.setTextColor(getResources().getColor(R.color.light_blue));
                        radioButtonPublic.setTextColor(getResources().getColor(R.color.black));
                        radioButtonCustom.setChecked(true);
                        Log.e("Debug", "here-3" + "share count" + shareCount);
                        setCustomShareCount();
                    }
                }

            Log.d("ShareFubar", "Is Public? : " + mCustomShare.getIs_public()) ;
            Log.d("ShareFubar", "Share count is set at: " + shareCount) ;



            shareList.clear();
            shareList.addAll(contactListItems);
            shareListAdapter.notifyDataSetChanged();
            shareListAdapter.setArray(shareList);
            shareListAdapter.setShareCount(shareCount);

        }
    }

    private static String getLabel(String key) {
        return CurrentUserProfileEditFragment.convertKeyToLabel(key);
    }

    private class SearchContacts extends AsyncTask<String, Void, ArrayList<ContactListItem>> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ArrayList<ContactListItem> doInBackground(String... params) {

            ArrayList<ContactListItem> contactListItems = new ArrayList<>();
            if (params.length > 0) {
                DaoSession session = ((ContagApplication) ShareDialog.this.getActivity().
                        getApplicationContext()).getDaoSession();




                String query = params[0];
                if (query.length() != 0) {
                    query = query + "%";
                    ContagContagDao mContagContagDao = session.getContagContagDao();
                    List<ContagContag> contagContacts = mContagContagDao.queryBuilder().
                            where(ContagContagDao.Properties.Id.notEq(PrefUtils.getCurrentUserID())
                                    , ContagContagDao.Properties.Name.like(query),
                                    ContagContagDao.Properties.Is_contact.eq(true)).
                            orderAsc(ContagContagDao.Properties.Name).list();

                    if (contagContacts != null) {
                        for (ContagContag mContagContag : contagContacts) {
                            ContactListItem item = ContactUtils.getContactListItem(getActivity().getApplicationContext(), mContagContag);
                            contactListItems.add(item);
                        }
                    }


                }
            }


            return contactListItems;
        }

        @Override
        protected void onPostExecute(ArrayList<ContactListItem> contactListItems) {
            shareList.clear();
            shareList.addAll(contactListItems);
            shareListAdapter.notifyDataSetChanged();

        }
    }

}
