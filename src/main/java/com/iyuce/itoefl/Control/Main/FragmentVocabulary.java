package com.iyuce.itoefl.Control.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuce.itoefl.BaseFragment;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.Control.Main.Adapter.VocabularyAdapter;
import com.iyuce.itoefl.Control.Vocabulary.VocabularyActivity;
import com.iyuce.itoefl.Model.Vocabulary;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.HttpUtil;
import com.iyuce.itoefl.Utils.Interface.Http.DownLoadInterface;
import com.iyuce.itoefl.Utils.Interface.Http.RequestInterface;
import com.iyuce.itoefl.Utils.ParseJsonUtil;
import com.iyuce.itoefl.Utils.SDCardUtil;
import com.iyuce.itoefl.Utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LeBang on 2017/1/22
 */
public class FragmentVocabulary extends BaseFragment implements VocabularyAdapter.VocabularyListener {

    private RecyclerView mRecyclerView;
    private VocabularyAdapter mAdapter;
    private ArrayList<Vocabulary> mVocabularyList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_vocabulary, container, false);

        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_fragment_vocabulary);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mAdapter = new VocabularyAdapter(getActivity(), mVocabularyList);
//        mAdapter.setOnVocabularyListener(this);
//        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        HttpUtil.get(Constants.URL_GET_PDF_BOOK, new RequestInterface() {
            @Override
            public void doSuccess(String result, Call call, Response response) {
                mVocabularyList.clear();
                mVocabularyList = ParseJsonUtil.parseVocabulary(result);
                mAdapter = new VocabularyAdapter(getActivity(), mVocabularyList);
                mAdapter.setOnVocabularyListener(FragmentVocabulary.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public void OnItemClick(int pos) {
        File file = new File(SDCardUtil.getVocabularyPath() + File.separator + mVocabularyList.get(pos).title + ".pdf");
        if (file.exists()) {
            Intent intent = new Intent(getActivity(), VocabularyActivity.class);
            intent.putExtra("Vocabulary", mVocabularyList.get(pos));
            startActivity(intent);
        } else {
            downLoad(pos);
        }
    }

    /**
     * 下载
     */
    private void downLoad(final int pos) {
        ToastUtil.showMessage(getActivity(), "您希望下载" + mVocabularyList.get(pos).title + ",请稍等");
        HttpUtil.downLoad(mVocabularyList.get(pos).path, SDCardUtil.getVocabularyPath(), mVocabularyList.get(pos).title + ".pdf", new DownLoadInterface() {
            @Override
            public void inProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                progressdialogcancel();
                TextView textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_item_vocabulary_download);
                textView.setText(((int) (progress * 100) + "%"));
            }

            @Override
            public void doSuccess(File file, Call call, Response response) {
                TextView textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_item_vocabulary_download);
                textView.setVisibility(View.GONE);
            }

            @Override
            public void onBefore() {
                progressdialogshow(getActivity());
            }

            @Override
            public void onAfter() {
            }
        });
    }
}