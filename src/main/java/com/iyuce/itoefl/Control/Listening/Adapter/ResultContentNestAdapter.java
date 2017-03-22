package com.iyuce.itoefl.Control.Listening.Adapter;

import android.content.Context;
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
            holder.mTxtYes.setText(mDataList.get(position).options.get(0));
            holder.mTxtNull.setText(mDataList.get(position).options.get(1));
            holder.mTxtNo.setText(mDataList.get(position).options.get(2));
            if (mDataList.get(position).select == 0) {
                holder.mImgYes.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_full);
            } else if (mDataList.get(position).select == 1) {
                holder.mImgNull.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_full);
            } else {
                holder.mImgNo.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_full);
            }
            if (mDataList.get(position).answer == 0) {
                holder.mImgYes.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
            } else if (mDataList.get(position).answer == 1) {
                holder.mImgNull.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
            } else {
                holder.mImgNo.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
            }
        } else {
            holder.mRelayoutNull.setVisibility(View.GONE);
            holder.mTxtYes.setText(mDataList.get(position).options.get(0));
            holder.mTxtNo.setText(mDataList.get(position).options.get(1));
            if (mDataList.get(position).select == 0) {
                holder.mImgYes.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_full);
            } else {
                holder.mImgNo.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_full);
            }
            if (mDataList.get(position).answer == 0) {
                holder.mImgYes.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
            } else {
                holder.mImgNo.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
            }
        }

        //是判断题，先判断状态
//        if (mDataList.get(position).state.contains("true")) {
//            //用户答对了
////            if (mDataList.get(position).judgeSelect.contains("true")) {
////                holder.mImgYes.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
////            } else {
////                holder.mImgNo.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
////            }
//        } else if (mDataList.get(position).state.contains("null")) {
//            //用户没回答,只显示一个正确答案
////            if (mDataList.get(position).judgeAnswer.contains("true")) {
////                holder.mImgYes.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
////            } else {
////                holder.mImgNo.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
////            }
//        } else {
//            //用户答错了，要显示两个个圈
////            if (mDataList.get(position).judgeSelect.contains("true")) {
////                //用户选了true,因为做错了,YES要用红图
////                holder.mImgYes.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_full);
////                holder.mImgNo.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
////            } else {
////                //用户选了false,因为做错了,NO要用红图
////                holder.mImgNo.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_full);
////                holder.mImgYes.setBackgroundResource(R.mipmap.icon_answer_cycle_right_full);
////            }
//        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtNumber, mTxtAnswer;
        TextView mTxtYes, mTxtNo, mTxtNull;
        ImageView mImgYes, mImgNo, mImgNull;
        RelativeLayout mRelayoutNull;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTxtNumber = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_number);
            mTxtAnswer = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_answer);
            mImgYes = (ImageView) itemView.findViewById(R.id.img_item_fragment_do_result_judge_yes);
            mImgNull = (ImageView) itemView.findViewById(R.id.img_item_fragment_do_result_judge_null);
            mImgNo = (ImageView) itemView.findViewById(R.id.img_item_fragment_do_result_judge_no);
            mTxtYes = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_judge_yes);
            mTxtNull = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_judge_null);
            mTxtNo = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_judge_no);
            mRelayoutNull = (RelativeLayout) itemView.findViewById(R.id.rlayout_item_fragment_do_result_judge_null);
        }
    }
}