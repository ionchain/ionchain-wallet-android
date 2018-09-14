package org.ionchain.wallet.callback.base;

/**
 * author  binny
 * date 5/9
 * 请求数据的回到接口
 *
 * @param <T> 请求成功的实体类
 */
public interface OnSuccessCallback<T> {
    void onSuccess(T t);
}
