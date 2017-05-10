package com.shabiao.joy.customizeviewtraining.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.shabiao.joy.customizeviewtraining.R;
import com.shabiao.joy.customizeviewtraining.custiomizeView.ZoomImageView;

public class ZoomImageViewActivity extends AppCompatActivity {

    private int[] mImgs = new int[]{R.mipmap.food, R.mipmap.song, R.mipmap.min};
    private ZoomImageView[] mImageViews = new ZoomImageView[mImgs.length];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image_view);

        ViewPager viewpager = (ViewPager) findViewById(R.id.view_pager);
        viewpager.setAdapter(new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ZoomImageView imageView = new ZoomImageView(getApplicationContext());
                imageView.setImageResource(mImgs[position]);
                container.addView(imageView);
                mImageViews[position] = imageView;
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mImageViews[position]);
            }

            @Override
            public int getCount() {
                return mImgs.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }

        });
    }
}
