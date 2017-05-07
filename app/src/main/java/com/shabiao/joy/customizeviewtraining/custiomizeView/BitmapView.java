package com.shabiao.joy.customizeviewtraining.custiomizeView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.shabiao.joy.customizeviewtraining.R;

/**
 * Created by joy on 2017/4/6.
 *
 * 简介：通过对一张图片的截取实现动画，节省资源
 */

public class BitmapView extends View {

    private final static String TAG = "BitmapView";
    private int minWidth = 800, minHeight = 600;
    private Bitmap bitmap;
    private RectF dst;
    private Rect src;

    private static final int ANIM_NULL = 0;         //动画状态-没有
    private static final int ANIM_CHECK = 1;        //动画状态-开启
    private static final int ANIM_UNCHECK = 2;      //动画状态-结束

    private Handler mHandler;           // handler
    private int animState = ANIM_NULL;  // 动画状态
    private int curPage;
    private int totalPage = 20;
    private int animTime = 1000;//单位毫秒
    private float perWidth;
    float bitmapWidth,bitmapHeight;

    public BitmapView(Context context) {
        super(context);
        initBitmap();
    }

    public BitmapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initBitmap();
    }

    public BitmapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBitmap();
    }

    public void initBitmap(){
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.done);
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();
        perWidth = bitmapWidth/totalPage;
//        dst = new Rect(-activity_bitmap.getWidth()/2,-activity_bitmap.getHeight()/2,activity_bitmap.getWidth()/2,activity_bitmap.getHeight()/2);
//        src = new Rect(0,0,activity_bitmap.getWidth(),activity_bitmap.getHeight());
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                if(animState==ANIM_CHECK){
                    if(curPage==totalPage){
                        animState = ANIM_NULL;
                    }
                }else if(animState==ANIM_UNCHECK){
                    if(curPage==0){
                        animState = ANIM_NULL;
                    }
                }
                invalidate();
                if(animState==ANIM_NULL)
                    return;
                sendEmptyMessageDelayed(0,animTime/totalPage);
                if(animState==ANIM_CHECK){
                    curPage++;
                }else if(animState==ANIM_UNCHECK){
                    curPage--;
                }
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {

        } else {
            //默认最小宽度
            widthSize = minWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {

        } else {
            //默认最小高度
            heightSize = minHeight;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        if(curPage==totalPage||curPage==0){
//            animState=ANIM_NULL;
//        }
        canvas.translate(getMeasuredWidth()/2,getMeasuredHeight()/2);
//        canvas.drawBitmap(activity_bitmap,src,dst,null);
//        int start = -activity_bitmap.getWidth() / 2;
//        int end = activity_bitmap.getWidth()/2;
//        for(int i=start;i<end;i++){
//            dst = new Rect(-activity_bitmap.getWidth()/2,-activity_bitmap.getHeight()/2,i,activity_bitmap.getHeight()/2);
//            src = new Rect(0,0,i+activity_bitmap.getWidth()/2,activity_bitmap.getHeight());
//            canvas.drawBitmap(activity_bitmap,src,dst,null);
//        }
        dst = new RectF(-bitmapWidth/2,-bitmapHeight/2,perWidth*curPage-bitmapWidth/2,bitmapHeight/2);
        src = new Rect(0,0,(int)perWidth*curPage,(int)bitmapHeight);

        canvas.drawBitmap(bitmap,src,dst,null);
    }

    public void done(){
        if(animState!=ANIM_NULL)
            return;
        animState=ANIM_CHECK;
        curPage=0;
        mHandler.sendEmptyMessageDelayed(0,0);

    }

    public void unDone(){
        if(animState!=ANIM_NULL)
            return;
        animState=ANIM_UNCHECK;
        curPage=totalPage;
        mHandler.sendEmptyMessageDelayed(0,0);
    }
}
