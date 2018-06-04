package com.example.ewang.helloworld;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.ewang.helloworld.adapter.SessionAdapter;
import com.example.ewang.helloworld.constants.SystemConstants;
import com.example.ewang.helloworld.helper.MyApplication;
import com.example.ewang.helloworld.model.Session;
import com.example.ewang.helloworld.model.User;
import com.example.ewang.helloworld.service.BaseActivity;
import com.example.ewang.helloworld.service.LoginService;
import com.example.ewang.helloworld.service.ShowSessionListService;

import java.util.List;

public class ShowSessionListActivity extends BaseActivity {

    private Button btnOff;

    public static RecyclerView friendRecyclerView;

    private static SessionAdapter adapter;

    private static List<Session> sessionList;

    private User user = MyApplication.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_session_list);

        btnOff = findViewById(R.id.btn_off);
        friendRecyclerView = findViewById(R.id.friend_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ShowSessionListActivity.this);
        friendRecyclerView.setLayoutManager(layoutManager);

        btnOff.setVisibility(View.VISIBLE);
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowSessionListActivity.this, MainActivity.class);
                MyApplication.setCurrentUser(null);
                SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                editor.remove("account");
                editor.remove("password");
                editor.apply();
                finish();
                startActivity(intent);
            }
        });

        setMessageAdapter(user);
    }

    //app退出要即使关闭socket的线程
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginService.socketTask.closeSocket();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMessageAdapter(user);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    void setMessageAdapter(User user) {
        Intent showFriendsIntent = new Intent(ShowSessionListActivity.this, ShowSessionListService.class)
                .putExtra("url", SystemConstants.DefaultBasicUrl.getValue() + "/session/find")
                .putExtra("userId", user.getId());
        startService(showFriendsIntent);

    }

    public static void setAdapter(List<Session> sessions) {
        sessionList = sessions;
        adapter = new SessionAdapter(sessionList);
        friendRecyclerView.setAdapter(adapter);
    }

    public static void notifyNewMsg(Long userId, String msgContent) {
        for (Session s : sessionList) {
            if (s.getToUser().getId() == userId) {
                s.setUnread(s.getUnread() + 1);
                s.setLatestMessage(msgContent);
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }
}
