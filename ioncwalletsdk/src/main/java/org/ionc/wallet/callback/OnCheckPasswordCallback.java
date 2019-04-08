package org.ionc.wallet.callback;

import org.ionc.wallet.bean.WalletBean;

/**
 * describe:
 * 密码验证回调
 * @author 596928539@qq.com
 * @date 2019/03/30
 */
public interface OnCheckPasswordCallback {
    /**
     * 密码正确
     * @param bean
     */
    void onPasswordRight(WalletBean bean);

    /**
     * 密码错误
     * @param errorMsg
     */
    void onPasswordWrong(String errorMsg);
}
