package com.shabiao.joy.customizeviewtraining.custiomizeView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by joy on 2017/5/16.
 */

public class SurfaceViewTemplate extends SurfaceView {

    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    /**
     * 用于绘制的线程
     */
    private Thread thread;
    private boolean isRunning;

    public SurfaceViewTemplate(Context context) {
        super(context);
        init();
    }

    public SurfaceViewTemplate(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SurfaceViewTemplate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                isRunning = true;
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isRunning) {
                            //TODO DRAW
                            draw();
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

    public void draw() {
        try {
            canvas = surfaceHolder.lockCanvas();
            //当离开surfaceView时，canvas会置为空，在线程中会发生其他各种问题，try住exception，则没问题了
            if (canvas != null) {
                //draw
            } else {
                isRunning = false;
            }
        } catch (Exception e) {
            isRunning = false;
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

}
