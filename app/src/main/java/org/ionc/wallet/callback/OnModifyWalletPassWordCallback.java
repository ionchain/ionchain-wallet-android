package org.ionc.wallet.callback;

import org.ionc.wallet.bean.WalletBeanNew;

/**
 * user: binny
 * date:2018/12/7
 * description：${END}
 */
public interface OnModifyWalletPassWordCallback {
    /**修改成功
     *
     */
    void onModifySuccess(WalletBeanNew walletBean);

    void onModifyFailure(String error);
}
