package org.ionc.wallet.callback;

import org.ionc.wallet.bean.WalletBean;

/**
 * user: binny
 * date:2018/12/7
 * description：更新钱包
 */
public interface OnUpdatePasswordCallback {
    void onUpdatePasswordSuccess(WalletBean wallet);

    void onUpdatePasswordFailure(String error);
}
