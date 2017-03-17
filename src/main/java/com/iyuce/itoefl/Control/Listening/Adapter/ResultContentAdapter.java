package com.iyuce.itoefl.Control.Listening.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.Model.Exercise.ListenResultContent;
import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/2/9
 */
public class ResultContentAdapter extends RecyclerView.Adapter<ResultContentAdapter.MyViewHolder> {

    private ArrayList<ListenResultContent> mDataList;
    private Context mContext;
    private String mQuestionType;

    public ResultContentAdapter(Context mContext, ArrayList<ListenResultContent> mDataList, String mQuestionType) {
        this.mDataList = mDataList;
        this.mContext = mContext;
        this.mQuestionType = mQuestionType;
    }

    @Override
    public ResultContentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //判断题的布局
        if (TextUtils.equals(mQuestionType, Constants.QUESTION_TYPE_JUDGE) ||
                TextUtils.equals(mQuestionType, Constants.QUESTION_TYPE_NEST)) {
            return new MyViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.recycler_item_do_result_content_judge, parent, false));
        }
        //非判断题的布局
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_do_result_content, parent, false));
    }

    @Override
    public void onBindViewHolder(ResultContentAdapter.MyViewHolder holder, int position) {
        holder.mTxtAnswer.setText(mDataList.get(position).content);
        //判断题型
        if (TextUtils.equals(mQuestionType, Constants.QUESTION_TYPE_JUDGE) ||
                TextUtils.equals(mQuestionType, Constants.QUESTION_TYPE_NEST)) {
            //是判断题，先判断状态
            if (mDataList.get(position).state.contains("true")) {
                //用户答对了
                if (mDataList.get(position).judgeSelect.contains("true")) {
                    holder.mImgYes.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
                } else {
                    holder.mImgNo.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
                }
            } else if (mDataList.get(position).state.contains("null")) {
                //用户没回答,只显示一个正确答案
                if (mDataList.get(position).judgeAnswer.contains("true")) {
                    holder.mImgYes.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
                } else {
                    holder.mImgNo.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
                }
            } else {
                //用户答错了，要显示两个个圈
                if (mDataList.get(position).judgeSelect.contains("true")) {
                    //用户选了true,因为做错了,YES要用红图
                    holder.mImgYes.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_full);
                    holder.mImgNo.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
                } else {
                    //用户选了false,因为做错了,NO要用红图
                    holder.mImgNo.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_full);
                    holder.mImgYes.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
                }
            }
            return;
        }

        //非判断题型
//        holder.mTxtNumber.setText(mDataList.get(position).number);
        if (mDataList.get(position).state.equals("true")) {
            holder.mTxtNumber.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
        } else if (mDataList.get(position).state.equals("false")) {
            holder.mTxtNumber.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_full);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtNumber, mTxtAnswer;
        ImageView mImgYes, mImgNo;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTxtNumber = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_number);
            mTxtAnswer = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_answer);
            if (TextUtils.equals(mQuestionType, Constants.QUESTION_TYPE_JUDGE) ||
                    TextUtils.equals(mQuestionType, Constants.QUESTION_TYPE_NEST)) {
                mImgYes = (ImageView) itemView.findViewById(R.id.img_item_fragment_do_result_judge_yes);
                mImgNo = (ImageView) itemView.findViewById(R.id.img_item_fragment_do_result_judge_no);
            }
        }
    }
}