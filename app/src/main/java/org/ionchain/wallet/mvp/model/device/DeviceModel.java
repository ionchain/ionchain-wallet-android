package org.ionchain.wallet.mvp.model.device;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.ionc.wallet.bean.WalletBean;
import org.ionc.wallet.utils.Logger;
import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.DeviceBean;
import org.ionchain.wallet.bean.DeviceListBean;
import org.ionchain.wallet.mvp.callback.OnBindDeviceCallback;
import org.ionchain.wallet.mvp.callback.OnDeviceDetailCallback;
import org.ionchain.wallet.mvp.callback.OnDeviceListCallback;
import org.ionchain.wallet.mvp.callback.OnUnbindDeviceCallback;
import org.ionchain.wallet.utils.NetUtils;
import org.ionchain.wallet.utils.ToastUtil;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import static org.ionchain.wallet.constant.ConstantErrorCode.ERROR_CODE_NET_WORK_ERROR_DEVICE_BIND;
import static org.ionchain.wallet.constant.ConstantErrorCode.ERROR_CODE_NET_WORK_ERROR_DEVICE_BIND_NULL;
import static org.ionchain.wallet.constant.ConstantErrorCode.ERROR_CODE_NET_WORK_ERROR_DEVICE_UNBIND;
import static org.ionchain.wallet.constant.ConstantErrorCode.ERROR_CODE_NET_WORK_ERROR_DEVICE_UNBIND_NULL;
import static org.ionchain.wallet.constant.ConstantUrl.DEVICES_BIND_POST;
import static org.ionchain.wallet.constant.ConstantUrl.DEVICES_GET;
import static org.ionchain.wallet.constant.ConstantUrl.DEVICES_UNBIND_POST;

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述: 首页数据的业务类
 */
public class DeviceModel implements IDeviceModel {
    private String TAG = "DeviceModel";
    private HttpParams params = new HttpParams();


    @Override
    public void getCurrentWalletDevicesList(WalletBean walletBean, final OnDeviceListCallback callback) {
        params.clear();
        params.put("eth_address", walletBean.getAddress());
        NetUtils.get(DEVICES_GET, params, new StringCallback() {
            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                callback.onLoadStart();
            }

            @Override
            public void onSuccess(Response<String> response) {
                String json = response.body();
                Logger.j(TAG, "onCreateSuccess: $json");
                DeviceListBean bean = NetUtils.gsonToBean(json, DeviceListBean.class);
                if (Objects.requireNonNull(bean).getData() == null) {
                    callback.onLoadFinish();
                    return;
                }
                callback.onDeviceListSuccess(Objects.requireNonNull(bean.getData()));
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                if (response.body() == null) {
                    callback.onDeviceListFailure(App.mContext.getResources().getString(R.string.device_service_error));
                } else {
                    callback.onDeviceListFailure(response.body());
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                callback.onLoadFinish();
            }
        }, callback);

    }

    /**
     * @param addressSet 所有设备的地址集合
     * @param callback   钱包数据集
     */
    @Override
    public void getAllWalletDeviceList(String addressSet, final OnDeviceListCallback callback) {
        params.clear();
        params.put("eth_address", addressSet);
        NetUtils.get(DEVICES_GET, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String json = response.body();
                DeviceListBean bean = NetUtils.gsonToBean(json, DeviceListBean.class);
                if (bean == null || bean.getData() == null) {
                    return;
                }
                callback.onDeviceListSuccess(bean.getData());
            }

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                callback.onLoadStart();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onDeviceListFailure(App.mContext.getResources().getString(R.string.device_all_service_error));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                callback.onLoadFinish();;
            }
        },callback);

    }

    @Override
    public void bindDeviceToWallet(String address, String cksn, final OnBindDeviceCallback callback) {
        params.clear();
        params.put("eth_address", address);
        params.put("cksn", cksn);
        NetUtils.post(DEVICES_BIND_POST, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                if (response == null) {
                    ToastUtil.showToastLonger(ERROR_CODE_NET_WORK_ERROR_DEVICE_BIND_NULL);
                    return;
                }

                String json = response.body();
                DeviceBean bindBean = NetUtils.gsonToBean(json, DeviceBean.class);
                if (bindBean == null || bindBean.getData() == null) {
                    callback.onBindFailure(ERROR_CODE_NET_WORK_ERROR_DEVICE_BIND_NULL);
                    return;
                }
                if (bindBean.getSuccess() != 0) {
                    ToastUtil.showToastLonger(bindBean.getMessage());
                } else {
                    callback.onBindSuccess(bindBean.getData());
                }
            }

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                callback.onLoadStart();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                Logger.i(String.valueOf(response.getException().getMessage().isEmpty()));
                callback.onBindFailure(ERROR_CODE_NET_WORK_ERROR_DEVICE_BIND);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                callback.onLoadFinish();
            }
        }, callback);

    }

    @Override
    public void unbindDeviceToWallet(String address, String cksn, final OnUnbindDeviceCallback callback) {
        HashMap params = new HashMap();
        params.put("eth_address", address);
        params.put("cksn", cksn);

        JSONObject jsonObject = new JSONObject(params);
        NetUtils.post(DEVICES_UNBIND_POST, jsonObject, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                if (response == null) {
                    ToastUtil.showToastLonger(ERROR_CODE_NET_WORK_ERROR_DEVICE_UNBIND_NULL);
                    return;
                }

                String json = response.body();
                DeviceBean bindBean = NetUtils.gsonToBean(json, DeviceBean.class);
                if (Objects.requireNonNull(bindBean).getData() == null) {
                    callback.onUnbindFailure(ERROR_CODE_NET_WORK_ERROR_DEVICE_UNBIND_NULL);
                    return;
                }
                if (bindBean.getSuccess() != 0) {
                    ToastUtil.showToastLonger(bindBean.getMessage());
                } else {
                    callback.onUnbindSuccess(Objects.requireNonNull(bindBean.getData()));
                }
            }

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                callback.onLoadStart();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onUnbindFailure(ERROR_CODE_NET_WORK_ERROR_DEVICE_UNBIND);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                callback.onLoadFinish();
            }
        }, callback);
    }

    @Override
    public void getDeviceDetail(String cksn, OnDeviceDetailCallback callback) {

    }
}
