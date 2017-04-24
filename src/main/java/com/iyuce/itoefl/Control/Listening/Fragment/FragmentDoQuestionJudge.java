package com.iyuce.itoefl.Control.Listening.Fragment;

import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.Control.Listening.Adapter.QuestionJudgeAdapter;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.DbUtil;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.TimeUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FragmentDoQuestionJudge extends FragmentDoQuestionDefault implements QuestionJudgeAdapter.OnQuestionItemClickListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    //题目序号、内容
    private TextView mTxtCurrentQuestion, mTxtTotalQuestion, mTxtQuestionContent, mTxtQuestionType;
    private TextView mTxtProgressCurrent, mTxtProgressTotal, mTxtListenAgainHint;
    private ProgressBar mProgressBar;
    //可选视图
    private RelativeLayout mRelativeLayout;

    //答题选项
    private RecyclerView mRecyclerView;
    private ArrayList<String> mJudgeContentList = new ArrayList<>();
    private ArrayList<String> mJudgeAnswerList = new ArrayList<>();
    private QuestionJudgeAdapter mAdapter;

    private MediaPlayer mMediaPlayer;

    //用于不止一段音频的题型
    private boolean isOnlyAudio = true;
    private int mEndPosition = 0;
    private String mEndText;
    //多音频列表
    private String[] mAudioList = new String[]{};
    private int current_audio = 0;

    //接收参数
    private String total_question, current_question, current_music, current_question_id, question_content, local_path, local_paper_code;

    private Handler mMediaProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Message message = Message.obtain();
            message.what = Constants.FLAG_AUDIO_PLAY;
            mMediaProgressHandler.sendMessageDelayed(message, Constants.HandlerDelay);
            getCurrent();
        }
    };

    public String selectAnswer() {
        return answerDefault;
    }

    public String realAnswer() {
        return mJudgeAnswerList.toString();
    }

    public boolean finishMediaPlayer() {
        return isFinish;
    }

    public static FragmentDoQuestionJudge newInstance(String total_question, String current_question,
                                                      String current_music, String current_question_id, String question_content,
                                                      String local_path, String local_paper_code) {
        FragmentDoQuestionJudge fragment = new FragmentDoQuestionJudge();
        Bundle args = new Bundle();
        args.putString("total_question", total_question);
        args.putString("current_question", current_question);
        args.putString("current_music", current_music);
        args.putString("current_question_id", current_question_id);
        args.putString("question_content", question_content);
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
            question_content = getArguments().getString("question_content");
            local_path = getArguments().getString("local_path");
            local_paper_code = getArguments().getString("local_paper_code");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mMediaProgressHandler.removeMessages(Constants.FLAG_AUDIO_PLAY);
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
        SQLiteDatabase mDatabase = DbUtil.getHelper(getActivity(), local_path + "/" + local_paper_code + ".sqlite").getWritableDatabase();
        //查表Child
        mJudgeContentList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_QUESTION_CHILD, Constants.Content, Constants.MasterId + " =? ", current_question_id);
        mJudgeAnswerList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_QUESTION_CHILD, Constants.Answer, Constants.MasterId + " =? ", current_question_id);
        mDatabase.close();
        LogUtil.i(mJudgeContentList.toString() + mJudgeAnswerList.toString());

        mTxtCurrentQuestion = (TextView) view.findViewById(R.id.txt_fragment_do_result_page_middle);
        mTxtTotalQuestion = (TextView) view.findViewById(R.id.txt_fragment_do_result_page_right);
        mTxtListenAgainHint = (TextView) view.findViewById(R.id.txt_fragment_do_question_listen_again);
        mTxtQuestionContent = (TextView) view.findViewById(R.id.txt_fragment_do_result_title);
        mTxtQuestionType = (TextView) view.findViewById(R.id.txt_fragment_do_result_question_type);
        mTxtProgressCurrent = (TextView) view.findViewById(R.id.txt_fragment_do_question_current);
        mTxtProgressTotal = (TextView) view.findViewById(R.id.txt_fragment_do_question_total);
        mProgressBar = (ProgressBar) view.findViewById(R.id.bar_fragment_do_question_progress);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_fragment_do_result);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relative_fragment_do_result_page);
        //非单音频题
        if (current_music.contains(",")) {
            isOnlyAudio = false;
            mAudioList = current_music.split(",");
            String type = local_paper_code.contains("L") ? "Lecture" : "Conversation";
            mTxtListenAgainHint.setText(getString(R.string.listen_again_to_part_of, type));
        } else {
            mRelativeLayout.setVisibility(View.GONE);
        }

        //布置参数到对应控件
        mTxtCurrentQuestion.setText(current_question);
        mTxtTotalQuestion.setText(total_question);
        mTxtQuestionContent.setText(question_content);
        mTxtQuestionType.setText("本题是判断题");
        mTxtQuestionType.setVisibility(View.VISIBLE);

        //MediaPlayer
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        try {
            String musicPath;
            if (isOnlyAudio) {
                musicPath = local_path + File.separator + current_music;
            } else {
                musicPath = local_path + File.separator + mAudioList[current_audio];
            }
            mMediaPlayer.setDataSource(musicPath);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //MediaPlayer
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (current_audio == mAudioList.length - 1) {
            isOnlyAudio = true;
            mMediaProgressHandler.removeMessages(Constants.FLAG_AUDIO_PLAY);
        }
        if (!isOnlyAudio) {
            current_audio++;
            //避免音频再次播放时，延迟1秒的handle持续更新进度条
            mMediaProgressHandler.removeMessages(Constants.FLAG_AUDIO_PLAY);
            mProgressBar.setProgress(mEndPosition);
            mTxtProgressCurrent.setText(mEndText);
            mMediaPlayer.reset();
            try {
                String musicPath = local_path + File.separator + mAudioList[current_audio];
                mMediaPlayer.setDataSource(musicPath);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new QuestionJudgeAdapter(getActivity(), mJudgeContentList);
        mAdapter.setOnQuestionItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        isFinish = true;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
        if (!isOnlyAudio) {
            Message msg = Message.obtain();
            msg.what = Constants.FLAG_AUDIO_PLAY;
            mMediaProgressHandler.sendMessage(msg);
            getDrution();
        }
    }

    //音频进度
    private void getCurrent() {
        mProgressBar.setProgress(mMediaPlayer.getCurrentPosition());
        int time = mMediaPlayer.getCurrentPosition() / 1000;
        String timer = TimeUtil.toTimeShow(time);
        mTxtProgressCurrent.setText(timer);
    }

    private void getDrution() {
        mEndPosition = mMediaPlayer.getDuration();
        mProgressBar.setMax(mEndPosition);
        int time = mMediaPlayer.getDuration() / 1000;
        String timer = TimeUtil.toTimeShow(time);
        mTxtProgressTotal.setText(timer);
        mEndText = timer;
    }

    //Adapter提供给Fragment的方法
    @Override
    public void onQuestionClick(int pos) {
        //判断
        answerDefault = mAdapter.returnSelectList().toString();
        LogUtil.i("Judge mAnswer = " + mJudgeAnswerList.toString() + ",and you choose " + answerDefault);
    }
}