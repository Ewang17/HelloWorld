package com.example.ewang.helloworld.helper;

/**
 * Created by ewang on 2018/6/3.
 */

public class SizeHelper {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        final float scale = MyApplication.getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue) {
        final float scale = MyApplication.getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
