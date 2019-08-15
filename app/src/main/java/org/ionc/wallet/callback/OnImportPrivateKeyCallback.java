package org.ionc.wallet.callback;
/**
 * user: binny
 * date:2018/12/7
 * description：导出私钥
 */
public interface OnImportPrivateKeyCallback {
    /**
     * @param privateKey
     */
    void onImportPriKeySuccess(String privateKey);

    /**
     * @param error
     */
    void onImportPriKeyFailure(String error);
}
