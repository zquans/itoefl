package com.iyuce.itoefl.Control.Vocabulary;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.Model.Vocabulary;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.DbUtil;
import com.iyuce.itoefl.Utils.SDCardUtil;

import java.io.File;
import java.util.ArrayList;

public class VocabularyActivity extends AppCompatActivity implements
        View.OnClickListener, AdapterView.OnItemClickListener, OnPageChangeListener {

    private PDFView mPdfView;

    private ListView mListView;

    private ArrayAdapter mAdapter;
    private ArrayList<String> mPageList = new ArrayList<>();

    //数据对象
    private Vocabulary mVocabulary;

    private boolean isVisiableListView = false;
    private boolean isFirst = true;

    //数据库路径
    private String downloaded_sql_path;
    //TODO 用数据库标识书签
    private int pdf_book_mark = 1;

    @Override
    public void onStop() {
        super.onStop();
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, downloaded_sql_path).getWritableDatabase();
        ContentValues mValues = new ContentValues();
        mValues.put(Constants.BookMark, pdf_book_mark);
        mDatabase.update(Constants.TABLE_PDF_DOWNLOAD, mValues, Constants.Title + " =? ", new String[]{mVocabulary.title});
        mDatabase.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        initView();
    }

    private void initView() {
        downloaded_sql_path = SDCardUtil.getVocabularyPath() + File.separator + Constants.SQLITE_DOWNLOAD;
        mVocabulary = (Vocabulary) getIntent().getSerializableExtra("Vocabulary");

        findViewById(R.id.imgbtn_header_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        TextView mTitle = (TextView) findViewById(R.id.txt_header_title_item);
        TextView mTxtShowListView = (TextView) findViewById(R.id.txt_header_title_menu);
        mTxtShowListView.setOnClickListener(this);
        mTxtShowListView.setText("目录");
        mTitle.setText(mVocabulary.title);
        mPdfView = (PDFView) findViewById(R.id.pdf_activity_vocabulary);
        mListView = (ListView) findViewById(R.id.list_activity_vocabulary);
        mListView.setOnItemClickListener(this);

        //获取书签，跳转指定页码
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, downloaded_sql_path).getWritableDatabase();
        String book_mark = DbUtil.queryToString(mDatabase, Constants.TABLE_PDF_DOWNLOAD, Constants.BookMark, Constants.Title, mVocabulary.title);
        mDatabase.close();
        book_mark = TextUtils.equals(Constants.NONE, book_mark) ? "1" : book_mark;
        showPage(Integer.parseInt(book_mark));
    }

    /**
     * 跳到对应页码
     */
    private void showPage(int page) {
        File file = new File(SDCardUtil.getVocabularyPath() + File.separator + mVocabulary.title + ".pdf");
        mPdfView.fromFile(file)
                .defaultPage(page)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .onPageChange(this)
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        //加载目录所需的数据
        if (isFirst) {
            for (int i = 0; i < pageCount; i++) {
                mPageList.add(i + 1 + "");
            }
            mAdapter = new ArrayAdapter(this, R.layout.item_list_vocabulary, mPageList);
            mListView.setAdapter(mAdapter);
            isFirst = false;
        }
        pdf_book_mark = page;
    }

    @Override
    public void onClick(View v) {
        //控制是否显示目录页码
        if (isVisiableListView)
            mListView.setVisibility(View.GONE);
        else
            mListView.setVisibility(View.VISIBLE);
        isVisiableListView = !isVisiableListView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showPage(position);
    }
}