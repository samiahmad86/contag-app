package com.contag.app.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.contag.app.R;
import com.contag.app.adapter.ShareListAdapter;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.listener.DatabaseRequestListener;
import com.contag.app.model.Contact;
import com.contag.app.model.ContactDao;
import com.contag.app.model.ContactListItem;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.ContagIntroductionRequestModel;
import com.contag.app.model.DaoSession;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestDao;
import com.contag.app.model.Response;
import com.contag.app.request.ContactRequest;
import com.contag.app.request.IntroductionRequest;
import com.contag.app.service.APIService;
import com.contag.app.tasks.DatabaseOperationTask;
import com.contag.app.util.ContactUtils;
import com.contag.app.util.PrefUtils;
import com.contag.app.util.RegexUtils;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by varunj on 04/11/15.
 */
public class IntroduceContagDialog extends DialogFragment implements View.OnClickListener, RequestListener<Response>,TextWatcher, TextView.OnEditorActionListener {

    private ListView lvContags;
    private SpiceManager mSpiceManager = new SpiceManager(APIService.class);
    private ArrayList<ContactListItem> shareList = new ArrayList<>();
    private ShareListAdapter shareListAdapter;
    private SearchContacts mSearchContacts;
    private EditText inviteMessage;
    private String contagName;
    private long contagID;
    EditText etSearchBox;
    int share;


    public static IntroduceContagDialog newInstance(String contagName, long contagID) {

        IntroduceContagDialog scDialog = new IntroduceContagDialog();
        Bundle args = new Bundle();
        args.putString(Constants.Keys.KEY_CONTACT_NAME, contagName);
        args.putLong(Constants.Keys.KEY_USER_ID, contagID);
        scDialog.setArguments(args);
        return scDialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        mSpiceManager.start(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_share_contag, container, false);


        contagName = getArguments().getString(Constants.Keys.KEY_CONTACT_NAME);
        contagID = getArguments().getLong(Constants.Keys.KEY_USER_ID);
        new LoadContags().execute(contagID);

        shareListAdapter = new ShareListAdapter(shareList, getActivity());
        lvContags = (ListView) view.findViewById(R.id.lv_contag_share);
        lvContags.setAdapter(shareListAdapter);





            //lvContags.setItemsCanFocus(false);
            lvContags.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        /*    lvContags.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              if(shareList.get(position).isSharedWith){
                  shareList.get(position).isSharedWith=false;
                  share=(shareListAdapter.getShareCount());
                  shareListAdapter.setShareCount(--share);
                  Log.d("share deselected", "in adapter: " + share) ;
              }
                else {
                  shareList.get(position).isSharedWith = true;
                  share=(shareListAdapter.getShareCount());
                  shareListAdapter.setShareCount(++share);
                  Log.d("share selected", "in adapter: " + share) ;

              }
                Intent sharedIntent = new Intent("com.contag.app.profile.sharecount") ;
                sharedIntent.putExtra("shareCount", share) ;
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(sharedIntent);

            }
        });


*/


        inviteMessage = (EditText) view.findViewById(R.id.et_invite_message);
        inviteMessage.setHint(Html.fromHtml("<b>Introduce "+ contagName+"</b> <br>(write a message)"));
       // inviteMessage.setHint("Introduce " + contagName+" \n(write a message)");
        Button shareDone = (Button) view.findViewById(R.id.btn_invite);

        etSearchBox = (EditText) view.findViewById(R.id.et_contact_search);
        etSearchBox.setOnEditorActionListener(this);
        etSearchBox.addTextChangedListener(this);

        shareDone.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_invite: {
                introduceContag();
                break;

            }
        }
    }


    private void introduceContag() {

        ArrayList<String> introduceTo = new ArrayList<>();
        for (ContactListItem cs : shareList) {
            if (cs.isSharedWith)
                introduceTo.add((String.valueOf(cs.mContagContag.getId())));
        }

        if (introduceTo.size() > 0) {
            Log.d("Introduce", StringUtils.join(introduceTo, ","));
            ContagIntroductionRequestModel crm = new ContagIntroductionRequestModel(contagID,
                    StringUtils.join(introduceTo, ","), inviteMessage.getText().toString());


            IntroductionRequest ir = new IntroductionRequest(crm);
            Log.d("Introduce", "Going to introduce");
            mSpiceManager.execute(ir, this);
        }
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Log.d("Introduce", "Request failed!");
        Toast.makeText(getActivity(), "Error occurred!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(Response response) {
        Log.d("Introduce", "Request worked!");
        Toast.makeText(getActivity(), "Introduced successfully!", Toast.LENGTH_LONG).show();
        getDialog().dismiss();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        shareListAdapter.filter(s.toString().toLowerCase());
        /* if (s.toString().length() != 0) {
            doSearch(s.toString());
        } else {
            new LoadContags().execute(contagID);
        }*/
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
       /* if (actionId == EditorInfo.IME_ACTION_SEARCH && v.getId() == R.id.et_contact_search) {
            doSearch(v.getText().toString());
        }*/
        shareListAdapter.filter(v.getText().toString().toLowerCase());
        return false;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    private void doSearch(final String query) {
        if (mSearchContacts != null) {
            mSearchContacts.cancel(true);
        }


            mSearchContacts = new SearchContacts();
            mSearchContacts.execute(query);


    }
    private class LoadContags extends AsyncTask<Long, Void, ArrayList<ContactListItem>> {

        @Override
        protected void onPreExecute() {
        }


        @Override
        protected ArrayList<ContactListItem> doInBackground(Long... params) {
            Long contagId = params[0];
            DaoSession session = ((ContagApplication) IntroduceContagDialog.this.getActivity().
                    getApplicationContext()).getDaoSession();

            ArrayList<ContactListItem> items = new ArrayList<>();

            ContagContagDao mContagContagDao = session.getContagContagDao();

            List<ContagContag> contagContacts = mContagContagDao.queryBuilder().
                    where(ContagContagDao.Properties.Id.notEq(PrefUtils.getCurrentUserID()),
                            ContagContagDao.Properties.Id.notEq(contagId)).list();


            Collections.sort(contagContacts, new Comparator<ContagContag>() {
                @Override
                public int compare(ContagContag lhs, ContagContag rhs) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            });

            for (ContagContag cc : contagContacts) {
                items.add(new ContactListItem(cc, false,
                        Constants.Types.ITEM_INTRODUCE_CONTAG));
            }

            return items;
        }

        @Override
        protected void onPostExecute(ArrayList<ContactListItem> contactListItems) {


            shareList.clear();
            shareList.addAll(contactListItems);
            shareListAdapter.setArray(shareList);
            shareListAdapter.notifyDataSetChanged();

        }
    }

    private class SearchContacts extends AsyncTask<String, Void, ArrayList<ContactListItem>> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ArrayList<ContactListItem> doInBackground(String... params) {

            ArrayList<ContactListItem> contactListItems = new ArrayList<>();
            if (params.length > 0) {
                DaoSession session = ((ContagApplication) IntroduceContagDialog.this.getActivity().
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

