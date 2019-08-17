package org.ionc.ionclib.utils;

import android.widget.Toast;

import org.ionc.ionclib.web3j.IONCSDK;


/**
 * Created by HuangQiang on 2017/6/28.
 */

public class ToastUtil {


    public static void showShortToast(String message) {
        Toast.makeText(IONCSDK.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLonger(String message) {

        Toast.makeText(IONCSDK.getAppContext(), message, Toast.LENGTH_LONG).show();
    }

    private ToastUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public static void showShort(CharSequence message) {
        Toast.makeText(IONCSDK.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }


    public static void showShort(int message) {
        Toast.makeText(IONCSDK.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }


    public static void showLong(CharSequence message) {
        Toast.makeText(IONCSDK.getAppContext(), message, Toast.LENGTH_LONG).show();
    }


    public static void show(CharSequence message, int duration) {
        Toast.makeText(IONCSDK.getAppContext(), message, duration).show();
    }


    public static void show(int message, int duration) {
        Toast.makeText(IONCSDK.getAppContext(), message, duration).show();
    }


}
