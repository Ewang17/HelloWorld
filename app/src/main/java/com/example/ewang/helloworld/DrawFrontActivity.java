package com.example.ewang.helloworld;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.ewang.helloworld.service.BaseActivity;
import com.example.ewang.helloworld.view.CanvasView;


public class DrawFrontActivity extends BaseActivity {

    FrameLayout layoutCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_front);
        layoutCanvas = findViewById(R.id.layout_view_canvas);
        CanvasView canvasView = new CanvasView(DrawFrontActivity.this);
        layoutCanvas.addView(canvasView);
    }
}
