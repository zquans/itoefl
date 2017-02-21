package com.iyuce.itoefl.UI.Listening.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Activity.TopListeneringPageActivity;
import com.iyuce.itoefl.Utils.DbUtil;
import com.iyuce.itoefl.Utils.RecyclerItemClickListener;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by LeBang on 2017/1/23
 */
public class TopListeneringAdapter extends RecyclerView.Adapter<TopListeneringAdapter.MyViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ArrayList<String> mList;
    private ArrayList<String> dataList;
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
        //TODO 修改数据源，这里的数据源这么写是因为，我们没有一级目录,如："TPO31-40"
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.FILE_PATH_ITOEFL_EXERCISE
                + File.separator + Constants.SQLITE_TPO;

        SQLiteDatabase mDatabase = DbUtil.getHelper(mContext, path, Constants.DATABASE_VERSION).getWritableDatabase();
        dataList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER, null, Constants.PaperName);
        mDatabase.close();

        holder.mItemTxtTitle.setText(mList.get(position));
        holder.mItemRecyclerView.setLayoutManager(new GridLayoutManager(mContext, mItemCount));
        holder.mItemRecyclerView.setAdapter(new TopListeneringModuleAdapter(mContext, dataList));
        //如此出发可能会引发问题,重复打开界面
        holder.mItemRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, holder.mItemRecyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(mContext, TopListeneringPageActivity.class);
                        intent.putExtra("local_section", dataList.get(position));
                        mContext.startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                }));
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mItemTxtTitle;
        RecyclerView mItemRecyclerView;

        public MyViewHolder(View view) {
            super(view);
            mItemTxtTitle = (TextView) view.findViewById(R.id.txt_item_top_listenering_order_title);
            mItemRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_item_top_listenering_order);
        }
    }
}