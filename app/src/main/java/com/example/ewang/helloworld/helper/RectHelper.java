package com.example.ewang.helloworld.helper;

import android.graphics.Matrix;
import android.graphics.PointF;

/**
 * Created by ewang on 2018/5/31.
 */

public class RectHelper {

    public static PointF getLeftTop(Matrix matrix) {
        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float leftTopX = 0.0F * arrayOfFloat[0] + 0.0F * arrayOfFloat[1] + 1.0F * arrayOfFloat[2];
        float leftTopY = 0.0F * arrayOfFloat[3] + 0.0F * arrayOfFloat[4] + 1.0F * arrayOfFloat[5];
        return new PointF(leftTopX, leftTopY);
    }

    public static PointF getRightTop(Matrix matrix, int width) {
        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float rightTopX = width * arrayOfFloat[0] + 0.0F * arrayOfFloat[1] + 1.0F * arrayOfFloat[2];
        float rightTopY = width * arrayOfFloat[3] + 0.0F * arrayOfFloat[4] + 1.0F * arrayOfFloat[5];
        return new PointF(rightTopX, rightTopY);
    }

    public static PointF getLeftBottom(Matrix matrix, int height) {
        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float leftBottomX = 0.0F * arrayOfFloat[0] + height * arrayOfFloat[1] + 1.0F * arrayOfFloat[2];
        float leftBottomY = 0.0F * arrayOfFloat[3] + height * arrayOfFloat[4] + 1.0F * arrayOfFloat[5];
        return new PointF(leftBottomX, leftBottomY);
    }

    public static PointF getRightBottom(Matrix matrix, int width, int height) {
        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float rightBottomX = width * arrayOfFloat[0] + height * arrayOfFloat[1] + 1.0F * arrayOfFloat[2];
        float rightBottomY = width * arrayOfFloat[3] + height * arrayOfFloat[4] + 1.0F * arrayOfFloat[5];
        return new PointF(rightBottomX, rightBottomY);
    }

    /**
     * 判断点是否在一个矩形内部
     *
     * @param xRange {左上的x, 右上的x, 右下的x, 左下的x}
     * @param yRange {左上的y, 右上的y, 右下的y, 左下的y}
     * @param x
     * @param y
     * @return
     */
    public static boolean pointInRect(float[] xRange, float[] yRange, float x, float y) {
        //四条边的长度
        double a1 = Math.hypot(xRange[0] - xRange[1], yRange[0] - yRange[1]);
        double a2 = Math.hypot(xRange[1] - xRange[2], yRange[1] - yRange[2]);
        double a3 = Math.hypot(xRange[3] - xRange[2], yRange[3] - yRange[2]);
        double a4 = Math.hypot(xRange[0] - xRange[3], yRange[0] - yRange[3]);
        //待检测点到四个点的距离
        double b1 = Math.hypot(x - xRange[0], y - yRange[0]);
        double b2 = Math.hypot(x - xRange[1], y - yRange[1]);
        double b3 = Math.hypot(x - xRange[2], y - yRange[2]);
        double b4 = Math.hypot(x - xRange[3], y - yRange[3]);

        double u1 = (a1 + b1 + b2) / 2;
        double u2 = (a2 + b2 + b3) / 2;
        double u3 = (a3 + b3 + b4) / 2;
        double u4 = (a4 + b4 + b1) / 2;

        //矩形的面积
        double s = a1 * a2;
        //海伦公式 计算4个三角形面积
        double ss = Math.sqrt(u1 * (u1 - a1) * (u1 - b1) * (u1 - b2))
                + Math.sqrt(u2 * (u2 - a2) * (u2 - b2) * (u2 - b3))
                + Math.sqrt(u3 * (u3 - a3) * (u3 - b3) * (u3 - b4))
                + Math.sqrt(u4 * (u4 - a4) * (u4 - b4) * (u4 - b1));
        return Math.abs(s - ss) < 0.5;


    }
}
