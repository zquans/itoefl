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
    private ArrayList<String> mClassifyNameList = new ArrayList<>();
    private ArrayList<String> mClassifyCodeList = new ArrayList<>();
    private ArrayList<ListenModule> mClassifyList = new ArrayList<>();

    public TopListeneringClassifyAdapter(Context context, ArrayList<String> namelist, ArrayList<String> codelist, ArrayList<ListenModule> classifyList) {
        this.mContext = context;
        this.mClassifyNameList = namelist;
        this.mClassifyCodeList = codelist;
        this.mClassifyList = classifyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_top_listenering_classify, parent, false));
    }

    @Override
    public int getItemCount() {
        return mClassifyNameList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mItemTxtTitle.setText(mClassifyNameList.get(position));
        //过滤出对应的Module
        ArrayList<ListenModule> mModuleList = new ArrayList<>();
        for (int i = 0; i < mClassifyList.size(); i++) {
            if (mClassifyList.get(i).parent.equals(mClassifyCodeList.get(position))) {
                mModuleList.add(mClassifyList.get(i));
            }
        }
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