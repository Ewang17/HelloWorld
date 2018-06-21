package com.example.ewang.helloworld.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.ewang.helloworld.R;
import com.example.ewang.helloworld.adapter.TypeFaceAdapter;
import com.example.ewang.helloworld.model.client.CustomTypeFace;
import com.example.ewang.helloworld.view.ColorPickerHorizontalView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by ewang on 2018/6/7.
 */

public class TextViewHelper implements View.OnClickListener {

    private View mainView;

    private Context context;

    private FrameLayout bottomToolLayout;

    private ImageView keyboard, color, typeface;

    private FloatingActionButton cancel, done;

    private Function onCancelClick, onDoneClick;

    private EditText editTextContent;

    private ColorPickerHorizontalView colorPickerHorizontalView;

    private boolean inKeyboard, inColorPicker, inTypeFace;

    private ConstraintLayout constraintLayout;

    private InputMethodManager imm;

    private RecyclerView typeFaceRecyclerView;

    private List<CustomTypeFace> typeFaceList;

    private Bitmap mainBitmap;

    public TextViewHelper(Context context, ViewGroup root) {
        this.context = context;
        this.mainView = LayoutInflater.from(context).inflate(R.layout.activity_view_add_text, root, false);
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

        typeFaceRecyclerView = new RecyclerView(context);

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
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewKeyboard:
                inTypeFace = false;
                inColorPicker = false;
                bottomToolLayout.removeAllViews();
                if (inKeyboard) {
                    editTextContent.clearFocus();
                } else {
                    editTextContent.requestFocus();
                }
                break;
            case R.id.imageViewColor:
                inTypeFace = false;
                if (inKeyboard || editTextContent.hasFocus() || imm.isActive()) {
                    editTextContent.clearFocus();
                }
                bottomToolLayout.removeAllViews();

                if (inColorPicker) {
                    inColorPicker = false;
                } else {
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
                    inColorPicker = true;
                }
                break;
            case R.id.imageViewType:
                inColorPicker = false;
                if (inKeyboard || editTextContent.hasFocus() || imm.isActive()) {
                    editTextContent.clearFocus();
                }
                bottomToolLayout.removeAllViews();

                if (inTypeFace) {
                    inTypeFace = false;
                } else {
                    initTypeFace();
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return setSpanSize(position, typeFaceList);
                        }
                    });
                    typeFaceRecyclerView.setLayoutManager(gridLayoutManager);
                    typeFaceRecyclerView.setAdapter(new TypeFaceAdapter(typeFaceList, editTextContent));
                    bottomToolLayout.addView(typeFaceRecyclerView);
                    bottomToolLayout.setVisibility(View.VISIBLE);
                    inTypeFace = true;
                }
                break;
            case R.id.floatingActionButtonCancel:
                onCancelClick.apply(null);
                break;
            case R.id.floatingActionButtonDone:
                mainBitmap = loadBitmapFromView();
                onDoneClick.apply(mainBitmap);
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

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                current = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                adjustTvTextSize(editTextContent, editTextContent.getWidth(), current);
            }
        });

        editTextContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    inKeyboard = true;
                    inColorPicker = false;
                    inTypeFace = false;
                    bottomToolLayout.removeAllViews();
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                } else {
                    inKeyboard = false;
                    imm.hideSoftInputFromWindow(editTextContent.getWindowToken(), 0);
                    bottomToolLayout.getLayoutParams().height = 0;
                    bottomToolLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    public void initLayoutListener() {
        constraintLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!inColorPicker && !inTypeFace) {

                    Rect rect = new Rect();
                    constraintLayout.getWindowVisibleDisplayFrame(rect);
                    int bottomLayoutHeight = MyApplication.getScreenHeight() - rect.bottom;
                    bottomToolLayout.getLayoutParams().height = bottomLayoutHeight;
                    if (bottomLayoutHeight > 0) {
                        bottomToolLayout.setVisibility(View.INVISIBLE);
                    } else {
                        bottomToolLayout.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private int setSpanSize(int position, List<CustomTypeFace> customTypeFaceList) {
        int count;
        String typeName = customTypeFaceList.get(position).getName();
        if (CharacterHelper.isChinese(typeName)) {
            if (typeName.length() > 8) {
                count = 2;
            } else {
                count = 1;
            }
        } else {
            if (typeName.length() > 16) {
                count = 2;
            } else {
                count = 1;
            }
        }

        return count;
    }

    void initTypeFace() {
        typeFaceList = new ArrayList<>();

        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "fonts/Satisfy-Regular.ttf");
        Typeface typeface2 = Typeface.createFromAsset(context.getAssets(), "fonts/HYShangWeiShouShuW.ttf");
        Typeface typeface3 = Typeface.createFromAsset(context.getAssets(), "fonts/GillSansShadowed.ttf");
        Typeface typeface4 = Typeface.createFromAsset(context.getAssets(), "fonts/迷你简漫步.ttf");
        Typeface typeface5 = Typeface.createFromAsset(context.getAssets(), "fonts/sg16.ttf");

        typeFaceList.add(new CustomTypeFace("Satisfy-Regular", typeface1));
        typeFaceList.add(new CustomTypeFace("汉仪尚巍手书", typeface2));
        typeFaceList.add(new CustomTypeFace("Gill Sans Shadowed", typeface3));
        typeFaceList.add(new CustomTypeFace("迷你简漫步", typeface4));
        typeFaceList.add(new CustomTypeFace("SG16-Regular", typeface5));

    }

    private void adjustTvTextSize(EditText editText, int maxWidth, String text) {

        int availableWidth = maxWidth - editText.getPaddingLeft() - editText.getPaddingRight() - 10;

        if (availableWidth <= 0) {
            return;
        }

        TextPaint textPaintClone = new TextPaint(editText.getPaint());
        // note that Paint text size works in px not sp
        float trySize = textPaintClone.getTextSize();

        while (textPaintClone.measureText(text) < availableWidth) {
            if (text.length() <= 2) {
                break;
            }
            trySize++;
            textPaintClone.setTextSize(trySize);
        }

        while (textPaintClone.measureText(text) > availableWidth) {
            trySize--;
            textPaintClone.setTextSize(trySize);
        }
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
    }

    private Bitmap loadBitmapFromView() {
        int w = editTextContent.getWidth();
        int h = editTextContent.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        TextPaint textPaint = editTextContent.getPaint();
        String text = editTextContent.getText().toString();
        float textHeight = textPaint.descent() + textPaint.ascent();
        c.drawText(text, (w - textPaint.measureText(text)) / 2f, (h - textHeight) / 2, textPaint);
        return bitmap;
    }
}
