package org.ionchain.wallet.view.activity.transaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnCheckWalletPasswordCallback;
import org.ionc.wallet.callback.OnGasPriceCallback;
import org.ionc.wallet.callback.OnTransationCallback;
import org.ionc.wallet.sdk.IONCTransfers;
import org.ionc.wallet.sdk.IONCWallet;
import org.ionc.wallet.transaction.TransactionHelper;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.StringUtils;
import org.ionchain.wallet.BuildConfig;
import org.ionchain.wallet.R;
import org.ionchain.wallet.qrcode.activity.CaptureActivity;
import org.ionchain.wallet.qrcode.activity.CodeUtils;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.view.base.AbsBaseActivityTitleThree;
import org.ionchain.wallet.view.widget.dialog.check.DialogPasswordCheck;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.ionc.wallet.sdk.IONCTxRecords.saveTxRecordBean;
import static org.ionc.wallet.sdk.IONCWallet.TX_FAILURE;
import static org.ionc.wallet.sdk.IONCWallet.TX_SUSPENDED;
import static org.ionchain.wallet.constant.ConstantIntentParam.INTENT_PARAM_CURRENT_WALLET;
import static org.ionchain.wallet.constant.ConstantParams.CURRENT_ADDRESS;
import static org.ionchain.wallet.constant.ConstantParams.GAS_LIMIT_DEFAULT;
import static org.ionchain.wallet.constant.ConstantParams.GAS_LIMIT_MAX_RANGE;
import static org.ionchain.wallet.constant.ConstantParams.GAS_LIMIT_MIN;
import static org.ionchain.wallet.constant.ConstantParams.GAS_PRICE_DEFAULT_WEI;
import static org.ionchain.wallet.constant.ConstantParams.TX_ACTIVITY_FOR_RESULT_CODE;
import static org.ionchain.wallet.constant.ConstantParams.TX_ACTIVITY_RESULT;
import static org.ionchain.wallet.constant.ConstantParams.TX_HASH_NULL;
import static org.ionchain.wallet.utils.UrlUtils.getHostNode;

/**
 * 596928539@qq.com
 * 转账
 */
public class TxOutActivity extends AbsBaseActivityTitleThree implements
        OnTransationCallback,
        OnCheckWalletPasswordCallback, SeekBar.OnSeekBarChangeListener, OnGasPriceCallback {

    /**
     * 收款人地址
     */
    private WalletBeanNew mWalletBeanNew;
    private EditText txToAddressEt;
    private EditText mTxValueEt;
    private TextView mTxCostTv;
    /**
     * 余额
     */
    private TextView mBalanceTv;

    private SeekBar txSeekBarIndex;

    /**
     * 当前钱包地址
     */
    private String mAddressFrom;//当前钱包的地址
    /**
     * 接收地址
     */
    private String mAddressTo;


    private DialogPasswordCheck dialogPasswordCheck;
    /**
     * 下一步
     */
    private Button txNext;
    private String mNodeIONC = getHostNode();

    /**
     * 默认30GWei
     */
    private BigDecimal mGasPrice = GAS_PRICE_DEFAULT_WEI;

    public BigInteger mGasLimit = BigInteger.valueOf((GAS_LIMIT_MIN));

    public static final String ADDRESS_DEBUG = "0x964a6027dfe068ab25bc4d2f0b997e65af4d7fb1";//  测试收款方
    /**
     * 交易金额
     */
    private String mTxAccount;
    private String mFee = "";

    private BigDecimal mGas = null;

    private void findViews() {
        txToAddressEt = findViewById(R.id.tx_to_address);
        mTxValueEt = findViewById(R.id.tx_value);
        mTxCostTv = findViewById(R.id.tx_cost);
        mBalanceTv = findViewById(R.id.balance_tv);
        txSeekBarIndex = findViewById(R.id.tx_seek_bar_index);
        txNext = findViewById(R.id.tx_next);
    }

    private TxRecordBean mTxRecordBean;


    @Override
    protected String getTitleName() {
        return getAppString(R.string.tx);
    }

    @Override
    protected void setListener() {
        super.setListener();
        txNext.setOnClickListener(v -> {

            mAddressTo = txToAddressEt.getText().toString();
            mTxAccount = mTxValueEt.getText().toString();
            double value = 0;
            try {
                value = Double.parseDouble(mTxAccount);
            } catch (NumberFormatException e) {
                ToastUtil.showLong(getAppString(R.string.please_check_amount));
                return;
            }
            if (mWalletBeanNew.getAddress().equals(mAddressTo)) {
                ToastUtil.showShort(getAppString(R.string.address_to_same_whit_from));
                return;
            }
            if (StringUtils.isEmpty(mAddressTo) || StringUtils.isEmpty(mTxAccount)) {
                ToastUtil.showToastLonger(getAppString(R.string.please_check_addr_amount));
                return;
            }
            //
            dialogPasswordCheck = new DialogPasswordCheck(mActivity);
            dialogPasswordCheck.setBtnClickedListener(v1 -> dialogPasswordCheck.dismiss(), v12 -> {
                dialogPasswordCheck.dismiss();
                closeInputMethod();
                LoggerUtils.i("主链节点获取成功（检查交易密码之前）：" + mWalletBeanNew.getPassword());
                //检查密码是否正确
                String pwd_input = dialogPasswordCheck.getPasswordEt().getText().toString();
                LoggerUtils.i("主链节点获取成功（检查交易密码之前）pwd_input ：" + pwd_input);

                IONCWallet.checkCurrentWalletPassword(mWalletBeanNew, pwd_input, mWalletBeanNew.getKeystore(), TxOutActivity.this); //转账
            }).show();
        });
        txSeekBarIndex.setOnSeekBarChangeListener(this);
        mTitleRightImage.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, CaptureActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    /**
     * 获取当前进度条下的费用
     *
     * @param gasLimit 消耗的最少的gas量
     * @return 矿工费
     */
    public String getCurrentFee(BigDecimal gasLimit) {

        mGas = mGasPrice.multiply(gasLimit);
        LoggerUtils.e("fee", "mGasPrice = " + mGasPrice);
        LoggerUtils.e("fee", "gasLimit = " + gasLimit);
        LoggerUtils.e("fee", "mGas = " + mGas);
        /*
         * 从Gwei到wei,再从wei到ether ,交易费用以ETH为单位，最低交易21000以wei 为单位
         * */
        return Convert.fromWei(mGas, Convert.Unit.ETHER).toPlainString();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        IONCTransfers.gasPrice(mNodeIONC, this);
        if (BuildConfig.APP_DEBUG) {
            txToAddressEt.setText(ADDRESS_DEBUG);
        }
        txSeekBarIndex.setMax(GAS_LIMIT_MAX_RANGE + GAS_LIMIT_MIN);// 500000+300000
        txSeekBarIndex.setProgress(GAS_LIMIT_MIN + GAS_LIMIT_DEFAULT);//300000+100000
        mGasLimit = BigInteger.valueOf(GAS_LIMIT_MIN + GAS_LIMIT_DEFAULT);
        mTxCostTv.setText(getAppString(R.string.tx_fee) + "：- -" + " IONC");
        mBalanceTv.setText(getAppString(R.string.wallet_balance) + ": " + mWalletBeanNew.getBalance());
    }

    @Override
    protected void handleIntent(Intent intent) {
        mAddressFrom = getIntent().getStringExtra(CURRENT_ADDRESS);
        mWalletBeanNew = intent.getParcelableExtra(INTENT_PARAM_CURRENT_WALLET);
    }

    @Override
    protected void initView() {
        findViews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tx_out;
    }

    /**
     * 交易成功，携带当前的交易信息，跳转到交易记录界面
     *
     * @param hashTx 转账成功的hash值
     * @param nonce
     */
    @Override
    public void onTxSuccess(String hashTx, BigInteger nonce) {
        LoggerUtils.i("交易hash :" + hashTx);
        ToastUtil.showToastLonger(getAppString(R.string.submit_success));
        hideProgress();
        mTxRecordBean = new TxRecordBean();
        mTxRecordBean.setTc_in_out(String.valueOf(System.currentTimeMillis()));
        mTxRecordBean.setTo(mAddressTo);
        mTxRecordBean.setFrom(mAddressFrom);
        mTxRecordBean.setValue(mTxAccount);
        mTxRecordBean.setPublicKey(mWalletBeanNew.getPublic_key());
        mTxRecordBean.setHash(hashTx);
        mTxRecordBean.setNonce(String.valueOf(nonce));
        mTxRecordBean.setGasPrice(String.valueOf(mGasPrice));
        mTxRecordBean.setGas(Convert.fromWei(mGas, Convert.Unit.ETHER).toPlainString());
        mTxRecordBean.setBlockNumber(TX_SUSPENDED);//可以作为是否交易成功的展示依据
        LoggerUtils.i("syncBrowser", "onTxSuccess" + "   TxOutActivity" + mTxRecordBean.toString());
        saveTxRecordBean(mTxRecordBean);
        Intent intent = new Intent();
        intent.putExtra(TX_ACTIVITY_RESULT, mTxRecordBean);
        /*
         *交易成功，返回成功的hash值给fragment
         */
        setResult(TX_ACTIVITY_FOR_RESULT_CODE, intent);
        finish();
    }

    @Override
    public void onTxFailure(String error) {
        LoggerUtils.i("交易   error 666:" + error);
        hideProgress();
        mTxRecordBean = new TxRecordBean();
        mTxRecordBean.setTc_in_out(String.valueOf(System.currentTimeMillis()));
        mTxRecordBean.setTo(mAddressTo);
        mTxRecordBean.setFrom(mAddressFrom);
        mTxRecordBean.setValue(mTxAccount);
        mTxRecordBean.setPublicKey(mWalletBeanNew.getPublic_key());
        mTxRecordBean.setHash(TX_HASH_NULL);
        mTxRecordBean.setGasPrice(String.valueOf(mGasPrice));
        mTxRecordBean.setGas(Convert.fromWei(mGas, Convert.Unit.ETHER).toPlainString());
        mTxRecordBean.setBlockNumber(TX_FAILURE);//交易失败

        Intent intent = new Intent();
        intent.putExtra(TX_ACTIVITY_RESULT, mTxRecordBean);
        /*
         *交易成功，返回成功的hash值给fragment
         */
        setResult(TX_ACTIVITY_FOR_RESULT_CODE, intent);
        finish();
    }

    @Override
    public void onTxStart() {
        showProgress(getAppString(R.string.please_wait));
        ToastUtil.showShort(getAppString(R.string.tx_doing));
    }

    /**
     * 密码检查成功
     *
     * @param bean
     */
    @Override
    public void onCheckWalletPasswordSuccess(WalletBeanNew bean) {
        LoggerUtils.i(" bean " + bean.getAddress());

        final String toAddress = txToAddressEt.getText().toString();
        TransactionHelper helper = new TransactionHelper()
                .setGasPrice(mGasPrice)
                .setToAddress(toAddress)
                .setTxValue(mTxAccount)
                .setGasLimit(mGasLimit)
                .setWalletBeanTx(bean);
        IONCTransfers.transaction(mNodeIONC, helper, TxOutActivity.this);

    }

    @Override
    public void onCheckWalletPasswordFailure(String errorMsg) {
        LoggerUtils.e("检查密码失败：" + errorMsg);
        ToastUtil.showToastLonger(getAppString(R.string.error_input_password));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (null != data) {
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }
            if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                final String result = bundle.getString(CodeUtils.RESULT_STRING);
                txToAddressEt.setText(result);
            } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                ToastUtil.showLong(getResources().getString(R.string.error_parase_toast_qr_code));
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        LoggerUtils.e("transfer", "mGasLimit = " + mGasLimit.toString());
        LoggerUtils.e("transfer", "progress = " + progress);
        //显示gas消耗
        mGasLimit = BigInteger.valueOf(progress + GAS_LIMIT_MIN);
        mFee = String.valueOf(getCurrentFee(new BigDecimal(mGasLimit)));
        LoggerUtils.e("transfer", "mFee = " + mFee);
        mTxCostTv.setText(getAppString(R.string.tx_fee) + ": " + mFee + " IONC");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onGasPriceSuccess(BigInteger gasPrice) {
        LoggerUtils.e("fee", "gasPrice  net = " + gasPrice.toString());
        mGasPrice = new BigDecimal(gasPrice);
        LoggerUtils.e("fee", "mGasPrice  net = " + mGasPrice.toString());

        mGasLimit = BigInteger.valueOf(GAS_LIMIT_MIN + GAS_LIMIT_DEFAULT);
        mTxCostTv.setText(getAppString(R.string.tx_fee) + "：" + getCurrentFee(new BigDecimal(mGasLimit)) + " IONC");
    }

    @Override
    public void onGasRiceFailure(String error) {
        ToastUtil.showLong(error);
    }
}
