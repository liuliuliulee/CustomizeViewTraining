package com.shabiao.joy.customizeviewtraining.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.shabiao.joy.customizeviewtraining.R;
import com.shabiao.joy.customizeviewtraining.custiomizeView.CircleSearchView;

public class CircleSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_search);
        CircleSearchView circleSearchView = (CircleSearchView)findViewById(R.id.circleSearch);

        findViewById(R.id.start).setOnClickListener(v -> {
            circleSearchView.startSearch();
        });

    }
}
