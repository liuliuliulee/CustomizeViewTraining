package com.shabiao.joy.customizeviewtraining.custiomizeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by joy on 2017/4/5.
 */

public class CanvasOperate extends View {
    private final static String TAG = "CanvasOperate";
    private int minWidth = 800, minHeight = 600;

    public CanvasOperate(Context context) {
        super(context);
    }

    public CanvasOperate(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasOperate(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        int measuredHeight = getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        //把原点移动到画布中心，画布的移动
        canvas.translate(measuredWidth/2,measuredHeight/2);

//        Rect rect = new Rect(0, -400, 400, 0);
//        canvas.drawRect(rect,paint);
        //画布缩放
//        canvas.scale(0.5f,-0.5f,200,0);
//        canvas.drawRect(rect,paint);


//        Rect rect = new Rect(-400,-400,400,400);
//        canvas.drawRect(rect,paint);
//        for(int i=0;i<300;i++){
//            canvas.scale(0.9f,0.9f);
//            canvas.drawRect(rect,paint);
//        }

        //旋转
        canvas.drawCircle(0,0,400,paint);
        canvas.drawCircle(0,0,380,paint);
        for(int i=0;i<360;i+=10){
            canvas.drawLine(0,380,0,400,paint);
            canvas.rotate(10);
        }

        //错切,值为tan angel,angle为倾斜的角度
//        canvas.skew(1,1);

        //保存画布状态及回滚画布状态
//        canvas.save();
        //具体操作
//        canvas.restore();


    }
}
