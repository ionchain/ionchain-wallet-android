package org.ionc.wallet.utils;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;


/**
 * ================================================
 * 描    述：日志的工具类
 * ================================================
 */
public class LoggerUtils {

    private static boolean isDebug = true;

    public static void initLogger(boolean debug) {
        isDebug = debug;
        Logger.addLogAdapter(new AndroidLogAdapter());
    }


    /**
     * 打印JSON
     *
     * @param jsonStr jsonString
     */
    public static void j(String jsonStr) {
        if (isDebug) {
            Logger.json(jsonStr);
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
            Logger.i(tag + " ： " + msg);
        }
    }

    /**
     * 用于打印信息
     *
     * @param msg 信息
     */
    public static void i(String msg) {
        if (isDebug) {
            Logger.i(msg);
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
            Logger.e(tag,msg);
        }
    }

    /**
     * 用于打印错误信息
     *
     * @param msg 错误码的伴随信息：描述信息错误码
     */
    public static void e(String msg) {
        if (isDebug) {
            Logger.e("e: " + msg);
        }
    }


}
