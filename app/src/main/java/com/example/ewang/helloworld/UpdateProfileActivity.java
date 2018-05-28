package com.example.ewang.helloworld;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewang.helloworld.helper.CustomActivityManager;
import com.example.ewang.helloworld.helper.MyApplication;
import com.example.ewang.helloworld.helper.ResponseWrapper;
import com.example.ewang.helloworld.model.Constants;
import com.example.ewang.helloworld.service.BaseActivity;
import com.example.ewang.helloworld.service.UploadImageService;
import com.example.ewang.helloworld.service.task.RequestTask;
import com.example.ewang.helloworld.service.task.ResponseListener;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class UpdateProfileActivity extends BaseActivity {

    private ImageView imageView;

    private TextView textView;

    private Button btn_submit;

    public static long imageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        imageView = findViewById(R.id.image_user_avatar);
        textView = findViewById(R.id.text_username_update);
        btn_submit = findViewById(R.id.btn_submit_profile);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picIntent = new Intent("android.intent.action.GET_CONTENT");
                picIntent.setType("image/*");
                startActivityForResult(picIntent, 0);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = textView.getText().toString().trim();
                if (imageId != 0 && !username.isEmpty()) {
                    RequestBody requestBody = new FormBody.Builder()
                            .add("userId", String.valueOf(MyApplication.getCurrentUser().getId()))
                            .add("username", username)
                            .add("imageId", String.valueOf(imageId))
                            .build();
                    new RequestTask(new ResponseListener() {
                        @Override
                        public void onSuccess(ResponseWrapper responseWrapper) {
                            Intent intent = new Intent(CustomActivityManager.getInstance().getCurrentActivity(), ShowSessionListActivity.class);
                            CustomActivityManager.getInstance().getCurrentActivity().finish();
                            CustomActivityManager.getInstance().getCurrentActivity().startActivity(intent);
                        }
                    }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            Constants.DefaultBasicUrl.getValue() + "/user/update/profile", requestBody);
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("TAG", "回传方法");
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        Log.e("TAG", "4.4及以上系统使用这个方法处理图片");
                        handleImageOnKitKat(data);
                    } else {
                        //4.4以下系统使用这个方法处理图片
                        Log.e("TAG", "4.4以下系统使用这个方法处理图片");
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                Log.e("TAG", "default");
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
            Intent uploadIntent = new Intent(UpdateProfileActivity.this, UploadImageService.class)
                    .putExtra("url", Constants.DefaultBasicUrl.getValue() + "/user/upload/avatar")
                    .putExtra("imagePath", imagePath);
            startService(uploadIntent);

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

}
