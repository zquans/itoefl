package com.iyuce.itoefl.Control.Listening.Adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuce.itoefl.Model.Exercise.QuestionTypeNest;
import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/2/9
 */
public class ResultContentNestAdapter extends RecyclerView.Adapter<ResultContentNestAdapter.MyViewHolder> {

    private ArrayList<QuestionTypeNest> mDataList;
    private Context mContext;

    public ResultContentNestAdapter(Context mContext, ArrayList<QuestionTypeNest> mDataList) {
        this.mDataList = mDataList;
        this.mContext = mContext;
    }

    @Override
    public ResultContentNestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_do_result_content_nest, parent, false));
    }

    @Override
    public void onBindViewHolder(ResultContentNestAdapter.MyViewHolder holder, int position) {
        //TODO
        holder.mTxtAnswer.setText(mDataList.get(position).content);
        holder.mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
//        ResultNestItemAdapter mAdapter = new ResultNestItemAdapter(mContext, mDataList.get(position).itemCntentList);
        ResultNestItemAdapter mAdapter = new ResultNestItemAdapter(mContext, mDataList);
        holder.mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtAnswer;
        RecyclerView mRecyclerView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTxtAnswer = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_answer);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_item_fragment_do_result_answer);
        }
    }
}