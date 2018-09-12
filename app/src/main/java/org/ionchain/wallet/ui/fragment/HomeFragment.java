package org.ionchain.wallet.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ionchain.wallet.R;
import org.ionchain.wallet.callback.OnBalanceRefreshCallback;
import org.ionchain.wallet.comm.api.ApiWalletManager;
import org.ionchain.wallet.comm.api.model.Wallet;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.utils.StringUtils;
import org.ionchain.wallet.db.WalletDaoTools;
import org.ionchain.wallet.ui.base.AbsBaseFragment;
import org.ionchain.wallet.ui.main.ShowAddressActivity;
import org.ionchain.wallet.ui.wallet.CreateWalletSelectActivity;
import org.ionchain.wallet.ui.wallet.ModifyWalletActivity;

import static org.ionchain.wallet.comm.utils.QRCodeUtils.generateQRCode;

public class HomeFragment extends AbsBaseFragment implements SwipeRefreshLayout.OnRefreshListener,OnBalanceRefreshCallback {


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Bitmap mBitmap;//二维码
    private Wallet mWallet;
    private TextView statusView;
    private TextView walletNameTx;
    private ImageView createWallet;
    private LinearLayout modifyWallet;
    private TextView walletBalanceTx;
    private RelativeLayout walletLayout;
    private ImageView codeIv;
    private TextView walletAddressTx;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-12 14:36:28 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews(View rootView) {
        mSwipeRefreshLayout = rootView.findViewById(R.id.refresh_asset);
        statusView = (TextView) rootView.findViewById(R.id.statusView);
        walletNameTx = (TextView) rootView.findViewById(R.id.walletNameTx);
        createWallet = (ImageView) rootView.findViewById(R.id.create_wallet);
        modifyWallet = (LinearLayout) rootView.findViewById(R.id.modify_wallet);
        walletBalanceTx = (TextView) rootView.findViewById(R.id.walletBalanceTx);
        walletLayout = (RelativeLayout) rootView.findViewById(R.id.walletLayout);
        codeIv = (ImageView) rootView.findViewById(R.id.codeIv);
        walletAddressTx = (TextView) rootView.findViewById(R.id.walletAddressTx);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mImmersionBar.titleBar(statusView)
                    .statusBarColor(R.color.blue_top)
                    .execute();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (walletNameTx != null) {
            String name = WalletDaoTools.getWalletTop().getName();
            walletNameTx.setText(name);
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_asset;
    }

    @Override
    protected void initView(View view) {
        findViews(view);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        /*
         * 长按复制
         * */
        walletAddressTx.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                StringUtils.copy(getActivity(), walletAddressTx.getText().toString());
                Toast.makeText(getActivity(), "已复制地址", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        /*
         * 显示地址信息
         * */
        walletLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowAddressActivity.class);
                intent.putExtra("msg", mWallet.getAddress());
                skip(intent);
            }
        });

        /*
         * 创建钱包
         * */
        createWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(CreateWalletSelectActivity.class);
            }
        });
        /*
         * 修改钱包
         * */
        modifyWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(ModifyWalletActivity.class, Comm.SERIALIZABLE_DATA, mWallet);
            }
        });
    }

    @Override
    protected void getData() {
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


        mBitmap = generateQRCode(ApiWalletManager.getInstance().getMainWallet().getAddress());
        codeIv.setImageBitmap(mBitmap);
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    protected void setImmersionBar() {
        mImmersionBar.titleBar(statusView)
                .statusBarColor("#3574FA")
                .execute();
    }


    @Override
    public void onRefresh() {
        ApiWalletManager.getInstance().getBalance(mWallet, this);
    }

    @Override
    public void onSuccess(Wallet balance) {
        walletBalanceTx.setText(mWallet.getBalance());
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onFailure(String s) {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
