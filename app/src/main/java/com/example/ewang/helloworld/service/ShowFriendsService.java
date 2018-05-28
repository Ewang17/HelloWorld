package com.example.ewang.helloworld.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.example.ewang.helloworld.ShowSessionListActivity;
import com.example.ewang.helloworld.helper.JsonHelper;
import com.example.ewang.helloworld.helper.ResponseWrapper;
import com.example.ewang.helloworld.model.Session;
import com.example.ewang.helloworld.model.User;
import com.example.ewang.helloworld.service.task.RequestTask;
import com.example.ewang.helloworld.service.task.ResponseListener;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by ewang on 2018/5/7.
 */

public class ShowFriendsService extends Service {

    ResponseListener responseListener = new ResponseListener() {
        @Override
        public void onSuccess(ResponseWrapper responseWrapper) {
            Map<String, Object> dataMap = responseWrapper.getData();
            List<Session> sessionList = JsonHelper.decode(
                    JsonHelper.encode(dataMap.get("sessionList")), new TypeReference<List<Session>>() {
                    });
            ShowSessionListActivity.setAdapter(sessionList);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("url");
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", String.valueOf(intent.getLongExtra("userId", 0)))
                .build();
        new RequestTask(responseListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, requestBody);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
