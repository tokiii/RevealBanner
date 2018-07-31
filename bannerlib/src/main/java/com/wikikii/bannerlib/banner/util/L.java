package com.wikikii.bannerlib.banner.util;

import android.util.Log;


public class L {
    public static boolean deBug = false;
    public static String TAG = "LoopView";

    public static void e(String msg) {
        if (deBug)
            Log.e(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        if (deBug)
            Log.e(TAG, msg);
    }

    public static void e(String TAG, Throwable tr) {
        if (deBug)
            Log.e(TAG, "Error——", tr);
    }

    public static void e(String TAG, String msg, Throwable tr) {
        if (deBug)
            Log.e(TAG, msg, tr);
    }
}
