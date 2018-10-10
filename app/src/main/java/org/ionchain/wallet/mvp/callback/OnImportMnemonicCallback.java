package org.ionchain.wallet.mvp.callback;

import org.ionchain.wallet.bean.WalletBean;

/**
 * USER: binny
 * DATE: 2018/9/17
 * 描述: 通过助记词创建钱包或导入钱包
 */
public interface OnImportMnemonicCallback {

    void onImportMnemonicSuccess(WalletBean walletBean);


    void onImportMnemonicFailure(String error);
}
