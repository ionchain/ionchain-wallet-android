package org.ionc.wallet.sdk.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
    }

    public static boolean isEmpty(String s) {
        return s == null || "".equals(s) || " ".equals(s);
    }

    /**
     * 8-12字符
     * 至少1数字字符
     * 至少1小写字母
     * 至少1大写字母
     * 至少1特殊字符
     *
     * @param value 要匹配的字符串
     * @return 是否匹配
     */
    public static boolean check(String value) {
        Pattern p = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^\\w\\s]).{8,12}$");
        Matcher m = p.matcher(value);
        return m.matches();
//        return true;
    }

    /**
     * 只包含数字 英文 汉字
     *
     * @param str
     * @return 是否符合匹配规则
     */
    public static boolean isNumENCN(String str) {
        String regex = "^[a-zA-Z0-9\u4E00-\u9FA5]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(str);
        return match.matches();
//        return true;
    }

    /**
     * 只英文
     *
     * @param str
     * @return 是否符合匹配规则
     */
    public static boolean isEN(String str) {
        String regex = "^[a-zA-Z]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    /**
     * @param msg 获取sha1码
     * @return
     */
    public static String getSHA(String msg) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("SHA-1");
            md5.update(msg.getBytes());
            byte[] m = md5.digest();//加密
            return getString(m);
        } catch (NoSuchAlgorithmException e) {
            return msg;
        }
    }

    private static String getString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte aB : b) {
            sb.append(aB);
        }
        return sb.toString();
    }

    /** 密码检查
     * @param old_in_db 保存的密码
     * @param p_input 输入的密码
     * @return
     */
    public static boolean chechPwd(String old_in_db, String p_input) {
        String in_sha = getSHA(p_input);
        boolean b = old_in_db.equals(in_sha) || old_in_db.equals(p_input);
        return b;
    }
}
