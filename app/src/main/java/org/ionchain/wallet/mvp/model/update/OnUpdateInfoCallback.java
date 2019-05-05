package org.ionchain.wallet.mvp.model.update;

/**
 * 更新接口的回调信息
 */
public interface OnUpdateInfoCallback {
    /**
     *  网络请求开始,用于展示进度条
     */
    void onStartCheckUpdate();

    /**
     * 网络请求成功,未解析数据时的回调,用于结束进度条
     */
    void onRequestSuccess();

    /**
     * 网络请求失败,用于结束进度条
     */
    void onErrorCheckUpdate();

    /**
     * 需要更新
     * @param url
     * @param update_info
     * @param v_code
     */
    void needUpdate(String url, String update_info, String v_code);

    /**
     * 不需要更新
     */
    void noNewVersion();

}
