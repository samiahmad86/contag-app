package com.contag.app.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.contag.app.R;
import com.contag.app.adapter.ShareListAdapter;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.model.ContactListItem;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.ContagIntroductionRequestModel;
import com.contag.app.model.DaoSession;
import com.contag.app.model.Response;
import com.contag.app.request.IntroductionRequest;
import com.contag.app.service.APIService;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by varunj on 04/11/15.
 */
public class IntroduceContagDialog extends DialogFragment implements View.OnClickListener, RequestListener<Response>{

    private ListView lvContags;
    private SpiceManager mSpiceManager = new SpiceManager(APIService.class);
    private ArrayList<ContactListItem> shareList = new ArrayList<>();
    private ShareListAdapter shareListAdapter;
    private EditText inviteMessage;
    private String contagName ;
    private long contagID ;


    public static IntroduceContagDialog newInstance(String contagName, long contagID) {

        IntroduceContagDialog scDialog = new IntroduceContagDialog();
        Bundle args = new Bundle();
        args.putString(Constants.Keys.KEY_CONTACT_NAME, contagName) ;
        args.putLong(Constants.Keys.KEY_USER_ID, contagID) ;
        scDialog.setArguments(args);
        return scDialog ;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        mSpiceManager.start(getActivity());
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


        contagName = getArguments().getString(Constants.Keys.KEY_CONTACT_NAME) ;
        contagID = getArguments().getLong(Constants.Keys.KEY_USER_ID) ;
        new LoadContags().execute(contagID) ;

        shareListAdapter= new ShareListAdapter(shareList, getActivity());
        lvContags = (ListView) view.findViewById(R.id.lv_contag_share);
        lvContags.setAdapter(shareListAdapter);

        inviteMessage = (EditText) view.findViewById(R.id.et_invite_message) ;

        inviteMessage.setHint("Introduce " + contagName) ;
        Button shareDone = (Button) view.findViewById(R.id.btn_invite) ;

        shareDone.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id) {
            case R.id.btn_invite:{
                introduceContag();
                break ;

            }
        }
    }


    private void introduceContag(){

        ArrayList<String> introduceTo = new ArrayList<>() ;
        for(ContactListItem cs : shareList){
            if(cs.isSharedWith)
                introduceTo.add((String.valueOf(cs.mContagContag.getId()))) ;
        }

        if(introduceTo.size() > 0) {
            Log.d("Introduce", StringUtils.join(introduceTo, ","));
            ContagIntroductionRequestModel crm = new ContagIntroductionRequestModel(contagID,
                    StringUtils.join(introduceTo, ","), inviteMessage.getText().toString());


            IntroductionRequest ir = new IntroductionRequest(crm);
            Log.d("Introduce", "Going to introduce") ;
            mSpiceManager.execute(ir, this)  ;
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

    private class LoadContags extends AsyncTask<Long, Void, ArrayList<ContactListItem>> {

        @Override
        protected void onPreExecute() {
        }


        @Override
        protected ArrayList<ContactListItem> doInBackground(Long...params) {
            Long contagId = params[0] ;
            DaoSession session = ((ContagApplication) IntroduceContagDialog.this.getActivity().
                    getApplicationContext()).getDaoSession();

            ArrayList<ContactListItem> items = new ArrayList<>();

            ContagContagDao mContagContagDao = session.getContagContagDao();

            List<ContagContag> contagContacts = mContagContagDao.queryBuilder().
                    where(ContagContagDao.Properties.Id.notEq(PrefUtils.getCurrentUserID()),
                            ContagContagDao.Properties.Id.notEq(contagId)).list();


            for(ContagContag cc: contagContacts){
                items.add(new ContactListItem(cc, false,
                        Constants.Types.ITEM_INTRODUCE_CONTAG)) ;
            }

            return items;
        }

        @Override
        protected void onPostExecute(ArrayList<ContactListItem> contactListItems) {


            shareList.clear();
            shareList.addAll(contactListItems);
            shareListAdapter.notifyDataSetChanged();

        }
    }


}
