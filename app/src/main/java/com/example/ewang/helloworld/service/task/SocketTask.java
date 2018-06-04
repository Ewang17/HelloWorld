package com.example.ewang.helloworld.service.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.ewang.helloworld.R;
import com.example.ewang.helloworld.SessionActivity;
import com.example.ewang.helloworld.ShowSessionListActivity;
import com.example.ewang.helloworld.helper.CustomActivityManager;
import com.example.ewang.helloworld.constants.SystemConstants;
import com.example.ewang.helloworld.helper.JsonHelper;
import com.example.ewang.helloworld.helper.MyApplication;
import com.example.ewang.helloworld.model.SocketMessage;
import com.example.ewang.helloworld.model.client.Msg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by ewang on 2018/5/8.
 */

public class SocketTask extends AsyncTask<Object, SocketMessage, Boolean> {

    private Socket socket;

    private OutputStream outputStream;

    private BufferedReader bufferedReader;

    private int notificationId = 0;

    @Override
    protected Boolean doInBackground(Object... objects) {
        try {
            socket = new Socket(SystemConstants.ServerIP.getValue(), 7777);
            if (socket == null) {
                return false;
            }
            outputStream = socket.getOutputStream();
            long id = (long) objects[0];
            outputStream.write((String.valueOf(id) + "\n").getBytes(SystemConstants.CharsetName.getValue()));

            EventBus.getDefault().register(this);

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                    SystemConstants.CharsetName.getValue()));

            String msgJson;
            while ((msgJson = readFromServer()) != null) {
                SocketMessage socketMessage = JsonHelper.decode(msgJson, SocketMessage.class);
                publishProgress(socketMessage);
            }

            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(SocketMessage... values) {
        super.onProgressUpdate(values);
        //TODO 插入本地数据库/缓存
        SocketMessage m = values[0];
        if (!CustomActivityManager.getInstance().isAppForeground()) {
            openNotification(MyApplication.getContext(), m.getUserId(), m.getUsername(), m.getContent());
            return;
        } else {
            if (CustomActivityManager.getInstance().getCurrentActivity() instanceof SessionActivity) {
                SessionActivity.notifyNewMsg(m, Msg.TYPE_RECEIVED);
            } else if (CustomActivityManager.getInstance().getCurrentActivity() instanceof ShowSessionListActivity) {
                ShowSessionListActivity.notifyNewMsg(m.getUserId(), m.getContent());
            }
        }
    }

    void openNotification(Context context, long userId, String username, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, CustomActivityManager.getInstance().getCurrentActivity().getClass())
                .setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(username)
                .setContentText(message.length() < 20 ? message : message.substring(0, 20) + "...")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo))
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500})
                .build();
        notificationManager.notify(String.valueOf(userId), (int) System.currentTimeMillis(), notification);
    }

    public String readFromServer() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public void closeSocket() {
        try {
            socket.close();
            bufferedReader.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("fail:", "in closing socket");
        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSendMsgEvent(SocketMessage m) {
        String msgJson = JsonHelper.encode(m);
        try {
            outputStream.write((msgJson + "\n").getBytes(SystemConstants.CharsetName.getValue()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
