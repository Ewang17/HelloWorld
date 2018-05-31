package com.example.ewang.helloworld.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.example.ewang.helloworld.R;
import com.example.ewang.helloworld.helper.MyApplication;
import com.example.ewang.helloworld.helper.RectHelper;

/**
 * Created by ewang on 2018/5/29.
 */

public class CanvasView extends View {

    private Bitmap mainBitmap;

    private Bitmap deleteIcon;

    private Bitmap resizeIcon;

    private int canvasWidth, canvasHeight;

    private int bitmapWidth, bitmapHeight;

    private int deleteIconWidth, deleteIconHeight;

    private int resizeIconWidth, resizeIconHeight;

    public Rect rect_delete;

    public Rect rect_resize;

    private Paint strokePaint;

    private float MIN_SCALE = 0.5f;

    private float MAX_SCALE = 1.2f;

    private static final float ICON_SCALE = 0.7f;

    private boolean inEdit = true;

    private boolean inResize;

    private boolean inBitmap;

    //记录每次移动的横、纵坐标
    private float lastX, lastY;

    //触摸的位置和图片左上角位置的中点
    private PointF mid = new PointF();

    //记录每次旋转的角度
    private float lastRotateDegree;

    //记录每次缩放的大小
    private float lastlengthFromTouchToMid;

    //原图片对角线长度的一半
    private double halfDiagonalLength;

    private OperationListener operationListener;

    private Matrix matrix = new Matrix();

    public Matrix getMatrixInstance() {
        return matrix;
    }

    public CanvasView(Context context, Bitmap bitmap) {
        super(context);
        mainBitmap = bitmap;

        matrix.reset();

        halfDiagonalLength = Math.hypot(mainBitmap.getWidth(), mainBitmap.getHeight()) / 2;

        canvasWidth = MyApplication.getCanvasWidth();
        canvasHeight = MyApplication.getCanvasHeight();

        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();

        initIcons();
        initPaint();
        initScale(bitmap);

        float initScale = (MIN_SCALE + MAX_SCALE) / 2;
        matrix.postScale(initScale, initScale, bitmapWidth / 2, bitmapHeight / 2);
        matrix.postTranslate(canvasWidth / 2 - bitmapWidth / 2, canvasHeight / 2 - bitmapHeight / 2);
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mainBitmap == null) {
            return;
        }
        canvas.save();
        canvas.drawBitmap(mainBitmap, matrix, null);

        drawIconsSize(canvas);
        canvas.restore();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = true;
        if (handled && operationListener != null) {
            operationListener.onEdit(this);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isInOperate(event, rect_delete)) {
                    if (operationListener != null) {
                        operationListener.onDeleteClick();
                    }
                } else if (isInOperate(event, rect_resize)) {
                    inResize = true;
                    lastRotateDegree = rotationDegreeToLeftTop(event);
                    setMidPointFromTouchToLeftTop(event);
                    lastlengthFromTouchToMid = lengthFromTouchToMid(event);
                } else if (isInBitmap(event)) {
                    inBitmap = true;
                    lastX = event.getX(0);
                    lastY = event.getY(0);
                } else {
                    handled = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (inResize) {
                    matrix.postRotate((rotationDegreeToLeftTop(event) - lastRotateDegree) * 2, mid.x, mid.y);
                    lastRotateDegree = rotationDegreeToLeftTop(event);

                    float scale = lengthFromTouchToMid(event) / lastlengthFromTouchToMid;
                    lastlengthFromTouchToMid = lengthFromTouchToMid(event);
                    matrix.postScale(scale, scale, mid.x, mid.y);
                    invalidate();
                } else if (inBitmap) {
                    float x = event.getX(0);
                    float y = event.getY(0);
                    matrix.postTranslate(x - lastX, y - lastY);
                    lastX = x;
                    lastY = y;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                inResize = false;
                inBitmap = false;
                break;

        }
        return handled;
    }

    void initScale(Bitmap bitmap) {
        //当图片的宽比高大时 按照宽计算 缩放大小根据图片的大小而改变 最小为图片的1/8 最大为屏幕宽
        if (bitmap.getWidth() >= bitmap.getHeight()) {
            float minWidth = canvasWidth / 8;
            if (bitmap.getWidth() < minWidth) {
                MIN_SCALE = 1f;
            } else {
                MIN_SCALE = 1.0f * minWidth / bitmap.getWidth();
            }

            if (bitmap.getWidth() > canvasWidth) {
                MAX_SCALE = 1;
            } else {
                MAX_SCALE = 1.0f * canvasWidth / bitmap.getWidth();
            }
        } else {
            //当图片高比宽大时，按照图片的高计算
            float minHeight = canvasWidth / 8;
            if (bitmap.getHeight() < minHeight) {
                MIN_SCALE = 1f;
            } else {
                MIN_SCALE = 1.0f * minHeight / bitmap.getHeight();
            }

            if (bitmap.getHeight() > canvasWidth) {
                MAX_SCALE = 1;
            } else {
                MAX_SCALE = 1.0f * canvasWidth / bitmap.getHeight();
            }
        }
    }

    void initIcons() {
        deleteIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_delete);
        resizeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_resize);

        deleteIconWidth = (int) (deleteIcon.getWidth() * ICON_SCALE);
        deleteIconHeight = (int) (deleteIcon.getHeight() * ICON_SCALE);

        resizeIconWidth = (int) (resizeIcon.getWidth() * ICON_SCALE);
        resizeIconHeight = (int) (resizeIcon.getHeight() * ICON_SCALE);

        rect_delete = new Rect();
        rect_resize = new Rect();
    }

    void initPaint() {
        strokePaint = new Paint();
        strokePaint.setColor(getResources().getColor(R.color.colorPrimary));
        strokePaint.setAntiAlias(true);
        strokePaint.setDither(true);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2.0f);
    }

    void drawIconsSize(Canvas canvas) {

        PointF leftTop = RectHelper.getLeftTop(matrix);
        PointF rightTop = RectHelper.getRightTop(matrix, bitmapWidth);
        PointF leftBottom = RectHelper.getLeftBottom(matrix, bitmapHeight);
        PointF rightBottom = RectHelper.getRightBottom(matrix, bitmapWidth, bitmapHeight);

        //删除在右上角
        rect_delete.left = (int) (rightTop.x - deleteIconWidth / 2);
        rect_delete.right = (int) (rightTop.x + deleteIconWidth / 2);
        rect_delete.top = (int) (rightTop.y - deleteIconHeight / 2);
        rect_delete.bottom = (int) (rightTop.y + deleteIconHeight / 2);

        //拉伸在右下角
        rect_resize.left = (int) (rightBottom.x - resizeIconWidth / 2);
        rect_resize.right = (int) (rightBottom.x + resizeIconWidth / 2);
        rect_resize.top = (int) (rightBottom.y - resizeIconHeight / 2);
        rect_resize.bottom = (int) (rightBottom.y + resizeIconHeight / 2);

        if (inEdit) {
            canvas.drawLine(leftTop.x, leftTop.y, rightTop.x, rightTop.y, strokePaint);
            canvas.drawLine(rightTop.x, rightTop.y, rightBottom.x, rightBottom.y, strokePaint);
            canvas.drawLine(rightBottom.x, rightBottom.y, leftBottom.x, leftBottom.y, strokePaint);
            canvas.drawLine(leftBottom.x, leftBottom.y, leftTop.x, leftTop.y, strokePaint);

            canvas.drawBitmap(deleteIcon, null, rect_delete, null);
            canvas.drawBitmap(resizeIcon, null, rect_resize, null);
        }
    }

    public void setInEdit(boolean inEdit) {
        this.inEdit = inEdit;
        invalidate();
    }

    public boolean isInEdit() {
        return inEdit;
    }

    public void setOperationListener(OperationListener operationListener) {
        this.operationListener = operationListener;
    }

    /**
     * 触摸的位置和图片左上角位置的中点
     *
     * @param event
     */
    private void setMidPointFromTouchToLeftTop(MotionEvent event) {
        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float f1 = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = f1 + event.getX(0);
        float f4 = f2 + event.getY(0);
        mid.set(f3 / 2, f4 / 2);
    }

    /**
     * 在滑动旋转过程中,总是以左上角原点作为绝对坐标计算偏转角度
     *
     * @param event
     * @return
     */
    private float rotationDegreeToLeftTop(MotionEvent event) {
        PointF leftTop = RectHelper.getLeftTop(matrix);
        double arc = Math.atan2(event.getY(0) - leftTop.y, event.getX(0) - leftTop.x);
        return (float) Math.toDegrees(arc);
    }

    /**
     * 触摸点到矩形中点的距离
     *
     * @param event
     * @return
     */
    private float lengthFromTouchToMid(MotionEvent event) {
        float lengthFromTouchToMid = (float) Math.hypot(event.getX(0) - mid.x, event.getY(0) - mid.y);
        return lengthFromTouchToMid;
    }

    /**
     * 是否在四条线内部
     * 图片旋转后 可能存在菱形状态 不能用4个点的坐标范围去判断点击区域是否在图片内
     *
     * @return
     */
    public boolean isInBitmap(MotionEvent event) {
        PointF leftTop = RectHelper.getLeftTop(matrix);
        PointF rightTop = RectHelper.getRightTop(matrix, bitmapWidth);
        PointF leftBottom = RectHelper.getLeftBottom(matrix, bitmapHeight);
        PointF rightBottom = RectHelper.getRightBottom(matrix, bitmapWidth, bitmapHeight);

        //确定X方向的范围 {左上的x, 右上的x, 右下的x, 左下的x}
        //确定Y方向的范围 {左上的y, 右上的y, 右下的y, 左下的y}
        return RectHelper.pointInRect(new float[]{leftTop.x, rightTop.x, rightBottom.x, leftBottom.x},
                new float[]{leftTop.y, rightTop.y, rightBottom.y, leftBottom.y},
                event.getX(0), event.getY(0));
    }

    public boolean isInOperate(MotionEvent event, Rect rect) {
        int left = rect.left + (rect == rect_resize ? -20 : 0);
        int right = rect.right + (rect == rect_resize ? -20 : 0);
        int top = rect.top + (rect == rect_resize ? -20 : 0);
        int bottom = rect.bottom + (rect == rect_resize ? -20 : 0);
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(0) <= bottom;
    }

}
