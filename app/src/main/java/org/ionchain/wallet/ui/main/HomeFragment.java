package org.ionchain.wallet.ui.main;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.ApiWalletManager;
import org.ionchain.wallet.comm.api.constant.ApiConstant;
import org.ionchain.wallet.comm.api.model.Wallet;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.utils.StringUtils;
import org.ionchain.wallet.ui.comm.BaseFragment;
import org.ionchain.wallet.ui.wallet.CreateWalletSelectActivity;

import butterknife.BindView;

import static org.ionchain.wallet.comm.utils.QRCodeUtils.generateQRCode;

public class HomeFragment extends BaseFragment implements OnRefreshListener {


    @BindView(R.id.codeIv)
    ImageView codeIv;
    @BindView(R.id.walletNameTx)
    TextView walletNameTx;

    @BindView(R.id.walletBalanceTx)
    TextView walletBalanceTx;

    @BindView(R.id.walletAddressTx)
    TextView walletAddressTx;
    @BindView(R.id.create_wallet)
    ImageView mImageView;

    private SmartRefreshLayout mSmartRefreshLayout;

    @SuppressLint("HandlerLeak")
    Handler walletHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                ApiConstant.WalletManagerType resulit = ApiConstant.WalletManagerType.codeOf(msg.what);
                if (null == resulit) return;
                ResponseModel<String> responseModel = (ResponseModel<String>) msg.obj;
                switch (resulit) {
                    case WALLET_BALANCE:
                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
//                            Toast.makeText(HomeFragment.this.getContext(), "余额度已刷新", Toast.LENGTH_SHORT).show();
                            walletBalanceTx.setText(ApiWalletManager.getInstance().getMyWallet().getBalance());
                        } else {
                            Toast.makeText(HomeFragment.this.getContext(), "余额度刷新失败", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        break;

                }
            } catch (Throwable e) {
                Log.e(TAG, "handleMessage", e);
            }
        }
    };



    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try {

            switch (what) {
                case R.id.addBtn:
                    transfer(CreateWalletSelectActivity.class);
                    break;
                case R.id.walletLayout:

                    break;
                case 0:
                    dismissProgressDialog();
                    if (obj == null)
                        return;

                    ResponseModel<String> responseModel = (ResponseModel) obj;
                    if (!verifyStatus(responseModel)) {
                        ToastUtil.showShortToast(responseModel.getMsg());
                    }
                    break;
//                case R.id.walletAddressTx:
//                    Toast.makeText(getActivity(), "ssss", Toast.LENGTH_SHORT).show();
//                    break;
            }

        } catch (Throwable e) {
            Logger.e(e, TAG);
        }
    }



    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_asset);
        mSmartRefreshLayout = getViewById(R.id.refresh_asset);
        mSmartRefreshLayout.setOnRefreshListener(this);
        mImmersionBar.titleBar(getViewById(R.id.statusView)).init();
    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.walletLayout);
        walletAddressTx.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager copy = (ClipboardManager) getActivity()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                copy.setText(walletAddressTx.getText());
                Toast.makeText(getActivity(), "已复制地址", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transfer(CreateWalletSelectActivity.class);
            }
        });

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        reloadInfo();
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    public int getActivityMenuRes() {
        return R.menu.menu_home;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return 0;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.tab_home;
    }

    /**
     * 获取钱包信息，并加载展示
     */
    private void reloadInfo() {


        Wallet wallet = ApiWalletManager.getInstance().getMyWallet();
        if (wallet == null) {
            return;
        }
        walletNameTx.setText(wallet.getName());
        walletAddressTx.setText(wallet.getAddress());
        if (!StringUtils.isEmpty(wallet.getBalance())) {
            walletBalanceTx.setText(wallet.getBalance());// 钱包金额
        } else {
            walletBalanceTx.setText("0.0000");// 钱包金额
        }

        ApiWalletManager.getInstance().reLoadBlance(wallet, walletHandler);
        generateQRCode(ApiWalletManager.getInstance().getMyWallet().getAddress(),codeIv);
        mSmartRefreshLayout.finishRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadInfo();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        reloadInfo();
    }
}
