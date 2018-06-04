package com.example.ewang.helloworld.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.example.ewang.helloworld.constants.PaintColor;
import com.example.ewang.helloworld.constants.PaintGraphics;
import com.example.ewang.helloworld.constants.PaintStatus;
import com.example.ewang.helloworld.constants.ShapeStyle;
import com.example.ewang.helloworld.helper.DrawGraphicsHelper;
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

    private Paint mainPaint;

    private Bitmap tempBitmap;

    private Paint bitmapPaint;// 画布的画笔

    private Canvas tempCanvas;

    private Path tempPath;

    private static final float TOUCH_MIN_LENGTH = 4;

    private int currentEraserSize = 20;

    private int currentPencilSize = 20;

    private int currentPencilAlpha = 255;

    private PaintColor currentColor = PaintColor.fromIndex(0);

    private ShapeStyle currentShapeStyle = ShapeStyle.PAINT_STROKE;

    private PaintStatus currentPaintStatus = PaintStatus.IN_PENCIL;

    private PaintGraphics currentPaintGraphics = PaintGraphics.DRAW_LINE;

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
        canvas.drawBitmap(tempBitmap, 0, 0, bitmapPaint);
        if (tempPath != null) {
            // 实时的显示
            canvas.drawPath(tempPath, mainPaint);
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
                tempPath = new Path();
                //每一次记录的路径对象是不一样的
                drawPath = new DrawPath();
                drawPath.path = tempPath;
                drawPath.paint = mainPaint;

                tempPath.moveTo(x, y);
                lastX = x;
                lastY = y;

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (currentPaintGraphics == PaintGraphics.DRAW_LINE) {
                    tempPath.lineTo(lastX, lastY);
                }
                tempCanvas.drawPath(tempPath, mainPaint);
                //将一条完整的路径保存下来(相当于入栈操作)
                tempPath = null;// 重新置空
                savePath.add(drawPath);
                invalidate();
                break;
        }
        return true;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - lastX);
        float dy = Math.abs(lastY - y);
        if (dx >= TOUCH_MIN_LENGTH || dy >= TOUCH_MIN_LENGTH) {
            if (currentPaintGraphics == PaintGraphics.DRAW_LINE) {
                // 从x1,y1到x2,y2画一条贝塞尔曲线，更平滑(直接用tempPath.lineTo也可以)
                tempPath.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
            } else if (currentPaintGraphics == PaintGraphics.DRAW_CIRCLE) {
                tempPath.reset();
                RectF ovalRectF = new RectF(startX, startY, x, y);
                //画椭圆
                tempPath.addOval(ovalRectF, Path.Direction.CCW);
            } else if (currentPaintGraphics == PaintGraphics.DRAW_RECTANGLE) {
                tempPath.reset();
                RectF rectF = new RectF(startX, startY, x, y);
                tempPath.addRect(rectF, Path.Direction.CCW);
            } else if (currentPaintGraphics == PaintGraphics.DRAW_ARROW) {
                tempPath.reset();
                DrawGraphicsHelper.drawArrow(tempPath, (int) startX, (int) startY, (int) x, (int) y);
            } else if (currentPaintGraphics == PaintGraphics.DRAW_TRIANGLE) {
                tempPath.reset();
                DrawGraphicsHelper.drawTriangle(tempPath, (int) startX, (int) startY, (int) x, (int) y);
            }

            lastX = x;
            lastY = y;
        }
    }

    void initCanvas() {
        initPencilStyle();
        bitmapPaint = new Paint(Paint.DITHER_FLAG);

        tempBitmap = Bitmap.createBitmap(MyApplication.getCanvasWidth(), MyApplication.getCanvasHeight(), Bitmap.Config.ARGB_8888);
        //tempBitmap设置为透明色
        tempBitmap.eraseColor(Color.argb(0, 0, 0, 0));
        tempCanvas = new Canvas(tempBitmap);  //所有tempCanvas画的东西都被保存在了mBitmap中
        tempCanvas.drawColor(Color.TRANSPARENT);
    }

    private void initPencilStyle() {
        mainPaint = new Paint();
        if (currentShapeStyle == ShapeStyle.PAINT_FILL) {
            mainPaint.setStyle(Paint.Style.FILL);
        } else {
            mainPaint.setStyle(Paint.Style.STROKE);
        }
        mainPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mainPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
        mainPaint.setAntiAlias(true);
        mainPaint.setDither(true);
        mainPaint.setColor(currentColor.getValue());

        //手动设置画笔样式
        mainPaint.setAlpha(currentPencilAlpha);
        mainPaint.setStrokeWidth(currentPencilSize);
    }

    private void initEraserStyle() {
        mainPaint = new Paint();
        if (currentShapeStyle == ShapeStyle.PAINT_FILL) {
            mainPaint.setStyle(Paint.Style.FILL);
        } else {
            mainPaint.setStyle(Paint.Style.STROKE);
        }
        mainPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mainPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
        mainPaint.setAntiAlias(true);
        mainPaint.setDither(true);
        mainPaint.setColor(Color.TRANSPARENT);

        mainPaint.setAlpha(0);
        mainPaint.setStrokeWidth(currentEraserSize);

        mainPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

    public void setPencilStyle(boolean reset, PaintStatus currentPencilStatus, int size, int alpha, PaintGraphics paintGraphics) {
        currentPaintGraphics = paintGraphics;
        if (currentPencilStatus == PaintStatus.IN_ERASER) {
            currentEraserSize = reset ? 20 : size;
            initEraserStyle();
        } else {
            currentPencilSize = reset ? 20 : size;
            currentPencilAlpha = reset ? 255 : alpha;
            initPencilStyle();
        }
    }

    public void setPaintColor(int colorIndex) {
        currentColor = PaintColor.fromIndex(colorIndex);
        initPencilStyle();
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

            tempCanvas.drawPath(dp.path, dp.paint);
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
            tempCanvas.drawPath(drawPath.path, drawPath.paint);
        }
        invalidate();// 刷新
    }

    public PaintStatus getCurrentPaintStatus() {
        return currentPaintStatus;
    }

    public void setCurrentPaintStatus(PaintStatus currentPaintStatus) {
        this.currentPaintStatus = currentPaintStatus;
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

    public Bitmap getTempBitmap() {
        return tempBitmap;
    }

    public Canvas getTempCanvas() {
        return tempCanvas;
    }

    public PaintColor getCurrentColor() {
        return currentColor;
    }

    public void setCurrentShapeStyle(ShapeStyle currentShapeStyle) {
        this.currentShapeStyle = currentShapeStyle;
    }

    public ShapeStyle getCurrentShapeStyle() {
        return currentShapeStyle;
    }

    public PaintGraphics getCurrentPaintGraphics() {
        return currentPaintGraphics;
    }
}