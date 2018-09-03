package org.ionchain.wallet.comm.utils;

import android.graphics.Bitmap;

import com.fast.lib.comm.LibGlobal;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class QRCodeUtils {

    /**
     * @param msg 二维码包含的信息
     */
    public static Bitmap generateQRCode(String msg) {
        return QRCodeEncoder.syncEncodeQRCode(msg, BGAQRCodeUtil.dp2px(LibGlobal.mContext, 80));
    }
    /**
     * @param msg 二维码包含的信息
     */
    public static Bitmap generateQRCode(String msg,int size_px) {
        return QRCodeEncoder.syncEncodeQRCode(msg, BGAQRCodeUtil.dp2px(LibGlobal.mContext, size_px));
    }
}
