package com.iyuce.itoefl.UI.Listening.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/2/9
 */
public class QuestionSortAdapter extends RecyclerView.Adapter<QuestionSortAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<String> mDataList;

    public QuestionSortAdapter(Context mContext, ArrayList<String> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @Override
    public QuestionSortAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_do_question, parent, false));
    }

    @Override
    public void onBindViewHolder(final QuestionSortAdapter.MyViewHolder holder, final int position) {
        holder.mTxtQuestion.setText(mDataList.get(position));
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
}