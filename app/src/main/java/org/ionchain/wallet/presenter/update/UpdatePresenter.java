package org.ionchain.wallet.presenter.update;

import org.ionchain.wallet.model.update.IUpdateModel;
import org.ionchain.wallet.model.update.OnCheckUpdateInfoCallback;
import org.ionchain.wallet.model.update.UpdateModelModel;

public class UpdatePresenter implements IUpdateModel {

    @Override
    public void update(OnCheckUpdateInfoCallback callback) {
        new UpdateModelModel().update(callback);
    }
}
