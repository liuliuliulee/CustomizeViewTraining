package com.shabiao.joy.customizeviewtraining.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;

import com.shabiao.joy.customizeviewtraining.R;
import com.shabiao.joy.customizeviewtraining.custiomizeView.LoadingView;

public class LoadingActivity extends AppCompatActivity {

    Handler handler;
    LoadingView loadingView;
    AppCompatButton btnStart;
    int progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

//        DisplayMetrics metric = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metric);

        loadingView = (LoadingView)findViewById(R.id.loading_view);
        btnStart = (AppCompatButton)findViewById(R.id.btn_start);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
//                Log.e("aaa",progress+"");
                loadingView.setProgress(++progress);
                if(progress>=100)
                    return;
                handler.sendEmptyMessageDelayed(0,100);
            }
        };

        btnStart.setOnClickListener(v -> {
            progress=0;
            loadingView.setProgress(0);
            handler.sendEmptyMessageDelayed(0,0);
        });

    }
}
