package org.ionchain.wallet.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.zxing.WriterException;
import com.ionc.wallet.sdk.utils.Logger;

import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.qrcode.bean.ZxingConfig;
import org.ionchain.wallet.qrcode.encode.CodeCreator;

import static org.ionchain.wallet.constant.ConstantParams.INTENT_ZXING_CONFIG;


public class QRCodeUtils {

    /**
     * @param msg 二维码包含的信息
     */
//    public static Bitmap generateQRCode(String msg) {
////        return QRCodeEncoder.syncEncodeQRCode(msg, BGAQRCodeUtil.dp2px(App.Companion.getMContext(), 80));
//    }

    /**
     * @param msg 二维码包含的信息
     */
    public static Bitmap generateQRCode(String msg, int size_px) {
        Bitmap bitmap = null;
        try {
            /*
             * contentEtString：字符串内容
             * w：图片的宽
             * h：图片的高
             * logo：不需要logo的话直接传null
             * */

            Bitmap logo = BitmapFactory.decodeResource(App.mContext.getResources(), R.mipmap.ic_launcher);
            bitmap = CodeCreator.createQRCode(msg, size_px, size_px, null);
        } catch (WriterException e) {
            Logger.e(e.getMessage());
        }
        return bitmap;
    }

    public static final void setQR(Intent intent) {
        ZxingConfig config = new ZxingConfig();
        config.setPlayBeep(true);//是否播放扫描声音 默认为true
        config.setShake(true);//是否震动  默认为true
        config.setDecodeBarCode(true);//是否扫描条形码 默认为true
        config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
        config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
        config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
        config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
        intent.putExtra(INTENT_ZXING_CONFIG, config);
    }
}
