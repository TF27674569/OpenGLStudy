package com.tf.camera.util;

import android.content.Context;

/**
 * create by TIAN FENG on 2019/9/17
 */
public class ScreenUtils {

    public static int getScreenWith(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
