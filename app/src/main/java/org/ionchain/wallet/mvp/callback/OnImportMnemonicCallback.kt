package org.ionchain.wallet.mvp.callback

import org.ionchain.wallet.bean.WalletBean

/**
 * USER: binny
 * DATE: 2018/9/17
 * 描述: 通过助记词创建钱包或导入钱包
 */
interface OnImportMnemonicCallback {

    fun onImportMnemonicSuccess(walletBean: WalletBean)


    fun onImportMnemonicFailure(error: String)
}
