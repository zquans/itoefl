package com.iyuce.itoefl.Control.Listening.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.iyuce.itoefl.Model.Exercise.QuestionNest;
import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/2/9
 */
public class QuestionNestAdapter extends RecyclerView.Adapter<QuestionNestAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<QuestionNest> mDataList;

    private ArrayList<Boolean> mIsSelectedList;

    private OnQuestionItemClickListener mListener;

    public void setOnQuestionItemClickListener(OnQuestionItemClickListener listener) {
        mListener = listener;
    }

    public QuestionNestAdapter(Context mContext, ArrayList<QuestionNest> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        mIsSelectedList = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {
            mIsSelectedList.add(null);
        }
    }

    @Override
    public QuestionNestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_do_question_nest, parent, false));
    }

    @Override
    public void onBindViewHolder(final QuestionNestAdapter.MyViewHolder holder, final int position) {
        holder.mTxtQuestion.setText(mDataList.get(position).content);
        holder.mRadiobtnYes.setText(mDataList.get(position).options.get(0));
        holder.mRadiobtnNull.setText(mDataList.get(position).options.get(1));
        holder.mRadiobtnNo.setText(mDataList.get(position).options.get(2));
        holder.mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_item_fragment_do_question_yes) {
                    mIsSelectedList.set(position, true);
//                    int radioButtonId = group.getCheckedRadioButtonId();
//                    RadioButton radiobtn = (RadioButton) holder.itemView.findViewById(checkedId);
//                    String radioButtonLabel = radiobtn.getText().toString();
//                    LogUtil.i("yes = " + radioButtonLabel);
//                    radiobtn.setText(mDataList.get(position).content);
                } else if (checkedId == R.id.radio_item_fragment_do_question_no) {
                    mIsSelectedList.set(position, false);
                } else {
                    mIsSelectedList.set(position, null);
                }
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
        RadioButton mRadiobtnYes, mRadiobtnNo, mRadiobtnNull;
        TextView mTxtQuestion;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTxtQuestion = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_question);
            mRadioGroup = (RadioGroup) itemView.findViewById(R.id.radio_group_item_fragment_do_question);
            mRadiobtnYes = (RadioButton) itemView.findViewById(R.id.radio_item_fragment_do_question_yes);
            mRadiobtnNo = (RadioButton) itemView.findViewById(R.id.radio_item_fragment_do_question_no);
            mRadiobtnNull = (RadioButton) itemView.findViewById(R.id.radio_item_fragment_do_question_middle);
        }
    }

    public interface OnQuestionItemClickListener {
        void onQuestionClick(int pos);
    }

    public ArrayList returnSelectList() {
        return mIsSelectedList;
    }
}