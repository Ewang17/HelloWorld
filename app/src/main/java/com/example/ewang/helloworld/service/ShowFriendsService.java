package com.example.ewang.helloworld.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.example.ewang.helloworld.ShowFriendsActivity;
import com.example.ewang.helloworld.helper.JsonHelper;
import com.example.ewang.helloworld.helper.ResponseWrapper;
import com.example.ewang.helloworld.model.Message;
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
            List<User> userList = JsonHelper.decode(
                    JsonHelper.encode(dataMap.get("userList")), new TypeReference<List<User>>() {
                    });
            Map<Long, Message> messageMap = JsonHelper.decode(
                    JsonHelper.encode(dataMap.get("messageMap")), new TypeReference<Map<Long, Message>>() {
                    });
            ShowFriendsActivity.setAdapter(userList, messageMap);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("url");
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", String.valueOf(intent.getLongExtra("userId", 0)))
                .build();
        new RequestTask(responseListener).execute(url, requestBody);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
