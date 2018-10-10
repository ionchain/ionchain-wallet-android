package org.ionchain.wallet.mvp.callback;

import org.ionchain.wallet.bean.WalletBean;

public interface OnUpdatePasswordCallback {

    void onUpdatePasswordSuccess(WalletBean wallet);

    void onUpdatePasswordFailure(String error);
}
