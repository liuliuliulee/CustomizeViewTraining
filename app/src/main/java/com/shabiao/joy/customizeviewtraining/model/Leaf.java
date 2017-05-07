package com.shabiao.joy.customizeviewtraining.model;

/**
 * Created by joy on 2017/4/9.
 */

public class Leaf {
    public final static int LITTLE = 0, MIDDLE = 1, BIG = 2;
    // 在绘制部分的位置
    public float x, y;
    // 控制叶子飘动的幅度
    public int amplitude;
    // 旋转开始角度
    public int startAngle;
    // 旋转方向--0代表顺时针，1代表逆时针
    public int rotateDirection;
    // 起始时间(ms)
    public long startTime;
}
