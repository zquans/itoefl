package com.iyuce.itoefl.UI.Listening.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuce.itoefl.Model.ListenResult;
import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/2/8
 */
public class ResultTitleAdapter extends RecyclerView.Adapter<ResultTitleAdapter.MyViewHolder> {

    private ArrayList<ListenResult> mDataList = new ArrayList<>();
    private Context mContext;

    public ResultTitleAdapter(ArrayList<ListenResult> mDataList, Context mContext) {
        this.mDataList = mDataList;
        this.mContext = mContext;
    }

    @Override
    public ResultTitleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_do_result, parent, false));
    }

    @Override
    public void onBindViewHolder(ResultTitleAdapter.MyViewHolder holder, int position) {
        holder.mTitle.setText(mDataList.get(position).question_name);
        if (mDataList.get(position).question_state)
            holder.mImg.setBackgroundResource(R.mipmap.icon_answer_talk_right_full);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        ImageView mImg;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txt_item_do_result);
            mImg = (ImageView) itemView.findViewById(R.id.img_item_do_result);
        }
    }
}