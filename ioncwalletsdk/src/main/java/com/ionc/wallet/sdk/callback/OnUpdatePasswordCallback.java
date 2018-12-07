package com.ionc.wallet.sdk.callback;

import com.ionc.wallet.sdk.bean.WalletBean;

/**
 * user: binny
 * date:2018/12/7
 * description：更新钱包
 */
public interface OnUpdatePasswordCallback {
    void onUpdatePasswordSuccess(WalletBean wallet);

    void onUpdatePasswordFailure(String error);
}
