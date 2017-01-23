package com.iyuce.itoefl.UI.Listening.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Activity.TopListeneringPageActivity;
import com.iyuce.itoefl.Utils.RecyclerItemClickListener;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/1/23
 */
public class TopListeneringAdapter extends RecyclerView.Adapter<TopListeneringAdapter.MyViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ArrayList<String> mList;
    private int mItemCount;

    public TopListeneringAdapter(Context context, ArrayList<String> list, int item_count) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mList = list;
        mItemCount = item_count;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mLayoutInflater.inflate(R.layout.recycler_item_top_listenering_order, parent, false));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mItemTxtTitle.setText(mList.get(position));
        holder.mItemTxtTitle.setBackgroundColor(Color.parseColor("#fafafa"));
        holder.mItemTxtTitle.setGravity(Gravity.LEFT);
        holder.mItemRecyclerView.setLayoutManager(new GridLayoutManager(mContext, mItemCount));
        holder.mItemRecyclerView.setAdapter(new RecyclerItemAdapter(mContext, mList));
        holder.mItemRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, holder.mItemRecyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mContext.startActivity(new Intent(mContext, TopListeneringPageActivity.class));
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                }));
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        Button mItemTxtTitle;
        RecyclerView mItemRecyclerView;

        public MyViewHolder(View view) {
            super(view);
            mItemTxtTitle = (Button) view.findViewById(R.id.txt_item_top_listenering_order_title);
            mItemRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_item_top_listenering_order);
        }
    }
}