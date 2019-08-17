package org.ionc.wallet.view.activity.transaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ionc.ionclib.bean.TxRecordBean;
import org.ionc.ionclib.bean.WalletBeanNew;
import org.ionc.ionclib.utils.ToastUtil;
import org.ionc.wallet.constant.ConstantParams;
import org.ionc.wallet.utils.DateUtils;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.QRCodeUtils;
import org.ionc.wallet.utils.StringUtils;
import org.ionc.wallet.view.activity.webview.TxRecordBrowserActivity;
import org.ionc.wallet.view.base.AbsBaseActivityTitleTwo;
import org.ionchain.wallet.R;
import org.web3j.utils.Convert;

import java.math.BigDecimal;

import static org.ionc.wallet.constant.ConstantParams.TX_FAILURE;
import static org.ionc.wallet.constant.ConstantParams.TX_HASH;
import static org.ionc.wallet.constant.ConstantParams.TX_HASH_NULL;
import static org.ionc.wallet.constant.ConstantParams.TX_SUSPENDED;
import static org.ionc.wallet.utils.DateUtils.Y4M2D2H2M2S2;

public class TxRecordDetailActivity extends AbsBaseActivityTitleTwo {

    private TxRecordBean mTxRecordBean;
    private ImageView txDetailIcon;
    private TextView txDetailText;
    private TextView txDetailTime;
    private TextView txDetailValue;
    private TextView txFee;
    private TextView txDetailTo;
    private TextView txDetailFrom;
    private TextView txDetailHash;
    private TextView txDetailBlock;
    private TextView wallet_name;
    private ImageView txDetailQrcode;

    private WalletBeanNew mWalletBeanNew;
    private TextView mGoToIonchainExplore;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-06-20 11:18:22 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        txDetailIcon = (ImageView) findViewById(R.id.tx_detail_icon);
        txDetailText = (TextView) findViewById(R.id.tx_detail_text);
        txDetailTime = (TextView) findViewById(R.id.tx_detail_time);
        txDetailValue = (TextView) findViewById(R.id.tx_detail_value);
        txFee = (TextView) findViewById(R.id.tx_detail_fee);
        txDetailTo = (TextView) findViewById(R.id.tx_detail_roll_in);
        txDetailFrom = (TextView) findViewById(R.id.tx_detail_roll_out);
        txDetailHash = (TextView) findViewById(R.id.tx_detail_hash);
        txDetailBlock = (TextView) findViewById(R.id.tx_detail_block);
        wallet_name = (TextView) findViewById(R.id.wallet_name);
        txDetailQrcode = findViewById(R.id.tx_detail_qrcode);
        mGoToIonchainExplore = findViewById(R.id.go_to_ionchain_explore);
    }

    @Override
    protected String getTitleName() {
        return getAppString(R.string.tx_detail);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tx_record_detail;
    }

    @Override
    protected void initView() {
        findViews();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        super.initData();
        LoggerUtils.e(mTxRecordBean.toString());
        if (TX_FAILURE.equals(mTxRecordBean.getBlockNumber())) {
            txDetailText.setText(getAppString(R.string.tx_failure));
            txDetailIcon.setImageResource(R.drawable.tx_failure);

        } else if (TX_SUSPENDED.equals(mTxRecordBean.getBlockNumber())) {
            txDetailText.setText(getAppString(R.string.tx_block_suspended));
            txDetailIcon.setImageResource(R.drawable.tx_suspended);
        } else {
            txDetailText.setText(getAppString(R.string.tx_success));
            txDetailIcon.setImageResource(R.drawable.tx_success);
        }

        String time = DateUtils.getDateToString(Long.parseLong(mTxRecordBean.getTc_in_out()), Y4M2D2H2M2S2);
        txDetailTime.setText(time);
        txDetailValue.setText(mTxRecordBean.getValue() + " IONC");
        txFee.setText(Convert.fromWei(new BigDecimal(mTxRecordBean.getGas()).multiply(new BigDecimal(mTxRecordBean.getGasPrice())), Convert.Unit.ETHER) + " IONC");
        txDetailTo.setText(mTxRecordBean.getTo().trim());
        txDetailFrom.setText(mTxRecordBean.getFrom().trim());
        txDetailHash.setText(mTxRecordBean.getHash() + "   ");

        txDetailBlock.setText(mTxRecordBean.getBlockNumber());
        wallet_name.setText(mWalletBeanNew.getName());
        txDetailQrcode.setImageBitmap(QRCodeUtils.generateQRCode(mTxRecordBean.getHash(), 200));
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
    protected int getCommonTitleBackgroundColor() {
        return R.color.blue_light;
    }

    @Override
    protected int getTitleHeaderBackgroundColor() {
        return R.color.blue_light;
    }

    @Override
    protected void setListener() {
        super.setListener();
        txDetailHash.setOnClickListener(v -> {
            StringUtils.copy(this, mTxRecordBean.getHash());
            Toast.makeText(this, getAppString(R.string.copy_hash_addr), Toast.LENGTH_LONG).show();
        });
        findViewById(R.id.tx_detail_roll_in).setOnClickListener(v -> {
            StringUtils.copy(this, mTxRecordBean.getTo());
            Toast.makeText(this, getAppString(R.string.copy_roll_in_addr), Toast.LENGTH_LONG).show();
        });
        findViewById(R.id.tx_detail_roll_out).setOnClickListener(v -> {
            StringUtils.copy(this, mTxRecordBean.getHash());
            Toast.makeText(this, getAppString(R.string.copy_roll_out_addr), Toast.LENGTH_LONG).show();
        });
        mGoToIonchainExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hash = mTxRecordBean.getHash();
                if (TX_HASH_NULL.equals(hash)) {
                    ToastUtil.showToastLonger(getAppString(R.string.error_tx_hash));
                    return;
                }
                Intent intent = new Intent(mActivity, TxRecordBrowserActivity.class);
                intent.putExtra(TX_HASH, hash);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        mTxRecordBean = intent.getParcelableExtra(ConstantParams.PARCELABLE_TX_RECORD);
        mWalletBeanNew = intent.getParcelableExtra(ConstantParams.PARCELABLE_WALLET_BEAN);
        LoggerUtils.i("mTxRecordBean = " + mTxRecordBean.toString());
    }
}
