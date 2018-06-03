package com.example.ewang.helloworld.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by ewang on 2018/6/3.
 */

public class DrawView extends BaseCanvasView {
    private Bitmap mainBitmap;
    private PencilView pencilView;

    public DrawView(Context context, PencilView pencilView) {
        super(context);

        Bitmap bitmap = pencilView.getTempBitmap();
        mainBitmap = bitmap;
        this.pencilView = pencilView;

        matrix.reset();

        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();

        initIcons();
        initPaint();
        initScale(bitmap);

        //matrix.postScale((float) canvasWidth / (float) bitmapWidth, (float) canvasHeight / (float) bitmapHeight, bitmapWidth / 2f, bitmapHeight / 2f);
        //matrix.postTranslate(canvasWidth / 2f - bitmapWidth / 2f, canvasHeight / 2f - bitmapHeight / 2f);

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

    public PencilView getPencilView() {
        return pencilView;
    }
}
