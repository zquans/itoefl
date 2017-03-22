package com.iyuce.itoefl.Control.Listening.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuce.itoefl.Model.Exercise.QuestionTypeNest;
import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/21
 */
public class ResultNestItemAdapter extends RecyclerView.Adapter<ResultNestItemAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<QuestionTypeNest> mDataList;

    public ResultNestItemAdapter(Context mContext, ArrayList<QuestionTypeNest> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_do_result_content_nest_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTxtContent.setText(mDataList.get(position).content);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtContent;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTxtContent = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_result_answer);
        }
    }
}
