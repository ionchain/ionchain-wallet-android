package org.ionchain.wallet.mvp.view.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ionc.wallet.sdk.utils.StringUtils;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.BitmapUtils;
import org.ionchain.wallet.utils.QRCodeUtils;
import org.ionchain.wallet.utils.ToastUtil;

import static org.ionchain.wallet.constant.ConstantParams.PICTURE_FILE_NAME;

public class ShowAddressActivity extends AbsBaseActivity {
    private ImageView back;
    private TextView walletAddressTv;
    private ImageView showQrImg;
    private Button copyBtn;

    private String msg = "";

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-03 17:09:14 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        back = (ImageView) findViewById(R.id.back);
        walletAddressTv = (TextView) findViewById(R.id.walletAddressTv);
        showQrImg = (ImageView) findViewById(R.id.show_qr_img);
        copyBtn = (Button) findViewById(R.id.copyBtn);
        msg = getIntent().getStringExtra("msg");
        walletAddressTv.setText(msg);
        showQrImg.setImageBitmap(QRCodeUtils.generateQRCode(msg, 200));
    }


    @Override
    protected void initData() {

    }


    @Override
    protected void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isEmpty(walletAddressTv.getText().toString())) {
                    StringUtils.copy(ShowAddressActivity.this, walletAddressTv.getText().toString());
                    Toast.makeText(ShowAddressActivity.this, "已复制地址", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ShowAddressActivity.this, "复制失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void initView() {
        findViews();
        getMImmersionBar().titleView(R.id.show_title).statusBarDarkFont(false).execute();
        showQrImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BitmapUtils.savePicture(QRCodeUtils.generateQRCode(msg, 200), PICTURE_FILE_NAME, msg+".jpg");
                ToastUtil.showLong("保存成功！\n保存路径: SD卡根目录的 ionchainAddress 文件夹下");
                return true;
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_address;
    }


}
