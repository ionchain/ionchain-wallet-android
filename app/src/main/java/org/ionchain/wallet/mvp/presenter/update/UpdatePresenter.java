package org.ionchain.wallet.mvp.presenter.update;

import org.ionchain.wallet.mvp.model.update.OnCheckUpdateInfoCallback;
import org.ionchain.wallet.mvp.model.update.UpdateModelModel;

public class UpdatePresenter implements IUpdatePresenter {
    @Override
    public void checkForUpdate(OnCheckUpdateInfoCallback callback) {
        new UpdateModelModel().update(callback);
    }
}
