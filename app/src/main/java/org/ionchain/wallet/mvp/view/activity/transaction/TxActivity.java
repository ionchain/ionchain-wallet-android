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
import org.ionc.wallet.sdk.utils.Logger;
import org.ionc.wallet.sdk.utils.StringUtils;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.widget.DialogPasswordCheck;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 转账
 */
public class TxActivity extends AbsBaseActivity implements OnTransationCallback {

    private RelativeLayout header;
    private EditText txToAddressEt;
    private EditText txAccountEt;
    private TextView txCostTv;

    private WalletBean mCurrentWallet;
    private SeekBar txSeekBarIndex;

    private double mSeekBarMaxValue = 0.0006;//seekbar 意义上最大值
    private int mSeekBarMaxProgress = 200;//seekbar 本身的最大值
    private double mSeekBarMinValue = 0.00003;
    private double mTotalValue;//seekbar所所代表的总长度 0.006-0.0003

    private BigDecimal mCurrentGasPrice;
    private DialogPasswordCheck dialogPasswordCheck;

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
                        if (StringUtils.chechPwd(pwd_dao,pwd_input)) {
                            IONCWalletSDK.getInstance().transaction(mCurrentWallet.getAddress(), toAddress, mCurrentGasPrice, mCurrentWallet.getPassword(), mCurrentWallet.getKeystore(), Double.parseDouble(txAccount), TxActivity.this);
                        }else {
                            ToastUtil.showToastLonger("请输入的正确的密码！");
                        }
                    }
                }).show();

            }
        });
        txSeekBarIndex.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double dynamicValue = mSeekBarMinValue + mTotalValue * progress / mSeekBarMaxProgress;
                DecimalFormat df = new DecimalFormat("0.00000000");
                txCostTv.setText("旷工费 " + df.format(dynamicValue) + " IONC");
                BigDecimal bigDecimal = Convert.toWei(String.valueOf(dynamicValue), Convert.Unit.ETHER);
                double d = bigDecimal.doubleValue() / 30000;
//                mCurrentGasPrice = Convert.toWei(String.valueOf(d), Convert.Unit.GWEI);
                mCurrentGasPrice = Convert.fromWei(String.valueOf(d), Convert.Unit.GWEI);
                Logger.i(getTAG(), "mCurrentGasPrice: " + mCurrentGasPrice);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void initData() {
        mTotalValue = mSeekBarMaxValue - mSeekBarMinValue;//0.006-0.0003

        double default_value =0.00009;
        double value = default_value - mSeekBarMinValue;//0.00006
        double max_fee = 0.0006;

        DecimalFormat df = new DecimalFormat("0.00000000");
        txCostTv.setText("旷工费 " + df.format(value) + " IONC");

        txSeekBarIndex.setMax(mSeekBarMaxProgress);// 200
        double v1 = value / max_fee;
        double progress = (v1 * mSeekBarMaxProgress);
        txSeekBarIndex.setProgress((int) progress);// 200
        mCurrentGasPrice = toGasPrice(default_value);
    }

    private BigDecimal toGasPrice(double progress) {
        BigDecimal price;
        double value = mSeekBarMinValue + mTotalValue * progress / mSeekBarMaxProgress;
        DecimalFormat df = new DecimalFormat("0.00000000");
        BigDecimal bigDecimal = Convert.toWei(String.valueOf(value), Convert.Unit.ETHER);
        double d = bigDecimal.doubleValue() / 30000;
        price = Convert.fromWei(String.valueOf(d), Convert.Unit.GWEI);
        return price;
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
        Logger.e("onTxFailure: " + error,getTAG() );
        dialogPasswordCheck.dismiss();
    }
}
