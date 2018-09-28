package org.ionchain.wallet.utils;

import android.graphics.Bitmap;


import org.ionchain.wallet.App;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class QRCodeUtils {

    /**
     * @param msg 二维码包含的信息
     */
    public static Bitmap generateQRCode(String msg) {
        return QRCodeEncoder.syncEncodeQRCode(msg, BGAQRCodeUtil.dp2px(App.mContext, 80));
    }
    /**
     * @param msg 二维码包含的信息
     */
    public static Bitmap generateQRCode(String msg,int size_px) {
        return QRCodeEncoder.syncEncodeQRCode(msg, BGAQRCodeUtil.dp2px(App.mContext, size_px));
    }
}
