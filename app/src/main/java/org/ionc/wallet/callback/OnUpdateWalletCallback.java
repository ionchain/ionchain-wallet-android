package org.ionc.wallet.callback;

import org.ionc.wallet.bean.WalletBeanNew;

/**
 * user: binny
 * date:2018/12/7
 * description：更新钱包
 */
public interface OnUpdateWalletCallback {
    void onUpdateWalletSuccess(WalletBeanNew wallet);

    void onUpdateWalletFailure(String error);
}
