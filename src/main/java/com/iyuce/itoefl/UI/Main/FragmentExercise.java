package com.iyuce.itoefl.UI.Main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Activity.TopListeneringActivity;

/**
 * Created by LeBang on 2017/1/22
 */
public class FragmentExercise extends Fragment {

    private CollapsingToolbarLayout mCollapLayout;
    private RelativeLayout mRelative;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_exercise, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        mRelative = (RelativeLayout) view.findViewById(R.id.relative_do_click);
        mRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), TopListeneringActivity.class));
            }
        });

        mCollapLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar_layout);
        //加载Gif图
//        mImg = (ImageView) view.findViewById(R.id.img_fragment_exercise_header);
//        Glide.with(getActivity()).load(R.raw.gif_header_exercise).into(mImg);
        //改变字体颜色
        mCollapLayout.setExpandedTitleColor(Color.parseColor("#FCFCFC"));
        mCollapLayout.setCollapsedTitleTextColor(Color.parseColor("#FF3370"));
    }
}