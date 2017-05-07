package com.shabiao.joy.customizeviewtraining.custiomizeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by joy on 2017/4/20.
 */
public class RadarView extends View {
    public final String TAG = "RADAR_VIEW";
    int minHeight = 300, minWidth = 300;

    int measureHeight, measureWidth;
    int centerX, centerY;
    int radius;
    int floorCount = 6;
    int interval;
    private float angle = (float) (Math.PI * 2 / floorCount);

    private Paint ordinaryLinePaint;
    private Paint textPaint;
    private Paint valuePaint;

    Path webPath;
    Path linePath;

    String[] src = {"攻击力", "防御力", "血量", "蓝量", "机动性", "操作难度"};
    float[] percents = {0.8f, 0.7f, 0.7f, 0.2f, 0.3f, 0.9f};

    public RadarView(Context context) {
        super(context);
        init();
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        Log.e("density", density + "");
        minHeight = (int) (minHeight * density);
        minWidth = (int) (minWidth * density);

        ordinaryLinePaint = new Paint();
        ordinaryLinePaint.setColor(Color.BLACK);
        ordinaryLinePaint.setStyle(Paint.Style.STROKE);
        ordinaryLinePaint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(50);

        valuePaint = new Paint();


        webPath = new Path();
        linePath = new Path();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST)
            widthSize = minWidth;
        if (heightMode == MeasureSpec.AT_MOST)
            heightSize = minHeight;

        int min = Math.min(widthSize, heightSize);
        setMeasuredDimension(min, min);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        measureWidth = w;
        measureHeight = h;
        radius = measureHeight / 2;
        centerX = measureWidth / 2;
        centerY = measureHeight / 2;

        interval = (int) ((radius - textPaint.measureText(src[0])) / (floorCount));

        Log.e(TAG, measureWidth + "," + measureHeight + "," + centerX + "," + centerY);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //绘制网线
        for (int i = 1; i < floorCount; i++) {
            float r = interval * i;
            webPath.reset();
            for (int j = 0; j < floorCount; j++) {
                if (j == 0) {
                    webPath.moveTo(centerX + r, centerY);
                } else {
                    float x = (float) (centerX + r * Math.cos((double) (angle * j)));
                    float y = (float) (centerY + r * Math.sin((double) (angle * j)));
                    webPath.lineTo(x, y);
                }
            }
            webPath.close();
            canvas.drawPath(webPath, ordinaryLinePaint);


        }

        //绘制直线
        for (int i = 0; i < floorCount; i++) {
            linePath.reset();
            linePath.moveTo(centerX, centerY);
            float x = (float) (centerX + (interval * (floorCount - 1)) * Math.cos((double) (angle * i)));
            float y = (float) (centerY + (interval * (floorCount - 1)) * Math.sin((double) (angle * i)));
            linePath.lineTo(x, y);
            canvas.drawPath(linePath, ordinaryLinePaint);
        }

        //绘制文本
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        Log.e(TAG, "top" + fontMetrics.top);
        Log.e(TAG, "bottom" + fontMetrics.bottom);
        Log.e(TAG, "ascent" + fontMetrics.ascent);
        Log.e(TAG, "descent " + fontMetrics.descent);
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
//        float top = fontMetrics.top;
//        float bottom = fontMetrics.bottom;
//        int baseLineY = (int) (rect.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
//        canvas.drawText("100%", rect.centerX(), baseLineY, paint);

        for (int i = 0; i < floorCount; i++) {
            float x = (float) (centerX + ((interval * (floorCount - 1)) + fontHeight / 2) * Math.cos(angle * i));
            float y = (float) (centerY + ((interval * (floorCount - 1)) + fontHeight / 2) * Math.sin(angle * i));
            float baseLine = y - (fontMetrics.ascent + fontMetrics.descent) / 2f;
//            if (angle * i >= 0 && angle * i <= Math.PI / 2) {//第4象限
//                float textWidth = textPaint.measureText(src[i]);//文本长度
//                if(angle*i==0){
//                    canvas.drawText(src[i], x+textWidth/2, baseLine, textPaint);
//                }else {
//                    canvas.drawText(src[i], x, baseLine, textPaint);
//                }
//            } else if (angle * i >= 3 * Math.PI / 2 && angle * i <= Math.PI * 2) {//第3象限
//                canvas.drawText(src[i], x, baseLine, textPaint);
//            } else if (angle * i > Math.PI / 2 && angle * i <= Math.PI) {//第2象限
//                float dis = textPaint.measureText(src[i]);//文本长度
//                canvas.drawText(src[i], x, baseLine, textPaint);
//            } else if (angle * i >= Math.PI && angle * i < 3 * Math.PI / 2) {//第1象限
////                float dis = textPaint.measureText(src[i]);//文本长度
//                canvas.drawText(src[i], x, baseLine, textPaint);
//            }
            float realAngle = angle * i;
            Log.e(TAG, "realAngle:" + realAngle + "," + i);

            if (realAngle >= 0 && realAngle <= Math.PI / 2) {
                if (realAngle == 0) {
                    float textWidth = textPaint.measureText(src[i]);//文本长度
                    canvas.drawText(src[i], x + textWidth / 2, baseLine, textPaint);
                } else {
                    canvas.drawText(src[i], x, baseLine, textPaint);
                }
            } else if (realAngle > Math.PI / 2 && realAngle <= Math.PI) {
                canvas.drawText(src[i], x, baseLine, textPaint);
            } else if (realAngle > Math.PI && realAngle <= 3 * Math.PI / 2) {
                if (i == 3) {
                    float textWidth = textPaint.measureText(src[i]);//文本长度
                    canvas.drawText(src[i], x - textWidth / 2, baseLine, textPaint);
                } else {
                    canvas.drawText(src[i], x, baseLine, textPaint);
                }
            } else if (realAngle > 3 * Math.PI / 2 && realAngle < 2 * Math.PI) {
                canvas.drawText(src[i], x, baseLine, textPaint);
            }
        }

        //绘制区域
        Path path = new Path();
        valuePaint.setAlpha(255);
        valuePaint.setColor(Color.BLUE);
        for (int i = 0; i < floorCount; i++) {
            float x = (float) (centerX + percents[i] * (interval * (floorCount - 1)) * Math.cos((double) (angle * i)));
            float y = (float) (centerY + percents[i] * (interval * (floorCount - 1)) * Math.sin((double) (angle * i)));
            if (i == 0) {
                path.moveTo(x, centerY);
            } else {
                path.lineTo(x, y);
            }
            //绘制小圆点
            canvas.drawCircle(x, y, 10, valuePaint);
        }
        path.close();
//        valuePaint.setStyle(Paint.Style.STROKE);
//        canvas.drawPath(path, valuePaint);
        valuePaint.setAlpha(127);
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, valuePaint);

    }
}
