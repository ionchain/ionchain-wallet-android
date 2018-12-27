package com.ionc.wallet.sdk.utils;

import android.text.TextUtils;
import android.util.Log;

import com.ionc.wallet.sdk.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;


/**
 * ================================================
 * 描    述：日志的工具类
 * ================================================
 */
public class Logger {

    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char HORIZONTAL_DOUBLE_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;

    private static final char I = 'I', E = 'E';

    private static String LINE_SEPARATOR = System.getProperty("line.separator"); //等价于"\n\r"

    private static boolean isDebug = BuildConfig.LOG_DEBUG;


    /**
     * 打印MAp
     */
    public static void map(String tag, Map map) {
        if (isDebug) {
            Set set = map.entrySet();
            if (set.size() < 1) {
                printLog(I, tag, "[]");
                return;
            }

            int i = 0;
            String[] s = new String[set.size()];
            for (Object aSet : set) {
                Map.Entry entry = (Map.Entry) aSet;
                s[i] = "key = " + entry.getKey() + " , " + "value = " + entry.getValue() + ",\n";
                i++;
            }
            printLog(I, tag, s);
        }
    }

    /**
     * 打印JSON
     *
     * @param tag     标志
     * @param jsonStr jsonString
     */
    public static void j(String jsonStr, String... tag) {
        if (isDebug) {
            String message;
            try {
                final int JSON_INDENT = 4;
                if (jsonStr.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    message = jsonObject.toString(JSON_INDENT); //这个是核心方法
                } else if (jsonStr.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    message = jsonArray.toString(JSON_INDENT);
                } else {
                    message = jsonStr;
                }
            } catch (JSONException e) {
                message = jsonStr;
            }

            message = LINE_SEPARATOR + message;
            String[] lines = message.split(LINE_SEPARATOR);
            printLog(I, tag[0], lines);
        }
    }

    /**
     * 用于打印信息
     *
     * @param tag 级别
     * @param msg 信息
     */
    public static void i(String msg, String... tag) {
        if (isDebug) {
            printLog(I, "info", msg);
        }
    }

    /**
     * 用于打印错误信息
     *
     * @param errorCode 错误码
     * @param msg       错误码的伴随信息：描述信息错误码
     */
    public static void e(String msg, String... errorCode) {
        if (isDebug) {
            if (TextUtils.isEmpty(errorCode[0])) {
                printLog(E, "inner_error", "错误信息 : " + msg);
            } else {
                printLog(E, "inner_error", "错误码 ：" + errorCode[0] + " 信息描述 ：" + msg);
            }
        }
    }

    /**
     * 用于打印错误信息
     *
     * @param msg       错误码的伴随信息：描述信息错误码
     */
    public static void e(String msg) {
        if (isDebug) {
            printLog(E, "inner_error", "错误信息 : " + msg);
        }
    }

    /**
     * @param type 打印类型
     * @param tag  筛选的tag
     * @param msg  要打印的信息
     */
    private static void printer(char type, String tag, String msg) {
        switch (type) {
            case I:
                Log.i(tag + " ", msg);
                break;
            case E:
                Log.e(tag + " ", msg);
                break;
        }
    }

    /**
     * 打印Log被调用的位置
     *
     * @param type 打印类型
     * @param tag  发音筛选的tag
     * @param msg  要打印的信息
     */
    private static void printLocation(char type, String tag, String... msg) {
        for (String str : msg) {
            printer(type, tag, HORIZONTAL_DOUBLE_LINE + "   " + str);
        }
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        int i = 0;
        for (StackTraceElement e : stack) {
            String name = e.getClassName();
            if (!name.equals(Logger.class.getName())) {
                i++;
            } else {
                break;
            }
        }
        i += 3;
        String location = stack[i].toString();
        StringBuilder sb = new StringBuilder();
        sb.append(HORIZONTAL_DOUBLE_LINE).append("   线程名 :  ").append(Thread.currentThread().getName()).append("  调用位置:").append(location);
        printer(type, tag, sb.toString());
    }

    /**
     * 打印消息
     *
     * @param type 打印类型
     * @param tag  发音筛选的tag
     * @param msg  要打印的信息
     */
    private static void printMsg(char type, String tag, String... msg) {
        printer(type, tag, HORIZONTAL_DOUBLE_LINE + "   信息:");
        for (String str : msg) {
            printer(type, tag, HORIZONTAL_DOUBLE_LINE + "   " + str);
        }
    }

    /**
     * 打印log
     *
     * @param type 日志级别
     * @param tag  标志
     * @param msg  描述信息
     */
    private static void printLog(char type, String tag, String... msg) {
        if (msg == null || msg.length == 0) {
            return;
        }
        printer(type, tag, TOP_BORDER);
        printLocation(type, tag, msg);
        printer(type, tag, BOTTOM_BORDER);
    }

    public static void w(String tag, String s) {
        e(s,tag);
    }


}