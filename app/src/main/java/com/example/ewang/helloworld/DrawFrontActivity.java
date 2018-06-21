package com.example.ewang.helloworld;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ewang.helloworld.constants.SystemConstants;
import com.example.ewang.helloworld.helper.DialogHelper;
import com.example.ewang.helloworld.helper.MyApplication;
import com.example.ewang.helloworld.helper.PopupDrawToolWindowHelper;
import com.example.ewang.helloworld.helper.TextViewHelper;
import com.example.ewang.helloworld.service.BaseActivity;
import com.example.ewang.helloworld.service.UploadDesignService;
import com.example.ewang.helloworld.service.UploadImageService;
import com.example.ewang.helloworld.view.BaseCanvasView;
import com.example.ewang.helloworld.view.ChildDrawView;
import com.example.ewang.helloworld.view.PencilView;
import com.example.ewang.helloworld.view.ChildPhotoView;
import com.example.ewang.helloworld.view.OperationListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class DrawFrontActivity extends BaseActivity implements View.OnClickListener {

    ConstraintLayout layoutWhole;
    FrameLayout canvasLayout;
    ImageView drawBackground;
    private BaseCanvasView topView;


    PencilView pencilView;

    public static final int CHOOSE_PHOTO = 2;

    final int IN_LARGE = 0;
    final int IN_NORMAL = 1;
    int scaleStatus = IN_NORMAL;
    private float scaleX, scaleY, transEndY;

    ImageView addPhoto;
    ImageView addPic;
    ImageView addPencil;
    ImageView addText;
    ImageView totalDone;
    ImageView totalCancel;

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
        setContentView(R.layout.activity_draw_front_constraint);
        canvasLayout = findViewById(R.id.framelayout_canvas);
        layoutWhole = findViewById(R.id.constrant_layout_draw);
        drawBackground = findViewById(R.id.image_view_background);
        setCanvasSize();

        pencil = findViewById(R.id.image_draw_pencil);
        eraser = findViewById(R.id.image_draw_eraser);
        cancel = findViewById(R.id.image_draw_cancel);
        done = findViewById(R.id.image_draw_done);
        undo = findViewById(R.id.image_draw_undo);
        recover = findViewById(R.id.image_draw_recover);
        color = findViewById(R.id.image_draw_color);
        shape = findViewById(R.id.image_draw_shape);

        addPhoto = findViewById(R.id.image_view_add_pic);
        addPic = findViewById(R.id.image_view_add_inner_pic);
        addPencil = findViewById(R.id.image_view_add_draw);
        addText = findViewById(R.id.image_view_add_text);
        totalDone = findViewById(R.id.image_view_done);
        totalCancel = findViewById(R.id.image_view_cancel);

        addPhoto.setOnClickListener(this);
        addPic.setOnClickListener(this);
        addPencil.setOnClickListener(this);
        addText.setOnClickListener(this);
        totalDone.setOnClickListener(this);
        totalCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_view_add_pic:
                if (topView != null) {
                    topView.setInEdit(false);
                }
                Intent picIntent = new Intent("android.intent.action.GET_CONTENT");
                picIntent.setType("image/*");
                startActivityForResult(picIntent, CHOOSE_PHOTO);
                break;
            case R.id.image_view_add_draw:
                if (topView != null) {
                    topView.setInEdit(false);
                }
                doScale();
                pencilView = new PencilView(this);
                PopupDrawToolWindowHelper popupDrawToolWindowHelper = new PopupDrawToolWindowHelper(this,
                        pencilView, v, DrawFrontActivity.this);
                popupDrawToolWindowHelper.setOnCancelClick((a) -> {
                    doScale();
                    canvasLayout.removeView(pencilView);
                    popupDrawToolWindowHelper.dismissDrawTool();

                    addPhoto.setVisibility(View.VISIBLE);
                    addPic.setVisibility(View.VISIBLE);
                    addPencil.setVisibility(View.VISIBLE);
                    addText.setVisibility(View.VISIBLE);

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
                    addPhoto.setVisibility(View.VISIBLE);
                    addPic.setVisibility(View.VISIBLE);
                    addPencil.setVisibility(View.VISIBLE);
                    addText.setVisibility(View.VISIBLE);
                    return null;
                });
                canvasLayout.addView(pencilView);
                popupDrawToolWindowHelper.popDrawTool();
                addPhoto.setVisibility(View.GONE);
                addPic.setVisibility(View.GONE);
                addPencil.setVisibility(View.GONE);
                addText.setVisibility(View.GONE);
                break;
            case R.id.image_view_add_text:
                if (topView != null) {
                    topView.setInEdit(false);
                }
                TextViewHelper textViewHelper = new TextViewHelper(DrawFrontActivity.this, layoutWhole);
                View addTextView = textViewHelper.getMainView();

                textViewHelper.setOnCancelClick((a) -> {
                    layoutWhole.removeView(addTextView);
                    addPhoto.setVisibility(View.VISIBLE);
                    addPic.setVisibility(View.VISIBLE);
                    addPencil.setVisibility(View.VISIBLE);
                    addText.setVisibility(View.VISIBLE);
                    return null;
                });

                textViewHelper.setOnDoneClick((bitmap) -> {
                    ChildPhotoView childPhotoView = new ChildPhotoView(DrawFrontActivity.this, (Bitmap) bitmap);
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

                    layoutWhole.removeView(addTextView);
                    addPhoto.setVisibility(View.VISIBLE);
                    addPic.setVisibility(View.VISIBLE);
                    addPencil.setVisibility(View.VISIBLE);
                    addText.setVisibility(View.VISIBLE);
                    return null;
                });
                layoutWhole.addView(addTextView);
                addPhoto.setVisibility(View.GONE);
                addPic.setVisibility(View.GONE);
                addPencil.setVisibility(View.GONE);
                addText.setVisibility(View.GONE);
                break;
            case R.id.image_view_cancel:
                DialogHelper.showAlertDialog(DrawFrontActivity.this, "提示", "确认不保存直接退出?",
                        ((dialog, which) -> {
                            finish();
                        }), ((dialog, which) -> {

                        }));
                break;
            case R.id.image_view_done:
                if (topView != null) {
                    topView.setInEdit(false);
                }
                DialogHelper.showAlertDialog(DrawFrontActivity.this, "提示", "保存后的作品将不能再被返回修改",
                        ((dialog, which) -> {
                            File detailImageFile, previewImageFile;
                            try {
                                Bitmap bitmapDraw = Bitmap.createBitmap(canvasLayout.getWidth(), canvasLayout.getHeight(), Bitmap.Config.ARGB_8888);
                                Canvas canvasDraw = new Canvas(bitmapDraw);
                                canvasLayout.draw(canvasDraw);
                                detailImageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".png");
                                bitmapDraw.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(detailImageFile));

                                drawBackground.setDrawingCacheEnabled(true);
                                Bitmap bitmapMain = drawBackground.getDrawingCache();
                                Canvas canvasMain = new Canvas(bitmapMain);
                                canvasMain.drawBitmap(bitmapDraw, canvasLayout.getLeft(), canvasLayout.getTop() - MyApplication.getScreenHeight() * 0.1f, null);
                                previewImageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".png");

                                bitmapMain.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(previewImageFile));
                                drawBackground.setDrawingCacheEnabled(false);

                                Intent intent = new Intent(DrawFrontActivity.this, UploadDesignService.class)
                                        .putExtra("detailImageUrl", detailImageFile.getAbsolutePath())
                                        .putExtra("previewImageUrl", previewImageFile.getAbsolutePath());
                                startService(intent);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                        }), ((dialog, which) -> {

                        }));
                break;
            default:
                break;
        }
    }

    void doScale() {
        AnimatorSet animatorSet = new AnimatorSet();
        float startX, startY, transStartY;
        if (scaleStatus == IN_NORMAL) {
            startX = 1.0f;
            startY = 1.0f;
            transStartY = 0f;
            scaleX = drawBackground.getWidth() * 0.95f / MyApplication.getCanvasWidth();
            scaleY = drawBackground.getHeight() * 1f / MyApplication.getCanvasHeight();
            transEndY = -(drawBackground.getHeight() - canvasLayout.getHeight()) / 4f / scaleY;

            scaleStatus = IN_LARGE;
        } else {
            startX = scaleX;
            startY = scaleY;
            transStartY = transEndY;

            scaleX = 1.0f;
            scaleY = 1.0f;
            transEndY = 0f;

            scaleStatus = IN_NORMAL;
        }
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(drawBackground, "scaleX", startX, scaleX);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(drawBackground, "scaleY", startY, scaleY);
        ObjectAnimator scaleXCanvasAnimator = ObjectAnimator.ofFloat(canvasLayout, "scaleX", startX, scaleX);
        ObjectAnimator scaleYCanvasAnimator = ObjectAnimator.ofFloat(canvasLayout, "scaleY", startY, scaleY);
        ObjectAnimator transYAnimator = ObjectAnimator.ofFloat(drawBackground, "translationY", transStartY, transEndY);
        ObjectAnimator transYCanvasAnimator = ObjectAnimator.ofFloat(canvasLayout, "translationY", transStartY, transEndY);
        scaleXAnimator.setDuration(500);
        scaleYAnimator.setDuration(500);
        scaleXCanvasAnimator.setDuration(500);
        scaleYCanvasAnimator.setDuration(500);
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, scaleXCanvasAnimator, scaleYCanvasAnimator, transYAnimator, transYCanvasAnimator);
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
        topView.setMoveEvent((a) -> {
            canvasLayout.setBackground(getResources().getDrawable(R.drawable.shape_border, getTheme()));
            layoutWhole.setAlpha(.8f);
            return null;
        });
        topView.setUpEvent((a) -> {
            canvasLayout.setBackground(null);
            layoutWhole.setAlpha(1f);
            return null;
        });
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
