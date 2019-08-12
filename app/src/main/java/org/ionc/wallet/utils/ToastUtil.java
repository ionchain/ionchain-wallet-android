package org.ionc.wallet.utils;

import android.widget.Toast;

import org.ionc.wallet.App;

/**
 * Created by HuangQiang on 2017/6/28.
 */

public class ToastUtil {


    public static void showShortToast(String message) {
        Toast.makeText(App.mAppInstance, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLonger(String message) {

        Toast.makeText(App.mAppInstance, message, Toast.LENGTH_LONG).show();
    }

    private ToastUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public static void showShort(CharSequence message) {
        Toast.makeText(App.mAppInstance, message, Toast.LENGTH_SHORT).show();
    }


    public static void showShort(int message) {
        Toast.makeText(App.mAppInstance, message, Toast.LENGTH_SHORT).show();
    }


    public static void showLong(CharSequence message) {
        Toast.makeText(App.mAppInstance, message, Toast.LENGTH_LONG).show();
    }


    public static void show(CharSequence message, int duration) {
        Toast.makeText(App.mAppInstance, message, duration).show();
    }


    public static void show(int message, int duration) {
        Toast.makeText(App.mAppInstance, message, duration).show();
    }


}
