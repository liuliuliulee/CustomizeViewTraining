package com.shabiao.joy.customizeviewtraining.custiomizeView;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

/**
 * Created by joy on 2017/5/9.
 */

public class ZoomImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "ZoomImageView";

    /**
     * 初始化时缩放的值
     */
    private float mInitScale;
    /**
     * 双击放大值到达的值
     */
    private float mMaxScale;
    /**
     * 双击最小值到达的值
     */
    private float mMidScale;

    private Matrix mScaleMatrix;

    /**
     * 捕获用户多指触控时缩放的比例
     */
    private ScaleGestureDetector mScaleGestureDetector;

    //双击放大与缩小
    private GestureDetector mGestureDetector;
    //是否在自动缩放
    private boolean isAutoScale;

    //自由移动
    /**
     * 记录上一次多点触控的数量
     */
    private int mLastPointCount;
    private float mLastX;
    private float mLastY;
    //系统判断的可move的最小值。
    private float mTouchSlop;
    private boolean isCanDrag;

    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;

    private boolean mOnce;

    public ZoomImageView(Context context) {
        super(context);
        init();
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        mScaleMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (getDrawable() == null)
                    return true;
                float scale = getScale();
                float scaleFactor = detector.getScaleFactor();
                if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mInitScale && scaleFactor < 1.0f)) {
                    //缩放值小于初始值,修正
                    if (scale * scaleFactor < mInitScale) {
                        scaleFactor = mInitScale / scale;
                    }
                    //缩放值大于最大放大值,修正
                    if (scale * scaleFactor > mMaxScale) {
                        scaleFactor = mMaxScale / scale;
                    }
                    mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                    checkBorderAndCenterWhenScale();
                    setImageMatrix(mScaleMatrix);
                }

                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }
        });
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isAutoScale) {
                    return true;
                }
                float x = e.getX();
                float y = e.getY();
                float scale = getScale();
                if (scale < mMidScale) {
//                    mScaleMatrix.postScale(mMidScale / scale, mMidScale / scale,x,y);
//                    checkBorderAndCenterWhenScale();
//                    setImageMatrix(mScaleMatrix);
                    postDelayed(new AutoScaleRunnable(mMidScale, x, y), 16);
                    isAutoScale = true;
                } else {
//                    mScaleMatrix.postScale(mInitScale / scale, mInitScale / scale,x,y);
//                    checkBorderAndCenterWhenScale();
//                    setImageMatrix(mScaleMatrix);
                    postDelayed(new AutoScaleRunnable(mInitScale, x, y), 16);
                    isAutoScale = true;

                }
                return true;
            }
        });

        setOnTouchListener((v, event) -> {
            //如果有双击事件，则就不让有其他操作。
            if (mGestureDetector.onTouchEvent(event)) {
                return true;
            }
            mScaleGestureDetector.onTouchEvent(event);

            float x = 0;
            float y = 0;
            //拿到多点触控的数量
            int pointerCount = event.getPointerCount();
            //获取中心位置
            for (int i = 0; i < pointerCount; i++) {
                x += event.getX(i);
                y += event.getY(i);
            }
            if (pointerCount != 0) {
                x /= pointerCount;
                y /= pointerCount;
            }

            if (mLastPointCount != pointerCount) {
                mLastPointCount = pointerCount;
                mLastX = x;
                mLastY = y;
                isCanDrag = false;
            }
            RectF rect = getMatrixRectF();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    if (rect.width() > getWidth() || rect.height() > getHeight()) {
                        if (getParent() instanceof ViewPager) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:

                    if (rect.width() > getWidth() || rect.height() > getHeight()) {
                        if (getParent() instanceof ViewPager) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }

                    }
                    float dx = x - mLastX;
                    float dy = y - mLastY;
                    if (!isCanDrag) {
                        isCanDrag = isMoveAction(dx, dy);
                    }
                    if (isCanDrag) {

                        if (getDrawable() != null) {
                            isCheckLeftAndRight = isCheckTopAndBottom = true;
                            //如果宽度小于控件的宽度，不允许横向移动
                            if (rect.width() < getWidth()) {
                                isCheckLeftAndRight = false;
                                dx = 0;
                            }
                            //如果高度小于控件的宽度，不允许纵向移动
                            if (rect.height() < getHeight()) {
                                isCheckTopAndBottom = false;
                                dy = 0;
                            }
                            mScaleMatrix.postTranslate(dx, dy);
                            checkBorderWhenTranslate();
                            setImageMatrix(mScaleMatrix);
                        }
                    }
                    mLastX = x;
                    mLastY = y;

                    break;
                case MotionEvent.ACTION_UP:
                    mLastPointCount = 0;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mLastPointCount = 0;
                    break;
                default:
                    break;
            }
            return true;
        });
        //getScaledTouchSlop是一个距离,表示滑动的时候,手的移动要大于这个距离才开始移动控件
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * 获取图片放大缩小以后的宽和高，以及l,r,t,b
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            //计算原始图片的rect经过平移和缩放后得到的rect
            matrix.mapRect(rectF);
        }
        return rectF;
    }

    /**
     * 获取当前变换矩形的缩放大小
     *
     * @return
     */
    public float getScale() {
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    /**
     * 在缩放的时候进行边界的控制，已知我们的位置的控制
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        //检查边界，不让留下白边

        //图宽大于viewWidth,可能有白边
        if (rect.width() >= width) {
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }
        //如果宽度或者高度小于控件的宽或者高，则让其居中
        if (rect.width() < width) {
            deltaX = width * 1.0f / 2f - rect.right + rect.width() * 1.0f / 2f;
        }
        if (rect.height() < height) {
            deltaY = height * 1.0f / 2f - rect.bottom + rect.height() * 1.0f / 2f;
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);

    }

    @Override
    public void onGlobalLayout() {
        if (!mOnce) {
            //得到控件的宽和高
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();

            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            //获取图片的width , height.
            int dw = drawable.getIntrinsicWidth();
            int dh = drawable.getIntrinsicHeight();

            float scale = 1.0f;
            /**
             * 如果图片的宽度大于屏幕的宽度，但是高度小于屏幕的高度
             */
            if (dw > width && dh < height) {
                scale = width * 1.0f / dw;
            }
            /**
             * 如果图片的高度大于屏幕的高度，但是宽度小于屏幕的宽度
             */
            if (dw < width && dh > height) {
                scale = height * 1.0f / dh;
            }

            //缩小选缩小倍数最大的，放大选择放大倍数最小的
            if ((dw > width && dh > height) || (dw < width && dh < height)) {
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }
            /**
             * 初始化时缩放的比例
             */
            mInitScale = scale;
            mMaxScale = mInitScale * 4;
            mMidScale = mInitScale * 2;

            int dx = getWidth() / 2 - dw / 2;
            int dy = getHeight() / 2 - dh / 2;


            //将图片移到控件的中心。
            mScaleMatrix.postTranslate(dx, dy);
            mScaleMatrix.postScale(mInitScale, mInitScale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mScaleMatrix);

            mOnce = true;
        }
    }

    /**
     * 当移动时，进行边界检查
     */
    private void checkBorderWhenTranslate() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;
        int width = getWidth();
        int height = getHeight();
        if (isCheckTopAndBottom && rect.top > 0) {
            deltaY = -rect.top;
        }
        if (isCheckTopAndBottom && rect.bottom < height) {
            deltaY = height - rect.bottom;
        }
        if (isCheckLeftAndRight && rect.left > 0) {
            deltaX = -rect.left;
        }
        if (isCheckLeftAndRight && rect.right < width) {
            deltaX = width - rect.right;
        }
        mScaleMatrix.postTranslate(deltaX,deltaY);
    }

    private class AutoScaleRunnable implements Runnable {

        /**
         * 缩放的目标值
         */
        private float mTargetScale;
        //缩放的中心点
        private float x;
        private float y;

        private final float BIGGER = 1.3f;
        private final float SMALL = 0.70f;
        private float tmpScale;

        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            }
            if (getScale() > mTargetScale) {
                tmpScale = SMALL;
            }
        }

        @Override
        public void run() {
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
            float currentScale = getScale();
            if ((tmpScale > 1.0f && currentScale < mTargetScale)
                    || (tmpScale < 1.0f && currentScale > mTargetScale)) {
                postDelayed(this, 16);
            } else {
                float scale = mTargetScale / currentScale;
                mScaleMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }

    }

    /**
     * 判断是否能触发move
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) >= mTouchSlop;
    }
}
