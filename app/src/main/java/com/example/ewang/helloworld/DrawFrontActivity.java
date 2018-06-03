package com.example.ewang.helloworld;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.ewang.helloworld.helper.MyApplication;
import com.example.ewang.helloworld.helper.PopWindowHelper;
import com.example.ewang.helloworld.service.BaseActivity;
import com.example.ewang.helloworld.view.BaseCanvasView;
import com.example.ewang.helloworld.view.PencilView;
import com.example.ewang.helloworld.view.PhotoView;
import com.example.ewang.helloworld.view.OperationListener;


public class DrawFrontActivity extends BaseActivity implements View.OnClickListener {

    FrameLayout canvasLayout;
    PercentRelativeLayout topDrawLayout;
    private BaseCanvasView topView;

    private PencilView currentDrawView;

    private PopWindowHelper popWindowHelper;

    PercentRelativeLayout basicDrawBar;
    PercentRelativeLayout pencilDrawBar;

    public static final int CHOOSE_PHOTO = 2;

    final int IN_LARGE = 0;
    final int IN_NORMAL = 1;
    int scaleStatus = IN_NORMAL;
    private float scaleX, scaleY;

    ImageView addPhoto;
    ImageView addPencil;

    ImageView pencil;
    ImageView eraser;
    ImageView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_front);
        canvasLayout = findViewById(R.id.layout_view_canvas);
        topDrawLayout = findViewById(R.id.layout_top_draw_area);
        setCanvasSize();

        basicDrawBar = findViewById(R.id.layout_basic_bar);
        pencilDrawBar = findViewById(R.id.layout_pencil_bar);

        pencil = findViewById(R.id.image_draw_pencil);
        eraser = findViewById(R.id.image_draw_eraser);
        cancel = findViewById(R.id.image_draw_cancel);

        addPhoto = findViewById(R.id.image_menu_add_photo);
        addPencil = findViewById(R.id.image_menu_pencil);

        addPhoto.setOnClickListener(this);
        addPencil.setOnClickListener(this);
        pencil.setOnClickListener(this);
        eraser.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_menu_add_photo:
                Intent picIntent = new Intent("android.intent.action.GET_CONTENT");
                picIntent.setType("image/*");
                startActivityForResult(picIntent, CHOOSE_PHOTO);
                break;
            case R.id.image_menu_pencil:
                doDraw();
                currentDrawView = new PencilView(this);
                popWindowHelper = new PopWindowHelper(currentDrawView);
                canvasLayout.addView(currentDrawView);
                basicDrawBar.setVisibility(View.INVISIBLE);
                pencilDrawBar.setVisibility(View.VISIBLE);
                pencil.setColorFilter(R.color.pink);
                break;
            case R.id.image_draw_pencil:
                pencil.setColorFilter(R.color.pink);
                eraser.setColorFilter(null);
                if (currentDrawView.getCurrentPencilStatus() == PencilView.IN_PENCIL) {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 0.5f;
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    getWindow().setAttributes(lp);
                    popWindowHelper.showPencilStyle(this, v, new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            WindowManager.LayoutParams lp = getWindow().getAttributes();
                            lp.alpha = 1.0f;
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                            getWindow().setAttributes(lp);
                        }
                    });
                } else {
                    currentDrawView.setPencilStyle(false, PencilView.IN_PENCIL,
                            currentDrawView.getCurrentPencilSize(), currentDrawView.getCurrentPencilAlpha());
                    currentDrawView.setCurrentPencilStatus(PencilView.IN_PENCIL);
                }
                break;
            case R.id.image_draw_eraser:
                eraser.setColorFilter(R.color.pink);
                pencil.setColorFilter(null);
                if (currentDrawView.getCurrentPencilStatus() == PencilView.IN_ERASER) {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 0.5f;
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    getWindow().setAttributes(lp);
                    popWindowHelper.showPencilStyle(this, v, new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            WindowManager.LayoutParams lp = getWindow().getAttributes();
                            lp.alpha = 1.0f;
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                            getWindow().setAttributes(lp);
                        }
                    });
                } else {
                    currentDrawView.setPencilStyle(false, PencilView.IN_ERASER,
                            currentDrawView.getCurrentEraserSize(), 0);
                    currentDrawView.setCurrentPencilStatus(PencilView.IN_ERASER);
                }
                break;
            case R.id.image_draw_cancel:
                doDraw();
                canvasLayout.removeView(currentDrawView);
                pencilDrawBar.setVisibility(View.INVISIBLE);
                basicDrawBar.setVisibility(View.VISIBLE);
            default:
                break;
        }
    }

    void doDraw() {
        AnimatorSet animatorSet = new AnimatorSet();
        float startX, startY;
        if (scaleStatus == IN_NORMAL) {
            startX = 1.0f;
            startY = 1.0f;
            scaleX = MyApplication.getScreenWidth() * 0.9f / (float) MyApplication.getCanvasWidth();
            scaleY = MyApplication.getScreenHeight() * 0.8f / (float) MyApplication.getCanvasHeight();

            MyApplication.setCanvasWidth((int) (MyApplication.getCanvasWidth() * scaleX));
            MyApplication.setCanvasHeight((int) (MyApplication.getCanvasHeight() * scaleY));

            scaleStatus = IN_LARGE;
        } else {
            startX = scaleX;
            startY = scaleY;

            MyApplication.setCanvasWidth((int) (MyApplication.getCanvasWidth() * 1.0f / scaleX));
            MyApplication.setCanvasHeight((int) (MyApplication.getCanvasHeight() * 1.0f / scaleY));

            scaleX = 1.0f;
            scaleY = 1.0f;

            scaleStatus = IN_NORMAL;
        }
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(topDrawLayout, "scaleX", startX, scaleX);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(topDrawLayout, "scaleY", startY, scaleY);
        scaleXAnimator.setDuration(500);
        scaleYAnimator.setDuration(500);
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.start();

    }

    void doScale() {
        ScaleAnimation scaleAnimation;
        float startX, startY, scaleX, scaleY;
        if (scaleStatus == IN_NORMAL) {
            startX = 1.0f;
            startY = 1.0f;
            scaleX = (float) MyApplication.getScreenWidth() / (float) MyApplication.getCanvasWidth();
            scaleY = MyApplication.getScreenHeight() * 0.85f / (float) MyApplication.getCanvasHeight();

            scaleStatus = IN_LARGE;
        } else {
            startX = (float) MyApplication.getScreenWidth() / (float) MyApplication.getCanvasWidth();
            startY = MyApplication.getScreenHeight() * 0.85f / (float) MyApplication.getCanvasHeight();
            scaleX = 1.0f;
            scaleY = 1.0f;

            scaleStatus = IN_NORMAL;
        }
        scaleAnimation = new ScaleAnimation(startX, scaleX, startY, scaleY);
        scaleAnimation.setDuration(500);
        scaleAnimation.setFillAfter(true);

        topDrawLayout.setAnimation(scaleAnimation);
        scaleAnimation.startNow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (topView != null) {
            if (!(topView.isInBitmap(event) ||
                    topView.isInOperate(event, topView.rect_resize) ||
                    topView.isInOperate(event, topView.rect_delete))) {
                topView.setInEdit(false);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        Log.i("TAG", "4.4及以上系统使用这个方法处理图片");
                        handleImageOnKitKat(data);
                    } else {
                        //4.4以下系统使用这个方法处理图片
                        Log.i("TAG", "4.4以下系统使用这个方法处理图片");
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                Log.i("TAG", "default");
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.
                        EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //content类型的uri,使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(
                        MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            PhotoView photoView = new PhotoView(DrawFrontActivity.this, bitmap);
            photoView.setOperationListener(new OperationListener() {
                @Override
                public void onDeleteClick() {
                    photoView.setInEdit(false);
                    canvasLayout.removeView(photoView);
                }

                @Override
                public void onEdit(BaseCanvasView canvasView) {
                    setTopView(canvasView);
                    canvasLayout.bringChildToFront(canvasView);

                }
            });

            canvasLayout.addView(photoView);
            setTopView(photoView);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    public void setTopView(BaseCanvasView canvasView) {
        if (topView != null) {
            topView.setInEdit(false);
        }
        topView = canvasView;
        topView.setInEdit(true);
    }

    void setCanvasSize() {
        ViewTreeObserver viewTreeObserver = canvasLayout.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (MyApplication.getCanvasWidth() == 0 || MyApplication.getCanvasHeight() == 0) {
                    MyApplication.setCanvasHeight(canvasLayout.getHeight());
                    MyApplication.setCanvasWidth(canvasLayout.getWidth());
                    Log.i("DrawFrontActivity", "onPreDraw, 画布————" + "宽度:" + MyApplication.getCanvasWidth() + ",高度:" + MyApplication.getCanvasHeight());
                }
                return true;
            }
        });
    }
}
