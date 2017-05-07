package com.shabiao.joy.customizeviewtraining.custiomizeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by joy on 2017/4/24.
 */

public class DynamicCircleView extends View {
    public final String TAG = "DYNAMIC_CIRCLE_VIEW";
    int minHeight = 300, minWidth = 300;
    int mWidth, mHeight;
    private Paint paint, pointPaint;
    private float blackMagic = 0.551915024494f;
    private Path mPath;
    private float c;
    float density;
    private float radius = 30;
    //偏移值
    private float distance = 20;
    private float distance1 = 15;

    HorizontalPoint leftHorizontalPoint, rightHorizontalPoint;
    VerticalPoint upVerticalPoint, downVerticalPoint;
    PointF cPoint;
    //左右点偏移速度
    float v1;
    float v4;
    //圆心移动速度
    float v2;
    float v3;

    int loadingStatus = 0;

    public DynamicCircleView(Context context) {
        super(context);
        init();
    }

    public DynamicCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DynamicCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        density = dm.density;
        Log.e("density", density + "");
        minHeight = (int) (minHeight * density);
        minWidth = (int) (minWidth * density);
        radius = radius * density;
        distance = distance * density;
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setStrokeWidth(10);
        pointPaint.setColor(Color.RED);
        pointPaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();

        c = radius * blackMagic;

        cPoint = new PointF();
        cPoint.x = radius + 100f;
        cPoint.y = radius + 100f;

        leftHorizontalPoint = new HorizontalPoint(c);
        rightHorizontalPoint = new HorizontalPoint(c);
        upVerticalPoint = new VerticalPoint(c);
        downVerticalPoint = new VerticalPoint(c);

        v1 = distance / 600f;
        v4 = distance1 / 600f;
        v2 = 8 * distance / 800f;
        v3 = 8f / 600f;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
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

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        cPoint.x = event.getX();
        cPoint.y = event.getY();
        postInvalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (loadingStatus == 0) {
            rightHorizontalPoint.x = cPoint.x + radius;
            rightHorizontalPoint.y = cPoint.y;
            leftHorizontalPoint.x = cPoint.x - radius;
            leftHorizontalPoint.y = cPoint.y;

            downVerticalPoint.x = cPoint.x;
            downVerticalPoint.y = cPoint.y + radius;
            upVerticalPoint.x = cPoint.x;
            upVerticalPoint.y = cPoint.y - radius;

            rightHorizontalPoint.calculate();
            leftHorizontalPoint.calculate();
            downVerticalPoint.calculate();
            upVerticalPoint.calculate();

            mPath.reset();
            mPath.moveTo(downVerticalPoint.x, downVerticalPoint.y);
            mPath.cubicTo(downVerticalPoint.rightPoint.x, downVerticalPoint.rightPoint.y, rightHorizontalPoint.downPoint.x, rightHorizontalPoint.downPoint.y,
                    rightHorizontalPoint.x, rightHorizontalPoint.y);
            mPath.cubicTo(rightHorizontalPoint.upPoint.x, rightHorizontalPoint.upPoint.y, upVerticalPoint.rightPoint.x, upVerticalPoint.rightPoint.y,
                    upVerticalPoint.x, upVerticalPoint.y);
            mPath.cubicTo(upVerticalPoint.leftPoint.x, upVerticalPoint.leftPoint.y, leftHorizontalPoint.upPoint.x, leftHorizontalPoint.upPoint.y,
                    leftHorizontalPoint.x, leftHorizontalPoint.y);
            mPath.cubicTo(leftHorizontalPoint.downPoint.x, leftHorizontalPoint.downPoint.y, downVerticalPoint.leftPoint.x, downVerticalPoint.leftPoint.y,
                    downVerticalPoint.x, downVerticalPoint.y);
            canvas.drawPath(mPath, paint);
        } else if (loadingStatus == 1) {
            long interval = System.currentTimeMillis() - startTime;
            if (interval < 600) {
                float dx = v1 * interval;
                rightHorizontalPoint.x = cPoint.x + radius + dx;
                leftHorizontalPoint.x = cPoint.x - radius;

                downVerticalPoint.y = cPoint.y + radius;
                upVerticalPoint.y = cPoint.y - radius;
            } else if (interval < 1200) {
                float dx = (interval - 600) * v1;
                float dx4 = (interval - 600) * v4;
                float dx2 = (interval - 600) * v2;
                float dx3 = (interval - 600) * v3;
                cPoint.x = radius + 100f + dx2;
                rightHorizontalPoint.x = cPoint.x + radius + distance-dx;
                leftHorizontalPoint.x = cPoint.x - radius - dx4;

                downVerticalPoint.y = cPoint.y + radius - dx3;
                upVerticalPoint.y = cPoint.y - radius + dx3;
            } else if (interval < 2200) {
                float dx2 = (interval - 600) * v2;
                cPoint.x = radius + 100f + dx2;
                rightHorizontalPoint.x = cPoint.x + radius;
                leftHorizontalPoint.x = cPoint.x - radius - distance1;

                downVerticalPoint.y = cPoint.y + radius - 8;
                upVerticalPoint.y = cPoint.y - radius + 8;
            } else if (interval < 2800) {
                float dx2 = (interval - 600) * v2;
                float dx = (interval - 2200) * v1;
                float dx4 = (interval - 2200) * v4;
                float dx3 = (interval - 2200) * v3;
                cPoint.x = radius + 100f + dx2;
                rightHorizontalPoint.x = cPoint.x + radius;
                leftHorizontalPoint.x = cPoint.x - radius - distance1 + dx4;

                downVerticalPoint.y = cPoint.y + radius - 8 + dx3;
                upVerticalPoint.y = cPoint.y - radius + 8 - dx3;
            } else if (interval < 3000) {
                float dx = (interval - 2800) * v1;
                cPoint.x = radius + 100f + v2 * 2200;
                rightHorizontalPoint.x = cPoint.x + radius;
                leftHorizontalPoint.x = cPoint.x - radius + dx;

                downVerticalPoint.y = cPoint.y + radius;
                upVerticalPoint.y = cPoint.y - radius;
            } else if (interval < 3200) {
                float dx = (interval - 3000) * v1;
                cPoint.x = radius + 100f + v2 * 2200;
                rightHorizontalPoint.x = cPoint.x + radius;
                leftHorizontalPoint.x = cPoint.x - radius + v1 * 200 - dx;

                downVerticalPoint.y = cPoint.y + radius;
                upVerticalPoint.y = cPoint.y - radius;
            } else if (interval < 3400) {
                cPoint.x = radius + 100f + v2 * 2200;
                rightHorizontalPoint.x = cPoint.x + radius;
                leftHorizontalPoint.x = cPoint.x - radius;

                downVerticalPoint.y = cPoint.y + radius;
                upVerticalPoint.y = cPoint.y - radius;
            } else {
                loadingStatus = 0;
                return;
            }
//            rightHorizontalPoint.x = cPoint.x + radius;
            rightHorizontalPoint.y = cPoint.y;
//            leftHorizontalPoint.x = cPoint.x - radius;
            leftHorizontalPoint.y = cPoint.y;

            downVerticalPoint.x = cPoint.x;
//            downVerticalPoint.y = cPoint.y + radius;
            upVerticalPoint.x = cPoint.x;
//            upVerticalPoint.y = cPoint.y - radius;

            rightHorizontalPoint.calculate();
            leftHorizontalPoint.calculate();
            downVerticalPoint.calculate();
            upVerticalPoint.calculate();

            mPath.reset();
            mPath.moveTo(downVerticalPoint.x, downVerticalPoint.y);
            mPath.cubicTo(downVerticalPoint.rightPoint.x, downVerticalPoint.rightPoint.y, rightHorizontalPoint.downPoint.x, rightHorizontalPoint.downPoint.y,
                    rightHorizontalPoint.x, rightHorizontalPoint.y);
            mPath.cubicTo(rightHorizontalPoint.upPoint.x, rightHorizontalPoint.upPoint.y, upVerticalPoint.rightPoint.x, upVerticalPoint.rightPoint.y,
                    upVerticalPoint.x, upVerticalPoint.y);
            mPath.cubicTo(upVerticalPoint.leftPoint.x, upVerticalPoint.leftPoint.y, leftHorizontalPoint.upPoint.x, leftHorizontalPoint.upPoint.y,
                    leftHorizontalPoint.x, leftHorizontalPoint.y);
            mPath.cubicTo(leftHorizontalPoint.downPoint.x, leftHorizontalPoint.downPoint.y, downVerticalPoint.leftPoint.x, downVerticalPoint.leftPoint.y,
                    downVerticalPoint.x, downVerticalPoint.y);
            canvas.drawPath(mPath, paint);

            postInvalidate();
        }

    }

    public class HorizontalPoint extends PointF {
        public PointF upPoint, downPoint;
        private float c;

        public HorizontalPoint(float c) {
            this.c = c;
            upPoint = new PointF();
            downPoint = new PointF();
        }

        public void calculate() {
            upPoint.x = this.x;
            downPoint.x = this.x;

            upPoint.y = this.y - c;
            downPoint.y = this.y + c;
        }

        public void setC(float c) {
            this.c = c;
        }
    }

    public class VerticalPoint extends PointF {
        public PointF leftPoint, rightPoint;
        private float c;

        public VerticalPoint(float c) {
            this.c = c;
            leftPoint = new PointF();
            rightPoint = new PointF();
        }

        public void calculate() {
            leftPoint.x = this.x - c;
            rightPoint.x = this.x + c;

            leftPoint.y = this.y;
            rightPoint.y = this.y;
        }

        public void setC(float c) {
            this.c = c;
        }
    }

    long startTime;

    public void startMove() {
        if (loadingStatus == 0) {
            cPoint.x = radius + 100f;
            cPoint.y = radius + 100f;
            loadingStatus = 1;
            startTime = System.currentTimeMillis();
            postInvalidate();
        }
    }

    public void drawCircle(Canvas canvas) {
        canvas.translate(radius + 150, radius + 150);

        //正圆
        mPath.reset();
        mPath.moveTo(0, radius);
        mPath.cubicTo(c, radius, radius, c, radius, 0);

        mPath.cubicTo(radius, -c, c, -radius, 0, -radius);

        mPath.cubicTo(-c, -radius, -radius, -c, -radius, 0);

        mPath.cubicTo(-radius, c, -c, radius, 0, radius);

        canvas.drawPath(mPath, paint);

        //带偏移的圆1
        mPath.reset();
        canvas.translate(3 * radius, 0);
        canvas.drawPoint(0, 0, pointPaint);

        mPath.moveTo(0, radius);
        canvas.drawPoint(0, radius, pointPaint);

        mPath.cubicTo(c, radius, radius + distance, c, radius + distance, 0);
        canvas.drawPoints(new float[]{c, radius, radius + distance, c, radius + distance, 0}, pointPaint);

        mPath.cubicTo(radius + distance, -c, c, -radius, 0, -radius);
        canvas.drawPoints(new float[]{radius + distance, -c, c, -radius, 0, -radius}, pointPaint);

        mPath.cubicTo(-c, -radius, -radius, -c, -radius, 0);
        canvas.drawPoints(new float[]{-c, -radius, -radius, -c, -radius, 0}, pointPaint);

        mPath.cubicTo(-radius, c, -c, radius, 0, radius);
        canvas.drawPoints(new float[]{-radius, c, -c, radius, 0, radius}, pointPaint);

        canvas.drawPath(mPath, paint);

        //带偏移的圆2
        mPath.reset();
        canvas.translate(-3 * radius, 3 * radius);
        canvas.drawPoint(0, 0, pointPaint);

        mPath.moveTo(0, radius);
        canvas.drawPoint(0, radius, pointPaint);

        mPath.cubicTo(c, radius, radius + distance, c, radius + distance, 0);
        canvas.drawPoints(new float[]{c, radius, radius + distance, c, radius + distance, 0}, pointPaint);

        mPath.cubicTo(radius + distance, -c, c, -radius, 0, -radius);
        canvas.drawPoints(new float[]{radius + distance, -c, c, -radius, 0, -radius}, pointPaint);

        mPath.cubicTo(-c, -radius, -radius - distance, -c, -radius - distance, 0);
        canvas.drawPoints(new float[]{-c, -radius, -radius - distance, -c, -radius - distance, 0}, pointPaint);

        mPath.cubicTo(-radius - distance, c, -c, radius, 0, radius);
        canvas.drawPoints(new float[]{-radius - distance, c, -c, radius, 0, radius}, pointPaint);

        canvas.drawPath(mPath, paint);

        //带偏移的圆3
        mPath.reset();
        canvas.translate(0, 3 * radius);
        canvas.drawPoint(0, 0, pointPaint);

        mPath.moveTo(0, radius);
        canvas.drawPoint(0, radius, pointPaint);

        mPath.cubicTo(c, radius, radius, c, radius, 0);
        canvas.drawPoints(new float[]{c, radius, radius, c, radius, 0}, pointPaint);

        mPath.cubicTo(radius, -c, c, -radius, 0, -radius);
        canvas.drawPoints(new float[]{radius, -c, c, -radius, 0, -radius}, pointPaint);

        mPath.cubicTo(-c, -radius, -radius - distance, -c, -radius - distance, 0);
        canvas.drawPoints(new float[]{-c, -radius, -radius - distance, -c, -radius - distance, 0}, pointPaint);

        mPath.cubicTo(-radius - distance, c, -c, radius, 0, radius);
        canvas.drawPoints(new float[]{-radius - distance, c, -c, radius, 0, radius}, pointPaint);

        canvas.drawPath(mPath, paint);
    }


}
