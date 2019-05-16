package org.ionc.wallet.utils;

import android.util.Log;

import com.ionc.wallet.sdk.BuildConfig;


/**
 * ================================================
 * 描    述：日志的工具类
 * ================================================
 */
public class Logger {

    private static boolean isDebug = BuildConfig.DEBUG;

    public static void initLogger(boolean debug) {
        isDebug = debug;
    }

    /**
     * 打印JSON
     *
     * @param tag     标志
     * @param jsonStr jsonString
     */
    public static void j(String jsonStr, String tag) {
        if (isDebug) {
            Log.i(tag, "j: " + jsonStr);
        }
    }

    /**
     * 用于打印信息
     *
     * @param tag 级别
     * @param msg 信息
     */
    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    /**
     * 用于打印信息
     *
     * @param msg 信息
     */
    public static void i(String msg) {
        if (isDebug) {
            Log.i("msg-info", "msg: " + msg);
        }
    }

    /**
     * 用于打印错误信息
     *
     * @param tag 错误码
     * @param msg 错误码的伴随信息：描述信息错误码
     */
    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, "e: " + msg);
        }
    }

    /**
     * 用于打印错误信息
     *
     * @param msg 错误码的伴随信息：描述信息错误码
     */
    public static void e(String msg) {
        if (isDebug) {
            Log.e("default", "e: " + msg);
        }
    }


}
