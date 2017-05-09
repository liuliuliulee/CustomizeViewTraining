package com.shabiao.joy.customizeviewtraining.custiomizeView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by joy on 2017/4/20.
 */
public class CircleSearchView extends View {
    public final String TAG = "CIRCLE_SEARCH_VIEW";
    //默认view的宽高
    int minHeight = 300, minWidth = 300;
    //默认线条粗
    int stroke = 5;

    int measureHeight, measureWidth;

    int circleRadius = 20, searchRadius = 10;

    // 画笔
    private Paint mPaint;
    private Paint paint;
    // 当前的状态(非常重要)
    private int mCurrentState = 0;//0为none,1为starting,2为searching,3为ending
    // 放大镜与外部圆环
    private Path pathSearch;
    private Path pathCircle;
    // 测量Path 并截取部分的工具
    private PathMeasure mMeasure;
    // 默认的动效周期 2s
    private int defaultDuration = 2000;
    // 控制各个过程的动画
    private ValueAnimator mStartingAnimator;
    private ValueAnimator mSearchingAnimator;
    private ValueAnimator mEndingAnimator;
    // 动画数值(用于控制动画状态,因为同一时间内只允许有一种状态出现,具体数值处理取决于当前状态)
    private float mAnimatorValue = 0;
    // 动效过程监听器
    private ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private Animator.AnimatorListener mAnimatorListener;
    // 用于控制动画状态转换
    private Handler mAnimatorHandler;
    // 判断是否已经搜索结束
    private boolean isOver = false;
    private int count = 0;

    public CircleSearchView(Context context) {
        super(context);
        init();
    }

    public CircleSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        stroke = (int) (stroke * density);
        circleRadius = (int) (circleRadius * density);
        searchRadius = (int) (searchRadius * density);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);


        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(stroke);
        //画笔图形样式为圆形
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

        pathSearch = new Path();
        pathCircle = new Path();
        mMeasure = new PathMeasure();

        RectF circleRect = new RectF(-circleRadius, -circleRadius, circleRadius, circleRadius);
        pathCircle.addArc(circleRect, 45, 359.9f);

        RectF searchRect = new RectF(-searchRadius, -searchRadius, searchRadius, searchRadius);
        pathSearch.addArc(searchRect, 45, 359.9f);
        //存放外环的起点位置
        float[] pos = new float[2];
        mMeasure.setPath(pathCircle, false);
        mMeasure.getPosTan(0, pos, null);
        //连接内环和外环,其实就是画手柄,将内环和手柄连接
        pathSearch.lineTo(pos[0], pos[1]);

        initHandler();

        initListener();

        initAnimator();


    }


    private void initAnimator() {
        mStartingAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);
        mSearchingAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);
        mEndingAnimator = ValueAnimator.ofFloat(1, 0).setDuration(defaultDuration);

        mStartingAnimator.addUpdateListener(mUpdateListener);
        mSearchingAnimator.addUpdateListener(mUpdateListener);
        mEndingAnimator.addUpdateListener(mUpdateListener);

        mStartingAnimator.addListener(mAnimatorListener);
        mSearchingAnimator.addListener(mAnimatorListener);
        mEndingAnimator.addListener(mAnimatorListener);

//        mStartingAnimator = ValueAnimator.ofFloat(0,1).setDuration(defaultDuration);
//        mStartingAnimator.addUpdateListener(animation -> {
//            mAnimatorValue = (float) animation.getAnimatedValue();
//            invalidate();
//        });
//        mStartingAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                // getHandle发消息通知动画状态更新
//                mAnimatorHandler.sendEmptyMessage(0);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
    }

    private void initListener() {
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        };

        mAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // getHandle发消息通知动画状态更新
                mAnimatorHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    private void initHandler() {
        mAnimatorHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (mCurrentState) {
                    case 1:
                        // 从开始动画转换好搜索动画
                        isOver = false;
                        mCurrentState = 2;
//                        mStartingAnimator.removeAllListeners();
                        mSearchingAnimator.start();
                        break;
                    case 2:
                        if (!isOver) {  // 如果搜索未结束 则继续执行搜索动画
                            mSearchingAnimator.start();
                            Log.e("Update", "RESTART");

                            count++;
                            if (count > 2) {       // count大于2则进入结束状态
                                isOver = true;
                            }
                        } else {        // 如果搜索已经结束 则进入结束动画
                            mCurrentState = 3;
                            mEndingAnimator.start();
                        }
                        break;
                    case 3:
                        // 从结束动画转变为无状态
                        mCurrentState = 0;
                        break;
                }
            }
        };
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

        heightSize = heightSize - getPaddingTop() - getPaddingBottom();
        widthSize = widthSize - getPaddingStart() - getPaddingEnd() - getPaddingRight() - getPaddingLeft();

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        measureWidth = w;
        measureHeight = h;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(measureWidth / 2, measureHeight / 2);
//        canvas.drawLine(0,0,0,-searchRadius,paint);
        switch (mCurrentState) {
            case 0:
                canvas.drawPath(pathSearch, mPaint);
                break;
            case 1:
                mMeasure.setPath(pathSearch, false);
                Path dst = new Path();
                mMeasure.getSegment(mMeasure.getLength() * mAnimatorValue, mMeasure.getLength(), dst, true);
                canvas.drawPath(dst, mPaint);
                break;
            case 2:
                mMeasure.setPath(pathCircle, false);
                Path dst2 = new Path();
                float stop = mMeasure.getLength() * mAnimatorValue;
                float start = (float) (stop - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * (2 * Math.PI * circleRadius) / 3));
                mMeasure.getSegment(start, stop, dst2, true);
                canvas.drawPath(dst2, mPaint);
                break;
            case 3:
                mMeasure.setPath(pathSearch, false);
                Path dst3 = new Path();
                mMeasure.getSegment(mMeasure.getLength() * mAnimatorValue, mMeasure.getLength(), dst3, true);
                canvas.drawPath(dst3, mPaint);
                break;
        }

    }

    public void startSearch() {
        if (mCurrentState == 0) {
            // 进入开始动画
            count=0;
            mCurrentState = 1;
            mStartingAnimator.start();

        }
    }

}
