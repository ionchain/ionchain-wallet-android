package org.ionchain.wallet.mvp.callback;

import org.ionchain.wallet.bean.DeviceBean;

/**
 * USER: binny
 * DATE: 2018/9/14
 * 描述: 绑定设备回调接口
 */
public interface OnBindDeviceCallback extends OnLoadingView {
    /**
     * 绑定失败
     */
    void onBindSuccess(DeviceBean.DataBean dataBean);

    /**
     * 绑定失败
     */
    void onBindFailure(String result);
}
