package com.iyuce.itoefl.Control.Listening.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuce.itoefl.Model.Exercise.QuestionNest;
import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/2/9
 */
public class ResultContentNestAdapter extends RecyclerView.Adapter<ResultContentNestAdapter.MyViewHolder> {

    private ArrayList<QuestionNest> mDataList;
    private Context mContext;

    public ResultContentNestAdapter(Context mContext, ArrayList<QuestionNest> mDataList) {
        this.mDataList = mDataList;
        this.mContext = mContext;
    }

    @Override
    public ResultContentNestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //判断题的布局
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_do_result_content_nest, parent, false));
    }

    @Override
    public void onBindViewHolder(ResultContentNestAdapter.MyViewHolder holder, int position) {
        holder.mTxtAnswer.setText(mDataList.get(position).content);
        if (mDataList.get(position).options.size() == 3) {
            holder.mTxtA.setText(mDataList.get(position).options.get(0));
            holder.mTxtB.setText(mDataList.get(position).options.get(1));
            holder.mTxtC.setText(mDataList.get(position).options.get(2));
            if (mDataList.get(position).select == 0) {
                holder.mImgA.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_full);
            } else if (mDataList.get(position).select == 1) {
                holder.mImgB.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_full);
            } else if (mDataList.get(position).select == 2) {
                holder.mImgC.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_full);
            } else {
                holder.mImgC.setBackgroundColor(Color.TRANSPARENT);
            }
            if (mDataList.get(position).answer == 0) {
                holder.mImgA.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
            } else if (mDataList.get(position).answer == 1) {
                holder.mImgB.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
            } else if (mDataList.get(position).answer == 2) {
                holder.mImgC.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
            }
        } else {
            holder.mRelayoutC.setVisibility(View.GONE);
            holder.mTxtA.setText(mDataList.get(position).options.get(0));
            holder.mTxtB.setText(mDataList.get(position).options.get(1));
            if (mDataList.get(position).select == 0) {
                holder.mImgA.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_full);
            } else if (mDataList.get(position).select == 1) {
                holder.mImgB.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_full);
            }
            if (mDataList.get(position).answer == 0) {
                holder.mImgA.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
            } else if (mDataList.get(position).answer == 1) {
                holder.mImgB.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtNumber, mTxtAnswer;
        TextView mTxtA, mTxtB, mTxtC;
        ImageView mImgA, mImgB, mImgC;
        RelativeLayout mRelayoutC;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTxtNumber = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_number);
            mTxtAnswer = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_answer);
            mImgA = (ImageView) itemView.findViewById(R.id.img_item_fragment_do_result_judge_a);
            mImgB = (ImageView) itemView.findViewById(R.id.img_item_fragment_do_result_judge_b);
            mImgC = (ImageView) itemView.findViewById(R.id.img_item_fragment_do_result_judge_c);
            mTxtA = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_judge_a);
            mTxtB = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_judge_b);
            mTxtC = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_judge_c);
            mRelayoutC = (RelativeLayout) itemView.findViewById(R.id.rlayout_item_fragment_do_result_judge_c);
        }
    }
}