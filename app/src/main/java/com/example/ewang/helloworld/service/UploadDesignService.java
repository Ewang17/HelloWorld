package com.example.ewang.helloworld.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;

import com.example.ewang.helloworld.constants.DesignSide;
import com.example.ewang.helloworld.constants.SystemConstants;
import com.example.ewang.helloworld.helper.CustomActivityManager;
import com.example.ewang.helloworld.helper.DialogHelper;
import com.example.ewang.helloworld.helper.JsonHelper;
import com.example.ewang.helloworld.helper.MyApplication;
import com.example.ewang.helloworld.helper.ResponseWrapper;
import com.example.ewang.helloworld.service.task.RequestTask;
import com.example.ewang.helloworld.service.task.ResponseListener;

import java.io.File;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadDesignService extends Service {

    ProgressDialog progressDialog;

    String detailImageUrl, previewImageUrl;

    DesignSide designSide;

    ResponseListener uploadDetailResponseListener = new ResponseListener() {
        @Override
        public void onSuccess(ResponseWrapper responseWrapper) {

            Long detailImageId = JsonHelper.decode(JsonHelper.encode(responseWrapper.getData().get("imageId")), Long.class);
            SharedPreferences.Editor editor = getSharedPreferences("design", MODE_PRIVATE).edit();
            editor.putLong("detailImageId_" + designSide, detailImageId);
            editor.apply();

            doUpload(previewImageUrl, uploadPreviewResponseListener);
        }

        @Override
        public void onFail(String errMessage) {
            progressDialog.dismiss();
            errMessage = errMessage == null ? "连接服务器异常" : errMessage;
            DialogHelper.showAlertDialog(CustomActivityManager.getInstance().getCurrentActivity(), "Warning", errMessage, null, null);

        }

    };

    ResponseListener uploadPreviewResponseListener = new ResponseListener() {
        @Override
        public void onSuccess(ResponseWrapper responseWrapper) {
            Long previewImageId = JsonHelper.decode(JsonHelper.encode(responseWrapper.getData().get("imageId")), Long.class);
            SharedPreferences.Editor editor = getSharedPreferences("design", MODE_PRIVATE).edit();
            editor.putLong("previewImageId_" + designSide, previewImageId);
            editor.apply();

            progressDialog.dismiss();
            CustomActivityManager.getInstance().getCurrentActivity().finish();
        }

        @Override
        public void onFail(String errMessage) {
            progressDialog.dismiss();
            SharedPreferences sharedPreferences = getSharedPreferences("design", MODE_PRIVATE);
            if (sharedPreferences.getLong("detailImageId_" + designSide, 0) != 0) {
                sharedPreferences.edit().remove("detailImageId_" + designSide);
            }
            errMessage = errMessage == null ? "连接服务器异常" : errMessage;
            DialogHelper.showAlertDialog(CustomActivityManager.getInstance().getCurrentActivity(), "Warning", errMessage, null, null);

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        progressDialog = DialogHelper.showProgressDialog(CustomActivityManager.getInstance().getCurrentActivity(), "正在上传", "请稍候", null);

        detailImageUrl = intent.getStringExtra("detailImageUrl");
        previewImageUrl = intent.getStringExtra("previewImageUrl");
        int sideValue = intent.getIntExtra("designSide", 500);
        designSide = DesignSide.fromValue(sideValue);

        doUpload(detailImageUrl, uploadDetailResponseListener);
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void doUpload(String filePath, ResponseListener responseListener) {
        File file = new File(filePath);
        MediaType mediaType = MediaType.parse("image/png");
        String url = SystemConstants.DefaultBasicUrl.getValue() + "/upload/image";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(mediaType, file))
                .build();
        new RequestTask(responseListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, requestBody);
    }
}
