package com.iyuce.itoefl.Control.Listening.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.iyuce.itoefl.Common.EnumNest;
import com.iyuce.itoefl.Model.Exercise.QuestionNest;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/2/9
 */
public class QuestionNestAdapter extends RecyclerView.Adapter<QuestionNestAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<QuestionNest> mDataList;

    private ArrayList<EnumNest.NestSelect> mIsSelectedList;

    private OnQuestionItemClickListener mListener;

    public void setOnQuestionItemClickListener(OnQuestionItemClickListener listener) {
        mListener = listener;
    }

    public QuestionNestAdapter(Context mContext, ArrayList<QuestionNest> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        mIsSelectedList = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {
            mIsSelectedList.add(EnumNest.NestSelect.None);
        }
        LogUtil.i(mIsSelectedList.toString());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_do_question_nest, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.mTxtQuestion.setText(mDataList.get(position).content);

        int length_option = mDataList.get(position).options.size();
        if (length_option == 3) {
            holder.mRadiobtnA.setText(mDataList.get(position).options.get(0));
            holder.mRadiobtnB.setText(mDataList.get(position).options.get(1));
            holder.mRadiobtnC.setText(mDataList.get(position).options.get(2));
            holder.mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.radio_item_fragment_do_question_a) {
                        mIsSelectedList.set(position, EnumNest.NestSelect.A);
//                    int radioButtonId = group.getCheckedRadioButtonId();
//                    RadioButton radiobtn = (RadioButton) holder.itemView.findViewById(checkedId);
//                    String radioButtonLabel = radiobtn.getText().toString();
//                    LogUtil.i("yes = " + radioButtonLabel);
//                    radiobtn.setText(mDataList.get(position).content);
                    } else if (checkedId == R.id.radio_item_fragment_do_question_b) {
                        mIsSelectedList.set(position, EnumNest.NestSelect.B);
                    } else {
                        mIsSelectedList.set(position, EnumNest.NestSelect.C);
                    }
                    //传递数据,所需的数据，仅仅是position而已，若只涉及到自身view,不要把表现交给外面,自己控制
                    mListener.onQuestionClick(position);
                }
            });
        } else {
            holder.mRadiobtnC.setVisibility(View.GONE);
            holder.mRadiobtnA.setText(mDataList.get(position).options.get(0));
            holder.mRadiobtnB.setText(mDataList.get(position).options.get(1));
            holder.mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.radio_item_fragment_do_question_a) {
                        mIsSelectedList.set(position, EnumNest.NestSelect.A);
                    } else if (checkedId == R.id.radio_item_fragment_do_question_b) {
                        mIsSelectedList.set(position, EnumNest.NestSelect.B);
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
        RadioButton mRadiobtnA, mRadiobtnB, mRadiobtnC;
        TextView mTxtQuestion;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTxtQuestion = (TextView) itemView.findViewById(R.id.txt_item_fragment_do_question);
            mRadioGroup = (RadioGroup) itemView.findViewById(R.id.radio_group_item_fragment_do_question);
            mRadiobtnA = (RadioButton) itemView.findViewById(R.id.radio_item_fragment_do_question_a);
            mRadiobtnB = (RadioButton) itemView.findViewById(R.id.radio_item_fragment_do_question_b);
            mRadiobtnC = (RadioButton) itemView.findViewById(R.id.radio_item_fragment_do_question_c);
        }
    }

    public interface OnQuestionItemClickListener {
        void onQuestionClick(int pos);
    }

    public ArrayList returnSelectList() {
        return mIsSelectedList;
    }
}