package com.example.ewang.helloworld.helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.example.ewang.helloworld.R;
import com.example.ewang.helloworld.constants.PaintGraphics;
import com.example.ewang.helloworld.constants.PaintStatus;
import com.example.ewang.helloworld.view.ColorPickerDialog;
import com.example.ewang.helloworld.view.PencilView;

import java.util.function.Function;

/**
 * Created by ewang on 2018/6/5.
 */

public class PopupDrawToolWindowHelper implements View.OnClickListener {

    ImageView cancel, undo, recover, done;
    ImageView pencil, eraser, shape, color;

    private PencilView pencilView;

    private Activity parentActivity;

    private View parentView;

    private PopupPencilWindowHelper popupPencilWindowHelper;

    private PopupWindow popupWindowTop, popupWindowBottom;

    private Context context;

    private Function onCancelClick, onDoneClick;

    public PopupDrawToolWindowHelper(Context context, PencilView pencilView, View parentView, Activity parentActivity) {
        this.context = context;
        this.pencilView = pencilView;
        this.parentActivity = parentActivity;
        this.parentView = parentView;
        this.popupPencilWindowHelper = new PopupPencilWindowHelper(pencilView, parentActivity);
    }

    public void popDrawTool() {
        popupWindowBottom = popBottomTool();
        popupWindowTop = popTopTool();

        popupWindowBottom.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
        popupWindowTop.showAtLocation(parentView, Gravity.TOP, 0, 0);
    }

    public void dismissDrawTool() {
        popupWindowTop.dismiss();
        popupWindowBottom.dismiss();
    }

    private PopupWindow popBottomTool() {
        View windowContentView = LayoutInflater.from(context).inflate(R.layout.popup_window_draw_tool_bottom, null);
        PopupWindow popupWindowBottom = new PopupWindow(windowContentView, ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (MyApplication.getScreenHeight() * 0.1f), true);
        popupWindowBottom.setOutsideTouchable(false);
        popupWindowBottom.setFocusable(false);
        popupWindowBottom.setAnimationStyle(R.style.translate_bottom);

        pencil = windowContentView.findViewById(R.id.image_draw_pencil);
        eraser = windowContentView.findViewById(R.id.image_draw_eraser);
        shape = windowContentView.findViewById(R.id.image_draw_shape);
        color = windowContentView.findViewById(R.id.image_draw_color);

        pencil.setOnClickListener(this);
        eraser.setOnClickListener(this);
        color.setOnClickListener(this);
        shape.setOnClickListener(this);
        return popupWindowBottom;
    }

    private PopupWindow popTopTool() {
        View windowContentView = LayoutInflater.from(context).inflate(R.layout.popup_window_draw_tool_top, null);
        PopupWindow popupWindowTop = new PopupWindow(windowContentView, ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (MyApplication.getScreenHeight() * 0.1f), true);
        popupWindowTop.setOutsideTouchable(false);
        popupWindowTop.setFocusable(false);
        popupWindowTop.setAnimationStyle(R.style.translate_top);

        cancel = windowContentView.findViewById(R.id.image_draw_cancel);
        undo = windowContentView.findViewById(R.id.image_draw_undo);
        recover = windowContentView.findViewById(R.id.image_draw_recover);
        done = windowContentView.findViewById(R.id.image_draw_done);

        cancel.setOnClickListener(this);
        undo.setOnClickListener(this);
        recover.setOnClickListener(this);
        done.setOnClickListener(this);
        return popupWindowTop;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_draw_pencil:
                pencil.setColorFilter(R.color.pink);
                eraser.setColorFilter(null);
                shape.setColorFilter(null);
                if (pencilView.getCurrentPaintStatus() == PaintStatus.IN_PENCIL) {
                    setBackgroundWindowDim();
                    popupPencilWindowHelper.showPencilStyle(parentView, getOnDismissListener());
                } else {
                    pencilView.setCurrentPaintStatus(PaintStatus.IN_PENCIL);
                    pencilView.setPencilStyle(false, pencilView.getCurrentPencilSize(),
                            pencilView.getCurrentPencilAlpha(), PaintGraphics.DRAW_LINE);
                }
                break;
            case R.id.image_draw_eraser:
                eraser.setColorFilter(R.color.pink);
                pencil.setColorFilter(null);
                shape.setColorFilter(null);
                if (pencilView.getCurrentPaintStatus() == PaintStatus.IN_ERASER) {
                    setBackgroundWindowDim();
                    popupPencilWindowHelper.showPencilStyle(parentView, getOnDismissListener());
                } else {
                    pencilView.setCurrentPaintStatus(PaintStatus.IN_ERASER);
                    pencilView.setPencilStyle(false, pencilView.getCurrentEraserSize(),
                            0, PaintGraphics.DRAW_LINE);
                }
                break;
            case R.id.image_draw_shape:
                pencilView.setCurrentPaintStatus(PaintStatus.IN_SHAPE);
                shape.setColorFilter(R.color.pink);
                pencil.setColorFilter(null);
                eraser.setColorFilter(null);
                setBackgroundWindowDim();
                popupPencilWindowHelper.showPencilStyle(parentView, getOnDismissListener());
                break;
            case R.id.image_draw_color:
                ColorPickerDialog dialog = new ColorPickerDialog(parentActivity, pencilView.getCurrentColorValue(),
                        "ColorPicker",
                        new ColorPickerDialog.OnColorChangedListener() {

                            @Override
                            public void colorChanged(int color) {
                                pencilView.setCurrentColorValue(color);
                            }
                        });
                dialog.show();
                break;
            case R.id.image_draw_cancel:
                onCancelClick.apply(pencil);
                break;
            case R.id.image_draw_undo:
                pencilView.undo();
                break;
            case R.id.image_draw_recover:
                pencilView.recover();
                break;
            case R.id.image_draw_done:
                onDoneClick.apply(pencil);
                break;
        }
    }

    private void setBackgroundWindowDim() {
        WindowManager.LayoutParams lp = parentActivity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        parentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        parentActivity.getWindow().setAttributes(lp);
    }

    private PopupWindow.OnDismissListener getOnDismissListener() {
        return new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = parentActivity.getWindow().getAttributes();
                lp.alpha = 1.0f;
                parentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                parentActivity.getWindow().setAttributes(lp);
            }
        };
    }

    public void setOnCancelClick(Function onCancelClick) {
        this.onCancelClick = onCancelClick;
    }

    public void setOnDoneClick(Function onDoneClick) {
        this.onDoneClick = onDoneClick;
    }
}
