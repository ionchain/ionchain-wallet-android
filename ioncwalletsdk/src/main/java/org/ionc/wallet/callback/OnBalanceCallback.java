package org.ionc.wallet.callback;

public interface OnBalanceCallback {
    /**
     * 查询余额成功
     *
     * @param ballance 余额
     * @param nodeUrlTag
     */
    void onBalanceSuccess(String ballance, String nodeUrlTag);
    /**
     * 查询余额失败
     *
     * @param error 失败信息
     */
    void onBalanceFailure(String error );

}
