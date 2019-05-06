package org.ionchain.wallet.widget.dialog;

import android.util.Log;

/**
 * 日志相关类:默认是测试环境<br>
 * <b>支持：存储Log日志文件到本地。发送Log日志信息到服务器</b>
 *
 * @since 2016-5-13 14:31:21
 */
public class LogUtils {
 
    public static boolean isDebug = true;
 
    private final static String APP_TAG = "demoUtils";
 
    /**
     * 获取相关数据:类名,方法名,行号等.用来定位行<br>
     * at cn.utils.MainActivity.onCreate(MainActivity.java:17) 就是用來定位行的代碼<br>
     *
     * @return [ Thread:main, at
     * cn.utils.MainActivity.onCreate(MainActivity.java:17)]
     */
    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts != null) {
            for (StackTraceElement st : sts) {
                if (st.isNativeMethod()) {
                    continue;
                }
                if (st.getClassName().equals(Thread.class.getName())) {
                    continue;
                }
                if (st.getClassName().equals(LogUtils.class.getName())) {
                    continue;
                }
                return "[ Thread:" + Thread.currentThread().getName() + ", at " + st.getClassName() + "." + st.getMethodName()
                        + "(" + st.getFileName() + ":" + st.getLineNumber() + ")" + " ]";
            }
        }
        return null;
    }
 
 
 
    public static void v(String msg) {
        if (isDebug) {
            Log.v(APP_TAG, getMsgFormat(msg));
        }
    }
 
    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, getMsgFormat(msg));
        }
    }
 
 
    public static void d(String msg) {
        if (isDebug) {
            Log.d(APP_TAG, getMsgFormat(msg));
        }
    }
 
    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, getMsgFormat(msg));
        }
    }
 
 
    public static void i(String msg) {
        if (isDebug) {
            Log.i(APP_TAG, getMsgFormat(msg));
        }
    }
 
    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, getMsgFormat(msg));
        }
    }
 
 
 
    public static void w(String msg) {
        if (isDebug) {
            Log.w(APP_TAG, getMsgFormat(msg));
        }
    }
 
    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, getMsgFormat(msg));
        }
    }
 
 
    public static void e(String msg) {
        if (isDebug) {
            Log.e(APP_TAG, getMsgFormat(msg));
        }
    }
 
    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, getMsgFormat(msg));
        }
    }
 
    /**
     * 输出格式定义
     */
    private static String getMsgFormat(String msg) {
        return msg + " ;" + getFunctionName();
    }
 
}
 

