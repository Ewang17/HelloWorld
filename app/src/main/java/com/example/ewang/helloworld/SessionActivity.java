package com.example.ewang.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ewang.helloworld.adapter.MsgAdapter;
import com.example.ewang.helloworld.helper.CustomActivityManager;
import com.example.ewang.helloworld.model.Constants;
import com.example.ewang.helloworld.helper.MyApplication;
import com.example.ewang.helloworld.model.SocketMessage;
import com.example.ewang.helloworld.model.Msg;
import com.example.ewang.helloworld.model.User;
import com.example.ewang.helloworld.service.BaseActivity;
import com.example.ewang.helloworld.service.ClearUnreadService;
import com.example.ewang.helloworld.service.SendMessageService;
import com.example.ewang.helloworld.service.ShowMessagesService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SessionActivity extends BaseActivity {

    private static List<Msg> msgList = new ArrayList<>();

    static long userId;

    static long toUserId;

    static String toUsername;

    private EditText editText;

    private Button sendBtn;

    private LinearLayoutManager linearLayoutManager;

    public static RecyclerView msgRecyclerView;

    public static MsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Session", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        toUserId = getIntent().getLongExtra("toUserId", 0);
        toUsername = getIntent().getStringExtra("toUsername");

        User user = MyApplication.getCurrentUser();
        userId = user.getId();

        editText = findViewById(R.id.input_text);
        sendBtn = findViewById(R.id.btn_send);
        msgRecyclerView = findViewById(R.id.msg_recycler_view);

        TextView textView = findViewById(R.id.username_text);
        textView.setText(toUsername);

        linearLayoutManager = new LinearLayoutManager(SessionActivity.this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        showMessages();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSendListener(user.getId(), toUserId);

            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    setSendListener(userId, toUserId);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //清理当前会话持有者的未读消息
        Intent clearUnreadIntent = new Intent(CustomActivityManager.getInstance().getCurrentActivity(), ClearUnreadService.class)
                .putExtra("url", Constants.DefaultBasicUrl.getValue() + "/session/clear/unread")
                .putExtra("userId", userId)
                .putExtra("toUserId", toUserId);
        CustomActivityManager.getInstance().getCurrentActivity().startService(clearUnreadIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showMessages();
    }

    void showMessages() {
        Intent showMessagesIntent = new Intent(SessionActivity.this, ShowMessagesService.class)
                .putExtra("url", Constants.DefaultBasicUrl.getValue() + "/session/message/find")
                .putExtra("userId", userId)
                .putExtra("toUserId", toUserId);
        startService(showMessagesIntent);
    }

    public static void notifyNewMsg(SocketMessage socketMessage, int messageType) {
        if ((socketMessage.getUserId() == toUserId && messageType == Msg.TYPE_RECEIVED) ||
                (socketMessage.getUserId() == userId && messageType == Msg.TYPE_SENT)) {
            msgList.add(new Msg(socketMessage.getContent(), messageType));
            adapter.notifyItemInserted(msgList.size() - 1);
            msgRecyclerView.scrollToPosition(msgList.size() - 1);
        }
    }

    public static void setAdapter(List<Msg> msgs) {
        msgList = msgs;
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        msgRecyclerView.scrollToPosition(msgList.size() - 1);
    }

    void setSendListener(long userId, long toUserId) {
        String content = editText.getText().toString();
        if (content.isEmpty()) {
            return;
        }

        Intent sendMessageIntent = new Intent(SessionActivity.this, SendMessageService.class)
                .putExtra("url", Constants.DefaultBasicUrl.getValue() + "/session/message/send")
                .putExtra("content", content)
                .putExtra("userId", userId)
                .putExtra("toUserId", toUserId);
        startService(sendMessageIntent);

        SocketMessage socketMessage = new SocketMessage(userId, toUserId, content, toUsername);

        //TODO 插入本地数据库/缓存
        EventBus.getDefault().post(socketMessage);
        notifyNewMsg(socketMessage, Msg.TYPE_SENT);
        editText.setText("");
    }


}
