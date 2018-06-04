package com.example.ewang.helloworld.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.example.ewang.helloworld.SessionActivity;
import com.example.ewang.helloworld.helper.JsonHelper;
import com.example.ewang.helloworld.helper.MyApplication;
import com.example.ewang.helloworld.helper.ResponseWrapper;
import com.example.ewang.helloworld.model.Message;
import com.example.ewang.helloworld.model.client.Msg;
import com.example.ewang.helloworld.model.User;
import com.example.ewang.helloworld.service.task.RequestTask;
import com.example.ewang.helloworld.service.task.ResponseListener;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ShowMessagesService extends Service {

    ResponseListener responseListener = new ResponseListener() {
        @Override
        public void onSuccess(ResponseWrapper responseWrapper) {
            Map<String, Object> dataMap = responseWrapper.getData();
            List<Message> messageList = JsonHelper.decode(JsonHelper.encode(dataMap.get("messageList")),
                    new TypeReference<List<Message>>() {
                    });
            List<Msg> msgList = new ArrayList<>();
            User user = MyApplication.getCurrentUser();
            for (Message m : messageList) {
                if (m.getUser().getId() == user.getId()) {
                    msgList.add(new Msg(m.getContent(), Msg.TYPE_SENT));
                } else if (m.getToUser().getId() == user.getId()) {
                    msgList.add(new Msg(m.getContent(), Msg.TYPE_RECEIVED));
                }
            }
            SessionActivity.setAdapter(msgList);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("url");
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", String.valueOf(intent.getLongExtra("userId", 0)))
                .add("toUserId", String.valueOf(intent.getLongExtra("toUserId", 0)))
                .build();
        new RequestTask(responseListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, requestBody);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
