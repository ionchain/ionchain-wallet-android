package org.ionchain.wallet.mvp.view.activity.transaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.utils.DateUtils;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.StringUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.constant.ConstantParams;
import org.ionchain.wallet.mvp.view.base.AbsBaseCommonTitleTwoActivity;
import org.ionchain.wallet.utils.QRCodeUtils;

import static org.ionc.wallet.utils.DateUtils.Y4M2D2H2M2S2;
import static org.ionchain.wallet.constant.ConstantParams.DEFAULT_TRANSCATION_BLOCK_NUMBER;

public class TxRecordDetailActivity extends AbsBaseCommonTitleTwoActivity {

    private TxRecordBean mTxRecordBean;
    private ImageView txDetailIcon;
    private TextView txDetailText;
    private TextView txDetailTime;
    private TextView txDetailValue;
    private TextView txDetailFee;
    private TextView txDetailFee1;
    private TextView txDetailTo;
    private TextView txDetailFrom;
    private TextView txDetailHash;
    private TextView txDetailBlock;
    private ImageView txDetailQrcode;

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
        txDetailFee = (TextView) findViewById(R.id.tx_detail_fee);
        txDetailFee1 = (TextView) findViewById(R.id.tx_detail_fee1);
        txDetailTo = (TextView) findViewById(R.id.tx_detail_to_copy);
        txDetailFrom = (TextView) findViewById(R.id.tx_detail_from);
        txDetailHash = (TextView) findViewById(R.id.tx_detail_hash);
        txDetailBlock = (TextView) findViewById(R.id.tx_detail_block);
        txDetailQrcode = (ImageView) findViewById(R.id.tx_detail_qrcode);
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

        if (!DEFAULT_TRANSCATION_BLOCK_NUMBER.equals(mTxRecordBean.getBlockNumber())) {
            txDetailText.setText(getAppString(R.string.tx_success));
            txDetailIcon.setImageResource(R.drawable.tx_success);
        } else {
            txDetailText.setText(getAppString(R.string.tx_failure));
            txDetailIcon.setImageResource(R.drawable.tx_failure);
        }
        String time = DateUtils.getDateToString(Long.parseLong(mTxRecordBean.getTc_in_out()), Y4M2D2H2M2S2);
        txDetailTime.setText(time);
        txDetailValue.setText(mTxRecordBean.getValue() + " IONC");
        txDetailFee.setText(mTxRecordBean.getGas() + " IONC");
        txDetailFee1.setText("=Gas(21,000)*" + "GasPrice(" + mTxRecordBean.getGasPrice() + "GWei)");
        txDetailTo.setText(mTxRecordBean.getTo()+"   ");
//        SpannableString spannableString = new SpannableString(mTxRecordBean.getTo()+"1");
//        ImageSpan imageSpan = new ImageSpan(this, R.drawable.copy);
//        spannableString.setSpan(imageSpan, mTxRecordBean.getTo().length(), mTxRecordBean.getTo().length()+1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        txDetailTo.setText(spannableString);
        txDetailFrom.setText(mTxRecordBean.getFrom()+"   ");
        txDetailHash.setText(mTxRecordBean.getHash()+"   ");
        txDetailBlock.setText(mTxRecordBean.getBlockNumber());
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
        findViewById(R.id.tx_detail_to_copy).setOnClickListener(v -> {
            StringUtils.copy(this, mTxRecordBean.getTo());
            Toast.makeText(this, getAppString(R.string.copy_to_addr), Toast.LENGTH_LONG).show();
        });
        findViewById(R.id.tx_from_ll_copy).setOnClickListener(v -> {
            StringUtils.copy(this, mTxRecordBean.getHash());
            Toast.makeText(this, getAppString(R.string.copy_from_addr), Toast.LENGTH_LONG).show();
        });

    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        mTxRecordBean = intent.getParcelableExtra(ConstantParams.PARCELABLE_TX_RECORD);
        LoggerUtils.i("mTxRecordBean = " + mTxRecordBean.toString());
    }
}
