package org.ionchain.wallet.mvp.model.update;

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
     */
    void onCheckForUpdateError();

    /**
     * 需要更新
     * @param url
     * @param update_info
     * @param v_code
     */
    void onCheckForUpdateNeedUpdate(String url, String update_info, String v_code);

    /**
     * 不需要更新
     */
    void onCheckForUpdateNoNewVersion();

}
