package com.fast.lib.utils;

import android.app.Activity;
import android.widget.Toast;

import com.fast.lib.comm.LibGlobal;
import com.fast.lib.logger.Logger;

/**
 * Created by HuangQiang on 2017/6/28.
 */

public class ToastUtil {

    public static Toast notifyToast = null;

    public static void showShortToast(String info) {

        try{
            if (LibGlobal.mContext != null && info != null) {
                if (notifyToast != null)
                    notifyToast.cancel();

                notifyToast = Toast.makeText(LibGlobal.mContext, info, Toast.LENGTH_SHORT);
                notifyToast.show();

            }
        }catch (Throwable e){
            Logger.e(e,"ToastUtil");
        }


    }


    public static void showShortToast(Activity activity,String info) {

        try{


            if (LibGlobal.mContext != null && info != null) {
                if (notifyToast != null)
                    notifyToast.cancel();

                notifyToast = Toast.makeText(activity, info, Toast.LENGTH_SHORT);
                notifyToast.show();

            }
        }catch (Throwable e){
            Logger.e(e,"ToastUtil");
        }


    }




    public static void showToastLonger(String info) {

        try{
            if (LibGlobal.mContext != null && info != null) {
                if (notifyToast != null)
                    notifyToast.cancel();
                notifyToast = Toast.makeText(LibGlobal.mContext, info, Toast.LENGTH_LONG);
                notifyToast.show();

            }
        }catch (Throwable e){
            Logger.e(e,"ToastUtil");
        }



    }




}
