package org.ionc.wallet.presenter.update;

import org.ionc.wallet.model.update.IUpdateModel;
import org.ionc.wallet.model.update.OnCheckUpdateInfoCallback;
import org.ionc.wallet.model.update.UpdateModelModel;

public class UpdatePresenter implements IUpdateModel {

    @Override
    public void update(OnCheckUpdateInfoCallback callback) {
        new UpdateModelModel().update(callback);
    }
}
