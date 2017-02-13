package com.iyuce.itoefl.UI.Listening.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/1/23
 */
public class TopListeneringPageAdapter extends RecyclerView.Adapter<TopListeneringPageAdapter.PageViewHolder> {

    private Context mContext;
    private ArrayList<String> mDataList;

    private OnPageItemClickListener mListener;

    public void setOnPageItemClickListener(OnPageItemClickListener listener) {
        mListener = listener;
    }

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
    public void onBindViewHolder(PageViewHolder holder, final int position) {
        holder.mTxtContent.setText(mDataList.get(position));
        if (position == mDataList.size() - 1) {
            holder.mImgProgress.setBackgroundResource(R.mipmap.icon_progress_normal_last);
        }
        if (position % 3 == 0) {
            holder.mTxtContentState.setText("精听次数 : 3");
            holder.mImgProgress.setBackgroundResource(R.mipmap.icon_progress_finish_center);
//            holder.mImgDownload.setBackgroundResource(R.mipmap.icon_download_finish);
            holder.mImgDownload.setVisibility(View.INVISIBLE);
            holder.mTxtPracticed.setVisibility(View.VISIBLE);
            holder.mTxtPracticed.setText("5/6");
        }
        if (position == 0) {
            holder.mImgDownload.setVisibility(View.VISIBLE);
            holder.mImgProgress.setBackgroundResource(R.mipmap.icon_progress_finish_first);
            holder.mTxtPracticed.setVisibility(View.INVISIBLE);
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
        ImageView mImgProgress, mImgDownload;

        public PageViewHolder(View itemView) {
            super(itemView);
            mTxtContent = (TextView) itemView.findViewById(R.id.txt_recycler_item_top_listenering_page_content);
            mTxtContentState = (TextView) itemView.findViewById(R.id.txt_recycler_item_top_listenering_page_state);
            mTxtDownload = (TextView) itemView.findViewById(R.id.txt_recycler_item_top_listenering_page_download);
            mTxtPracticed = (TextView) itemView.findViewById(R.id.txt_recycler_item_top_listenering_page_practiced);
            mImgProgress = (ImageView) itemView.findViewById(R.id.img_recycler_item_top_listenering_page_progress);
            mImgDownload = (ImageView) itemView.findViewById(R.id.img_recycler_item_top_listenering_page_download);
        }
    }

    public interface OnPageItemClickListener {
        void OnPageItemClick(int pos);
    }
}