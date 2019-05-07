package org.ionchain.wallet.mvp.presenter.update;

import org.ionchain.wallet.mvp.model.update.OnCheckUpdateInfoCallback;

public interface IUpdatePresenter {

    /**
     * @param callback 检查APP更新 检查结果回调
     */
    void checkForUpdate(OnCheckUpdateInfoCallback callback);
}
