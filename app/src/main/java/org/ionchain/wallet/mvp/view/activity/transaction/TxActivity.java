package org.ionchain.wallet.mvp.view.activity.transaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.ionc.wallet.bean.WalletBean;
import org.ionc.wallet.callback.OnBalanceCallback;
import org.ionc.wallet.callback.OnCheckCallback;
import org.ionc.wallet.callback.OnTransationCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.transaction.TransactionHelper;
import org.ionc.wallet.utils.StringUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.widget.dialog.check.DialogPasswordCheck;

import java.math.BigDecimal;

import static org.ionchain.wallet.constant.ConstantParams.CURRENT_ADDRESS;
import static org.ionchain.wallet.constant.ConstantParams.CURRENT_KSP;
import static org.ionchain.wallet.constant.ConstantParams.SEEK_BAR_MAX_VALUE_100_GWEI;
import static org.ionchain.wallet.constant.ConstantParams.SEEK_BAR_MIN_VALUE_1_GWEI;
import static org.ionchain.wallet.constant.ConstantParams.SEEK_BAR_SRART_VALUE;

/**
 * 596928539@qq.com
 * 转账
 */
public class TxActivity extends AbsBaseActivity implements OnTransationCallback, OnBalanceCallback, OnCheckCallback {

    private RelativeLayout header;
    private EditText txToAddressEt;
    private EditText txAccountEt;
    private TextView txCostTv;
    private TextView balance_tv;

    private SeekBar txSeekBarIndex;

    private String mAddress;//当前钱包的地址
    private String mKsPath;//钱包的ksp

    private BigDecimal mCurrentGasPrice;
    private DialogPasswordCheck dialogPasswordCheck;
    private int mProgress = SEEK_BAR_SRART_VALUE;//进度值,起始值为 30 ,最大值为100

    private void findViews() {
        header = findViewById(R.id.header);
        txToAddressEt = findViewById(R.id.tx_to_address);
        txAccountEt = findViewById(R.id.tx_account);
        txCostTv = findViewById(R.id.tx_cost);
        balance_tv = findViewById(R.id.balance_tv);
        txSeekBarIndex = findViewById(R.id.tx_seek_bar_index);
        Button txNext = findViewById(R.id.tx_next);
        ImageView back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                finish();
            }
        });
        txNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String toAddress = txToAddressEt.getText().toString();
                final String txAccount = txAccountEt.getText().toString();
                if (StringUtils.isEmpty(toAddress) || StringUtils.isEmpty(txAccount)) {
                    ToastUtil.showToastLonger(getAppString(R.string.please_check_addr_amount));
                    return;
                }
                dialogPasswordCheck = new DialogPasswordCheck(mActivity);
                dialogPasswordCheck.setBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogPasswordCheck.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //检查密码是否正确
                        String pwd_input = dialogPasswordCheck.getPasswordEt().getText().toString();
                        IONCWalletSDK.getInstance().checkPassword(pwd_input,mKsPath,TxActivity.this);

                    }
                }).show();

            }
        });
        txSeekBarIndex.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * @param seekBar
             * @param progress  Gwei
             * @param fromUser
             */
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = SEEK_BAR_MIN_VALUE_1_GWEI;
                }
                mProgress = progress;
                txCostTv.setText( getAppString(R.string.tx_fee) + IONCWalletSDK.getInstance().getCurrentFee(mProgress).toPlainString() + " IONC");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        txSeekBarIndex.setMax(SEEK_BAR_MAX_VALUE_100_GWEI);
        txSeekBarIndex.setProgress(mProgress);
        txCostTv.setText(getAppString(R.string.tx_fee)+ IONCWalletSDK.getInstance().getCurrentFee(mProgress).toPlainString() + " IONC");
    }

    @Override
    protected void handleIntent(Intent intent) {
        mAddress = getIntent().getStringExtra(CURRENT_ADDRESS);
        mKsPath = getIntent().getStringExtra(CURRENT_KSP);
        IONCWalletSDK.getInstance().getIONCWalletBalance( mAddress, this);
    }

    @Override
    protected void initView() {
        findViews();
        mImmersionBar.titleView(header).statusBarDarkFont(true).execute();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_tx_out;
    }

    @Override
    public void OnTxSuccess(String hashTx) {
        ToastUtil.showToastLonger(getAppString(R.string.submit_success));
        dialogPasswordCheck.dismiss();
    }

    @Override
    public void onTxFailure(String error) {
        ToastUtil.showToastLonger(getAppString(R.string.submit_failure));
        dialogPasswordCheck.dismiss();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBalanceSuccess(String balance, String nodeUrlTag) {
        balance_tv.setText(getAppString(R.string.balance_) + balance);
    }

    @Override
    public void onBalanceFailure(String error) {
        balance_tv.setText(getAppString(R.string.get_balance_error));
    }

    @Override
    public void onCheckSuccess(WalletBean bean) {
        final String toAddress = txToAddressEt.getText().toString();
        final String txAccount = txAccountEt.getText().toString();
        TransactionHelper helper = new TransactionHelper()
                .setGasPrice(mProgress)
                .setToAddress(toAddress)
                .setTxValue(txAccount)
                .setWalletBeanTx(bean);
        IONCWalletSDK.getInstance().transaction(helper, TxActivity.this);
    }

    @Override
    public void onCheckFailure(String errorMsg) {
        ToastUtil.showToastLonger(getAppString(R.string.input_password_error));
    }
}
