package com.iyuce.itoefl.Control.Listening.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/2/9
 * 单选题
 */
public class QuestionSingleAdapter extends RecyclerView.Adapter<QuestionSingleAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<String> mDataList;
    private ArrayList<Boolean> mSelectedList;

    private OnQuestionItemClickListener mListener;

    public void setOnQuestionItemClickListener(OnQuestionItemClickListener listener) {
        mListener = listener;
    }

    public QuestionSingleAdapter(Context mContext, ArrayList<String> mDataList, ArrayList<Boolean> mSelectedList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mSelectedList = mSelectedList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_do_question, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.mTxtQuestion.setText(mDataList.get(position));
        if (mSelectedList.get(position)) {
            holder.mTxtQuestion.setTextColor(Color.parseColor("#000000"));
            holder.mTxtQuestion.setBackgroundResource(R.drawable.view_bound_pink_stroke_five);
        } else {
            holder.mTxtQuestion.setTextColor(Color.parseColor("#888888"));
            holder.mTxtQuestion.setBackgroundResource(R.color.White);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onQuestionClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtQuestion;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTxtQuestion = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_question);
        }
    }

    public interface OnQuestionItemClickListener {
        void onQuestionClick(int pos);
    }
}