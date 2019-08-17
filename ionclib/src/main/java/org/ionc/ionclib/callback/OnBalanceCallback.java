package org.ionc.ionclib.callback;

import java.math.BigDecimal;

public interface OnBalanceCallback {
    /**
     * 查询余额成功
     * @param balanceBigDecimal
     * @param nodeUrlTag
     */
    void onBalanceSuccess(BigDecimal balanceBigDecimal, String nodeUrlTag);
    /**
     * 查询余额失败
     *
     * @param error 失败信息
     */
    void onBalanceFailure(String error);

}
