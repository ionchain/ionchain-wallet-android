package org.ionchain.wallet.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.utils.StringUtils;
import org.ionchain.wallet.db.WalletDaoTools;
import org.ionchain.wallet.ui.comm.BaseFragment;
import org.ionchain.wallet.ui.wallet.CreateWalletSelectActivity;
import org.ionchain.wallet.ui.wallet.ModifyWalletActivity;

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
    @BindView(R.id.walletLayout)
    RelativeLayout walletLayout;

    private SmartRefreshLayout mSmartRefreshLayout;
    private Bitmap mBitmap;//二维码
    private Wallet mWallet;

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
                            /*
                            * 这个地方重新设置显示信息
                            * */
                            walletBalanceTx.setText(mWallet.getBalance());
//                            walletNameTx.setText(ApiWalletManager.getInstance().getMainWallet().getName());
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
                case R.id.modify_wallet:
                    Toast.makeText(getActivity(), "修改钱包", Toast.LENGTH_SHORT).show();
                    transfer(ModifyWalletActivity.class, Comm.SERIALIZABLE_DATA,mWallet);
                    break;
            }

        } catch (Throwable e) {
            Logger.e(e, TAG);
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            reloadInfo();
            mImmersionBar.titleBar(getViewById(R.id.statusView))
                    .init();
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_asset);
        mSmartRefreshLayout = getViewById(R.id.refresh_asset);
        mSmartRefreshLayout.setOnRefreshListener(this);
        mImmersionBar.titleBar(getViewById(R.id.statusView))
                .statusBarColor("#3574FA")
                .init();

    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.walletLayout);
        setOnClickListener(R.id.modify_wallet);
        walletAddressTx.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                StringUtils.copy(getActivity(), walletAddressTx.getText().toString());
                Toast.makeText(getActivity(), "已复制地址", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        walletLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),ShowAddressActivity.class);
                i.putExtra("msg", mWallet.getAddress());
                getActivity().startActivity(i);
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

        mWallet = WalletDaoTools.getWalletTop();
        if (mWallet == null) {
            return;
        }
        walletNameTx.setText(mWallet.getName());
        walletAddressTx.setText(mWallet.getAddress());
        if (!StringUtils.isEmpty(mWallet.getBalance())) {
            walletBalanceTx.setText(mWallet.getBalance());// 钱包金额
        } else {
            walletBalanceTx.setText("0.0000");// 钱包金额
        }

        ApiWalletManager.getInstance().getBlance(mWallet, walletHandler);
        mBitmap = generateQRCode(ApiWalletManager.getInstance().getMainWallet().getAddress());
        codeIv.setImageBitmap(mBitmap);
        mSmartRefreshLayout.finishRefresh();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        reloadInfo();
    }


}
