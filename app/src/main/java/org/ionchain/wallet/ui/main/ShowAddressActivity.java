package org.ionchain.wallet.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.utils.QRCodeUtils;
import org.ionchain.wallet.comm.utils.StringUtils;
import org.ionchain.wallet.ui.comm.BaseActivity;

public class ShowAddressActivity extends BaseActivity {
    private ImageView bgImg;
    private RelativeLayout showTitle;
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
        bgImg = (ImageView) findViewById(R.id.bg_img);
        showTitle = (RelativeLayout) findViewById(R.id.show_title);
        back = (ImageView) findViewById(R.id.back);
        walletAddressTv = (TextView) findViewById(R.id.walletAddressTv);
        showQrImg = (ImageView) findViewById(R.id.show_qr_img);
        copyBtn = (Button) findViewById(R.id.copyBtn);
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.layout_show_address);
        findViews();

        mImmersionBar.titleBar(getViewById(R.id.show_title)).statusBarDarkFont(false).init();
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
    protected void initData(Bundle savedInstanceState) {
        msg = getIntent().getStringExtra("msg");
        walletAddressTv.setText(msg);
        showQrImg.setImageBitmap(QRCodeUtils.generateQRCode(msg, 200));
    }

    @Override
    public int getActivityMenuRes() {
        return 0;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return 0;
    }

    @Override
    public int getActivityTitleContent() {
        return 0;
    }

}
