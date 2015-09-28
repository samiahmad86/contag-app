package com.contag.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.model.ProfileModel;

import java.util.HashMap;
import java.util.zip.Inflater;

/**
 * Created by tanay on 28/9/15.
 */
public class ProfileListAdapter extends BaseAdapter {

    private int profileType;
    private HashMap<Integer, ProfileModel> hmModel;
    private Context mContext;
    private ViewHolder vh;

    public ProfileListAdapter(int profileType, HashMap<Integer, ProfileModel> hmProfileModel, Context context) {
        this.mContext = context;
        this.profileType = profileType;
        this.hmModel = hmProfileModel;
    }

    @Override
    public int getCount() {
        return hmModel.size();
    }

    @Override
    public Object getItem(int position) {
        return hmModel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_profile_edit, parent, false);
            vh.btnEdit = (Button) convertView.findViewById(R.id.btn_edit);
            vh.tvFieldValue = (TextView) convertView.findViewById(R.id.tv_field_value);
            vh.etFieldValue = (EditText) convertView.findViewById(R.id.et_field_value);
            vh.pbUpdate = (ProgressBar) convertView.findViewById(R.id.pb_update);
            vh.tvFieldLabel = (TextView) convertView.findViewById(R.id.tv_field_label);
            vh.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = (View) v.getTag();
                    Button btn = (Button)v;
                    if (btn.getText().toString().equalsIgnoreCase("edit")) {
                        view.findViewById(R.id.et_field_value).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.tv_field_value).setVisibility(View.INVISIBLE);
                        btn.setText("Update");
                    }
                }
            });
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.btnEdit.setTag(convertView);

        return convertView;
    }


    public static class ViewHolder {
        protected TextView tvFieldValue;
        protected TextView tvFieldLabel;
        protected EditText etFieldValue;
        protected Button btnEdit;
        protected ProgressBar pbUpdate;

        public ViewHolder() {

        }
    }
}
