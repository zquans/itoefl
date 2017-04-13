package com.iyuce.itoefl.Control.Main.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iyuce.itoefl.Model.Vocabulary;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.FileUtil;
import com.iyuce.itoefl.Utils.SDCardUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by LeBang on 2017/4/5
 */
public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Vocabulary> mDataList = new ArrayList<>();

    private VocabularyListener mVocabularyListener;

    public void setOnVocabularyListener(VocabularyListener listener) {
        this.mVocabularyListener = listener;
    }

    public VocabularyAdapter(Context mContext, ArrayList<Vocabulary> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @Override
    public VocabularyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_vocabulary, parent, false));
    }

    @Override
    public void onBindViewHolder(VocabularyAdapter.MyViewHolder holder, final int position) {
        holder.mTxtTitle.setText(mDataList.get(position).title);
        holder.mTxtContent.setText(mDataList.get(position).description);
        holder.mTxtSize.setText(FileUtil.dealLength(mDataList.get(position).size));
//        holder.mTxtSize.setText(mDataList.get(position).size);
        Glide.with(mContext).load(mDataList.get(position).img).into(holder.mImgBook);

        File file = new File(SDCardUtil.getVocabularyPath() + File.separator + mDataList.get(position).title + ".pdf");
        if (file.exists())
            holder.mTxtDownLoad.setVisibility(View.GONE);
        else
            holder.mTxtDownLoad.setVisibility(View.VISIBLE);
        holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVocabularyListener != null) {
                    mVocabularyListener.OnItemClick(position);
                }
            }
        });
        holder.mCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mVocabularyListener != null) {
                    mVocabularyListener.OnItemLongClick(position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtTitle, mTxtContent, mTxtSize, mTxtDownLoad;
        ImageView mImgBook;
        CardView mCard;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTxtTitle = (TextView) itemView.findViewById(R.id.txt_item_vocabulary_title);
            mTxtContent = (TextView) itemView.findViewById(R.id.txt_item_vocabulary_content);
            mTxtSize = (TextView) itemView.findViewById(R.id.txt_item_vocabulary_size);
            mImgBook = (ImageView) itemView.findViewById(R.id.img_item_vocabulary);
            mTxtDownLoad = (TextView) itemView.findViewById(R.id.txt_item_vocabulary_download);
            mCard = (CardView) itemView.findViewById(R.id.card_item_vocabulary);
        }
    }

    public interface VocabularyListener {
        void OnItemClick(int pos);

        void OnItemLongClick(int pos);
    }
}