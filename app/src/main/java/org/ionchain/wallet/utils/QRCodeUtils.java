package org.ionchain.wallet.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.ionc.qrcode.activity.CodeUtils;

import org.ionchain.wallet.App;
import org.ionchain.wallet.R;



public class QRCodeUtils {


    /**
     * @param msg 二维码包含的信息
     */
    public static Bitmap generateQRCode(String msg, int size_px) {
        Bitmap bitmap = null;
        Bitmap logo = BitmapFactory.decodeResource(App.mContext.getResources(), R.mipmap.login_logo);
        bitmap =  CodeUtils.createImage(msg, size_px, size_px, null);
        return bitmap;
    }

}
