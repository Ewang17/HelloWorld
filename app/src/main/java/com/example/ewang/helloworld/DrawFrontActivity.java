package com.example.ewang.helloworld;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ewang.helloworld.helper.MyApplication;
import com.example.ewang.helloworld.service.BaseActivity;
import com.example.ewang.helloworld.view.CanvasView;
import com.example.ewang.helloworld.view.OperationListener;

import java.util.ArrayList;
import java.util.List;


public class DrawFrontActivity extends BaseActivity implements View.OnClickListener {

    FrameLayout canvasLayout;
    List<View> viewList = new ArrayList<>();
    private CanvasView topView;

    public static final int CHOOSE_PHOTO = 2;

    ImageView addPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_front);
        canvasLayout = findViewById(R.id.layout_view_canvas);
        setCanvasSize();
        addPhoto = findViewById(R.id.image_menu_add_photo);
        addPhoto.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_menu_add_photo:
                Intent picIntent = new Intent("android.intent.action.GET_CONTENT");
                picIntent.setType("image/*");
                startActivityForResult(picIntent, CHOOSE_PHOTO);
                break;
            default:
                break;
        }
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
            CanvasView canvasView = new CanvasView(DrawFrontActivity.this, bitmap);
            canvasView.setOperationListener(new OperationListener() {
                @Override
                public void onDeleteClick() {
                    canvasView.setInEdit(false);
                    canvasLayout.removeView(canvasView);
                    //viewList.remove(canvasView);
                }

                @Override
                public void onEdit(CanvasView canvasView) {
                    setTopView(canvasView);
                    canvasLayout.bringChildToFront(canvasView);

                }
            });

            canvasLayout.addView(canvasView);
            setTopView(canvasView);
            //viewList.add(canvasView);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    public void setTopView(CanvasView canvasView) {
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
