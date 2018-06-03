package com.example.ewang.helloworld.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by ewang on 2018/5/29.
 */

public class PhotoView extends BaseCanvasView {

    private Bitmap mainBitmap;

    public PhotoView(Context context, Bitmap bitmap) {
        super(context);
        mainBitmap = bitmap;

        matrix.reset();

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

}
