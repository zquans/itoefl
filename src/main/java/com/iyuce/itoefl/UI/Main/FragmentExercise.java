package com.iyuce.itoefl.UI.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Activity.TopListeneringActivity;

/**
 * Created by LeBang on 2017/1/22
 */
public class FragmentExercise extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_exercise, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        TextView mTxt = (TextView) view.findViewById(R.id.txt_do_click);
        mTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), TopListeneringActivity.class));
            }
        });
    }
}