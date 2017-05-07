package com.shabiao.joy.customizeviewtraining.custiomizeView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.shabiao.joy.customizeviewtraining.R;

import java.util.ArrayList;

/**
 * Created by joy on 2017/4/2.
 */

public class PieChartView extends View {
    private final static String TAG = "PieChartView";

    private int districtCount = 1;
    private int[] colorArray = {Color.BLUE, Color.GREEN, Color.GRAY,Color.CYAN,Color.RED,Color.BLACK};
    private String title = "";
    private int minWidth = 800, minHeight = 600;
    private Paint paint;
    private ArrayList<InfoData> infoDataList;

    public PieChartView(Context context) {
        this(context, null);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PieChartView, defStyleAttr, 0);
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            if (typedArray.getIndex(i) == R.styleable.PieChartView_districtCount) {
                districtCount = typedArray.getInt(i, 1);
            } else if (typedArray.getIndex(i) == R.styleable.PieChartView_title) {
                title = typedArray.getString(i);
            }
        }
        init();
    }

    public void init() {
        paint = new Paint();
        //填充的模式
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        infoDataList = new ArrayList<>();
        for(int i=0;i<6;i++){
            InfoData infoData = new InfoData();
            infoData.value=i+1;
            allValue+=infoData.value;
            infoDataList.add(infoData);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        Log.e(TAG,width+","+height);
        int min = Math.min(width, height);
        RectF rectF = new RectF((width - min) / 2, (height - min) / 2, (width + min) / 2, (height + min) / 2);
//        paint.setColor(Color.BLUE);
//        canvas.drawArc(rectF, -90, 90, true, paint);
//        paint.setColor(Color.GREEN);
//        canvas.drawArc(rectF, 0, 270, true, paint);
        startAngle=-90;
        for(int i=0;i< infoDataList.size();i++){
            float sweepAngle = infoDataList.get(i).value/allValue*360;
            paint.setColor(colorArray[i]);
            Log.e(TAG,sweepAngle+"");
            canvas.drawArc(rectF,startAngle,sweepAngle,true,paint);
            startAngle+=sweepAngle;
        }
    }


    public class InfoData{
        String content = "";
        float value = 0;
    }
    private float startAngle=0;
    private float allValue;

    public void setInfoDataList(ArrayList<InfoData> infoDataList) {
        this.infoDataList = infoDataList;
        allValue=0;
        for(InfoData infoData : infoDataList){
            allValue+=infoData.value;
        }
        postInvalidate();
    }
}
