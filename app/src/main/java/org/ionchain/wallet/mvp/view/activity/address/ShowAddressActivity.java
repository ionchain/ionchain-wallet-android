package org.ionchain.wallet.mvp.view.activity.address;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ionc.wallet.utils.StringUtils;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.BitmapUtils;
import org.ionchain.wallet.utils.QRCodeUtils;
import org.ionchain.wallet.utils.ToastUtil;

import static org.ionchain.wallet.constant.ConstantParams.INTENT_PARAME_WALLTE_ADDRESS;
import static org.ionchain.wallet.constant.ConstantParams.PICTURE_FILE_NAME;

public class ShowAddressActivity extends AbsBaseActivity {
    private ImageView back;
    private TextView walletAddressTv;
    private ImageView showQrImg;
    private Button copyBtn;

    private String address = "";

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-03 17:09:14 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        back = findViewById(R.id.back);
        walletAddressTv = findViewById(R.id.walletAddressTv);
        showQrImg = findViewById(R.id.show_qr_img);
        copyBtn = findViewById(R.id.copyBtn);
        walletAddressTv.setText(address);
        showQrImg.setImageBitmap(QRCodeUtils.generateQRCode(address, 200));
    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        address = intent.getStringExtra(INTENT_PARAME_WALLTE_ADDRESS);
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
                    Toast.makeText(ShowAddressActivity.this, getAppString(R.string.copy_done_addr), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ShowAddressActivity.this, getAppString(R.string.copy_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void initView() {
        findViews();
        mImmersionBar.titleView(R.id.show_title).statusBarDarkFont(false).execute();
        showQrImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BitmapUtils.savePicture(QRCodeUtils.generateQRCode(address, 200), PICTURE_FILE_NAME, address +".jpg");
                ToastUtil.showLong(getAppString(R.string.save_pos));
                return true;
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_address;
    }


}
