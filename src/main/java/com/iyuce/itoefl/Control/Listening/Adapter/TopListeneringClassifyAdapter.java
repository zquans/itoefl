package com.iyuce.itoefl.Control.Listening.Adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuce.itoefl.Model.Exercise.ListenModule;
import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/1/23
 */
public class TopListeneringClassifyAdapter extends RecyclerView.Adapter<TopListeneringClassifyAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<String> mCategoryList = new ArrayList<>();
    private ArrayList<ListenModule> mModuleList = new ArrayList<>();

    public TopListeneringClassifyAdapter(Context context, ArrayList<String> list, ArrayList<ListenModule> moduleList) {
        this.mContext = context;
        this.mCategoryList = list;
        this.mModuleList = moduleList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_top_listenering_classify, parent, false));
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mItemTxtTitle.setText(mCategoryList.get(position));
        holder.mItemRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        holder.mItemRecyclerView.setAdapter(new TopListeneringModuleAdapter(mContext, mModuleList));
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mItemTxtTitle;
        RecyclerView mItemRecyclerView;

        public MyViewHolder(View view) {
            super(view);
            mItemTxtTitle = (TextView) view.findViewById(R.id.txt_item_top_listenering_classify_title);
            mItemRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_item_top_listenering_classify);
        }
    }
}