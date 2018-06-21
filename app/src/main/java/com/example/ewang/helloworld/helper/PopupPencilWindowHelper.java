package com.example.ewang.helloworld.helper;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ewang.helloworld.R;
import com.example.ewang.helloworld.adapter.ShapeAdapter;
import com.example.ewang.helloworld.constants.PaintGraphics;
import com.example.ewang.helloworld.constants.PaintStatus;
import com.example.ewang.helloworld.constants.ShapeStyle;
import com.example.ewang.helloworld.model.client.Shape;
import com.example.ewang.helloworld.view.PencilView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ewang on 2018/6/2.
 */

public class PopupPencilWindowHelper implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Context context;

    ImageView popSure, popCancel, plus, minus, plus2, minus2;
    SeekBar seekBar1, seekBar2;
    Button reset;
    TextView text_size, text_alpha;
    RadioGroup radioGroupShape;
    RecyclerView recyclerViewShape;


    PencilView pencilView;
    PopupWindow pencil_window;

    private PaintGraphics currentPaintGraphics = PaintGraphics.DRAW_LINE;

    public PopupPencilWindowHelper(PencilView pencilView, Context context) {
        this.pencilView = pencilView;
        this.context = context;
    }

    public void showPencilStyle(View parentView, PopupWindow.OnDismissListener onDismissListener) {
        View winContentView = LayoutInflater.from(context).inflate(
                R.layout.popup_window_pencil_style, null);
        pencil_window = new PopupWindow(winContentView, (int) (MyApplication.getScreenWidth() * 0.8),
                (int) (MyApplication.getScreenHeight() * 0.6), true);
        pencil_window.setOnDismissListener(onDismissListener);
        initPencilPopWinListener(winContentView);
        if (pencilView.getCurrentPaintStatus() != PaintStatus.IN_SHAPE) {
            currentPaintGraphics = PaintGraphics.DRAW_LINE;
        }
        pencil_window.showAtLocation(parentView, Gravity.CENTER, 0, 0);

    }


    private void initPencilPopWinListener(View winContentView) {
        reset = winContentView.findViewById(R.id.reset);
        popSure = winContentView.findViewById(R.id.popbtn_sure);
        popCancel = winContentView.findViewById(R.id.popbtn_cancel);
        plus = winContentView.findViewById(R.id.plus);
        minus = winContentView.findViewById(R.id.minus);
        plus2 = winContentView.findViewById(R.id.plus2);
        minus2 = winContentView.findViewById(R.id.minus2);
        seekBar1 = winContentView.findViewById(R.id.size_seekbar);
        seekBar2 = winContentView.findViewById(R.id.size_seekbar2);
        text_size = winContentView.findViewById(R.id.text_size);
        text_alpha = winContentView.findViewById(R.id.text_alpha);
        radioGroupShape = winContentView.findViewById(R.id.radioGroup);
        recyclerViewShape = winContentView.findViewById(R.id.recycler_view_shape);

        seekBar1.setProgress((pencilView.getCurrentPaintStatus() == PaintStatus.IN_ERASER ? pencilView.getCurrentEraserSize() : pencilView.getCurrentPencilSize()) / 2);

        if (pencilView.getCurrentPaintStatus() == PaintStatus.IN_ERASER) {
            seekBar2.setEnabled(false);
        } else {
            seekBar2.setProgress(pencilView.getCurrentPencilAlpha() * 100 / 255);
        }

        text_size.setText("尺寸：" + seekBar1.getProgress());
        text_alpha.setText("透明度：" + seekBar2.getProgress() + "%");

        reset.setOnClickListener(this);
        popSure.setOnClickListener(this);
        popCancel.setOnClickListener(this);
        plus.setOnClickListener(this);
        minus.setOnClickListener(this);
        plus2.setOnClickListener(this);
        minus2.setOnClickListener(this);
        seekBar1.setOnSeekBarChangeListener(this);
        seekBar2.setOnSeekBarChangeListener(this);

        radioGroupShape.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radiobtn = winContentView.findViewById(checkedId);
                if (radiobtn.getId() == R.id.fill_shape) {
                    pencilView.setCurrentShapeStyle(ShapeStyle.PAINT_FILL);
                    seekBar1.setEnabled(false);
                } else if (radiobtn.getId() == R.id.stroke_shape) {
                    pencilView.setCurrentShapeStyle(ShapeStyle.PAINT_STROKE);
                    seekBar1.setEnabled(true);
                }
            }
        });

        if (pencilView.getCurrentPaintStatus() == PaintStatus.IN_SHAPE) {
            if (pencilView.getCurrentShapeStyle() == ShapeStyle.PAINT_FILL) {
                radioGroupShape.check(R.id.fill_shape);
            } else if (pencilView.getCurrentShapeStyle() == ShapeStyle.PAINT_STROKE) {
                radioGroupShape.check(R.id.stroke_shape);
            }
            radioGroupShape.setVisibility(View.VISIBLE);
            recyclerViewShape.setVisibility(View.VISIBLE);
            reset.setVisibility(View.INVISIBLE);
            initShapeRecyclerView();
        } else {
            radioGroupShape.setVisibility(View.INVISIBLE);
            recyclerViewShape.setVisibility(View.INVISIBLE);
            reset.setVisibility(View.VISIBLE);
        }

    }

    void initShapeRecyclerView() {
        Shape circle = new Shape(PaintGraphics.DRAW_CIRCLE.getValue(), "circle", R.drawable.ic_shape_circle);
        Shape rectangle = new Shape(PaintGraphics.DRAW_RECTANGLE.getValue(), "rectangle", R.drawable.ic_shape_rectangle);
        Shape triangle = new Shape(PaintGraphics.DRAW_TRIANGLE.getValue(), "triangle", R.drawable.ic_shape_triangle);
        Shape arrow = new Shape(PaintGraphics.DRAW_ARROW.getValue(), "arrow", R.drawable.ic_shape_arrow);

        List<Shape> shapeList = new ArrayList<>();
        shapeList.add(circle);
        shapeList.add(rectangle);
        shapeList.add(triangle);
        shapeList.add(arrow);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewShape.setLayoutManager(linearLayoutManager);
        ShapeAdapter shapeAdapter = new ShapeAdapter(shapeList, this);
        recyclerViewShape.setAdapter(shapeAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset:
                pencilView.setPencilStyle(true, 0, 0, currentPaintGraphics);
                break;
            case R.id.popbtn_sure:
                pencilView.setPencilStyle(false, seekBar1.getProgress() * 2,
                        (seekBar2.getProgress() * 255 / 100), currentPaintGraphics);
                pencil_window.dismiss();
                break;
            case R.id.popbtn_cancel:
                pencil_window.dismiss();
                break;
            case R.id.plus:
                seekBar1.setProgress(seekBar1.getProgress() + 1);
                text_size.setText("尺寸：" + seekBar1.getProgress());
                break;
            case R.id.minus:
                seekBar1.setProgress(seekBar1.getProgress() - 1);
                text_size.setText("尺寸：" + seekBar1.getProgress());

                break;
            case R.id.plus2:
                seekBar2.setProgress(seekBar2.getProgress() + 1);
                text_alpha.setText("透明度：" + seekBar2.getProgress() + "%");
                break;
            case R.id.minus2:
                seekBar2.setProgress(seekBar2.getProgress() - 1);
                text_alpha.setText("透明度：" + seekBar2.getProgress() + "%");
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.size_seekbar:
                text_size.setText("尺寸：" + seekBar1.getProgress());
                break;
            case R.id.size_seekbar2:
                text_alpha.setText("透明度：" + seekBar2.getProgress() + "%");
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void setCurrentPaintGraphics(PaintGraphics currentPaintGraphics) {
        this.currentPaintGraphics = currentPaintGraphics;
    }

    public PaintGraphics getCurrentPaintGraphics() {
        return currentPaintGraphics;
    }
}
