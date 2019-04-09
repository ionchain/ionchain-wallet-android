package org.ionc.wallet.sdk.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ionc.wallet.sdk.R;

import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.adapterhelper.AllWalletViewHepler;
import org.ionc.wallet.bean.WalletBean;
import org.ionc.wallet.callback.OnTransationCallback;

import java.util.List;

/**
 * user: binny
 * date:2018/12/24
 * description：${END}
 */
public class IONCAllWalletDialogSDK extends BaseDialog implements AllWalletViewHepler.OnAllWalletItemClickedListener, OnTransationCallback {

    private ListView all_wallet_lv;

    private CommonAdapter mAdapter;
    private ProgressDialog dialog;
    private OnTxResultCallback mOnTxResultCallback;
    private List<WalletBean> mWalletBeanList;

    private Context mContext;
    private Button btn_cancel;
    private Button btn_sure;
    private String sum = "0.0000";
    private WalletBean walletBean;
    /**
     * 显示进度提示窗
     *
     * @param msg 显示信息
     */
    protected void showProgress(String msg) {
        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setMessage(msg);
        dialog.show();

    }
    /**
     * 隐藏进度弹窗
     */
    protected void hideProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    public IONCAllWalletDialogSDK(@NonNull Context context, List<WalletBean> walletBeans, OnTxResultCallback callback) {
        super(context);
        mContext = context;
        mWalletBeanList = walletBeans;
        this.mOnTxResultCallback = callback;
    }

    @Override
    protected void initDialog() {
//        Window window = this.getWindow();
//        if (window != null) {
//            window.setGravity(Gravity.CENTER);
//            DisplayMetrics dm = mActivity.getResources().getDisplayMetrics();
//            WindowManager.LayoutParams params = window.getAttributes();
//            params.width = dm.widthPixels;
//            params.height = dm.widthPixels;
//            window.setAttributes(params);
//        }
    }

    TransactionDialogSDK dialogPasswordCheckSDK;

    @Override
    protected void initView() {
        all_wallet_lv = findViewById(R.id.all_wallet_lv);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_sure = findViewById(R.id.btn_sure);
        for (WalletBean w :
                mWalletBeanList) {
            w.setChoosen(false);
        }
        mAdapter = new CommonAdapter(mContext, mWalletBeanList, R.layout.item_all_wallet_layout, new AllWalletViewHepler(this));
        all_wallet_lv.setAdapter(mAdapter);

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断金额是否大于0
                if ("0.0000".equals(walletBean.getBalance()) || walletBean.getBalance() == null) {
                    Toast.makeText(mContext, "该钱包余额为0", Toast.LENGTH_SHORT).show();
                    return;
                }
                //支付
                dialogPasswordCheckSDK = new TransactionDialogSDK(mContext);
                dialogPasswordCheckSDK.setName(walletBean.getName()).setBtnClickedListener(new TransactionDialogSDK.OnTransactionBtnClickedListener() {
                    @Override
                    public void onSure(String toAddress) {
                        //调用支付接口
//                        String pwd = dialogPasswordCheckSDK.getPasswordEt().getText().toString();
//                        IONCWalletSDK.getInstance().transaction(walletBean.getAddress(), toAddress, BigDecimal.valueOf(0.0003),
//                                pwd,
//                                walletBean.getKeystore(),
//                                dialogPasswordCheckSDK.getAccountMoney(), IONCAllWalletDialogSDK.this);
                        showProgress("请稍后……");
                    }

                    @Override
                    public void onCancel() {
//                        dialogPasswordCheckSDK.dismiss();
                    }
                });
                dialogPasswordCheckSDK.show();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_all_wallet_layout;
    }

    @Override
    public void onItemClick(WalletBean walletBean) {
        this.walletBean = walletBean;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnTxSuccess(String hashTx) {
        dialogPasswordCheckSDK.dismiss();
        this.dismiss();
        hideProgress();
        mOnTxResultCallback.OnTxSuccess(hashTx);
    }

    @Override
    public void onTxFailure(String error) {
        hideProgress();

        mOnTxResultCallback.onTxFailure(error);
    }

    public interface OnTxResultCallback {
        void OnTxSuccess(String hashTx);

        void onTxFailure(String error);
    }
}
