package com.example.ewang.helloworld.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.example.ewang.helloworld.UpdateProfileActivity;
import com.example.ewang.helloworld.helper.CustomActivityManager;
import com.example.ewang.helloworld.helper.DialogHelper;
import com.example.ewang.helloworld.helper.JsonHelper;
import com.example.ewang.helloworld.helper.ResponseWrapper;
import com.example.ewang.helloworld.service.task.RequestTask;
import com.example.ewang.helloworld.service.task.ResponseListener;

import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadImageService extends Service {
    ProgressDialog progressDialog;

    ResponseListener responseListener = new ResponseListener() {
        @Override
        public void onSuccess(ResponseWrapper responseWrapper) {
            progressDialog.dismiss();
            Long imageId = JsonHelper.decode(JsonHelper.encode(responseWrapper.getData().get("imageId")), Long.class);
            UpdateProfileActivity.imageId = imageId;
        }

        @Override
        public void onFail(String errMessage) {
            progressDialog.dismiss();
            errMessage = errMessage == null ? "连接服务器异常" : errMessage;
            DialogHelper.showAlertDialog(CustomActivityManager.getInstance().getCurrentActivity(), "Warning", errMessage, null, null);

        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        progressDialog = DialogHelper.showProgressDialog(CustomActivityManager.getInstance().getCurrentActivity(), "正在上传", "请稍候", null);

        String url = intent.getStringExtra("url");
        String imagePath = intent.getStringExtra("imagePath");

        File imageFile = new File(imagePath);
        MediaType TYPE_PNG = MediaType.parse("image/png");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imageFile.getName(), RequestBody.create(TYPE_PNG, imageFile))
                .build();
        new RequestTask(responseListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, requestBody);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
