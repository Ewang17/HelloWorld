package com.example.ewang.helloworld.helper;

import android.app.Application;
import android.content.Context;

import com.example.ewang.helloworld.model.User;

/**
 * Created by ewang on 2018/4/21.
 */

public class MyApplication extends Application {

    private static Context context;

    private static User currentUser;

    private static int screenWidth;

    private static int screenHeight;

    private static int canvasWidth;

    private static int canvasHeight;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        currentUser = null;
    }

    public static void setCurrentUser(User currentUser) {
        MyApplication.currentUser = currentUser;
    }

    public static void setScreenWidth(int screenWidth) {
        MyApplication.screenWidth = screenWidth;
    }

    public static void setScreenHeight(int screenHeight) {
        MyApplication.screenHeight = screenHeight;
    }

    public static void setCanvasWidth(int canvasWidth) {
        MyApplication.canvasWidth = canvasWidth;
    }

    public static void setCanvasHeight(int canvasHeight) {
        MyApplication.canvasHeight = canvasHeight;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static Context getContext() {
        return context;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static int getCanvasWidth() {
        return canvasWidth;
    }

    public static int getCanvasHeight() {
        return canvasHeight;
    }
}
