package org.ionchain.wallet.mvp.model.update;

import org.ionchain.wallet.bean.UpdateBean;

/**
 * 更新接口的回调信息
 */
public interface OnCheckUpdateInfoCallback {
    /**
     *  网络请求开始,用于展示进度条
     */
    void onCheckForUpdateStart();

    /**
     * 网络请求成功,未解析数据时的回调,用于结束进度条
     */
    void onCheckForUpdateSuccess();

    /**
     * 网络请求失败,用于结束进度条
     * @param error
     */
    void onCheckForUpdateError(String error);

    /**
     * 需要更新
     * @param updateBean
     * @param must_update
     */
    void onCheckForUpdateNeedUpdate(UpdateBean updateBean, String must_update);

    /**
     * 不需要更新
     */
    void onCheckForUpdateNoNewVersion();

}
