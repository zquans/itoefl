package com.iyuce.itoefl.Control.Main;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.ToastUtil;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/3/17
 */
public class DisCreteScrollAdapter extends RecyclerView.Adapter<DisCreteScrollAdapter.ViewHolder> {

    private ArrayList<Integer> mDataList;
    private Context mContext;

    private OnScrollSelectListener mListener;

    public void setOnScrollSelectListener(OnScrollSelectListener listener) {
        this.mListener = listener;
    }

    public DisCreteScrollAdapter(ArrayList<Integer> mDataList, Context mContext) {
        this.mDataList = mDataList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.discrete_scroll_item_show_flip, null, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(mContext).load(mDataList.get(position)).into(holder.mImg);
        holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSelect(position);
                } else {
                    ToastUtil.showMessage(mContext, "暂未开放其余功能哦");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImg;
        CardView mCard;

        public ViewHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.img_fragment_show_flip);
            mCard = (CardView) itemView.findViewById(R.id.card_fragment_show_flip);
        }
    }

    public interface OnScrollSelectListener {
        void onSelect(int pos);
    }
}