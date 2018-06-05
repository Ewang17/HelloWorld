package com.example.ewang.helloworld.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by ewang on 2018/6/3.
 */

public class ChildDrawView extends BaseCanvasView {
    private Bitmap mainBitmap;
    private PencilView pencilView;

    public ChildDrawView(Context context, PencilView pencilView) {
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
