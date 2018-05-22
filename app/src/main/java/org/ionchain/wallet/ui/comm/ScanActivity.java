package org.ionchain.wallet.ui.comm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.widget.TextView;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;


import java.nio.ByteBuffer;

import butterknife.BindView;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class ScanActivity extends BaseActivity implements QRCodeView.Delegate{

    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;

    private QRCodeView mQRCodeView;

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.navigationBack:
                    finish();
                    break;
                case R.id.albumBtn:
                Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                        .cameraFileDir(null)
                        .maxChooseCount(1)
                        .selectedPhotos(null)
                        .pauseOnScroll(false)
                        .build();
                startActivityForResult(photoPickerIntent, REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
                    break;
                case 0:
                    dismissProgressDialog();
                    if(obj == null)
                        return;

                    ResponseModel<String> responseModel = (ResponseModel)obj;
                    if(!verifyStatus(responseModel)){
                        ToastUtil.showShortToast(responseModel.getMsg());
                        return;
                    }


                    break;
            }

        }catch (Throwable e){
            Logger.e(e,TAG);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_scan);
        mQRCodeView = (ZXingView) findViewById(R.id.zbarview);

    }

    @Override
    protected void setListener() {
        mQRCodeView.setDelegate(this);

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mQRCodeView.startSpot();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        mQRCodeView.showScanRect();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Logger.i(TAG, "result:" + result);
        vibrate();
        mQRCodeView.startSpot();
        scanSuccess(result,"未发现二维码");
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Logger.e(TAG, "打开相机出错");
    }


    void scanSuccess(String result,String msg){
        try{

            if(!TextUtils.isEmpty(result)){
                Intent intent = new Intent();
                intent.putExtra("result",result);
                setResult(RESULT_OK,intent);
            }else{
                ToastUtil.showShortToast(msg);
            }

            finish();

        }catch (Throwable e){
            Logger.e(e,TAG);
        }
    }

    @Override
    public int getActivityMenuRes() {
        return R.menu.menu_scan;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return R.drawable.qmui_icon_topbar_back;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.activity_scan_title;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 识别图片中的二维码还有问题，占时不要用
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
            final String picturePath = BGAPhotoPickerActivity.getSelectedPhotos(data).get(0);

            /*
            这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
            请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
             */
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    Bitmap bitmap = getDecodeAbleBitmap(picturePath);
                    int picw = bitmap.getWidth();
                    int pich = bitmap.getHeight();
                    int[] pix = new int[picw * pich];
                    byte[] pixytes = new byte[picw * pich];
                    bitmap.getPixels(pix, 0, picw, 0, 0, picw, pich);
                    int R, G, B, Y;

                    for (int y = 0; y < pich; y++) {
                        for (int x = 0; x < picw; x++) {
                            int index = y * picw + x;
                            R = (pix[index] >> 16) & 0xff;     //bitwise shifting
                            G = (pix[index] >> 8) & 0xff;
                            B = pix[index] & 0xff;

                            //R,G.B - Red, Green, Blue
                            //to restore the values after RGB modification, use
                            //next statement
                            pixytes[index] = (byte) (0xff000000 | (R << 16) | (G << 8) | B);
                        }
                    }
                    ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
                    byte[] data = new byte[(int) (bitmap.getHeight() * bitmap.getWidth() * 1.5)];
                    rgba2Yuv420(pixytes, data, bitmap.getWidth(), bitmap.getHeight());
                    return mQRCodeView.processData(data, bitmap.getWidth(), bitmap.getHeight(), true);
                }

                @Override
                protected void onPostExecute(String result) {
                    scanSuccess(result,"未发现二维码");
                }
            }.execute();
        }
    }


    /**
     * 将本地图片文件转换成可解码二维码的 Bitmap。为了避免图片太大，这里对图片进行了压缩。感谢 https://github.com/devilsen 提的 PR
     *
     * @param picturePath 本地图片文件路径
     * @return
     */
    private static Bitmap getDecodeAbleBitmap(String picturePath) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picturePath, options);
            int sampleSize = options.outHeight / 400;
            if (sampleSize <= 0) {
                sampleSize = 1;
            }
            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeFile(picturePath, options);
        } catch (Exception e) {
            return null;
        }
    }

    public static void rgba2Yuv420(byte[] src, byte[] dst, int width, int height) {
        // Y
        for (int y = 0; y < height; y++) {
            int dstOffset = y * width;
            int srcOffset = y * width * 4;
            for (int x = 0; x < width && dstOffset < dst.length && srcOffset < src.length; x++) {
                dst[dstOffset] = src[srcOffset];
                dstOffset += 1;
                srcOffset += 4;
            }
        }
        /* Cb and Cr */
        for (int y = 0; y < height / 2; y++) {
            int dstUOffset = y * width + width * height;
            int srcUOffset = y * width * 8 + 1;

            int dstVOffset = y * width + width * height + 1;
            int srcVOffset = y * width * 8 + 2;
            for (int x = 0; x < width / 2 && dstUOffset < dst.length && srcUOffset < src.length && dstVOffset < dst.length && srcVOffset < src.length; x++) {
                dst[dstUOffset] = src[srcUOffset];
                dst[dstVOffset] = src[srcVOffset];

                dstUOffset += 2;
                dstVOffset += 2;

                srcUOffset += 8;
                srcVOffset += 8;
            }
        }
    }
}
