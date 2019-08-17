package org.ionc.wallet.view.activity.exchange;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.ionclib.bean.WalletBeanNew;
import org.ionc.ionclib.callback.OnCheckWalletPasswordCallback;
import org.ionc.ionclib.callback.OnContractCoinBalanceCallback;
import org.ionc.ionclib.callback.OnContractCoinTransferCallback;
import org.ionc.ionclib.callback.OnGasPriceCallback;
import org.ionc.ionclib.utils.ToastUtil;
import org.ionc.ionclib.web3j.IONCCancelTag;
import org.ionc.ionclib.web3j.IONCSDKTransfers;
import org.ionc.ionclib.web3j.IONCSDKWallet;
import org.ionc.wallet.constant.ConstantParams;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.view.base.AbsBaseActivityTitleTwo;
import org.ionc.wallet.view.widget.dialog.check.DialogPasswordCheck;
import org.ionchain.wallet.R;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nonnull;

import static org.ionc.wallet.constant.ConstantParams.GAS_LIMIT_DEFAULT;
import static org.ionc.wallet.constant.ConstantParams.GAS_LIMIT_MAX_RANGE;
import static org.ionc.wallet.constant.ConstantParams.GAS_LIMIT_MIN;
import static org.ionc.wallet.constant.ConstantParams.GAS_PRICE_DEFAULT_WEI;

public class ContractWalletDetailActivity extends AbsBaseActivityTitleTwo implements
        OnContractCoinBalanceCallback,
        OnCheckWalletPasswordCallback, OnContractCoinTransferCallback, OnRefreshListener, SeekBar.OnSeekBarChangeListener, OnGasPriceCallback {

    private WalletBeanNew mWalletBeanNew;

    private TextView mExWalletName;
    private TextView mExWalletBalacne;
    private TextView mExWalletAddress;
    private TextView mExWalletContractAddress;
    private AppCompatButton mExchangeMainChain;
    private EditText mExWalletAmount;

    private SmartRefreshLayout mSmartRefreshLayout;
    private String mContractAddress = "0xbC647aAd10114B89564c0a7aabE542bd0cf2C5aF";
    private String mContractToAddress = "0xaCb1B4fF1974e7EF03CacBbad98448237B913036";
    private DialogPasswordCheck mDialogPasswordCheck;

    private int pos;
    private double mValue;

    private EditText mTxValueEt;
    private TextView mTxCostTv;
    private SeekBar txSeekBarIndex;
    private BigDecimal mGas = null;
    private String mFee = "";
    private BigDecimal mGasPrice = GAS_PRICE_DEFAULT_WEI;

    public BigInteger mGasLimit = BigInteger.valueOf((GAS_LIMIT_MIN));
    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-08-03 16:44:29 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        mTxValueEt = findViewById(R.id.tx_value);
        mTxCostTv = findViewById(R.id.tx_cost);
        txSeekBarIndex = findViewById(R.id.tx_seek_bar_index);
        mExWalletName = findViewById(R.id.ex_wallet_name);
        mExWalletBalacne = findViewById(R.id.ex_wallet_balacne);
        mExWalletAddress = findViewById(R.id.ex_wallet_address);
        mExWalletAmount = findViewById(R.id.ex_wallet_amount);
        mExWalletContractAddress = findViewById(R.id.ex_wallet_contract_address);
        mExchangeMainChain = findViewById(R.id.exchange_main_chain);
        mSmartRefreshLayout = findViewById(R.id.srl);
        mSmartRefreshLayout.setOnRefreshListener(this);
        txSeekBarIndex.setOnSeekBarChangeListener(this);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_contract_wallet_detail;
    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        mWalletBeanNew = intent.getParcelableExtra(ConstantParams.PARCELABLE_WALLET_BEAN);
        pos = intent.getIntExtra("pos", 0);
    }

    @Override
    protected void initView() {
        findViews();
    }

    @Override
    protected void initData() {
        super.initData();
        if (mWalletBeanNew == null) {
            return;
        }
        txSeekBarIndex.setMax(GAS_LIMIT_MAX_RANGE + GAS_LIMIT_MIN);// 500000+300000
        txSeekBarIndex.setProgress(GAS_LIMIT_MIN + GAS_LIMIT_DEFAULT);//300000+100000
        mTxCostTv.setText(getAppString(R.string.tx_fee) + "：- -" + " IONC");
        mExWalletName.setText(mWalletBeanNew.getName());
        mExWalletAddress.setText(getAppString(R.string.wallet_address) + ": " + mWalletBeanNew.getAddress());
        mExWalletContractAddress.setText(getAppString(R.string.wallet_contract_address) + ": " + mContractAddress); //合约地址
        mExWalletBalacne.setText(mWalletBeanNew.getContracBalance());
        IONCSDKTransfers.contractBalance(0, mWalletBeanNew.getAddress(), mContractAddress, this);
        IONCSDKTransfers.getGasPriceETH(this);
    }

    @Override
    protected String getTitleName() {
        return getAppString(R.string.contract_wallet_detail);
    }

    @Override
    public void onContractCoinBalanceSuccess(int position, String balance) {
        mSmartRefreshLayout.finishRefresh();
        if (IONCCancelTag.CONTRACT_BALANCE_CANCEL) {
            LoggerUtils.e("取消 onContractCoinBalanceSuccess");
            return;
        }
        mExWalletBalacne.setText(balance);
        mWalletBeanNew.setContracBalance(balance);
        IONCSDKWallet.saveWallet(mWalletBeanNew);
    }

    @Override
    public void onContractCoinBalanceFailure(int position, String error) {
        mSmartRefreshLayout.finishRefresh();
        if (IONCCancelTag.CONTRACT_BALANCE_CANCEL) {
            LoggerUtils.e("取消 onContractCoinBalanceFailure");
            return;
        }
        mExWalletBalacne.setText(mWalletBeanNew.getContracBalance());//  取出历史数据
    }

    @Override
    protected void setListener() {
        mTitleLeftImage.setOnClickListener(v -> {
            backToExWallet();
        });
        mExchangeMainChain.setOnClickListener(v -> {
            try {
                mValue = Double.parseDouble(mExWalletAmount.getText().toString());
            } catch (NumberFormatException e) {
                ToastUtil.showLong(getAppString(R.string.please_check_amount));
                return;
            }
            mDialogPasswordCheck = new DialogPasswordCheck(mActivity);
            mDialogPasswordCheck.setTitleText(getAppString(R.string.mine_change_main_chain));
            mDialogPasswordCheck.setLeftBtnClickedListener(v13 -> {
                mDialogPasswordCheck.dismiss(); //导出KS
            });
            mDialogPasswordCheck.setRightBtnClickedListener(v14 -> {
                /*比对密码是否正确*/
                String pwd1 = mDialogPasswordCheck.getPasswordEt().getText().toString();
                IONCSDKWallet.checkCurrentWalletPassword(false, mWalletBeanNew, pwd1, mWalletBeanNew.getKeystore(), ContractWalletDetailActivity.this);//导出KS
            });
            mDialogPasswordCheck.show();
        });
    }

    @Override
    public void onCheckWalletPasswordSuccess(WalletBeanNew bean) {
        mDialogPasswordCheck.dismiss();
        showProgress(getAppString(R.string.please_wait));
        String password = mDialogPasswordCheck.getPasswordEt().getText().toString();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showLong(getAppString(R.string.please_input_current_password));
        }
        IONCSDKTransfers.contractTransfer(mWalletBeanNew, mGasPrice.toBigInteger(), mGasLimit.toString(), password, mValue, this);
    }

    @Override
    public void onCheckWalletPasswordFailure(String errorMsg) {
        ToastUtil.showToastLonger(getAppString(R.string.please_check_password));
    }

    @Override
    public void onContractCoinTransferSuccess(String hash) {
        LoggerUtils.e("hash = " + hash);
        hideProgress();

        ToastUtil.showLong(getAppString(R.string.ex_success_submit));
    }

    @Override
    public void onContractCoinTransferFailure(String error) {
        hideProgress();
        ToastUtil.showLong(getAppString(R.string.ex_success_failure));
    }

    @Override
    public void onRefresh(@Nonnull RefreshLayout refreshLayout) {
        IONCSDKTransfers.contractBalance(0, mWalletBeanNew.getAddress(), mContractAddress, this);
        IONCSDKTransfers.getGasPriceETH(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backToExWallet();
            return false;//拦截事件
        }

        return super.onKeyDown(keyCode, event);

    }

    private void backToExWallet() {
        IONCCancelTag.CONTRACT_BALANCE_CANCEL = true;
        Intent intent = new Intent();
        intent.putExtra(ConstantParams.PARCELABLE_WALLET_BEAN, mWalletBeanNew);
        intent.putExtra("pos", pos);
        setResult(10, intent);
        finish();

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mGasLimit = BigInteger.valueOf(progress + GAS_LIMIT_MIN);
        mFee = String.valueOf(getCurrentFee(new BigDecimal(mGasLimit)));
        LoggerUtils.e("fee", "mGasLimit  = " + mGasLimit);
        mTxCostTv.setText(getAppString(R.string.tx_fee) + ": " + mFee + " IONC");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**
     * 获取当前进度条下的费用
     *
     * @param gasLimit 消耗的最少的gas量
     * @return 矿工费
     */
    public String getCurrentFee(BigDecimal gasLimit) {
        mGas = mGasPrice.multiply(gasLimit);
        LoggerUtils.e("fee", "mGasPrice  getCurrentFee = " + mGasPrice.toString());
        LoggerUtils.e("fee", "gasLimit  getCurrentFee = " + gasLimit.toString());
        return Convert.fromWei(mGas, Convert.Unit.ETHER).toPlainString();
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onGasPriceSuccess(BigInteger gasPrice) {
        LoggerUtils.e("fee", "getGasPriceETH  net = " + gasPrice.toString());
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
