package com.shabiao.joy.customizeviewtraining.custiomizeView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.shabiao.joy.customizeviewtraining.R;
import com.shabiao.joy.customizeviewtraining.UiUtils;
import com.shabiao.joy.customizeviewtraining.model.Leaf;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by joy on 2017/4/9.
 */

public class LoadingView extends View {
    private final static String TAG = "LoadingView";
    int minWidth, minHeight;
    private Paint paint;
    private Bitmap mLeafBitmap;
    private Bitmap mFSBitmap;
    private int mLeafWidth;
    private int mLeafHeight;
    private int mFSWidth;
    private int mFSHeight;
    private Paint mBitmapPaint;
    //    RectF fsSrcRect;
    int density;
    long startTime;
    int loadingStatus = 1;//加载状态：1为暂停，2为loading，3为加载完毕
    int measuredHeight;
    int measuredWidth;
    int strokeWidth;
    float viewProgress;
    float curProgress;
    float realProgress = 0;
    float totalProgress = 100;
    long loadingCompletedTime = 0;


    // 用于产生叶子信息
    private LeafFactory mLeafFactory;
    // 产生出的叶子信息
    private List<Leaf> mLeafInfos;

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        strokeWidth = UiUtils.dipToPx(getContext(), 10);
        minHeight = UiUtils.dipToPx(getContext(), 70);
        minWidth = UiUtils.dipToPx(getContext(), 300);
        startTime = System.currentTimeMillis();
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        density = (int) dm.density;

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);


        paint = new Paint();
        paint.setAntiAlias(true);

        mLeafFactory = new LeafFactory();
        mLeafInfos = mLeafFactory.generateLeafs(6);

        mLeafBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.leaf);
        mLeafWidth = UiUtils.dipToPx(getContext(), mLeafBitmap.getWidth());
        mLeafHeight = UiUtils.dipToPx(getContext(), mLeafBitmap.getHeight());

        mFSBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.fengshan);
        mFSWidth = UiUtils.dipToPx(getContext(), mFSBitmap.getWidth());
        mFSHeight = UiUtils.dipToPx(getContext(), mFSBitmap.getHeight());
//        fsSrcRect = new RectF(0, 0, mFSWidth, mFSHeight);

//        random_ = new Random();
    }

//    Random random_;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY)
            widthSize = minWidth;
        if (heightMode != MeasureSpec.EXACTLY)
            heightSize = minHeight;
        setMeasuredDimension(widthSize, heightSize);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        Log.e("on size change", w + "," + h);
        measuredWidth = w;
        measuredHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        measuredHeight = getMeasuredHeight();
//        measuredWidth = getMeasuredWidth();
        viewProgress = measuredWidth - measuredHeight / 2;
        curProgress = realProgress / totalProgress * viewProgress;
        //画矩形时，如果带有stroke,则矩形的真实边界要给多stroke/2的数值，不然会被截取掉
        RectF background = new RectF(strokeWidth / 2, strokeWidth / 2, measuredWidth - strokeWidth / 2, measuredHeight - strokeWidth / 2);
//        paint.setColor(Color.parseColor("#Ffffff"));
//        paint.setStyle(Paint.Style.FILL);
//        canvas.drawRect(background,paint);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.parseColor("#FCE298"));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRoundRect(background, measuredHeight / 2, measuredHeight / 2, paint);
        //0为默认值，hairline mode，发丝模式
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);


        int r = measuredHeight / 2;
        int angle = (int) Math.toDegrees(Math.acos((r - curProgress) / r));
        int startAngle = 180 - angle;
        int sweepAngle = 2 * angle;
        if (curProgress <= measuredHeight / 2) {
            RectF rectF = new RectF(strokeWidth, strokeWidth, 2 * r - strokeWidth, 2 * r - strokeWidth);
            paint.setColor(Color.parseColor("#F5A418"));
            canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);
//            Rect rect = new Rect()
        } else {
            RectF rectF = new RectF(strokeWidth, strokeWidth, 2 * r - strokeWidth, 2 * r - strokeWidth);
//            paint.setColor(Color.parseColor("#F5A418"));
            paint.setColor(Color.parseColor("#ffa800"));
            canvas.drawArc(rectF, 90, 180, false, paint);

            RectF rectF1 = new RectF(r, strokeWidth, strokeWidth + curProgress, measuredHeight - strokeWidth);
            canvas.drawRect(rectF1, paint);
        }

        paint.setColor(Color.WHITE);
        canvas.drawCircle(measuredWidth - measuredHeight / 2, measuredHeight / 2, r, paint);
        paint.setColor(Color.parseColor("#ffa800"));
        canvas.drawCircle(measuredWidth - measuredHeight / 2, measuredHeight / 2, r - 10, paint);

        if (realProgress >= totalProgress && loadingStatus != 3) {
            loadingStatus = 3;
            loadingCompletedTime = System.currentTimeMillis();
        }

        int r_ = r - 10;
        float sqrt = (float) Math.sqrt(r_ * r_ / 2);
        Matrix matrix = new Matrix();
        switch (loadingStatus) {
            case 1:
                //stop
                //绘制停止风扇
//                matrix.postScale(2 * sqrt / (float) (mFSBitmap.getWidth()), 2 * sqrt / (float) (mFSBitmap.getWidth()));
//                matrix.postTranslate(measuredWidth - measuredHeight / 2 - sqrt, r - sqrt);
//                canvas.drawBitmap(mFSBitmap, matrix, mBitmapPaint);

                RectF src = new RectF(0, 0, mFSBitmap.getWidth(), mFSBitmap.getHeight());
                RectF dst = new RectF(measuredWidth - measuredHeight / 2 - sqrt, r - sqrt,
                        measuredWidth - measuredHeight / 2 + sqrt, r + sqrt);
                matrix.setRectToRect(src, dst, Matrix.ScaleToFit.FILL);
                canvas.drawBitmap(mFSBitmap, matrix, mBitmapPaint);
                break;
            case 2:
                //loading
                // 绘制叶子
                drawLeafs(canvas);
                //绘制转动风扇
                long currentTimeMillis = System.currentTimeMillis();
                long l = currentTimeMillis - startTime;
                if (l > mFsFloatTime) {
                    startTime = currentTimeMillis;
                }
                float v = l / mFsFloatTime;
                int angel = (int) (v * 360);
//                matrix.postScale(2 * sqrt / (float) (mFSBitmap.getWidth()), 2 * sqrt / (float) (mFSBitmap.getWidth()));
//                matrix.postTranslate(measuredWidth - measuredHeight / 2 - sqrt, r - sqrt);

                RectF src_ = new RectF(0, 0, mFSBitmap.getWidth(), mFSBitmap.getHeight());
                RectF dst_ = new RectF(measuredWidth - measuredHeight / 2 - sqrt, r - sqrt,
                        measuredWidth - measuredHeight / 2 + sqrt, r + sqrt);
                //把源rect变换为dstRect,期间会进行缩放，方式为填满dstRect
                matrix.setRectToRect(src_, dst_, Matrix.ScaleToFit.FILL);
                matrix.postRotate(-angel, measuredWidth - r, r);
                canvas.drawBitmap(mFSBitmap, matrix, mBitmapPaint);
                postInvalidate();
                break;
            case 3:
                //loading complete
                //加载完毕
                float interval = (float) (System.currentTimeMillis() - loadingCompletedTime);
//                Log.e("aaaa", interval + "");
                if (interval < 300) {
                    float originalScale = 2 * sqrt / (float) (mFSBitmap.getWidth());
                    float curScale = originalScale * (1 - interval / 300f);
                    matrix.postScale(curScale, curScale);
                    float v1 = (2 * sqrt - mFSBitmap.getWidth() * curScale) / 2;
                    matrix.postTranslate(measuredWidth - measuredHeight / 2 - sqrt + v1, r - sqrt + v1);
                    canvas.drawBitmap(mFSBitmap, matrix, mBitmapPaint);
                    postInvalidate();
                } else {
                    Rect rect = new Rect(measuredWidth - measuredHeight, 0, measuredWidth, measuredHeight);
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(50);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setTextAlign(Paint.Align.CENTER);
                    Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                    float top = fontMetrics.top;
                    float bottom = fontMetrics.bottom;
                    int baseLineY = (int) (rect.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
                    canvas.drawText("100%", rect.centerX(), baseLineY, paint);
                }
                break;
        }

    }

    private void drawLeafs(Canvas canvas) {
        mLeafRotateTime = mLeafRotateTime <= 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < mLeafInfos.size(); i++) {
            Leaf leaf = mLeafInfos.get(i);
            if (currentTime > leaf.startTime && leaf.startTime != 0) {
                // 绘制叶子－－根据叶子的类型和当前时间得出叶子的（x，y）
                getLeafLocation(leaf, currentTime);
                // 根据时间计算旋转角度
//                canvas.save();
                // 通过Matrix控制叶子旋转
                Matrix matrix = new Matrix();
                float transX = leaf.x;
                float transY = leaf.y;
                if (leaf.x < (strokeWidth + curProgress))
                    continue;
                //按density缩放叶子
                matrix.postScale(density, density);
                //平移叶子到相应位置
                matrix.postTranslate(transX, transY);
                // 通过时间关联旋转角度，则可以直接通过修改LEAF_ROTATE_TIME调节叶子旋转快慢
                float rotateFraction = ((currentTime - leaf.startTime) % mLeafRotateTime)
                        / mLeafRotateTime;
                int angle = (int) (rotateFraction * 360);
                // 根据叶子旋转方向确定叶子旋转角度
                int rotate = leaf.rotateDirection == 0 ? angle + leaf.startAngle : -angle
                        + leaf.startAngle;
                //按照时间设定叶子旋转角度
                matrix.postRotate(rotate, transX
                        + mLeafWidth / 2, transY + mLeafHeight / 2);
//                Log.e(TAG, transX + "," + transY);
                canvas.drawBitmap(mLeafBitmap, matrix, mBitmapPaint);
//                canvas.restore();
            } else {
                continue;
            }
        }
    }

    // 叶子飘动一个周期所花的时间
    private static final float LEAF_FLOAT_TIME = 3000;
    // 叶子旋转一周需要的时间
    private static final float LEAF_ROTATE_TIME = 2000;
    // 叶子旋转一周需要的时间
    private float mLeafRotateTime = LEAF_ROTATE_TIME;
    //叶子飘动周期时间
    private float mLeafFloatTime = LEAF_FLOAT_TIME;
    //风扇旋转周期
    private float mFsFloatTime = 1000;

    public class LeafFactory {
        Random random = new Random();

        public Leaf getLeaf() {
            Leaf leaf = new Leaf();
            leaf.rotateDirection = random.nextInt(2);
            leaf.amplitude = random.nextInt(3);
            leaf.startAngle = random.nextInt(360);
            leaf.startTime = System.currentTimeMillis() + random.nextInt((int) (mLeafFloatTime));
            return leaf;
        }

        // 根据传入的叶子数量产生叶子信息
        public List<Leaf> generateLeafs(int leafSize) {
            List<Leaf> leafs = new LinkedList<>();
            for (int i = 0; i < leafSize; i++) {
                leafs.add(getLeaf());
            }
            return leafs;
        }
    }

    private void getLeafLocation(Leaf leaf, long currentTime) {
        long intervalTime = currentTime - leaf.startTime;
        mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        if (intervalTime < 0) {
            return;
        } else if (intervalTime > mLeafFloatTime) {
            leaf.startTime = System.currentTimeMillis() + new Random().nextInt((int) (mLeafFloatTime));
        }

        float fraction = (float) intervalTime / mLeafFloatTime;
        leaf.x = (int) (viewProgress - viewProgress * fraction);
//        Log.e("x:",leaf.x +"");
        leaf.y = getLocationY(leaf);
//        Log.e("y:",leaf.y +"");
    }

    // 通过叶子信息获取当前叶子的Y值
    private int getLocationY(Leaf leaf) {
        // y = A(wx+Q)+h、y=A*sin(wx+f)+h
        //先求出w,w=2*pi/T
        float w = (float) ((float) 2 * Math.PI / viewProgress);
        float A = (measuredHeight - 2 * strokeWidth) / 2 - Math.max(mLeafHeight, mLeafWidth) / 2;
//        Log.e(TAG,A+"");
//        int i = random_.nextInt(2);
//
//        switch (i){
//            case 0:
//
//                break;
//            case 1:
//
//                break;
//        }
        switch (leaf.amplitude) {
            case 0:
                A = A / 3;
                break;
            case 1:
                A = A / 2;
                break;
            case 2:
                break;
        }
//        Log.e(TAG,"y:"+(A * Math.sin(w * leaf.x)+measuredHeight/2));
        return (int) (A * Math.sin(w * (leaf.x - mLeafWidth / 2)) + (measuredHeight) / 2) - mLeafHeight / 2;
    }

    public void setProgress(int progress) {
        this.realProgress = progress;
        if (loadingStatus != 2) {
            loadingStatus = 2;
            postInvalidate();
        }
    }

}
