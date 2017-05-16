package com.shabiao.joy.customizeviewtraining.viewPageTranfrom;

import android.support.v4.view.ViewPager;
import android.view.View;

public class RotatePageTransformer implements ViewPager.PageTransformer {
    //    private static final float MIN_SCALE = 0.75f;
    private static final float MAX_ROTATE = 20.0f;

    public void transformPage(View view, float position) {
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            //屏幕看不到的左页
            view.setAlpha(0);
            view.setRotation(0);
        } else if (position <= 0) { // [-1,0]
            //左页移动
            view.setPivotX(view.getMeasuredWidth()/2.0f);
            view.setPivotY(view.getMeasuredHeight());
            view.setRotation(position * MAX_ROTATE);
            view.setAlpha(1 + position);
        } else if (position <= 1) { // (0,1]
            view.setPivotX(view.getMeasuredWidth()/2.0f);
            view.setPivotY(view.getMeasuredHeight());
            view.setRotation(position * MAX_ROTATE);
            view.setAlpha(1 - position);
            //右页移动
        } else { // (1,+Infinity]
            //屏幕看不到的右页
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }
}