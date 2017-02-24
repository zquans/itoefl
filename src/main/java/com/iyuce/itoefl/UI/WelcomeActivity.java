package com.iyuce.itoefl.UI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Main.MainActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_welcome);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toMain();
            }
        }, 1000);
    }

    private void toMain() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
        WelcomeActivity.this.finish();
    }
}