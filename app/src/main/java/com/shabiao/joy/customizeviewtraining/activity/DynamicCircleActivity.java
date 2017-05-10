package com.shabiao.joy.customizeviewtraining.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;

import com.shabiao.joy.customizeviewtraining.R;
import com.shabiao.joy.customizeviewtraining.custiomizeView.DynamicCircleView;

public class DynamicCircleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_circle);
        DynamicCircleView circle = (DynamicCircleView)findViewById(R.id.circle);
        AppCompatButton start = (AppCompatButton)findViewById(R.id.start);
        start.setOnClickListener(v -> {
            circle.startMove();
        });
    }
}
