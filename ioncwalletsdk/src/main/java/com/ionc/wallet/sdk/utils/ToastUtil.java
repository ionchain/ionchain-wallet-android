package com.ionc.wallet.sdk.utils;

import android.content.Context;
import android.widget.Toast;

import static com.ionc.wallet.sdk.IONCWalletSDK.AppContext;

/**
 * Created by HuangQiang on 2017/6/28.
 */

public class ToastUtil {


    private static Context getContext(){
        return AppContext;
    }
    public static void showShortToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLonger(String message) {

        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private ToastUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public static void showShort(CharSequence message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    public static void showShort(int message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    public static void showLong(CharSequence message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }


    public static void show(CharSequence message, int duration) {
        Toast.makeText(getContext(), message, duration).show();
    }


    public static void show(int message, int duration) {
        Toast.makeText(getContext(), message, duration).show();
    }


}
