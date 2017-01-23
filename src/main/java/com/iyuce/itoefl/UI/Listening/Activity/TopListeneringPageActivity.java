package com.iyuce.itoefl.UI.Listening.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.TopListeneringPageAdapter;

import java.util.ArrayList;

public class TopListeneringPageActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_listenering_page);

        initView();
    }

    private void initView() {
        //back button in <include header>
        findViewById(R.id.imgbtn_header_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_activity_top_listenering_page);
        for (int i = 0; i < 7; i++) {
            mDataList.add("Lecture " + i);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new TopListeneringPageAdapter(this, mDataList));
    }
}