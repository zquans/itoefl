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
public class ResultContentAdapter extends RecyclerView.Adapter<ResultContentAdapter.MyViewHolder> {

    private ArrayList<String> mDataList;
    private Context mContext;

    public ResultContentAdapter(ArrayList<String> mDataList, Context mContext) {
        this.mDataList = mDataList;
        this.mContext = mContext;
    }

    @Override
    public ResultContentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_do_result_content, parent, false));
    }

    @Override
    public void onBindViewHolder(ResultContentAdapter.MyViewHolder holder, int position) {
        holder.mTxtAnswer.setText(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtNumber, mTxtAnswer;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTxtNumber = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_number);
            mTxtAnswer = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_answer);
        }
    }
}
