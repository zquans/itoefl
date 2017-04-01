package com.iyuce.itoefl.Control.Listening.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.Control.Listening.Activity.TopListeneringPageActivity;
import com.iyuce.itoefl.Model.Exercise.ListenModule;
import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/1/23
 */
public class TopListeneringModuleAdapter extends RecyclerView.Adapter<TopListeneringModuleAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<ListenModule> mList;

    public TopListeneringModuleAdapter(Context context, ArrayList<ListenModule> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_top_listenering_module, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        holder.mItemTxtTitle.setText(mList.get(position).name.replace("TPO", ""));
        if (!TextUtils.isEmpty(mList.get(position).practiced_count)
                && !TextUtils.isEmpty(mList.get(position).total_count)
                && Integer.parseInt(mList.get(position).practiced_count) > 0) {
            holder.mProgressBar.setVisibility(View.VISIBLE);
            holder.mProgressBar.setMax(Integer.parseInt(mList.get(position).total_count));
            holder.mProgressBar.setSecondaryProgress(Integer.parseInt(mList.get(position).total_count));
            holder.mProgressBar.setProgress(Integer.parseInt(mList.get(position).practiced_count));
        } else {
            holder.mProgressBar.setVisibility(View.GONE);
        }
        holder.mItemTxtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TopListeneringPageActivity.class);
                if (TextUtils.isEmpty(mList.get(position).code)) {
                    intent.putExtra("local_code", mList.get(position).name);
                    intent.putExtra("from_where", Constants.MODULE);
                } else {
                    intent.putExtra("local_code", mList.get(position).code);
                    intent.putExtra("from_where", Constants.Classify);
                }
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        ProgressBar mProgressBar;
        TextView mItemTxtTitle;

        public ItemViewHolder(View view) {
            super(view);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar_item_top_listenering_order);
            mItemTxtTitle = (TextView) view.findViewById(R.id.txt_item_top_listenering_order_title);
        }
    }
}