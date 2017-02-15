package com.iyuce.itoefl.UI.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Mine.AboutUsActivity;

/**
 * Created by LeBang on 2017/1/22
 */
public class FragmentMine extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_mine, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.txt_to_about_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), AboutUsActivity.class));
            }
        });
    }
}