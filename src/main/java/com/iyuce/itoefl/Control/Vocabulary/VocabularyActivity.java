package com.iyuce.itoefl.Control.Vocabulary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.iyuce.itoefl.Model.Vocabulary;
import com.iyuce.itoefl.R;

public class VocabularyActivity extends AppCompatActivity {

    private Vocabulary mVocabulary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        initView();
    }

    private void initView() {
        mVocabulary = (Vocabulary) getIntent().getSerializableExtra("Vocabulary");
        findViewById(R.id.imgbtn_header_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.txt_header_title_menu).setVisibility(View.GONE);
        TextView mTitle = (TextView) findViewById(R.id.txt_header_title_item);
        mTitle.setText(mVocabulary.title);
    }
}