package org.ionchain.wallet.mvp.callback

/**
 * USER: binny
 * DATE: 2018/9/17
 * 描述: 导出私钥
 */
interface OnImportPrivateKeyCallback {
    /**
     * @param privateKey
     */
    fun onImportPriKeySuccess(privateKey: String)

    /**
     * @param erroe
     */
    fun onImportPriKeyFailure(erroe: String)
}
