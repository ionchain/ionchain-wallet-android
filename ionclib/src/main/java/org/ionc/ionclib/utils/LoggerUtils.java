package org.ionc.ionclib.utils;


import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;

/**
 * ================================================
 * 描    述：日志的工具类
 * ================================================
 */
public class LoggerUtils {

    public static void initLogger(boolean debug) {
        XLog.init(debug ? LogLevel.ALL : LogLevel.NONE);
    }


    /**
     * 打印JSON
     *
     * @param jsonStr jsonString
     */
    public static void j(String jsonStr) {
        XLog.tag("json").json(jsonStr);
    }  /**
     * 打印JSON
     *
     * @param tag
     * @param jsonStr jsonString
     */
    public static void j(String tag, String jsonStr) {
        XLog.tag(tag).json(jsonStr);
    }

    /**
     * 用于打印信息
     *
     * @param tag 级别
     * @param msg 信息
     */
    public static void i(String tag, String msg) {
        XLog.tag(tag).i(msg);
    }

    /**
     * 用于打印信息
     *
     * @param tag 级别
     * @param msg 信息
     */
    public static void i(String tag, int msg) {
        XLog.tag(tag).i( String.valueOf(msg));
    }

    /**
     * 用于打印信息
     *
     * @param msg 信息
     */
    public static void i(String msg) {
        XLog.i(msg);
    }

    /**
     * 用于打印错误信息
     *
     * @param tag 错误码
     * @param msg 错误码的伴随信息：描述信息错误码
     */
    public static void e(String tag, String msg) {
        XLog.tag(tag).e(msg);
    }

    /**
     * 用于打印错误信息
     *
     * @param msg 错误码的伴随信息：描述信息错误码
     */
    public static void e(String msg) {
        XLog.e(msg);
    }


}
