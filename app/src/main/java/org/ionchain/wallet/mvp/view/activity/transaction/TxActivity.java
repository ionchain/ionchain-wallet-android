package org.ionchain.wallet.mvp.view.activity.transaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnBalanceCallback;
import org.ionc.wallet.callback.OnCheckWalletPasswordCallback;
import org.ionc.wallet.callback.OnTransationCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.transaction.TransactionHelper;
import org.ionc.wallet.utils.StringUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.NodeBean;
import org.ionchain.wallet.mvp.callback.OnIONCNodeCallback;
import org.ionchain.wallet.mvp.presenter.node.IONCNodePresenter;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.qrcode.activity.CaptureActivity;
import org.ionchain.wallet.qrcode.activity.CodeUtils;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.widget.dialog.check.DialogPasswordCheck;

import java.math.BigDecimal;
import java.util.List;

import static org.ionchain.wallet.constant.ConstantIntentParam.INTENT_PARAM_CURRENT_WALLET;
import static org.ionchain.wallet.constant.ConstantParams.CURRENT_ADDRESS;
import static org.ionchain.wallet.constant.ConstantParams.CURRENT_KSP;
import static org.ionchain.wallet.constant.ConstantParams.SEEK_BAR_MAX_VALUE_100_GWEI;
import static org.ionchain.wallet.constant.ConstantParams.SEEK_BAR_MIN_VALUE_1_GWEI;
import static org.ionchain.wallet.constant.ConstantParams.SEEK_BAR_SRART_VALUE;

/**
 * 596928539@qq.com
 * 转账
 */
public class TxActivity extends AbsBaseActivity implements OnTransationCallback, OnBalanceCallback, OnCheckWalletPasswordCallback, OnIONCNodeCallback, OnRefreshListener {

    private RelativeLayout header;
    /**
     * 收款人地址
     */
    private WalletBeanNew mWalletBeanNew;
    private EditText txToAddressEt;
    private EditText txAccountEt;
    private TextView txCostTv;
    private TextView balance_tv;
    private ImageView scan_address;

    private SeekBar txSeekBarIndex;

    private String mAddress;//当前钱包的地址
    private String mKsPath;//钱包的ksp ,用于密码检查

    private DialogPasswordCheck dialogPasswordCheck;
    private int mProgress = SEEK_BAR_SRART_VALUE;//进度值,起始值为 30 ,最大值为100
    private ImageView back;
    private Button txNext;
    private String mNodeIONC = "";
    private SmartRefreshLayout smart_refresh_layout;

    private void findViews() {
        header = findViewById(R.id.header);
        txToAddressEt = findViewById(R.id.tx_to_address);
        txAccountEt = findViewById(R.id.tx_account);
        txCostTv = findViewById(R.id.tx_cost);
        scan_address = findViewById(R.id.scan_address);
        balance_tv = findViewById(R.id.balance_tv);
        txSeekBarIndex = findViewById(R.id.tx_seek_bar_index);
        txNext = findViewById(R.id.tx_next);
        back = findViewById(R.id.back);
        smart_refresh_layout = findViewById(R.id.smart_refresh_layout);


    }

    @Override
    protected void setListener() {
        super.setListener();
        smart_refresh_layout.setOnRefreshListener(this);
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
                dialogPasswordCheck.setBtnClickedListener(v1 -> dialogPasswordCheck.dismiss(), v12 -> {
                    //主链节点检查
                    if ("".equals(mNodeIONC)) {
                        ToastUtil.showToastLonger(getAppString(R.string.please_refresh));
                        return;
                    }
                    //检查密码是否正确
                    String pwd_input = dialogPasswordCheck.getPasswordEt().getText().toString();
                    IONCWalletSDK.getInstance().checkCurrentWalletPassword(mWalletBeanNew,pwd_input, mKsPath, TxActivity.this); //转账
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
                txCostTv.setText(getAppString(R.string.tx_fee) + IONCWalletSDK.getInstance().getCurrentFee(mProgress).toPlainString() + " IONC");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        scan_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CaptureActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        txSeekBarIndex.setMax(SEEK_BAR_MAX_VALUE_100_GWEI);
        txSeekBarIndex.setProgress(mProgress);
        txCostTv.setText(getAppString(R.string.tx_fee) + IONCWalletSDK.getInstance().getCurrentFee(mProgress).toPlainString() + " IONC");
        new IONCNodePresenter().getNodes(this);
    }

    @Override
    protected void handleIntent(Intent intent) {
        mAddress = getIntent().getStringExtra(CURRENT_ADDRESS);
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

    @Override
    public void OnTxSuccess(String hashTx) {
        ToastUtil.showToastLonger(getAppString(R.string.submit_success));
        hideProgress();
    }

    @Override
    public void onTxFailure(String error) {
        hideProgress();
        ToastUtil.showToastLonger(getAppString(R.string.submit_failure));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBalanceSuccess(BigDecimal balanceBigDecimal, String nodeUrlTag) {
        balance_tv.setText(getAppString(R.string.balance_) + balanceBigDecimal + " IONC");
    }

    @Override
    public void onBalanceFailure(String error) {
        balance_tv.setText(getAppString(R.string.error_net_get_balance));
    }

    /**
     * 密码检查成功
     *
     * @param bean
     */
    @Override
    public void onCheckWalletPasswordSuccess(WalletBeanNew bean) {
        dialogPasswordCheck.dismiss();
        showProgress(getAppString(R.string.please_wait));
        final String toAddress = txToAddressEt.getText().toString();
        final String txAccount = txAccountEt.getText().toString();
        TransactionHelper helper = new TransactionHelper()
                .setGasPrice(mProgress)
                .setToAddress(toAddress)
                .setTxValue(txAccount)
                .setWalletBeanTx(bean);
        IONCWalletSDK.getInstance().transaction(mNodeIONC, helper, TxActivity.this);
    }

    @Override
    public void onCheckWalletPasswordFailure(String errorMsg) {
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
    public void onIONCNodeSuccess(List<NodeBean.DataBean> dataBean) {
        mNodeIONC = dataBean.get(0).getIonc_node();
        getNodes();
    }

    /**
     * 获取主链节点
     */
    private void getNodes() {
        IONCWalletSDK.getInstance().getIONCWalletBalance(mNodeIONC, mAddress, this);
    }

    @Override
    public void onIONCNodeError(String error) {
        getAppString(R.string.error_net_node);
    }

    @Override
    public void onIONCNodeStart() {

    }

    @Override
    public void onIONCNodeFinish() {

    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        //刷新 获取 主链节点
        getNodes();
    }
}
