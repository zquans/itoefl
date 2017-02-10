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
 * Created by LeBang on 2017/2/10
 */
public class BottomDoQuestionAdapter extends RecyclerView.Adapter<BottomDoQuestionAdapter.MyViewHolder> {

    private ArrayList<String> mDataList;
    private Context mContext;

    private OnButtonItemClickListener mListener;

    public BottomDoQuestionAdapter(ArrayList<String> mDataList, Context mContext) {
        this.mDataList = mDataList;
        this.mContext = mContext;
    }

    public void setOnBottomItemClickListener(OnButtonItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public BottomDoQuestionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_do_question_bottom, parent, false));
    }

    @Override
    public void onBindViewHolder(BottomDoQuestionAdapter.MyViewHolder holder, final int position) {
        holder.mTxtContent.setText(mDataList.get(position));
        holder.mTxtContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBottomClick(position + 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtContent;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTxtContent = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_question_bottom);
        }
    }

    public interface OnButtonItemClickListener {
        void onBottomClick(int position);
    }
}