package com.iyuce.itoefl.UI.Mine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.PreferenceUtil;
import com.iyuce.itoefl.Utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LeBang on 2016/9/23
 */
public class SuggestionActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTxtTitle;
    private EditText mEdtContent;
    private Button mBtnCommit;

    private String mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        initView();
    }

    private void initView() {
        mTxtTitle = (TextView) findViewById(R.id.txt_header_title_item);
        mTxtTitle.setText("意见反馈");
        findViewById(R.id.txt_header_title_menu).setVisibility(View.GONE);
        findViewById(R.id.imgbtn_header_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mEdtContent = (EditText) findViewById(R.id.edt_activity_suggestion);
        mBtnCommit = (Button) findViewById(R.id.btn_activity_suggestion_commit);
        mBtnCommit.setOnClickListener(this);
    }

    /**
     * 返回日期字符串
     */
    public String getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String nowtime = formatter.format(currentTime);
        return nowtime;
    }

    private void getJson() {
        HttpParams params = new HttpParams();
        params.put("user_id", PreferenceUtil.getSharePre(SuggestionActivity.this).getString("userId", "ANDROID_TOEFL"));
        params.put("createat", getNowDate());
        params.put("content", mContent);
        OkGo.post(Constants.URL_TO_SUGGESTION).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                LogUtil.e("suggest activty is here" + getNowDate());
                ToastUtil.showMessage(SuggestionActivity.this, "亲，提交成功啦，感谢您的宝贵意见");
                SuggestionActivity.this.finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_activity_suggestion_commit:
                mContent = mEdtContent.getText().toString();
                new AlertDialog.Builder(SuggestionActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("填好了")
                        .setNegativeButton("马上反馈", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getJson();
                            }
                        }).setPositiveButton("再想想", null).show();
                break;
        }
    }
}