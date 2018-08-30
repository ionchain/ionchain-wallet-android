package org.ionchain.wallet.ui.main;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fast.lib.immersionbar.ImmersionBar;
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
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

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
                            Toast.makeText(HomeFragment.this.getContext(), "余额度已刷新", Toast.LENGTH_SHORT).show();
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
    protected void immersionInit() {
        ImmersionBar.with(getActivity()).with(this)
                .statusBarDarkFont(false)
                .transparentStatusBar()
                .statusBarView(R.id.statusView)
                .navigationBarColor(R.color.black, 0.5f)
                .fitsSystemWindows(false)
                .init();
    }

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
                        return;
                    }


                    break;
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
    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.walletLayout);
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


    private void createChineseQRCode() {
        Log.i("time", "createChineseQRCode: "+System.currentTimeMillis());
        long t = System.currentTimeMillis();
        Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(ApiWalletManager.getInstance().getMyWallet().getAddress(), BGAQRCodeUtil.dp2px(getActivity(), 80));
        Log.i("time", "createChineseQRCode: "+(System.currentTimeMillis()-t));
        codeIv.setImageBitmap(bitmap);

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
        createChineseQRCode();
        mSmartRefreshLayout.finishRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
//        reloadInfo();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        reloadInfo();
    }
}
