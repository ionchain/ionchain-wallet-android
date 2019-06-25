package org.ionchain.wallet.mvp.view.activity.transaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.TxRecordBeanAllHelper;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnCheckWalletPasswordCallback;
import org.ionc.wallet.callback.OnTransationCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.transaction.TransactionHelper;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.StringUtils;
import org.ionchain.wallet.BuildConfig;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseCommonTitleThreeActivity;
import org.ionchain.wallet.qrcode.activity.CaptureActivity;
import org.ionchain.wallet.qrcode.activity.CodeUtils;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.widget.dialog.check.DialogPasswordCheck;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.ionchain.wallet.constant.ConstantIntentParam.INTENT_PARAM_CURRENT_WALLET;
import static org.ionchain.wallet.constant.ConstantParams.ADDRESS_DEBUG;
import static org.ionchain.wallet.constant.ConstantParams.CURRENT_ADDRESS;
import static org.ionchain.wallet.constant.ConstantParams.CURRENT_KSP;
import static org.ionchain.wallet.constant.ConstantParams.DEFAULT_TRANSCATION_BLOCK_NUMBER_NULL;
import static org.ionchain.wallet.constant.ConstantParams.DEFAULT_TRANSCATION_HASH_NULL;
import static org.ionchain.wallet.constant.ConstantParams.SEEK_BAR_MAX_VALUE_100_GWEI;
import static org.ionchain.wallet.constant.ConstantParams.SEEK_BAR_MIN_VALUE_1_GWEI;
import static org.ionchain.wallet.constant.ConstantParams.SEEK_BAR_SRART_VALUE;
import static org.ionchain.wallet.constant.ConstantParams.TX_ACTIVITY_FOR_RESULT_CODE;
import static org.ionchain.wallet.constant.ConstantParams.TX_ACTIVITY_RESULT;
import static org.ionchain.wallet.utils.UrlUtils.getHostNode;

/**
 * 596928539@qq.com
 * 转账
 */
public class TxActivity extends AbsBaseCommonTitleThreeActivity implements
        OnTransationCallback,
        OnCheckWalletPasswordCallback {

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
    /**
     * 钱包的ksp ,用于密码检查
     */
    private String mKsPath;

    private DialogPasswordCheck dialogPasswordCheck;
    /**
     * 下一步
     */
    private Button txNext;
    private String mNodeIONC = getHostNode();

    private final BigDecimal mGasPriceScale = BigDecimal.valueOf(10000); //gWei
    private BigDecimal mGasPrice = BigDecimal.valueOf(SEEK_BAR_SRART_VALUE);//进度值,起始值为 30GWei ,最大值为100


    /**
     * 交易金额
     */
    private String mTxAccount;

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
            if (IONCWalletSDK.getInstance().getTxRecordBeansByTimes(String.valueOf(System.currentTimeMillis())) != null) {
                return;
            }
            mAddressTo = txToAddressEt.getText().toString();
            mTxAccount = mTxValueEt.getText().toString();

            if (StringUtils.isEmpty(mAddressTo) || StringUtils.isEmpty(mTxAccount)) {
                ToastUtil.showToastLonger(getAppString(R.string.please_check_addr_amount));
                return;
            }
            //
            dialogPasswordCheck = new DialogPasswordCheck(mActivity);
            dialogPasswordCheck.setBtnClickedListener(v1 -> dialogPasswordCheck.dismiss(), v12 -> {
                dialogPasswordCheck.dismiss();
                closeInputMethod();
                LoggerUtils.i("主链节点获取成功（检查交易密码之前）：" + mNodeIONC);
                //检查密码是否正确
                String pwd_input = dialogPasswordCheck.getPasswordEt().getText().toString();
                LoggerUtils.i("主链节点获取成功（检查交易密码之前）pwd_input ：" + mNodeIONC);

                IONCWalletSDK.getInstance().checkCurrentWalletPassword(mWalletBeanNew, pwd_input, mKsPath, TxActivity.this); //转账
            }).show();
        });
        txSeekBarIndex.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * @param seekBar ..
             * @param progress  Gwei
             * @param fromUser   ...
             */
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = SEEK_BAR_MIN_VALUE_1_GWEI;
                }
                mGasPrice = BigDecimal.valueOf(progress).multiply(mGasPriceScale);
                mTxCostTv.setText(getAppString(R.string.tx_fee) + IONCWalletSDK.getInstance().getCurrentFee(mGasPrice).toPlainString() + " IONC");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTitleRightImage.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, CaptureActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        if (BuildConfig.APP_DEBUG) {
            txToAddressEt.setText(ADDRESS_DEBUG);
        }
        txSeekBarIndex.setMax(SEEK_BAR_MAX_VALUE_100_GWEI);
        txSeekBarIndex.setProgress(30);
        mTxCostTv.setText(getAppString(R.string.tx_fee) + IONCWalletSDK.getInstance().getCurrentFee(mGasPrice.multiply(mGasPriceScale)).toPlainString() + " IONC");
        mBalanceTv.setText(getAppString(R.string.balance_) + ": " + mWalletBeanNew.getBalance());
    }

    @Override
    protected void handleIntent(Intent intent) {
        mAddressFrom = getIntent().getStringExtra(CURRENT_ADDRESS);
        mKsPath = getIntent().getStringExtra(CURRENT_KSP);
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
    public void OnTxSuccess(String hashTx, BigInteger nonce) {
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
        mTxRecordBean.setLocal(true);
        mTxRecordBean.setSuccess(true);
        mTxRecordBean.setNonce(String.valueOf(nonce));
        mTxRecordBean.setGas(IONCWalletSDK.getInstance().getCurrentFee(mGasPrice).toPlainString());
        mTxRecordBean.setBlockNumber(DEFAULT_TRANSCATION_BLOCK_NUMBER_NULL);//可以作为是否交易成功的展示依据
        TxRecordBeanAllHelper txRecordBeanAllHelper = IONCWalletSDK.getInstance().getTxRecordBeanHelperByPublicKey(mWalletBeanNew.getPublic_key());
        if (txRecordBeanAllHelper == null) {
            txRecordBeanAllHelper = new TxRecordBeanAllHelper();
            txRecordBeanAllHelper.setIndexMaxForAll((long) 1);
            txRecordBeanAllHelper.setPublicKey(mWalletBeanNew.getPublic_key());
            IONCWalletSDK.getInstance().saveCurrentWalletTxRecordAllHelper(txRecordBeanAllHelper);
        } else {
            txRecordBeanAllHelper.setIndexMaxForAll(txRecordBeanAllHelper.getIndexMaxForAll()+1);
            IONCWalletSDK.getInstance().updateCurrentWalletTxRecordAllHelper(txRecordBeanAllHelper);
        }
        IONCWalletSDK.getInstance().saveTxRecordBean(mTxRecordBean);
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
        hideProgress();
        LoggerUtils.i("交易   error 666:" + error);
        hideProgress();
        mTxRecordBean = new TxRecordBean();
        mTxRecordBean.setTc_in_out(String.valueOf(System.currentTimeMillis()));
        mTxRecordBean.setTo(mAddressTo);
        mTxRecordBean.setFrom(mAddressFrom);
        mTxRecordBean.setValue(mTxAccount);
        mTxRecordBean.setPublicKey(mWalletBeanNew.getPublic_key());
        mTxRecordBean.setHash(DEFAULT_TRANSCATION_HASH_NULL);
        mTxRecordBean.setLocal(true);
        mTxRecordBean.setSuccess(true);
        mTxRecordBean.setGasPrice(IONCWalletSDK.getInstance().getCurrentFee(mGasPrice).toPlainString());
        mTxRecordBean.setGas(IONCWalletSDK.getInstance().getCurrentFee(mGasPrice).toPlainString());
        mTxRecordBean.setBlockNumber(DEFAULT_TRANSCATION_BLOCK_NUMBER_NULL);//可以作为是否交易成功的展示依据
        TxRecordBeanAllHelper txRecordBeanAllHelper = IONCWalletSDK.getInstance().getTxRecordBeanHelperByPublicKey(mWalletBeanNew.getPublic_key());
        if (txRecordBeanAllHelper == null) {
            txRecordBeanAllHelper = new TxRecordBeanAllHelper();
            txRecordBeanAllHelper.setIndexMaxForAll((long) 1);
            txRecordBeanAllHelper.setPublicKey(mWalletBeanNew.getPublic_key());
            IONCWalletSDK.getInstance().saveCurrentWalletTxRecordAllHelper(txRecordBeanAllHelper);
        } else {
            txRecordBeanAllHelper.setIndexMaxForAll(txRecordBeanAllHelper.getIndexMaxForAll()+1);
            IONCWalletSDK.getInstance().updateCurrentWalletTxRecordAllHelper(txRecordBeanAllHelper);
        }
        Intent intent = new Intent();
        intent.putExtra(TX_ACTIVITY_RESULT, mTxRecordBean);
        /*
         *交易成功，返回成功的hash值给fragment
         */
        setResult(TX_ACTIVITY_FOR_RESULT_CODE, intent);
        finish();
    }

    @Override
    public void OnTxDoing() {
        hideProgress();
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

        showProgress(getAppString(R.string.please_wait));
        final String toAddress = txToAddressEt.getText().toString();
        final String txAccount = mTxValueEt.getText().toString();
        TransactionHelper helper = new TransactionHelper()
                .setGasPrice(mGasPrice)
                .setToAddress(toAddress)
                .setTxValue(txAccount)
                .setWalletBeanTx(bean);
        IONCWalletSDK.getInstance().transaction(mNodeIONC, helper, TxActivity.this);

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

}
