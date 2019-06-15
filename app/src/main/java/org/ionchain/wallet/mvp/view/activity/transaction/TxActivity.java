package org.ionchain.wallet.mvp.view.activity.transaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnBalanceCallback;
import org.ionc.wallet.callback.OnCheckWalletPasswordCallback;
import org.ionc.wallet.callback.OnTransationCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.transaction.TransactionHelper;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.StringUtils;
import org.ionchain.wallet.BuildConfig;
import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.NodeBean;
import org.ionchain.wallet.constant.ConstantUrl;
import org.ionchain.wallet.helper.NodeHelper;
import org.ionchain.wallet.mvp.callback.OnIONCNodeCallback;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.qrcode.activity.CaptureActivity;
import org.ionchain.wallet.qrcode.activity.CodeUtils;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.widget.dialog.check.DialogPasswordCheck;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

import static org.ionchain.wallet.constant.ConstantIntentParam.INTENT_PARAM_CURRENT_WALLET;
import static org.ionchain.wallet.constant.ConstantParams.CURRENT_ADDRESS;
import static org.ionchain.wallet.constant.ConstantParams.CURRENT_KSP;
import static org.ionchain.wallet.constant.ConstantParams.DEFAULT_TRANSCATION_BLOCK_NUMBER;
import static org.ionchain.wallet.constant.ConstantParams.INTENT_PARAME_WALLET_ADDRESS;
import static org.ionchain.wallet.constant.ConstantParams.PARCELABLE_WALLET_BEAN;
import static org.ionchain.wallet.constant.ConstantParams.SEEK_BAR_MAX_VALUE_100_GWEI;
import static org.ionchain.wallet.constant.ConstantParams.SEEK_BAR_MIN_VALUE_1_GWEI;
import static org.ionchain.wallet.constant.ConstantParams.SEEK_BAR_SRART_VALUE;

/**
 * 596928539@qq.com
 * 转账
 */
public class TxActivity extends AbsBaseActivity implements OnTransationCallback, OnBalanceCallback, OnCheckWalletPasswordCallback, OnIONCNodeCallback, OnRefreshListener, NodeHelper.OnTryTimesCallback {

    private RelativeLayout header;
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
    private ImageView scan_address;

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
    private int mProgress = SEEK_BAR_SRART_VALUE;//进度值,起始值为 30 ,最大值为100
    /**
     * 返回键
     */
    private ImageView back;
    /**
     * 下一步
     */
    private Button txNext;
    private String mNodeIONC = ConstantUrl.HOST_NODE_MAIN;
    private SmartRefreshLayout smart_refresh_layout;


    private boolean tapNext = false;

    /**
     * 交易金额
     */
    private String mTxAccount;

    private void findViews() {
        header = findViewById(R.id.header);
        txToAddressEt = findViewById(R.id.tx_to_address);
        mTxValueEt = findViewById(R.id.tx_value);
        mTxCostTv = findViewById(R.id.tx_cost);
        scan_address = findViewById(R.id.scan_address);
        mBalanceTv = findViewById(R.id.balance_tv);
        txSeekBarIndex = findViewById(R.id.tx_seek_bar_index);
        txNext = findViewById(R.id.tx_next);
        back = findViewById(R.id.back);
        smart_refresh_layout = findViewById(R.id.smart_refresh_layout);


    }

    private TxRecordBean mTxRecordBean;

    @Override
    protected void setListener() {
        super.setListener();
        smart_refresh_layout.setOnRefreshListener(this);
        back.setOnClickListener(v -> {
            hideKeyboard();
            finish();
        });
        txNext.setOnClickListener(v -> {
            tapNext = true;
            mAddressTo = txToAddressEt.getText().toString();
            mTxAccount = mTxValueEt.getText().toString();

            if (StringUtils.isEmpty(mAddressTo) || StringUtils.isEmpty(mTxAccount)) {
                ToastUtil.showToastLonger(getAppString(R.string.please_check_addr_amount));
                return;
            }
            if (!tapNext) {
                LoggerUtils.i("获取主链节点成功：来自刷新或者初始化余额的时候");
                balance();
            } else {
                LoggerUtils.i("获取主链节点成功：来自下一步");
                tapNext = false;
                //
                dialogPasswordCheck = new DialogPasswordCheck(mActivity);
                dialogPasswordCheck.setBtnClickedListener(v1 -> dialogPasswordCheck.dismiss(), v12 -> {

                    LoggerUtils.i("主链节点获取成功（检查交易密码之前）：" + mNodeIONC);
                    //检查密码是否正确
                    String pwd_input = dialogPasswordCheck.getPasswordEt().getText().toString();
                    LoggerUtils.i("主链节点获取成功（检查交易密码之前）pwd_input ：" + mNodeIONC);

                    IONCWalletSDK.getInstance().checkCurrentWalletPassword(mWalletBeanNew, pwd_input, mKsPath, TxActivity.this); //转账
                }).show();
            }
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
                mProgress = progress;
                mTxCostTv.setText(getAppString(R.string.tx_fee) + IONCWalletSDK.getInstance().getCurrentFee(mProgress).toPlainString() + " IONC");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        scan_address.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, CaptureActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        if (BuildConfig.APP_DEBUG) {
            txToAddressEt.setText("0x1d858Dab4C8ADdcE47aaafD103705DBD23aAb023");
        }
        txSeekBarIndex.setMax(SEEK_BAR_MAX_VALUE_100_GWEI);
        txSeekBarIndex.setProgress(mProgress);
        mTxCostTv.setText(getAppString(R.string.tx_fee) + IONCWalletSDK.getInstance().getCurrentFee(mProgress).toPlainString() + " IONC");
        balance();
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
        mImmersionBar.titleView(header).statusBarDarkFont(true).execute();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_tx_out;
    }

    /**
     * 交易成功，携带当前的交易信息，跳转到交易记录界面
     *
     * @param hashTx 转账成功的hash值
     */
    @Override
    public void OnTxSuccess(String hashTx) {
        LoggerUtils.i("交易hash :" + hashTx);
        ToastUtil.showToastLonger(getAppString(R.string.submit_success));
        hideProgress();
        mTxRecordBean = new TxRecordBean();
        mTxRecordBean.setTo(mAddressTo);
        mTxRecordBean.setFrom(mAddressFrom);
        mTxRecordBean.setValue(mTxAccount);
        mTxRecordBean.setHash(hashTx);
        mTxRecordBean.setGas(IONCWalletSDK.getInstance().getCurrentFee(mProgress).toPlainString());
        mTxRecordBean.setBlockNumber(DEFAULT_TRANSCATION_BLOCK_NUMBER);//可以作为是否交易成功的展示依据
        IONCWalletSDK.getInstance().saveTxRecordBean(mTxRecordBean);
        finish();
        Intent intent = new Intent(this, TxRecordActivity.class);
        intent.putExtra(INTENT_PARAME_WALLET_ADDRESS, mAddressFrom);
        intent.putExtra(PARCELABLE_WALLET_BEAN, mWalletBeanNew);
        startActivity(intent);
    }

    @Override
    public void onTxFailure(String error) {
        hideProgress();
        LoggerUtils.i("交易hash:" + error);
        ToastUtil.showToastLonger(getAppString(R.string.submit_failure));
    }

    @Override
    public void OnTxDoing() {
        hideProgress();
        ToastUtil.showShort(getAppString(R.string.tx_doing));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBalanceSuccess(BigDecimal balanceBigDecimal, String nodeUrlTag) {
        mBalanceTv.setText(getAppString(R.string.balance_) + balanceBigDecimal + " IONC");
    }

    @Override
    public void onBalanceFailure(String error) {
        NodeHelper.getInstance().tryGetNode(this, this);
    }

    /**
     * 密码检查成功
     *
     * @param bean
     */
    @Override
    public void onCheckWalletPasswordSuccess(WalletBeanNew bean) {
        LoggerUtils.i(" bean " + bean.getAddress());
        dialogPasswordCheck.dismiss();
        showProgress(getAppString(R.string.please_wait));
        final String toAddress = txToAddressEt.getText().toString();
        final String txAccount = mTxValueEt.getText().toString();
        TransactionHelper helper = new TransactionHelper()
                .setGasPrice(mProgress)
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

    /**
     * tapNext 在点击下一步的时候，及准备交易的时候，回去一下主链节点
     *
     * @param dataBean 主链节点
     */
    @Override
    public void onIONCNodeSuccess(List<NodeBean.DataBean> dataBean) {
        mNodeIONC = dataBean.get(0).getIonc_node();
    }


    @Override
    public void onIONCNodeError(String error) {
        LoggerUtils.i("获取主链节点失败：" + error);
        NodeHelper.getInstance().tryGetNode(this, this);
    }

    @Override
    public void onIONCNodeStart() {
        LoggerUtils.i("正在获取主链节点……");
    }

    @Override
    public void onIONCNodeFinish() {
        smart_refresh_layout.finishRefresh();
    }

    @Override
    public void onRefresh(@NotNull RefreshLayout refreshLayout) {
        NodeHelper.getInstance().cancelAndReset();//取消自动尝试
        balance();
    }


    /**
     * 获取余额
     */
    private void balance() {
        IONCWalletSDK.getInstance().getIONCWalletBalance(mNodeIONC, mAddressFrom, this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        NodeHelper.getInstance().cancelAndReset();//取消自动尝试
    }


    @Override
    public void onTryTimes(int count) {
        ToastUtil.showToastLonger(getAppString(R.string.try_net_times, count));
    }

    @Override
    public void onTryFinish(int count) {
        ToastUtil.showToastLonger(getAppString(R.string.try_net_times_finish, count));
    }
}
