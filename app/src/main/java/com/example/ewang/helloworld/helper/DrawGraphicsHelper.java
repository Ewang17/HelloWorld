package com.example.ewang.helloworld.helper;


import android.graphics.Path;

/**
 * Created by ewang on 2018/6/4.
 */

public class DrawGraphicsHelper {

    /**
     * 矢量旋转函数，计算末点的位置
     *
     * @param x       x分量
     * @param y       y分量
     * @param ang     旋转角度
     * @param isChLen 是否改变长度
     * @param newLen  箭头长度长度
     * @return 返回末点坐标
     */
    public static double[] rotateVec(int x, int y, double ang, boolean isChLen, double newLen) {
        double pointXY[] = new double[2];
        double vx = x * Math.cos(ang) - y * Math.sin(ang);
        double vy = x * Math.sin(ang) + y * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            pointXY[0] = vx / d * newLen;
            pointXY[1] = vy / d * newLen;
        }
        return pointXY;
    }


    /**
     * 画箭头
     *
     * @param startX 开始位置x坐标
     * @param startY 开始位置y坐标
     * @param endX   结束位置x坐标
     * @param endY   结束位置y坐标
     */
    public static void drawArrow(Path Path, int startX, int startY, int endX, int endY) {
        double lineLength = Math.sqrt(Math.pow(Math.abs(endX - startX), 2) + Math.pow(Math.abs(endY - startY), 2));//线当前长度
        double H = 0;// 箭头高度
        double L = 0;// 箭头长度
        if (lineLength < 320) {//防止箭头开始时过大
            H = lineLength / 4;
            L = lineLength / 6;
        } else { //超过一定线长箭头大小固定
            H = 80;
            L = 50;
        }

        double arrawAngle = Math.atan(L / H); // 箭头角度
        double arraowLen = Math.sqrt(L * L + H * H); // 箭头的长度
        double[] pointXY1 = rotateVec(endX - startX, endY - startY, arrawAngle, true, arraowLen);
        double[] pointXY2 = rotateVec(endX - startX, endY - startY, -arrawAngle, true, arraowLen);
        int x3 = (int) (endX - pointXY1[0]);//(x3,y3)为箭头一端的坐标
        int y3 = (int) (endY - pointXY1[1]);
        int x4 = (int) (endX - pointXY2[0]);//(x4,y4)为箭头另一端的坐标
        int y4 = (int) (endY - pointXY2[1]);
        // 画线
        Path.moveTo(startX, startY);
        Path.lineTo(endX, endY);
        Path.moveTo(x3, y3);
        Path.lineTo(endX, endY);
        Path.lineTo(x4, y4);
    }

    /**
     * 画三角形
     *
     * @param startX 开始位置x坐标
     * @param startY 开始位置y坐标
     * @param endX   结束位置x坐标
     * @param endY   结束位置y坐标
     */
    public static void drawTriangle(Path path, int startX, int startY, int endX, int endY) {
        //double lineLength = Math.sqrt(Math.pow(Math.abs(endX-startX),2) + Math.pow(Math.abs(endY-startY),2));//线当前长度
        double dx = Math.abs(endX - startX);
        double dy = Math.abs(endY - startY);
        double x1, y1;
        double lineLength = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));//线当前长度
        if (endX > startX) {
            x1 = Math.abs(endX - 2 * dx);
            y1 = endY;
        } else {
            x1 = Math.abs(endX + 2 * dx);
            y1 = endY;
        }


        path.moveTo(startX, startY);
        path.lineTo(endX, endY);
        path.lineTo((int) x1, (int) y1);
        path.close();
    }
}
