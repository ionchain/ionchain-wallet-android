package org.ionchain.wallet.mvp.callback;

/**
 * USER: binny
 * DATE: 2018/9/17
 * 描述: 导出私钥
 */
public interface OnImportPrivateKeyCallback {
    /**
     * @param privateKey
     */
    void onImportPriKeySuccess(String privateKey);

    /**
     * @param erroe
     */
    void onImportPriKeyFailure(String erroe);
}
