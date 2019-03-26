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

import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.sdk.bean.WalletBean;
import org.ionc.wallet.sdk.callback.OnTransationCallback;
import org.ionc.wallet.sdk.transaction.TransactionHelper;
import org.ionc.wallet.sdk.utils.Logger;
import org.ionc.wallet.sdk.utils.StringUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.widget.DialogPasswordCheck;

import java.math.BigDecimal;

import static org.ionchain.wallet.constant.ConstantParams.SEEK_BAR_MAX_VALUE_100_GWEI;
import static org.ionchain.wallet.constant.ConstantParams.SEEK_BAR_MIN_VALUE_1_GWEI;
import static org.ionchain.wallet.constant.ConstantParams.SEEK_BAR_SRART_VALUE;

/**
 * 596928539@qq.com
 * 转账
 */
public class TxActivity extends AbsBaseActivity implements OnTransationCallback {

    private RelativeLayout header;
    private EditText txToAddressEt;
    private EditText txAccountEt;
    private TextView txCostTv;

    private WalletBean mCurrentWallet;
    private SeekBar txSeekBarIndex;


    private BigDecimal mCurrentGasPrice;
    private DialogPasswordCheck dialogPasswordCheck;
    private int mProgress = SEEK_BAR_SRART_VALUE;//进度值,起始值为 30 ,最大值为100

    private void findViews() {
        header = findViewById(R.id.header);
        txToAddressEt = findViewById(R.id.tx_to_address);
        txAccountEt = findViewById(R.id.tx_account);
        txCostTv = findViewById(R.id.tx_cost);
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
                    ToastUtil.showToastLonger("请检查转帐地址或金额是否全部输入！");
                    return;
                }

                dialogPasswordCheck = new DialogPasswordCheck(getMActivity());
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
                        String pwd_dao = mCurrentWallet.getPassword();
                        if (StringUtils.chechPwd(pwd_dao, pwd_input)) {
                            TransactionHelper helper = new TransactionHelper()
                                    .setGasPrice(mProgress)
                                    .setToAddress(toAddress)
                                    .setTxValue(txAccount)
                                    .setWalletBeanTx(mCurrentWallet);
                            IONCWalletSDK.getInstance().transaction(helper, TxActivity.this);
                        } else {
                            ToastUtil.showToastLonger("请输入的正确的密码！");
                        }
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
                txCostTv.setText("旷工费 " + IONCWalletSDK.getInstance().getCurrentFee(mProgress).toPlainString() + " IONC");
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
        txCostTv.setText("旷工费 " + IONCWalletSDK.getInstance().getCurrentFee(mProgress).toPlainString() + " IONC");
    }

    @Override
    protected void handleIntent(Intent intent) {
        mCurrentWallet = (WalletBean) getIntent().getSerializableExtra("wallet");
    }

    @Override
    protected void initView() {
        findViews();
        getMImmersionBar().titleView(header).statusBarDarkFont(true).execute();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_tx_out;
    }

    @Override
    public void OnTxSuccess(String hashTx) {
        ToastUtil.showToastLonger("提交成功！");
        dialogPasswordCheck.dismiss();
        Logger.i(getTAG(), "OnTxSuccess: " + hashTx);
    }

    @Override
    public void onTxFailure(String error) {
        ToastUtil.showToastLonger("交易失败！");
        Logger.e("onTxFailure: " + error, getTAG());
        dialogPasswordCheck.dismiss();
    }
}
