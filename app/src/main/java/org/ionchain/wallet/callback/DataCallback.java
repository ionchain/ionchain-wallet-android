package org.ionchain.wallet.callback;

import org.ionchain.wallet.callback.base.OnFailureCallback;
import org.ionchain.wallet.callback.base.OnSuccessCallback;

/**
 * author  binny
 * date 5/9
 * 请求成功和失败的接口
 * <p>
 * T 为 请求的实体类型
 */
public interface  DataCallback<T> extends OnSuccessCallback<T>, OnFailureCallback, ILoadingView {
}
