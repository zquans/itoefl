package com.iyuce.itoefl.UI.Listening.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/1/23
 */
public class RecyclerItemAdapter extends RecyclerView.Adapter<RecyclerItemAdapter.ItemViewHolder> {

    //private Context mContext;
    private ArrayList<String> mList;
    private LayoutInflater mLayoutInflater;

    public RecyclerItemAdapter(Context context, ArrayList<String> list) {
        //mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mList = list;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(mLayoutInflater
                .inflate(R.layout.recycler_item_top_listenering_order, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.mItemTxtTitle.setBackgroundColor(Color.parseColor("#ffffff"));
        holder.mItemTxtTitle.setTextColor(Color.parseColor("#000000"));
        holder.mItemTxtTitle.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView mItemTxtTitle;

        public ItemViewHolder(View view) {
            super(view);
            mItemTxtTitle = (TextView) view.findViewById(R.id.txt_item_top_listenering_order_title);
        }
    }
}