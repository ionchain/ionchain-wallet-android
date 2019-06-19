package org.ionchain.wallet.mvp.presenter.update;

import org.ionchain.wallet.mvp.model.update.IUpdateModel;
import org.ionchain.wallet.mvp.model.update.OnCheckUpdateInfoCallback;
import org.ionchain.wallet.mvp.model.update.UpdateModelModel;

public class UpdatePresenter implements IUpdateModel {

    @Override
    public void update(OnCheckUpdateInfoCallback callback) {
        new UpdateModelModel().update(callback);
    }
}
