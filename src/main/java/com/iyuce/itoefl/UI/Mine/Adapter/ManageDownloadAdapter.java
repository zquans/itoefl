package com.iyuce.itoefl.UI.Mine.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuce.itoefl.Model.ManageDownload;
import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/3/15
 */
public class ManageDownloadAdapter extends RecyclerView.Adapter<ManageDownloadAdapter.MyViewHolder> {

    private ArrayList<ManageDownload> mList;
    private Context mContext;

    private OnDeleteDownLoadListener mListener;

    public void setOnDeleteDownLoadListener(OnDeleteDownLoadListener listener) {
        this.mListener = listener;
    }

    public ManageDownloadAdapter(Context mContext, ArrayList mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_manage_download, null, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.mTxtFileName.setText(mList.get(position).name);
        holder.mTxtFileSize.setText(mList.get(position).size);
        holder.mImgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtFileName, mTxtFileSize;
        ImageView mImgDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTxtFileName = (TextView) itemView.findViewById(R.id.txt_item_activity_manage_download_name);
            mTxtFileSize = (TextView) itemView.findViewById(R.id.txt_item_activity_manage_download_size);
            mImgDelete = (ImageView) itemView.findViewById(R.id.img_item_activity_manage_download_delete);
        }
    }

    public interface OnDeleteDownLoadListener {
        void OnDelete(int pos);
    }
}