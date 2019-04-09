package org.ionc.wallet.callback;

import org.ionc.wallet.bean.WalletBean;

/**
 * describe:
 * 密码验证回调,助记词验证回调
 * @author 596928539@qq.com
 * @date 2019/03/30
 */
public interface OnCheckCallback {
    /**
     * 密码正确
     * @param bean
     */
    void onCheckSuccess(WalletBean bean);

    /**
     * 密码错误
     * @param errorMsg
     */
    void onCheckFailure(String errorMsg);
}
