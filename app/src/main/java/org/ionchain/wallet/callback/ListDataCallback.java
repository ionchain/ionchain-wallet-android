package org.ionchain.wallet.callback;

import org.ionchain.wallet.callback.base.OnFailureCallback;
import org.ionchain.wallet.callback.base.OnSuccessCallback;

/**
 * USER: binny
 * DATE: 2018/9/14
 * 描述:
 */
public interface ListDataCallback<T> extends OnSuccessCallback<T>, OnFailureCallback, ILoadingView {
}
