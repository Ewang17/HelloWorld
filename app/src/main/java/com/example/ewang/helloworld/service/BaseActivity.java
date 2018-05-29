package com.example.ewang.helloworld.service;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.ewang.helloworld.SelfActivity;
import com.example.ewang.helloworld.ShopActivity;
import com.example.ewang.helloworld.R;
import com.example.ewang.helloworld.ShowSessionListActivity;
import com.example.ewang.helloworld.WorldActivity;
import com.example.ewang.helloworld.helper.CustomActivityManager;

/**
 * Created by ewang on 2018/5/14.
 */

public class BaseActivity extends AppCompatActivity {

    RelativeLayout navBar;
    ImageView imageShop;
    ImageView imageWorld;
    ImageView imageDraw;
    ImageView imageSession;
    ImageView imageSelf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(this.getLocalClassName(), "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(this.getLocalClassName(), "onStart");
        navBar = findViewById(R.id.nav_bar);
        imageShop = findViewById(R.id.nav_shop);
        imageWorld = findViewById(R.id.nav_world);
        imageDraw = findViewById(R.id.nav_draw);
        imageSession = findViewById(R.id.nav_session);
        imageSelf = findViewById(R.id.nav_self);
        if (navBar != null) {
            cancelNavStatus();
            setNavClickListener();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(this.getLocalClassName(), "onResume");
        CustomActivityManager.getInstance().setCurrentActivity(this);
        CustomActivityManager.getInstance().setAppForeground(true);
        //移除所有通知
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        if (navBar != null)
            setNavStatus(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(this.getLocalClassName(), "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        CustomActivityManager.getInstance().setAppForeground(false);
        Log.i(this.getLocalClassName(), "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(this.getLocalClassName(), "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(this.getLocalClassName(), "onDestroy");
    }

    void setNavStatus(Activity currentActivity) {
        if (currentActivity instanceof ShopActivity) {
            imageShop.setImageResource(R.drawable.nav_choose);
        } else if (currentActivity instanceof WorldActivity) {
            imageWorld.setImageResource(R.drawable.nav_choose);
        } else if (currentActivity instanceof ShowSessionListActivity) {
            imageSession.setImageResource(R.drawable.nav_choose);
        } else if (currentActivity instanceof SelfActivity) {
            imageSelf.setImageResource(R.drawable.nav_choose);
        }
    }

    void cancelNavStatus() {
        imageShop.setImageResource(R.drawable.nav_unchoose);
        imageWorld.setImageResource(R.drawable.nav_unchoose);
        imageSession.setImageResource(R.drawable.nav_unchoose);
        imageSelf.setImageResource(R.drawable.nav_unchoose);
    }

    void setNavClickListener() {
        imageShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomActivityManager.getInstance().getCurrentActivity(), ShopActivity.class);
                startActivity(intent);
            }
        });
        imageWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomActivityManager.getInstance().getCurrentActivity(), WorldActivity.class);
                startActivity(intent);
            }
        });
        imageSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomActivityManager.getInstance().getCurrentActivity(), ShowSessionListActivity.class);
                startActivity(intent);
            }
        });
        imageSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomActivityManager.getInstance().getCurrentActivity(), SelfActivity.class);
                startActivity(intent);
            }
        });
        //TODO 绘制页面
    }
}
