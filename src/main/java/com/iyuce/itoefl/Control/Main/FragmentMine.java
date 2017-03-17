package com.iyuce.itoefl.Control.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Control.Mine.AboutUsActivity;
import com.iyuce.itoefl.Control.Mine.ManageDownLoadActivity;
import com.iyuce.itoefl.Control.Mine.SuggestionActivity;
import com.iyuce.itoefl.Utils.UpdateManager;

/**
 * Created by LeBang on 2017/1/22
 */
public class FragmentMine extends Fragment implements View.OnClickListener {

    private TextView mTxtManageDownLoad, mTxtAboutUs, mTxtSuggestion, mTxtUserSignOut, mTxtCheckUpdate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_mine, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mTxtAboutUs = (TextView) view.findViewById(R.id.txt_to_about_us);
        mTxtManageDownLoad = (TextView) view.findViewById(R.id.txt_to_manage_download);
        mTxtSuggestion = (TextView) view.findViewById(R.id.txt_to_suggestion);
        mTxtUserSignOut = (TextView) view.findViewById(R.id.txt_to_sign_out);
        mTxtCheckUpdate = (TextView) view.findViewById(R.id.txt_to_update);
        mTxtAboutUs.setOnClickListener(this);
        mTxtManageDownLoad.setOnClickListener(this);
        mTxtSuggestion.setOnClickListener(this);
        mTxtUserSignOut.setOnClickListener(this);
        mTxtCheckUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_to_about_us:
                getActivity().startActivity(new Intent(getActivity(), AboutUsActivity.class));
                break;
            case R.id.txt_to_manage_download:
                getActivity().startActivity(new Intent(getActivity(), ManageDownLoadActivity.class));
                break;
            case R.id.txt_to_suggestion:
                getActivity().startActivity(new Intent(getActivity(), SuggestionActivity.class));
                break;
            case R.id.txt_to_update:
                new UpdateManager(getActivity()).checkUpdate();
                break;
            case R.id.txt_to_sign_out:
                Snackbar.make(v, "暂未开通用户系统哦!", Snackbar.LENGTH_LONG).show();
                break;
        }
    }
}