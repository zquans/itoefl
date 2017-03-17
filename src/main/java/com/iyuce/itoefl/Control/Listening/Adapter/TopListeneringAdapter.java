//package com.iyuce.itoefl.UI.Listening.Adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.database.sqlite.SQLiteDatabase;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.iyuce.itoefl.Common.Constants;
//import com.iyuce.itoefl.Model.Exercise.ListenModule;
//import com.iyuce.itoefl.R;
//import com.iyuce.itoefl.UI.Listening.Activity.TopListeneringPageActivity;
//import com.iyuce.itoefl.Utils.DbUtil;
//import com.iyuce.itoefl.Utils.LogUtil;
//import com.iyuce.itoefl.Utils.RecyclerItemClickListener;
//import com.iyuce.itoefl.Utils.SDCardUtil;
//import com.iyuce.itoefl.Utils.ToastUtil;
//
//import java.io.File;
//import java.util.ArrayList;
//
///**
// * Created by LeBang on 2017/1/23
// */
//public class TopListeneringAdapter extends RecyclerView.Adapter<TopListeneringAdapter.MyViewHolder> {
//
//    private LayoutInflater mLayoutInflater;
//    private Context mContext;
////    private ArrayList<String> mList;
////    private ArrayList<String> nameList = new ArrayList<>();
//    private ArrayList<ListenModule> mModuleeList = new ArrayList<>();
//    private int mItemCount;
//
//    public TopListeneringAdapter(Context context, ArrayList<ListenModule> list, int item_count) {
//        mContext = context;
//        mLayoutInflater = LayoutInflater.from(context);
//        mModuleeList = list;
//        mItemCount = item_count;
//    }
//
//    @Override
//    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new MyViewHolder(mLayoutInflater.inflate(R.layout.recycler_item_top_listenering_order, parent, false));
//    }
//
//    @Override
//    public int getItemCount() {
//        return mModuleeList.size();
//    }
//
//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//        //TODO 修改数据源，这里的数据源这么写是因为，我们没有一级目录,如："TPO31-40"
////        String path = SDCardUtil.getExercisePath();
////        String tpo_path = path + File.separator + Constants.SQLITE_TPO;
////
////        SQLiteDatabase mDatabase = DbUtil.getHelper(mContext, tpo_path).getWritableDatabase();
////        //从默认主表中查，是否有这张表，其实还应该放在Main中去做
////        String isNone_Paper = DbUtil.queryToString(mDatabase, Constants.TABLE_SQLITE_MASTER, Constants.NAME, Constants.TABLE_NAME, Constants.TABLE_PAPER);
////        if (TextUtils.equals(isNone_Paper, Constants.NONE)) {
////            ToastUtil.showMessage(mContext, "网络不佳，未获取到数据,请重试");
////
////            mDatabase.close();
////            return;
////        }
////        nameList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER, null, Constants.PaperName);
////        mDatabase.close();
//
////        ListenModule mListenModule;
////        for (int i = 0; i < nameList.size(); i++) {
////            mListenModule = new ListenModule();
////            mListenModule.name = nameList.get(i);
////
////            String practiced_path = path + File.separator + Constants.SQLITE_DOWNLOAD;
////            SQLiteDatabase mDatabaseDownload = DbUtil.getHelper(mContext, practiced_path).getWritableDatabase();
////            String isNone_Download = DbUtil.queryToString(mDatabaseDownload, Constants.TABLE_SQLITE_MASTER, Constants.NAME, Constants.TABLE_NAME, Constants.TABLE_ALREADY_DOWNLOAD);
////            if (!TextUtils.equals(isNone_Download, Constants.NONE)) {
////                String practiced_count_sql = "SELECT COUNT(Module) FROM downloaded_table WHERE Section =? and Practiced =?";
////                String total_count_sql = "SELECT COUNT(Module) FROM downloaded_table WHERE Section =? ";
////                String practiced_count = DbUtil.cursorToString(mDatabaseDownload.rawQuery(practiced_count_sql, new String[]{"TPO18", "true"}));
////                String total_count = DbUtil.cursorToString(mDatabaseDownload.rawQuery(total_count_sql, new String[]{"TPO18"}));
////
////                mListenModule.practiced_count = practiced_count;
////                mListenModule.total_count = total_count;
////                LogUtil.i("count = " + practiced_count + "/" + total_count);
////            }
////            mDatabaseDownload.close();
//
////            mModuleeList.add(mListenModule);
////        }
//
//        holder.mItemTxtTitle.setText(mModuleeList.get(position).name);
//        holder.mItemRecyclerView.setLayoutManager(new GridLayoutManager(mContext, mItemCount));
//        holder.mItemRecyclerView.setAdapter(new TopListeneringModuleAdapter(mContext, mModuleeList));
//        //如此触发可能会引发问题,重复打开界面
//        holder.mItemRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, holder.mItemRecyclerView,
//                new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        Intent intent = new Intent(mContext, TopListeneringPageActivity.class);
//                        intent.putExtra("local_section", mModuleeList.get(position).name);
//                        mContext.startActivity(intent);
//                    }
//
//                    @Override
//                    public void onItemLongClick(View view, int position) {
//
//                    }
//                }));
//    }
//
//    class MyViewHolder extends RecyclerView.ViewHolder {
//        TextView mItemTxtTitle;
//        RecyclerView mItemRecyclerView;
//
//        public MyViewHolder(View view) {
//            super(view);
//            mItemTxtTitle = (TextView) view.findViewById(R.id.txt_item_top_listenering_order_title);
//            mItemRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_item_top_listenering_order);
//        }
//    }
//}