package com.iyuce.itoefl.UI.Listening.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/2/9
 */
public class QuestionMultiAdapter extends RecyclerView.Adapter<QuestionMultiAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<String> mDataList;

    private ArrayList<Boolean> mIsSelectedList;

    private OnQuestionItemClickListener mListener;

    public void setOnQuestionItemClickListener(OnQuestionItemClickListener listener) {
        mListener = listener;
    }

    public QuestionMultiAdapter(Context mContext, ArrayList<String> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        mIsSelectedList = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {
            mIsSelectedList.add(false);
        }
    }

    @Override
    public QuestionMultiAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_do_question, parent, false));
    }

    @Override
    public void onBindViewHolder(final QuestionMultiAdapter.MyViewHolder holder, final int position) {
        holder.mTxtQuestion.setText(mDataList.get(position));
        //单选和多选
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsSelectedList.get(position)) {
                    holder.mTxtQuestion.setBackgroundResource(R.color.Transparent);
                } else {
                    holder.mTxtQuestion.setBackgroundResource(R.drawable.view_bound_pink_stroke_five);
                }
                //反转boolean状态
                mIsSelectedList.set(position, !mIsSelectedList.get(position));
                //传递数据,所需的数据，仅仅是position而已，若只涉及到自身view,不要把表现交给外面,自己控制
                mListener.onQuestionClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RadioGroup mRadioGroup;
        TextView mTxtQuestion;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTxtQuestion = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_question);
        }
    }

    public interface OnQuestionItemClickListener {
        void onQuestionClick(int pos);
    }

    public ArrayList returnSelectList() {
        return mIsSelectedList;
    }
}