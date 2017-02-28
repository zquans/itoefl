package com.iyuce.itoefl.UI.Listening.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<String> mDataList;
    private String mQuestionType;

    //构造函数中给“多选题”答案集留的数组
    private ArrayList<Boolean> mIsSelectedList;

    private OnQuestionItemClickListener mListener;

    public void setOnQuestionItemClickListener(OnQuestionItemClickListener listener) {
        mListener = listener;
    }

    public QuestionAdapter(Context mContext, ArrayList<String> mDataList, String mQuestionType) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mQuestionType = mQuestionType;
        mIsSelectedList = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {
            mIsSelectedList.add(false);
        }
    }

    @Override
    public QuestionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //判断题
        if (TextUtils.equals(mQuestionType, "JUDGE")) {
            return new MyViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.recycler_item_do_question_judge, parent, false));
        }
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_do_question, parent, false));
    }

    @Override
    public void onBindViewHolder(final QuestionAdapter.MyViewHolder holder, final int position) {
        holder.mTxtQuestion.setText(mDataList.get(position));
        //判断题
        if (TextUtils.equals(mQuestionType, "JUDGE")) {
            holder.mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.radio_item_fragment_do_question_yes) {
                        mIsSelectedList.set(position, true);
                    } else {
                        mIsSelectedList.set(position, false);
                    }
                    //传递数据,所需的数据，仅仅是position而已，若只涉及到自身view,不要把表现交给外面,自己控制
                    mListener.onQuestionClick(position);
                }
            });
        } else {
            //单选和多选
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //多选
                    if (TextUtils.equals(mQuestionType, "MULTI")) {
                        //修改表现
                        if (mIsSelectedList.get(position)) {
                            holder.mTxtQuestion.setBackgroundColor(Color.parseColor("#ffffff"));
                        } else {
                            holder.mTxtQuestion.setBackgroundResource(R.drawable.view_bound_orange_stroke);
                        }
                        //反转boolean状态
                        mIsSelectedList.set(position, !mIsSelectedList.get(position));
                    }
                    //传递数据,所需的数据，仅仅是position而已，若只涉及到自身view,不要把表现交给外面,自己控制
                    mListener.onQuestionClick(position);
                }
            });
        }
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
            //判断题型
            if (TextUtils.equals(mQuestionType, "JUDGE")) {
                mRadioGroup = (RadioGroup) itemView.findViewById(R.id.radio_group_item_fragment_do_question);
            }
        }
    }

    public interface OnQuestionItemClickListener {
        void onQuestionClick(int pos);
    }

    public ArrayList returnSelectList() {
        return mIsSelectedList;
    }
}