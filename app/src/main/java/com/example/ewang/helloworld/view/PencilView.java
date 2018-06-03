package com.example.ewang.helloworld.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;

import com.example.ewang.helloworld.helper.MyApplication;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ewang on 2018/6/2.
 */
public class PencilView extends View {

    // 保存Path路径的集合,用List集合来模拟栈
    private static List<DrawPath> savePath;

    // 保存已删除Path路径的集合
    private static List<DrawPath> deletePath;

    private DrawPath drawPath;

    private float startX, startY;

    private float lastX, lastY;

    private Paint mPaint;

    private Bitmap mBitmap;

    private Paint mBitmapPaint;// 画布的画笔

    private Canvas mCanvas;

    private Path mPath;

    private final int PAINT_FILL = 0;

    private final int PAINT_STROKE = 1;

    public static final int IN_PENCIL = 0;

    public static final int IN_ERASER = 1;

    //颜色集合
    private final int[] paintColor = new int[]{
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.BLACK, Color.WHITE, Color.GRAY, Color.CYAN
    };

    private static final float TOUCH_MIN_LENGTH = 4;

    private int currentEraserSize = 20;

    private int currentPencilSize = 20;

    private int currentPencilAlpha = 255;

    private int currentColor = paintColor[0];

    private int currentPaintStyle = PAINT_STROKE;

    private int currentPencilStatus = IN_PENCIL;

    private class DrawPath {
        public Path path;// 路径
        public Paint paint;// 画笔
    }


    public PencilView(Context context) {
        super(context);
        initCanvas();
        //设置默认样式，去除dis-in的黑色方框以及clear模式的黑线效果
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        savePath = new ArrayList<>();
        deletePath = new ArrayList<>();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        if (mPath != null) {
            // 实时的显示
            canvas.drawPath(mPath, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();

                // 每次down下去重新new一个Path
                mPath = new Path();
                //每一次记录的路径对象是不一样的
                drawPath = new DrawPath();
                drawPath.path = mPath;
                drawPath.paint = mPaint;

                mPath.moveTo(x, y);
                lastX = x;
                lastY = y;

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - lastX);
                float dy = Math.abs(lastY - y);
                if (dx >= TOUCH_MIN_LENGTH || dy >= TOUCH_MIN_LENGTH) {
                    mPath.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
                }
                lastX = x;
                lastY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mPath.lineTo(lastX, lastY);
                mCanvas.drawPath(mPath, mPaint);
                //将一条完整的路径保存下来(相当于入栈操作)
                mPath = null;// 重新置空
                savePath.add(drawPath);
                invalidate();
                break;
        }
        return true;
    }

    void initCanvas() {
        initPencilStyle();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        //画布大小
        mBitmap = Bitmap.createBitmap(MyApplication.getCanvasWidth(), MyApplication.getCanvasWidth(), Bitmap.Config.ARGB_8888);
        //mBitmap设置为透明色
        mBitmap.eraseColor(Color.argb(0, 0, 0, 0));
        mCanvas = new Canvas(mBitmap);  //所有mCanvas画的东西都被保存在了mBitmap中
        mCanvas.drawColor(Color.TRANSPARENT);
    }

    private void initPencilStyle() {
        mPaint = new Paint();
        if (currentPaintStyle == PAINT_FILL) {
            mPaint.setStyle(Paint.Style.FILL);
        } else {
            mPaint.setStyle(Paint.Style.STROKE);
        }
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(currentColor);

        //手动设置画笔样式
        mPaint.setAlpha(currentPencilAlpha);
        mPaint.setStrokeWidth(currentPencilSize);
    }

    private void initEraserStyle() {
        mPaint = new Paint();
        if (currentPaintStyle == PAINT_FILL) {
            mPaint.setStyle(Paint.Style.FILL);
        } else {
            mPaint.setStyle(Paint.Style.STROKE);
        }
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.TRANSPARENT);

        mPaint.setAlpha(0);
        mPaint.setStrokeWidth(currentEraserSize);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

    public void setPencilStyle(boolean reset, int currentPencilStatus, int size, int alpha) {
        if (currentPencilStatus == IN_PENCIL) {
            currentPencilSize = reset ? 20 : size;
            currentPencilAlpha = reset ? 255 : alpha;
            initPencilStyle();
        } else if (currentPencilStatus == IN_ERASER) {
            currentEraserSize = reset ? 20 : size;
            initEraserStyle();
        }
    }

    /**
     * 撤销
     * 撤销的核心思想就是将画布清空，
     * 将保存下来的Path路径最后一个移除掉，
     * 重新将路径画在画布上面。
     */
    public void undo() {
        if (savePath != null && savePath.size() > 0) {
            DrawPath drawPath = savePath.get(savePath.size() - 1);
            deletePath.add(drawPath);
            savePath.remove(savePath.size() - 1);
            redrawOnBitmap();
        }
    }

    /**
     * 重做
     */
    public void redo() {
        if (savePath != null && savePath.size() > 0) {
            savePath.clear();
            redrawOnBitmap();
        }
    }

    /**
     * 恢复，恢复的核心就是将删除的那条路径重新添加到savepath中重新绘画即可
     */
    public void recover() {
        if (deletePath.size() > 0) {
            //将删除的路径列表中的最后一个，也就是最顶端路径取出（栈）,并加入路径保存列表中
            DrawPath dp = deletePath.get(deletePath.size() - 1);
            savePath.add(dp);
            //将取出的路径重绘在画布上

            mCanvas.drawPath(dp.path, dp.paint);
            //将该路径从删除的路径列表中去除
            deletePath.remove(deletePath.size() - 1);

            invalidate();
        }
    }

    private void redrawOnBitmap() {
        initCanvas();
        Iterator<DrawPath> iter = savePath.iterator();
        while (iter.hasNext()) {
            DrawPath drawPath = iter.next();
            mCanvas.drawPath(drawPath.path, drawPath.paint);
        }
        invalidate();// 刷新
    }

    public int getCurrentPencilStatus() {
        return currentPencilStatus;
    }

    public void setCurrentPencilStatus(int currentPencilStatus) {
        this.currentPencilStatus = currentPencilStatus;
    }

    public int getCurrentPencilSize() {
        return currentPencilSize;
    }

    public void setCurrentPencilSize(int currentPencilSize) {
        this.currentPencilSize = currentPencilSize;
    }

    public void setCurrentPencilAlpha(int currentPencilAlpha) {
        this.currentPencilAlpha = currentPencilAlpha;
    }

    public int getCurrentPencilAlpha() {
        return currentPencilAlpha;
    }

    public int getCurrentEraserSize() {
        return currentEraserSize;
    }

    public void setCurrentEraserSize(int currentEraserSize) {
        this.currentEraserSize = currentEraserSize;
    }
}