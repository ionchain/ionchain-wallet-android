package com.ionc.wallet.sdk.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ionc.wallet.sdk.IONCWalletSDK;
import com.ionc.wallet.sdk.R;
import com.ionc.wallet.sdk.adapter.CommonAdapter;
import com.ionc.wallet.sdk.adapterhelper.AllWalletViewHepler;
import com.ionc.wallet.sdk.bean.WalletBean;
import com.ionc.wallet.sdk.callback.OnTransationCallback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * user: binny
 * date:2018/12/24
 * description：${END}
 */
public class IONCAllWalletDialogSDK extends BaseDialog implements AllWalletViewHepler.OnAllWalletItemClickedListener, OnTransationCallback {

    private ListView all_wallet_lv;

    private CommonAdapter mAdapter;

    private OnTxResultCallback mOnTxResultCallback;
    private List<WalletBean> mWalletBeanList = new ArrayList<>();

    private Context mContext;
    private Button btn_cancel;
    private Button btn_sure;
    private String address = "";
    private String keyStore = "";
    private String sum = "0.0000";

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

    DialogPasswordCheckSDK dialogPasswordCheckSDK;

    @Override
    protected void initView() {
        all_wallet_lv = findViewById(R.id.all_wallet_lv);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_sure = findViewById(R.id.btn_sure);
        mAdapter = new CommonAdapter(mContext, mWalletBeanList, R.layout.item_all_wallet_layout, new AllWalletViewHepler(this));
        all_wallet_lv.setAdapter(mAdapter);

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断金额是否大于0
                if ("0.0000".equals(sum)) {
                    Toast.makeText(mContext, "该钱包余额为0", Toast.LENGTH_SHORT).show();
                    return;
                }
                //支付
                dialogPasswordCheckSDK = new DialogPasswordCheckSDK(mContext);
                dialogPasswordCheckSDK.setAddress(address).setBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消
                        dialogPasswordCheckSDK.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //调用支付接口
                        String pwd = dialogPasswordCheckSDK.getPasswordEt().getText().toString();
                        IONCWalletSDK.getInstance().transaction(address, "0x3d5e2c4232ff01388c288fd392cd955cbd177e2d", BigDecimal.valueOf(0.0003),
                                pwd,
                                keyStore,
                                dialogPasswordCheckSDK.getAccountMoney(), IONCAllWalletDialogSDK.this);
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
    public void onResult(String address, String keyStore, String sum) {
        this.address = address;
        this.keyStore = keyStore;
        this.sum = sum;
    }

    @Override
    public void OnTxSuccess(String hashTx) {
        dialogPasswordCheckSDK.dismiss();
        this.dismiss();
        mOnTxResultCallback.OnTxSuccess(hashTx);
    }

    @Override
    public void onTxFailure(String error) {
        mOnTxResultCallback.onTxFailure(error);
    }

    public interface OnTxResultCallback {
        void OnTxSuccess(String hashTx);

        void onTxFailure(String error);
    }
}
