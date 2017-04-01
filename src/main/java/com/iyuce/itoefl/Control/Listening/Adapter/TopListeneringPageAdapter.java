package com.iyuce.itoefl.Control.Listening.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.Model.UserOprate;
import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/1/23
 */
public class TopListeneringPageAdapter extends RecyclerView.Adapter<TopListeneringPageAdapter.PageViewHolder> {

    private Context mContext;
    private ArrayList<UserOprate> mDataList;

    private OnPageItemClickListener mListener;

    public void setOnPageItemClickListener(OnPageItemClickListener listener) {
        mListener = listener;
    }

    public TopListeneringPageAdapter(Context context, ArrayList<UserOprate> list) {
        mContext = context;
        mDataList = list;
    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PageViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_top_listenering_page, parent, false));
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, final int position) {
        String replace_string;
        if (mDataList.get(position).module.contains("C")) {
            replace_string = mDataList.get(position).module.replace("C", "Conversation ");
        } else {
            replace_string = mDataList.get(position).module.replace("L", "Lecture ");
        }
        holder.mTxtContent.setText(mDataList.get(position).section + "\r" + replace_string);
        holder.mTxtContentState.setVisibility(View.VISIBLE);
        if (mDataList.get(position).download.equals(Constants.TRUE) && mDataList.get(position).practiced.equals(Constants.TRUE)) {
            holder.mImgDownloadReady.setVisibility(View.INVISIBLE);
            holder.mImgProgress.setBackgroundResource(R.mipmap.icon_progress_finish_center);
            holder.mTxtContentState.setText("查看练习记录");
        } else if (mDataList.get(position).download.equals(Constants.TRUE)) {
            holder.mImgDownloadReady.setVisibility(View.INVISIBLE);
            holder.mImgProgress.setBackgroundResource(R.mipmap.icon_progress_normal_center);
            holder.mTxtContentState.setText("未练习");
        } else {
            holder.mImgDownloadReady.setVisibility(View.VISIBLE);
            holder.mImgProgress.setBackgroundResource(R.mipmap.icon_progress_normal_center);
            holder.mTxtContentState.setText("未下载");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnPageItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class PageViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtContent, mTxtContentState, mTxtDownload, mTxtPracticed;
        ImageView mImgProgress, mImgDownload, mImgDownloadReady;

        public PageViewHolder(View itemView) {
            super(itemView);
            mTxtContent = (TextView) itemView.findViewById(R.id.txt_recycler_item_top_listenering_page_content);
            mTxtContentState = (TextView) itemView.findViewById(R.id.txt_recycler_item_top_listenering_page_state);
            mTxtDownload = (TextView) itemView.findViewById(R.id.txt_recycler_item_top_listenering_page_download);
            mTxtPracticed = (TextView) itemView.findViewById(R.id.txt_recycler_item_top_listenering_page_practiced);
            mImgProgress = (ImageView) itemView.findViewById(R.id.img_recycler_item_top_listenering_page_progress);
            mImgDownload = (ImageView) itemView.findViewById(R.id.img_recycler_item_top_listenering_page_download);
            mImgDownloadReady = (ImageView) itemView.findViewById(R.id.img_recycler_item_top_listenering_page_download_ready);
        }
    }

    public interface OnPageItemClickListener {
        void OnPageItemClick(int pos);
    }
}