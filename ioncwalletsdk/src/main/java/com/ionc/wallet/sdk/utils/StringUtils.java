package com.ionc.wallet.sdk.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by long on 2016/10/18.
 */

public final class StringUtils {

    private StringUtils() {
        throw new AssertionError();
    }


    public static void copy(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("txt", text);//参数一：标签，参数二：要复制到剪贴板的文本
        clipboard.setPrimaryClip(clip);
    }

    public static boolean isEmpty(String s) {
        return s == null || "".equals(s) || " ".equals(s);
    }

    /**
     * 至少8字符
     * 至少1数字字符
     * 至少1小写字母
     * 至少1大写字母
     * 至少1特殊字符
     *
     * @param value 要匹配的字符串
     * @return 是否匹配
     */
    public static boolean check(String value) {
        Pattern p = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^\\w\\s]).{8,}$");
        Matcher m = p.matcher(value);
        return m.matches();
    }
}
