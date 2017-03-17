package com.iyuce.itoefl.Control.Main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.ToastUtil;

/**
 * Created by LeBang on 2017/1/22
 */
public class FragmentShowFlip extends Fragment {

    private ImageView mImg;

    private OnFlipShowListener mListener;

    public void setOnFlipShowListener(OnFlipShowListener listener) {
        this.mListener = listener;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_flip, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mImg = (ImageView) view.findViewById(R.id.img_fragment_show_flip);
        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.OnFlipShowClick();
                } else {
                    ToastUtil.showMessage(getActivity(), "暂未制作该部分内容");
                }
            }
        });
    }

    public interface OnFlipShowListener {
        void OnFlipShowClick();
    }
}