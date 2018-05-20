package org.ionchain.wallet.ui.wallet;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;
import com.fast.lib.utils.encrypt.base.TextUtils;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.model.ResponseModel;
import org.ionchain.wallet.ui.comm.BaseActivity;
import org.ionchain.wallet.ui.comm.ScanActivity;
import org.ionchain.wallet.ui.comm.WebViewActivity;

import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ImprotWalletActivity extends BaseActivity{

    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    @BindView(R.id.contentEt)
    AppCompatEditText contentEt;

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.navigationBack:
                    finish();
                    break;
                case R.id.scanBtn:
                    requestCodeQRCodePermissions();
                    break;
                case R.id.importBtn:

                    finish();

                    break;
                case R.id.linkUrlTv:
                    transfer(WebViewActivity.class,Comm.SERIALIZABLE_DATA,"https://www.baidu.com",Comm.SERIALIZABLE_DATA1,"条款");
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
        setContentView(R.layout.activity_import_wallet);

    }

    @Override
    protected void setListener() {

        setOnClickListener(R.id.importBtn);
        setOnClickListener(R.id.linkUrlTv);


    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == 999){
            String reslut = data.getStringExtra("result");
            contentEt.setText(reslut);

        }
    }

    @Override
    public int getActivityMenuRes() {
        return R.menu.menu_import_wallet;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return R.drawable.qmui_icon_topbar_back;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.activity_import_wallet;
    }


    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.VIBRATE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }else{
            transfer(ScanActivity.class,999);

        }
    }
}
