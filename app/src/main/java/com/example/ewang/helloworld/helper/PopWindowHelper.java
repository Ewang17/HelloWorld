package com.example.ewang.helloworld.helper;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ewang.helloworld.R;
import com.example.ewang.helloworld.view.PencilView;

/**
 * Created by ewang on 2018/6/2.
 */

public class PopWindowHelper implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    ImageView popSure, popCancel, plus, minus, plus2, minus2;
    SeekBar seekBar1, seekBar2;
    Button reset;
    TextView text_size, text_alpha;


    PencilView pencilView;
    PopupWindow pencil_window;

    public PopWindowHelper(PencilView pencilView) {
        this.pencilView = pencilView;
    }

    public void showPencilStyle(Context context, View parentView, PopupWindow.OnDismissListener onDismissListener) {
        View winContentView = LayoutInflater.from(context).inflate(
                R.layout.activity_pencil_style, null);
        pencil_window = new PopupWindow(winContentView, (int) (MyApplication.getScreenWidth() * 0.8),
                (int) (MyApplication.getScreenHeight() * 0.6), true);
        pencil_window.setOnDismissListener(onDismissListener);
        initPencilPopWinListener(winContentView);
        pencil_window.showAtLocation(parentView, Gravity.CENTER, 0, 0);

    }


    private void initPencilPopWinListener(View winContentView) {
        reset = (Button) winContentView.findViewById(R.id.reset);
        popSure = (ImageView) winContentView.findViewById(R.id.popbtn_sure);
        popCancel = (ImageView) winContentView.findViewById(R.id.popbtn_cancel);
        plus = (ImageView) winContentView.findViewById(R.id.plus);
        minus = (ImageView) winContentView.findViewById(R.id.minus);
        plus2 = (ImageView) winContentView.findViewById(R.id.plus2);
        minus2 = (ImageView) winContentView.findViewById(R.id.minus2);
        seekBar1 = (SeekBar) winContentView.findViewById(R.id.size_seekbar);
        seekBar2 = (SeekBar) winContentView.findViewById(R.id.size_seekbar2);
        text_size = (TextView) winContentView.findViewById(R.id.text_size);
        text_alpha = (TextView) winContentView.findViewById(R.id.text_alpha);

        seekBar1.setProgress((pencilView.getCurrentPencilStatus() == PencilView.IN_PENCIL ? pencilView.getCurrentPencilSize() : pencilView.getCurrentEraserSize()) / 2);

        if (pencilView.getCurrentPencilStatus() == PencilView.IN_PENCIL) {
            seekBar2.setProgress(pencilView.getCurrentPencilAlpha() * 100 / 255);
        } else {
            seekBar2.setEnabled(false);
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset:
                pencilView.setPencilStyle(true, pencilView.getCurrentPencilStatus(), 0, 0);
                break;
            case R.id.popbtn_sure:
                pencilView.setPencilStyle(false, pencilView.getCurrentPencilStatus(),
                        seekBar1.getProgress() * 2, (seekBar2.getProgress() * 255 / 100));
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

}
