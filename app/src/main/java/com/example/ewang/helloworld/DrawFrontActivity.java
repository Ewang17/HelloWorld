package com.example.ewang.helloworld;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.ewang.helloworld.constants.PaintGraphics;
import com.example.ewang.helloworld.constants.PaintStatus;
import com.example.ewang.helloworld.helper.MyApplication;
import com.example.ewang.helloworld.helper.PopupDrawToolWindowHelper;
import com.example.ewang.helloworld.helper.PopupPencilWindowHelper;
import com.example.ewang.helloworld.service.BaseActivity;
import com.example.ewang.helloworld.view.BaseCanvasView;
import com.example.ewang.helloworld.view.ChildDrawView;
import com.example.ewang.helloworld.view.PencilView;
import com.example.ewang.helloworld.view.ChildPhotoView;
import com.example.ewang.helloworld.view.OperationListener;


public class DrawFrontActivity extends BaseActivity implements View.OnClickListener {

    FrameLayout canvasLayout;
    PercentRelativeLayout topDrawLayout;
    private BaseCanvasView topView;

    private PopupPencilWindowHelper popupPencilWindowHelper;

    PencilView pencilView;

    PercentRelativeLayout basicDrawBar;
    PercentRelativeLayout pencilDrawBar;

    public static final int CHOOSE_PHOTO = 2;

    final int IN_LARGE = 0;
    final int IN_NORMAL = 1;
    int scaleStatus = IN_NORMAL;
    private float scaleX, scaleY;

    ImageView addPhoto;
    ImageView addPencil;
    ImageView addText;

    ImageView pencil;
    ImageView eraser;
    ImageView cancel;
    ImageView done;
    ImageView undo;
    ImageView recover;
    ImageView color;
    ImageView shape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_front);
        canvasLayout = findViewById(R.id.layout_view_canvas);
        topDrawLayout = findViewById(R.id.layout_center_draw_area);
        setCanvasSize();

        basicDrawBar = findViewById(R.id.layout_basic_bar);
        pencilDrawBar = findViewById(R.id.layout_pencil_bar);

        pencil = findViewById(R.id.image_draw_pencil);
        eraser = findViewById(R.id.image_draw_eraser);
        cancel = findViewById(R.id.image_draw_cancel);
        done = findViewById(R.id.image_draw_done);
        undo = findViewById(R.id.image_draw_undo);
        recover = findViewById(R.id.image_draw_recover);
        color = findViewById(R.id.image_draw_color);
        shape = findViewById(R.id.image_draw_shape);

        addPhoto = findViewById(R.id.image_menu_add_photo);
        addPencil = findViewById(R.id.image_menu_pencil);
        addText = findViewById(R.id.image_menu_text);

        addPhoto.setOnClickListener(this);
        addPencil.setOnClickListener(this);
        addText.setOnClickListener(this);

        pencil.setOnClickListener(this);
        eraser.setOnClickListener(this);
        cancel.setOnClickListener(this);
        done.setOnClickListener(this);
        undo.setOnClickListener(this);
        recover.setOnClickListener(this);
        color.setOnClickListener(this);
        shape.setOnClickListener(this);

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
                doScale();
                pencilView = new PencilView(this);
                PopupDrawToolWindowHelper popupDrawToolWindowHelper = new PopupDrawToolWindowHelper(this,
                        pencilView, v, DrawFrontActivity.this);
                popupDrawToolWindowHelper.setOnCancelClick((a) -> {
                    doScale();
                    canvasLayout.removeView(pencilView);
                    popupDrawToolWindowHelper.dismissDrawTool();
                    return null;
                });
                popupDrawToolWindowHelper.setOnDoneClick((a) -> {
                    doScale();
                    ChildDrawView childDrawView = new ChildDrawView(DrawFrontActivity.this, pencilView);
                    childDrawView.setOperationListener(new OperationListener() {
                        @Override
                        public void onDeleteClick() {
                            childDrawView.setInEdit(false);
                            canvasLayout.removeView(childDrawView);
                        }

                        @Override
                        public void onEdit(BaseCanvasView canvasView) {
                            setTopView(canvasView);
                            canvasLayout.bringChildToFront(canvasView);
                        }

                        @Override
                        public void onReeditClick() {
                            doScale();
                            canvasLayout.addView(childDrawView.getPencilView());
                            popupDrawToolWindowHelper.popDrawTool();
                            canvasLayout.removeView(childDrawView);
                        }
                    });

                    canvasLayout.addView(childDrawView);
                    setTopView(childDrawView);
                    canvasLayout.removeView(pencilView);
                    popupDrawToolWindowHelper.dismissDrawTool();
                    return null;
                });
                canvasLayout.addView(pencilView);
                popupDrawToolWindowHelper.popDrawTool();
                break;
            default:
                break;
        }
    }

    void doScale() {
        AnimatorSet animatorSet = new AnimatorSet();
        float startX, startY;
        if (scaleStatus == IN_NORMAL) {
            startX = 1.0f;
            startY = 1.0f;
            scaleX = topDrawLayout.getWidth() * 0.95f / MyApplication.getCanvasWidth();
            scaleY = topDrawLayout.getHeight() * 0.95f / MyApplication.getCanvasHeight();

            scaleStatus = IN_LARGE;
        } else {
            startX = scaleX;
            startY = scaleY;

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
        MyApplication.setCanvasWidth(canvasLayout.getWidth());
        MyApplication.setCanvasHeight(canvasLayout.getHeight());

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
            ChildPhotoView childPhotoView = new ChildPhotoView(DrawFrontActivity.this, bitmap);
            childPhotoView.setOperationListener(new OperationListener() {
                @Override
                public void onDeleteClick() {
                    childPhotoView.setInEdit(false);
                    canvasLayout.removeView(childPhotoView);
                }

                @Override
                public void onEdit(BaseCanvasView canvasView) {
                    setTopView(canvasView);
                    canvasLayout.bringChildToFront(canvasView);
                }

                @Override
                public void onReeditClick() {
                }
            });

            canvasLayout.addView(childPhotoView);
            setTopView(childPhotoView);
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
