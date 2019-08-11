package org.ionchain.wallet.view.activity.address;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.sdk.wallet.utils.StringUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.utils.BitmapUtils;
import org.ionchain.wallet.utils.QRCodeUtils;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.view.base.AbsBaseActivityTitleTwo;

import static org.ionchain.wallet.constant.ConstantParams.INTENT_PARAME_WALLET_ADDRESS;
import static org.ionchain.wallet.constant.ConstantParams.PICTURE_FILE_NAME;

public class ShowAddressActivity extends AbsBaseActivityTitleTwo {
    private ImageView back;
    private TextView walletAddressTv;
    private ImageView showQrImg;
    private TextView copyBtn;
    private TextView save_image_address;

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
        save_image_address = findViewById(R.id.save_image_address);
        showQrImg = findViewById(R.id.show_qr_img);
        copyBtn = findViewById(R.id.copyBtn);
        walletAddressTv.setText(address);
        showQrImg.setImageBitmap(QRCodeUtils.generateQRCode(address, 200));
    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        address = intent.getStringExtra(INTENT_PARAME_WALLET_ADDRESS);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initCommonTitle() {
        mTitleRl = findViewById(R.id.common_title_rl);
        mTitleHeader = findViewById(R.id.title_header);
        mTitleLeftImage = findViewById(R.id.common_image_back);
        mTitleLeftImage.setImageResource(getLeftArrow());
        mTitleNameTv = findViewById(R.id.common_title);
        mTitleNameTv.setText(getTitleName());
        mTitleNameTv.setTextColor(getTitleNameColor());
    }

    @Override
    protected String getTitleName() {
        return getAppString(R.string.tx_in_addrss_title);
    }

    @Override
    protected void setListener() {
        super.setListener();
        copyBtn.setOnClickListener(v -> {
            if (!StringUtils.isEmpty(walletAddressTv.getText().toString())) {
                StringUtils.copy(ShowAddressActivity.this, walletAddressTv.getText().toString());
                Toast.makeText(ShowAddressActivity.this, getAppString(R.string.copy_done_addr), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ShowAddressActivity.this, getAppString(R.string.error_copy), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void initView() {
        findViews();
        save_image_address.setOnClickListener(v -> {
            BitmapUtils.savePicture(QRCodeUtils.generateQRCode(address, 200), PICTURE_FILE_NAME, address + ".jpg");
            ToastUtil.showLong(getAppString(R.string.save_pos));
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_address;
    }


}
