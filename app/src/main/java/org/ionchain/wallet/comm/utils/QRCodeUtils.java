package org.ionchain.wallet.comm.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.fast.lib.comm.LibGlobal;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class QRCodeUtils {

    /**
     * @param msg 二维码包含的信息
     * @param imageView 二维码展示空间
     */
    public static void generateQRCode(String msg, ImageView imageView) {
        Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(msg, BGAQRCodeUtil.dp2px(LibGlobal.mContext, 80));
        imageView.setImageBitmap(bitmap);
    }
}
