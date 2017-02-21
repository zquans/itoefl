package com.iyuce.itoefl.UI.Listening.Fragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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

import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.QuestionAdapter;
import com.iyuce.itoefl.Utils.DbUtil;
import com.iyuce.itoefl.Utils.LogUtil;

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

    //题目序号、内容
    private TextView mTxtCurrentQuestion, mTxtTotalQuestion, mTxtQuestionContent;
    //可选视图
    private RelativeLayout mRelativeLayout;

    //答题选项
    private RecyclerView mRecyclerView;
    private ArrayList<String> mDataList = new ArrayList<>();
    private QuestionAdapter mAdapter;

    private MediaPlayer mMediaPlayer;
    private boolean isFinish = false;

    //接收参数
    private String total_question, current_question, current_music, current_question_id, local_path, local_paper_code;

    //查表所得的属性
    private String mQuestionType, mContent, mAnswer;

    private OnFragmentInteractionListener mListener;

    //获取到的参数  QuestionId(用于在Fragment中继续查表)    Sort题号     MusicQuestion音频
    public static FragmentDoQuestion newInstance(String total_question,
                                                 String current_question, String current_music, String current_question_id, String local_path, String local_paper_code) {
        FragmentDoQuestion fragment = new FragmentDoQuestion();
        Bundle args = new Bundle();
        args.putString("total_question", total_question);
        args.putString("current_question", current_question);
        args.putString("current_music", current_music);
        args.putString("current_question_id", current_question_id);
        args.putString("local_path", local_path);
        args.putString("local_paper_code", local_paper_code);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            total_question = getArguments().getString("total_question");
            current_question = getArguments().getString("current_question");
            current_music = getArguments().getString("current_music");
            current_question_id = getArguments().getString("current_question_id");
            local_path = getArguments().getString("local_path");
            local_paper_code = getArguments().getString("local_paper_code");
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
        //数据源
        SQLiteDatabase mDatabase = DbUtil.getHelper(getActivity(), local_path + "/" + local_paper_code + ".sqlite"
                , Constants.DATABASE_VERSION).getWritableDatabase();
        //查表Question
        mContent = DbUtil.queryToString(mDatabase, Constants.TABLE_QUESTION, Constants.Content, Constants.ID, current_question_id);
        mQuestionType = DbUtil.queryToString(mDatabase, Constants.TABLE_QUESTION, Constants.QuestionType, Constants.ID, current_question_id);
        mAnswer = DbUtil.queryToString(mDatabase, Constants.TABLE_QUESTION, Constants.Answer, Constants.ID, current_question_id);
        //查表Option
        mDataList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_OPTION, Constants.Content, Constants.QuestionId, current_question_id);
        mDatabase.close();

        mTxtCurrentQuestion = (TextView) view.findViewById(R.id.txt_fragment_do_result_page_middle);
        mTxtTotalQuestion = (TextView) view.findViewById(R.id.txt_fragment_do_result_page_right);
        mTxtQuestionContent = (TextView) view.findViewById(R.id.txt_fragment_do_result_title);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relative_fragment_do_result_page);
        if (TextUtils.equals(mQuestionType, "SINGLE")) {
            mRelativeLayout.setVisibility(View.GONE);
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_fragment_do_result);

        //布置参数到对应控件
        mTxtCurrentQuestion.setText(current_question);
        mTxtTotalQuestion.setText(total_question);
        mTxtQuestionContent.setText(mContent);

        //MediaPlayer
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        try {
            //路徑应该直接传递过来，从参数中直接获取
            String musicPath = local_path + File.separator + current_music;
            LogUtil.i(current_question_id + "fragment get musicPath = " + musicPath);

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

    public boolean finishMediaPlayer() {
        return isFinish;
    }

    //MediaPlayer
    @Override
    public void onCompletion(MediaPlayer mp) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new QuestionAdapter(mDataList, getActivity());
        mAdapter.setOnQuestionItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        isFinish = true;

        //TODO 额外尝试，读取本地json  ,IO流
        String myjson = "";
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
        LogUtil.i("mAnswer = " + mAnswer + ",,and you choose" + pos);
    }

    /**
     * 重设选中的Item及全部的Item
     */
    private void resetItemSelectStyle(int pos) {
        TextView textView;
        for (int i = 0; i < mDataList.size(); i++) {
            if (pos == i) {
                textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_item_fragment_do_question);
                textView.setBackgroundResource(R.drawable.view_bound_orange_stroke);
                continue;
            }
            textView = (TextView) mRecyclerView.getChildAt(i).findViewById(R.id.txt_item_fragment_do_question);
            textView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    //提供给Activity反馈的监听方法，让Activity响应Fragment的动作
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}