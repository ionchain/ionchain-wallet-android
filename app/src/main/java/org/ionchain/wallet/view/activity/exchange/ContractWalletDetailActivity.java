package org.ionchain.wallet.view.activity.exchange;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnCheckWalletPasswordCallback;
import org.ionc.wallet.callback.OnContractCoinBalanceCallback;
import org.ionc.wallet.callback.OnContractCoinTransfereCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.sdk.Web3jCancelTag;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.constant.ConstantParams;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.view.base.AbsBaseActivityTitleTwo;
import org.ionchain.wallet.view.widget.dialog.check.DialogPasswordCheck;

public class ContractWalletDetailActivity extends AbsBaseActivityTitleTwo implements OnContractCoinBalanceCallback, OnCheckWalletPasswordCallback, OnContractCoinTransfereCallback, OnRefreshListener {

    private WalletBeanNew mWalletBeanNew;

    private TextView exWalletName;
    private TextView exWalletBalacne;
    private TextView exWalletAddress;
    private TextView exWalletContractAddress;
    private AppCompatButton exchangeMainChain;
    private EditText ex_wallet_amount;

    private SmartRefreshLayout mSmartRefreshLayout;
    private String contractAddress = "0xbC647aAd10114B89564c0a7aabE542bd0cf2C5aF";
    private DialogPasswordCheck mDialogPasswordCheck;

    private int pos;
    private double mValue;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-08-03 16:44:29 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        exWalletName = findViewById(R.id.ex_wallet_name);
        exWalletBalacne = findViewById(R.id.ex_wallet_balacne);
        exWalletAddress = findViewById(R.id.ex_wallet_address);
        ex_wallet_amount = findViewById(R.id.ex_wallet_amount);
        exWalletContractAddress = findViewById(R.id.ex_wallet_contract_address);
        exchangeMainChain = findViewById(R.id.exchange_main_chain);
        mSmartRefreshLayout = findViewById(R.id.srl);
        mSmartRefreshLayout.setOnRefreshListener(this);
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
        exWalletName.setText(mWalletBeanNew.getName());
        exWalletAddress.setText(mWalletBeanNew.getAddress());
        exWalletContractAddress.setText(contractAddress); //合约地址
//        exWalletBalacne.setText(mWalletBeanNew.getContracBalance());
        IONCWalletSDK.getInstance().contractBalance(0, mWalletBeanNew.getAddress(), contractAddress, this);
    }

    @Override
    protected String getTitleName() {
        return getAppString(R.string.contract_wallet_detail);
    }

    @Override
    public void onContractCoinBalanceSuccess(int position, String balance) {
        mSmartRefreshLayout.finishRefresh();
        if (Web3jCancelTag.CONTRACT_BALANCE_CANCEL) {
            LoggerUtils.e("取消 onContractCoinBalanceSuccess");
            return;
        }
        exWalletBalacne.setText(balance + " IONC");
        mWalletBeanNew.setContracBalance(balance);
        IONCWalletSDK.getInstance().saveWallet(mWalletBeanNew);
    }

    @Override
    public void onContractCoinBalanceFailure(int position, String error) {
        mSmartRefreshLayout.finishRefresh();
        if (Web3jCancelTag.CONTRACT_BALANCE_CANCEL) {
            LoggerUtils.e("取消 onContractCoinBalanceFailure");
            return;
        }
        exWalletBalacne.setText(mWalletBeanNew.getContracBalance());//  取出历史数据
    }

    @Override
    protected void setListener() {
        mTitleLeftImage.setOnClickListener(v -> {
            backToExWallet();
        });
        exchangeMainChain.setOnClickListener(v -> {
            try {
                mValue = Double.parseDouble(ex_wallet_amount.getText().toString());
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
                IONCWalletSDK.getInstance().checkCurrentWalletPassword(mWalletBeanNew, pwd1, mWalletBeanNew.getKeystore(), ContractWalletDetailActivity.this);//导出KS
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
        IONCWalletSDK.getInstance().contractTransfer(mWalletBeanNew, password, contractAddress, "0xaCb1B4fF1974e7EF03CacBbad98448237B913036", mValue, this);
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
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        IONCWalletSDK.getInstance().contractBalance(0, mWalletBeanNew.getAddress(), contractAddress, this);
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
        Web3jCancelTag.CONTRACT_BALANCE_CANCEL = true;
        Intent intent = new Intent();
        intent.putExtra(ConstantParams.PARCELABLE_WALLET_BEAN, mWalletBeanNew);
        intent.putExtra("pos", pos);
        setResult(10, intent);
        finish();

    }
}
