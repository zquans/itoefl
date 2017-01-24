package com.iyuce.itoefl.UI.Listening.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Activity.PageReadyActivity;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/1/23
 */

//TODO BaseAdapter 引用泛型
public class TopListeneringPageAdapter extends RecyclerView.Adapter<TopListeneringPageAdapter.PageViewHolder> {

    private Context mContext;
    private ArrayList<String> mDataList;

    public TopListeneringPageAdapter(Context context, ArrayList<String> list) {
        mContext = context;
        mDataList = list;
    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PageViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_top_listenering_page, parent, false));
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, int position) {
        holder.mTxtContent.setText(mDataList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, PageReadyActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class PageViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtContent;

        public PageViewHolder(View itemView) {
            super(itemView);
            mTxtContent = (TextView) itemView.findViewById(R.id.txt_recycler_item_top_listenering_page_content);
        }
    }
}
