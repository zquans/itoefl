package com.iyuce.itoefl.UI.Listening.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.QuestionAdapter;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FragmentDoQuestion extends Fragment implements QuestionAdapter.OnQuestionItemClickListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    //题目序号
    private TextView mTxtCurrentQuestion;

    //可选视图
    private RelativeLayout mRelativeLayout;

    //答题选项
    private RecyclerView mRecyclerView;
    private ArrayList<String> mDataList = new ArrayList<>();
    private QuestionAdapter mAdapter;

    private MediaPlayer mMediaPlayer;

    //接收参数,应该有很多个
    private String current_question, local_path;

    private OnFragmentInteractionListener mListener;

    public static FragmentDoQuestion newInstance(String current_question, String local_path) {
        FragmentDoQuestion fragment = new FragmentDoQuestion();
        Bundle args = new Bundle();
        args.putString("current_question", current_question);
        args.putString("local_path", local_path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            current_question = getArguments().getString("current_question");
            local_path = getArguments().getString("local_path");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMediaPlayer.stop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listen_do_question, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        for (int i = 0; i < 5; i++) {
            mDataList.add("I have a dream,One day we will in a peaceful world" + i);
        }

        mTxtCurrentQuestion = (TextView) view.findViewById(R.id.txt_fragment_do_result_page_middle);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relative_fragment_do_result_page);
        if (TextUtils.equals(current_question, "2")) {
            mRelativeLayout.setVisibility(View.GONE);
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_fragment_do_result);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new QuestionAdapter(mDataList, getActivity());
        mAdapter.setOnQuestionItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        //布置参数到对应控件
        mTxtCurrentQuestion.setText(current_question);

        //MediaPlayer
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        try {
            //路徑应该直接传递过来，从参数中直接获取
            String musicPath = local_path + "/16899.mp3";
            LogUtil.i("musicPath = " + musicPath);

            mMediaPlayer.setDataSource(musicPath);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //MediaPlayer's Interface
    @Override
    public void onCompletion(MediaPlayer mp) {
        ToastUtil.showMessage(getActivity(), "播放题目录音完毕，显示题号");

        String myjson = "";
        //TODO 读取本地json  ,IO流
        File file = new File("/storage/emulated/0/ITOEFL_JSON/andoird.json");
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String mimeTypeLine;
            while ((mimeTypeLine = br.readLine()) != null) {
                myjson = myjson + mimeTypeLine;
            }
//            myjson = myjson.substring(1);
            JSONObject obj = new JSONObject(myjson);
            if (obj.getString("code").equals("0")) {
                JSONArray data = obj.getJSONArray("data");
                obj = data.getJSONObject(0);
                String mVersionURL = obj.getString("apkurl");
                LogUtil.i("my local json = VersionURL = " + mVersionURL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
    }

    //Adapter提供给Fragment的方法
    @Override
    public void onQuestionClick(int pos) {
        resetItemSelectStyle(pos);
    }

    /**
     * 重设选中的Item及全部的Item
     */
    private void resetItemSelectStyle(int pos) {
        for (int i = 0; i < mDataList.size(); i++) {
            if (pos == i) {
                mRecyclerView.getChildAt(pos).findViewById(R.id.txt_item_fragment_do_question).setBackgroundResource(R.drawable.view_bound_orange_stroke);
                continue;
            }
            mRecyclerView.getChildAt(i).findViewById(R.id.txt_item_fragment_do_question).setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    //提供给Activity反馈的监听方法，让Activity响应Fragment的动作
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}