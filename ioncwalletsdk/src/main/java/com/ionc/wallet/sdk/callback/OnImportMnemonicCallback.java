package com.ionc.wallet.sdk.callback;

import com.ionc.wallet.sdk.bean.WalletBean;

/**
*
* 导出助记词的回调接口
*/
public interface OnImportMnemonicCallback {

    /**
     * 导出成功
     *
     * @param walletBean 钱包
     */
    void onImportMnemonicSuccess(WalletBean walletBean);


    /**
     * 导出失败
     *
     * @param error 失败原因
     */
    void onImportMnemonicFailure(String error);

}
