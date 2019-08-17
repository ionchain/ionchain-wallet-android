package org.ionc.ionclib.callback;


import org.ionc.ionclib.bean.WalletBeanNew;

/**
 * describe:
 * 密码验证回调,助记词验证回调
 * @author 596928539@qq.com
 * @date 2019/03/30
 */
public interface OnCheckWalletPasswordCallback {
    /**
     * 密码正确
     * @param bean
     */
    void onCheckWalletPasswordSuccess(WalletBeanNew bean);

    /**
     * 密码错误
     * @param errorMsg
     */
    void onCheckWalletPasswordFailure(String errorMsg);
}
