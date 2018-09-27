package org.ionchain.wallet.mvp.view.activity;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.mvp.callback.OnTransationCallback;
import org.ionchain.wallet.manager.WalletManager;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.StringUtils;
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

    private double mSeekBarMaxValue;//seekbar 表示的最大值
    private double mSeekBarMinValue;
    private double mTotalValue;//seekbar所所代表的总长度 0.006-0.0003

    private BigDecimal mCurrentGasPrice;

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
                if (StringUtils.isEmpty(toAddress)) {
                    ToastUtil.showToastLonger("请输入转入地址！");
                }
                final String txAccount = txAccountEt.getText().toString();
                if (StringUtils.isEmpty(txAccount)) {
                    ToastUtil.showToastLonger("请输入转账金额！");
                }
                final DialogPasswordCheck dialogPasswordCheck = new DialogPasswordCheck(mActivity);
                dialogPasswordCheck.setBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogPasswordCheck.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //检查密码是否正确
                        if (!dialogPasswordCheck.getPasswordEt().getText().toString().equals(mCurrentWallet.getPassword())) {
                            ToastUtil.showToastLonger("请输入的正确的密码！");
                            return;
                        }
                        WalletManager.getInstance().transaction(mCurrentWallet.getAddress(), toAddress, mCurrentGasPrice, mCurrentWallet.getPrivateKey(), Double.parseDouble(txAccount), TxActivity.this);
                    }
                }).show();

            }
        });
        txSeekBarIndex.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double dynamicValue = mSeekBarMinValue + mTotalValue * progress / WalletManager.getInstance().getSeekBarMaxValue();
                DecimalFormat df = new DecimalFormat("0.00000000");
                txCostTv.setText("旷工费 " + df.format(dynamicValue) + " IONC");
                BigDecimal bigDecimal = Convert.toWei(String.valueOf(dynamicValue), Convert.Unit.ETHER);
                double d = bigDecimal.doubleValue() / 30000;
                mCurrentGasPrice = Convert.fromWei(String.valueOf(d), Convert.Unit.GWEI);
                Log.i(TAG, "mCurrentGasPrice: " + mCurrentGasPrice);
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
        mSeekBarMaxValue = WalletManager.getInstance().getMaxFee().doubleValue();//0.006
        mSeekBarMinValue = WalletManager.getInstance().getMinFee().doubleValue();//0.0003
        mTotalValue = mSeekBarMaxValue - mSeekBarMinValue;//0.006-0.0003

        double default_value;
        try {
            default_value = WalletManager.getInstance().getDefaultPrice().doubleValue();//默认值 0.00009
        } catch (NullPointerException e) {
            default_value = 0.00009;//默认值 0.00009
        }
        if (default_value == 0) {
            default_value = 0.00009;
        }
        double min_value = WalletManager.getInstance().getMinFee().doubleValue();//最小值 0.0003
        double value = default_value - min_value;//0.0006
        double max_fee = WalletManager.getInstance().getMaxFee().doubleValue();
        double v1 = value / max_fee;
        double progress = (v1 * WalletManager.getInstance().getSeekBarMaxValue());
        Log.i(TAG, "default_value: " + default_value);
        Log.i(TAG, "min_value: " + min_value);
        Log.i(TAG, "value: " + value);
        Log.i(TAG, "max_fee: " + max_fee);
        Log.i(TAG, "v1: " + progress);

        DecimalFormat df = new DecimalFormat("0.00000000");
        txCostTv.setText("旷工费 " + df.format(value) + " IONC");

        int max = WalletManager.getInstance().getSeekBarMaxValue();
        Log.i(TAG, "max: " + max);
        txSeekBarIndex.setMax(max);// 200
        txSeekBarIndex.setProgress((int) progress);// 200
        mCurrentGasPrice = toGasPrice(default_value);
        Log.i(TAG, "initData: " + mCurrentGasPrice);
    }

    private BigDecimal toGasPrice(double progress) {
        BigDecimal price;
        double value = mSeekBarMinValue + mTotalValue * progress / WalletManager.getInstance().getSeekBarMaxValue();
        DecimalFormat df = new DecimalFormat("0.00000000");
        BigDecimal bigDecimal = Convert.toWei(String.valueOf(value), Convert.Unit.ETHER);
        double d = bigDecimal.doubleValue() / 30000;
        price = Convert.fromWei(String.valueOf(d), Convert.Unit.GWEI);
        return price;
    }

    @Override
    protected void handleIntent() {
        mCurrentWallet = (WalletBean) getIntent().getSerializableExtra("wallet");
    }

    @Override
    protected void initView() {
        findViews();
        mImmersionBar.titleBar(header).statusBarDarkFont(true).execute();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_tx_out;
    }

    @Override
    public void OnTxSuccess(String hashTx) {
        ToastUtil.showToastLonger("提交成功！");
        Log.i(TAG, "OnTxSuccess: " + hashTx);
    }

    @Override
    public void onTxFailure(String error) {
        ToastUtil.showToastLonger(error);
    }
}
