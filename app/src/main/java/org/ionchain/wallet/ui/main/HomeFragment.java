package org.ionchain.wallet.ui.main;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fast.lib.immersionbar.ImmersionBar;
import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.ApiWalletManager;
import org.ionchain.wallet.comm.api.constant.ApiConstant;
import org.ionchain.wallet.comm.api.model.Wallet;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.ui.comm.BaseFragment;
import org.ionchain.wallet.ui.wallet.CreateWalletSelectActivity;

import butterknife.BindView;
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

import static com.fast.lib.utils.LibSystemUtils.hideKeyBoard;

public class HomeFragment extends BaseFragment {


    @BindView(R.id.codeIv)
    ImageView codeIv;
    @BindView(R.id.walletNameTx)
    TextView walletNameTx;

    @BindView(R.id.walletBalanceTx)
    TextView walletBalanceTx;

    @BindView(R.id.walletAddressTx)
    TextView walletAddressTx;

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
        ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .transparentStatusBar()
                .statusBarView(R.id.statusView)
                .navigationBarColor(R.color.black,0.5f)
                .fitsSystemWindows(false)
                .init();
    }

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.addBtn:
                    transfer(CreateWalletSelectActivity.class);
                    break;
                case R.id.walletLayout:

                    break;
                case 0:
                    dismissProgressDialog();
                    if(obj == null)
                        return;

                    ResponseModel<String> responseModel = (ResponseModel)obj;
                    if(!verifyStatus(responseModel)){
                        ToastUtil.showShortToast(responseModel.getMsg());
                        return;
                    }


                    break;
            }

        }catch (Throwable e){
            Logger.e(e,TAG);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_home);
    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.walletLayout);

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        reloadInfo();
    }


    private void createChineseQRCode() {
        /*
        这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
        请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
         */
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return QRCodeEncoder.syncEncodeQRCode(ApiWalletManager.getInstance().getMyWallet().getAddress(), BGAQRCodeUtil.dp2px(getActivity(), 80));
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    codeIv.setImageBitmap(bitmap);
                } else {
                }
            }
        }.execute();
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

    private void reloadInfo(){

        Wallet wallet = ApiWalletManager.getInstance().getMyWallet();
        walletNameTx.setText(wallet.getName());
        walletAddressTx.setText(wallet.getAddress());
        walletBalanceTx.setText("0.0000");
        ApiWalletManager.getInstance().reLoadBlance(wallet,walletHandler);
        createChineseQRCode();
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadInfo();
    }
}
