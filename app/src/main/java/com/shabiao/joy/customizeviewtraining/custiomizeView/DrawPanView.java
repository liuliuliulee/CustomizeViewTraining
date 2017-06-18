package com.shabiao.joy.customizeviewtraining.custiomizeView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.shabiao.joy.customizeviewtraining.R;
import com.shabiao.joy.customizeviewtraining.UiUtils;

/**
 * Created by joy on 2017/5/16.
 */

public class DrawPanView extends SurfaceView {

    int minWidth, minHeight;
    int measuredHeight;
    int measuredWidth;
    int padding;
    private String[] mStrs = {"单反相机", "IPAD", "恭喜发财", "IPHONE", "服装一套", "恭喜发财"};
    private int[] mImgs = new int[]{R.mipmap.danfan, R.mipmap.ipad, R.mipmap.f015,
            R.mipmap.iphone, R.mipmap.meizi, R.mipmap.f040};
    private int mItemCount = 6;
    /**
     * 与图片相对的Bitmap
     */
    private Bitmap[] mImgsBitmap;
    /***
     * 盘块的颜色
     */
    private int[] mColor = new int[]{0xffffc300, 0xfff17e01, 0xffffc300, 0xfff17e01, 0xffffc300, 0xfff17e01};
    /**
     * 整个盘块的范围
     */
    private RectF mRange;
    /**
     * 绘制盘块的画笔
     */
    private Paint mArcPaint;

    /***
     * 绘制文本的画笔
     */
    private Paint mTextPanit;
    /**
     * 滚动的速度
     */
    private double mSpeed = 0;
    private volatile float mStartAngle = 0;
    /**
     * 整个盘块的半径
     */
    private int mRadius;
    private Bitmap mBgBitmap;
    private float mTextSize;
    private SurfaceHolder surfaceHolder;
    private Canvas mCanvas;
    /**
     * 用于绘制的线程
     */
    private Thread thread;
    private boolean isRunning;
    /**
     * 判断是否点击了停止按钮
     */
    private boolean isShouldEnd;


    public void init() {
//        mSpeed=5;
        initSize();
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mRange = new RectF(-mRadius, -mRadius, mRadius, mRadius);
                //盘块画笔
                mArcPaint = new Paint();
                mArcPaint.setAntiAlias(true);
                mArcPaint.setDither(true);
                // 初始化绘制盘块的文本画笔
                mTextPanit = new Paint();
                mTextPanit.setColor(0xffffffff);
                mTextPanit.setTextSize(mTextSize);
                //初始化图片
                mImgsBitmap = new Bitmap[mItemCount];
                for (int i = 0; i < mItemCount; i++) {
                    mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(), mImgs[i]);
                }

                isRunning = true;
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isRunning) {
                            long start = System.currentTimeMillis();
                            draw();
                            long end = System.currentTimeMillis();
                            //确保50毫秒刷新一次，小于50毫秒则睡50毫秒减去draw所花的时间
                            if (end - start < 50) {
                                try {
                                    Thread.sleep(50 - (end - start));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                thread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                isRunning = false;
            }
        });
        //获取焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常量
        setKeepScreenOn(true);
    }

    public void initSize() {
        minHeight = UiUtils.dipToPx(getContext(), 300);
        minWidth = UiUtils.dipToPx(getContext(), 300);
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics());
        mRange = new RectF();
        mBgBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bg2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        measuredWidth = w;
        measuredHeight = h;
    }

    public void draw() {
        try {
            mCanvas = surfaceHolder.lockCanvas();
            //当离开surfaceView时，canvas会置为空，在线程中会发生其他各种问题，try住exception，则没问题了
            if (mCanvas != null) {
                //draw
                mCanvas.translate(measuredWidth / 2, measuredHeight / 2);
                drawBg();

                //绘制盘块
                float tmpAngle = mStartAngle;
                float sweepAngle = 360 / mItemCount;
                for (int i = 0; i < mItemCount; i++) {
                    mArcPaint.setColor(mColor[i]);
                    //绘制盘块
                    mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);

                    //绘制文本
                    drawText(tmpAngle, sweepAngle, mStrs[i]);

                    //绘制icon
                    drawIcon(tmpAngle, mImgsBitmap[i]);

                    tmpAngle += sweepAngle;
                }

                mStartAngle += mSpeed;
                //如果点击了停止按钮
                if(isShouldEnd){
                    mSpeed -=1;
                }
                if(mSpeed<=0){
                    mSpeed = 0;
                    isShouldEnd = false;
                }
            } else {
                isRunning = false;
            }
        } catch (Exception e) {
            isRunning = false;
        } finally {
            if (mCanvas != null) {
                surfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    /**
     * 点击启动旋转
     */
    public void luckyStart(int index){
        //计算每一项的角度
//        float angle = 360/mItemCount;
//
//        //计算每一项中奖范围（当前index）
//        //1-->150~210
//        //0-->210~270
//        float from = 270-(index
//                +1)*angle;
//        float end = from+angle;
//
//        //设置停下来需要旋转的距离
//        float targetFrom = 4*360 + from;
//        float targetEnd = 4*360 + end;
//
//        float v1= (float) ((-1+Math.sqrt(1+8*targetFrom))/2);
//        float v2= (float) ((-1+Math.sqrt(1+8*targetEnd))/2);

        mSpeed = 20;
        isShouldEnd = false;
    }
    /**
     * 停止
     */
    public void luckyEnd(){
//        mStartAngle = 0;
        isShouldEnd = true;
    }

    private void drawBg() {
        mCanvas.drawColor(0xffffffff);
//        mCanvas.drawBitmap(mBgBitmap, null, new Rect(mPadding/2, mPadding/2, getMeasuredWidth()- mPadding/2 , getMeasuredHeight()- mPadding/2) , null);
        mCanvas.drawBitmap(mBgBitmap, null, new RectF(-measuredWidth / 2, -measuredHeight / 2, measuredWidth / 2, measuredHeight / 2), mArcPaint);
    }

    /**
     * 绘制文本
     *
     * @param tmpAngle
     * @param sweepAngle
     * @param string
     */
    private void drawText(float tmpAngle, float sweepAngle, String string) {
        Path path = new Path();
        path.addArc(mRange, tmpAngle, sweepAngle);

        // 利用水平偏移量让文字居中
        float textWidth = mTextPanit.measureText(string);
        int hOffset = (int) (2 * mRadius * Math.PI / mItemCount / 2 - textWidth / 2);

        //垂直偏移量
        int vOffset = mRadius / 5;

        mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPanit);
    }

    /**
     * 绘制盘块图片
     *
     * @param tmpAngle
     * @param bitmap
     */
    private void drawIcon(float tmpAngle, Bitmap bitmap) {
        //设置图片的宽度为直径的1/8
        float imgWidth = mRadius / 3.5f;

        float angle = (float) ((tmpAngle + 360 / mItemCount / 2) * Math.PI / 180);

//        int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
//        int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));
        int x = (int) (mRadius / 2 * Math.cos(angle));
        int y = (int) (mRadius / 2 * Math.sin(angle));

        //确定那个图片的位置
        RectF rect = new RectF(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        mCanvas.drawBitmap(bitmap, null, rect,null);

    }


    public DrawPanView(Context context) {
        super(context);
        init();
    }

    public DrawPanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawPanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        int min = Math.min(widthSize, heightSize);
        int max1 = Math.max(getPaddingStart(), getPaddingEnd());
        int max2 = Math.max(getPaddingTop(), getPaddingBottom());
        int width = widthSize - max1 * 2;
        int height = heightSize - max2 * 2;
        mRadius = Math.min(width, height) / 2;
        setMeasuredDimension(min, min);
    }
}
