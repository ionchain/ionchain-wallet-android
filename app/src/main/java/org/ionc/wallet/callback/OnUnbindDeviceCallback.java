package org.ionc.wallet.callback;


import org.ionc.wallet.bean.DeviceBean;

/**
 * USER: binny
 * DATE: 2018/9/14
 * 描述: 绑定设备回调接口
 */
public interface OnUnbindDeviceCallback extends OnLoadingView {
    /**
     * 绑定失败
     */
    void onUnbindSuccess(DeviceBean.DataBean dataBean);

    /**
     * 绑定失败
     */
    void onUnbindFailure(String result);
}
