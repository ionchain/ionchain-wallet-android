package org.ionc.wallet.callback;

import org.ionc.wallet.bean.WalletBean;

/**
 * user: binny
 * date:2018/12/7
 * description：更新钱包
 */
public interface OnUpdateWalletCallback {
    void onUpdateWalletSuccess(WalletBean wallet);

    void onUpdateWalletFailure(String error);
}
