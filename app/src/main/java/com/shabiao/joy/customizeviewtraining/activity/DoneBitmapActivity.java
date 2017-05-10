package com.shabiao.joy.customizeviewtraining.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.shabiao.joy.customizeviewtraining.R;
import com.shabiao.joy.customizeviewtraining.custiomizeView.BitmapView;

public class DoneBitmapActivity extends AppCompatActivity {
    BitmapView bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);

        bitmap = (BitmapView) findViewById(R.id.bitmap);
        Button done = (Button) findViewById(R.id.done);
        Button undone = (Button) findViewById(R.id.undone);

        done.setOnClickListener(v -> bitmap.done());

        undone.setOnClickListener(v -> bitmap.unDone());
    }
}
