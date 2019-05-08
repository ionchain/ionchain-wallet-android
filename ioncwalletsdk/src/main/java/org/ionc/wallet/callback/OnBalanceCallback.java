package org.ionc.wallet.callback;

import java.math.BigDecimal;

public interface OnBalanceCallback {
    /**
     * 查询余额成功
     *  @param ballance 余额
     * @param balanceBigDecimal
     * @param nodeUrlTag
     */
    void onBalanceSuccess(String ballance, BigDecimal balanceBigDecimal, String nodeUrlTag);
    /**
     * 查询余额失败
     *
     * @param error 失败信息
     */
    void onBalanceFailure(String error );

}
