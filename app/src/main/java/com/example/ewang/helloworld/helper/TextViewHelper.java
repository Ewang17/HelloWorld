package com.example.ewang.helloworld.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.ewang.helloworld.R;
import com.example.ewang.helloworld.view.ColorPickerHorizontalView;

import java.util.function.Function;

/**
 * Created by ewang on 2018/6/7.
 */

public class TextViewHelper implements View.OnClickListener {

    private View mainView;

    private Context context;

    private View parentView;

    private Activity parentActivity;

    private FrameLayout bottomToolLayout;

    private ImageView keyboard, color, typeface;

    private FloatingActionButton cancel, done;

    private Function onCancelClick, onDoneClick;

    private EditText editTextContent;

    private ColorPickerHorizontalView colorPickerHorizontalView;

    private boolean isInColorPicker, isInTypeFace;

    private ConstraintLayout constraintLayout;

    private int scrollHeight;

    private InputMethodManager imm;

    public TextViewHelper(Context context, View parentView, Activity parentActivity) {
        this.context = context;
        this.parentView = parentView;
        this.parentActivity = parentActivity;
        this.mainView = LayoutInflater.from(context).inflate(R.layout.activity_view_add_text, null);
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        initViews();
    }

    void initViews() {
        constraintLayout = mainView.findViewById(R.id.constraintLayoutAddText);
        bottomToolLayout = mainView.findViewById(R.id.changeableBottomLayout);
        keyboard = mainView.findViewById(R.id.imageViewKeyboard);
        color = mainView.findViewById(R.id.imageViewColor);
        typeface = mainView.findViewById(R.id.imageViewType);
        editTextContent = mainView.findViewById(R.id.editText);

        cancel = mainView.findViewById(R.id.floatingActionButtonCancel);
        done = mainView.findViewById(R.id.floatingActionButtonDone);

        keyboard.setOnClickListener(this);
        color.setOnClickListener(this);
        typeface.setOnClickListener(this);
        cancel.setOnClickListener(this);
        done.setOnClickListener(this);

        initTextListener();

        initLayoutListener();

        editTextContent.setFocusable(true);
        editTextContent.setFocusableInTouchMode(true);
        editTextContent.requestFocus();
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewKeyboard:
                isInTypeFace = false;
                isInColorPicker = false;
                bottomToolLayout.removeAllViews();
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.imageViewColor:
                isInTypeFace = false;
                bottomToolLayout.removeAllViews();
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(editTextContent.getWindowToken(), 0);
                }
                if (isInColorPicker) {
                    bottomToolLayout.setVisibility(View.GONE);
                    isInColorPicker = false;
                    return;
                }
                bottomToolLayout.getLayoutParams().height = 0;
                colorPickerHorizontalView = new ColorPickerHorizontalView(bottomToolLayout.getContext(),
                        MyApplication.getScreenWidth() * 9 / 16, MyApplication.getScreenWidth(),
                        editTextContent.getTextColors().getDefaultColor(), new ColorPickerHorizontalView.OnColorChangedListener() {
                    @Override
                    public void colorChanged(int color) {
                        editTextContent.setTextColor(color);
                    }
                });
                bottomToolLayout.addView(colorPickerHorizontalView);
                bottomToolLayout.setVisibility(View.VISIBLE);
                isInColorPicker = true;
                break;
            case R.id.imageViewType:
                break;
            case R.id.floatingActionButtonCancel:
                onCancelClick.apply(null);
                break;
            case R.id.floatingActionButtonDone:
                onDoneClick.apply(null);
                break;
            default:
                break;
        }
    }

    public void setOnCancelClick(Function onCancelClick) {
        this.onCancelClick = onCancelClick;
    }

    public void setOnDoneClick(Function onDoneClick) {
        this.onDoneClick = onDoneClick;
    }

    public View getMainView() {
        return mainView;
    }

    void initTextListener() {
        editTextContent.addTextChangedListener(new TextWatcher() {
            private String current;
            private int lastCount;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastCount = current == null ? 0 : current.length();
                current = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (current.length() - lastCount < 0) {
                    if (current.length() < 2) {
                        return;
                    }
                    editTextContent.setTextSize(SizeHelper.px2sp(editTextContent.getTextSize()) / 0.8f);
                } else if (current.length() - lastCount > 0) {
                    if (current.length() < 3) {
                        return;
                    }
                    editTextContent.setTextSize(SizeHelper.px2sp(editTextContent.getTextSize()) * 0.8f);
                }
            }
        });
    }

    public void initLayoutListener() {
        constraintLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (imm.isActive() && !isInColorPicker && !isInTypeFace) {

                    Rect rect = new Rect();
                    constraintLayout.getWindowVisibleDisplayFrame(rect);

                    int[] location = new int[2];
                    color.getLocationInWindow(location);
                    scrollHeight = MyApplication.getScreenHeight() - rect.bottom;

                    bottomToolLayout.getLayoutParams().height = scrollHeight;
                    if (scrollHeight > 0) {
                        bottomToolLayout.setVisibility(View.INVISIBLE);
                    } else {
                        bottomToolLayout.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

}
