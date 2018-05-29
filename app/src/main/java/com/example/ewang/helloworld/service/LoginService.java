package com.example.ewang.helloworld.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;

import com.example.ewang.helloworld.ShopActivity;
import com.example.ewang.helloworld.LoginActivity;
import com.example.ewang.helloworld.MainActivity;
import com.example.ewang.helloworld.UpdateProfileActivity;
import com.example.ewang.helloworld.helper.CustomActivityManager;
import com.example.ewang.helloworld.helper.DialogHelper;
import com.example.ewang.helloworld.helper.JsonHelper;
import com.example.ewang.helloworld.helper.MyApplication;
import com.example.ewang.helloworld.helper.ResponseWrapper;
import com.example.ewang.helloworld.model.User;
import com.example.ewang.helloworld.service.task.RequestTask;
import com.example.ewang.helloworld.service.task.ResponseListener;
import com.example.ewang.helloworld.service.task.SocketTask;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class LoginService extends Service {

    ProgressDialog progressDialog;

    public static SocketTask socketTask;

    ResponseListener responseListener = new ResponseListener() {
        @Override
        public void onSuccess(ResponseWrapper responseWrapper) {
            Object userObject = responseWrapper.getData().get("user");
            User currentUser = JsonHelper.decode(JsonHelper.encode(userObject), User.class);

            MyApplication.setCurrentUser(currentUser);

            socketTask = new SocketTask();
            socketTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentUser.getId());

            progressDialog.dismiss();
            CustomActivityManager.getInstance().getCurrentActivity().finish();
            Intent intent = new Intent(CustomActivityManager.getInstance().getCurrentActivity(),
                    LoginActivity.requestType == MainActivity.request_reg ? UpdateProfileActivity.class : ShopActivity.class);
            startActivity(intent);
        }

        @Override
        public void onFail(String errMessage) {
            progressDialog.dismiss();
            errMessage = errMessage == null ? "连接服务器异常" : errMessage;
            SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
            editor.remove("account");
            editor.remove("password");
            editor.apply();
            DialogHelper.showAlertDialog(CustomActivityManager.getInstance().getCurrentActivity(), "Warning", errMessage, null, null);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        progressDialog = DialogHelper.showProgressDialog(CustomActivityManager.getInstance().getCurrentActivity(), "请稍侯", "loading", null);
        String url = intent.getStringExtra("url");
        RequestBody requestBody = new FormBody.Builder()
                .add("account", intent.getStringExtra("account"))
                .add("password", intent.getStringExtra("password"))
                .build();
        new RequestTask(responseListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, requestBody);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
