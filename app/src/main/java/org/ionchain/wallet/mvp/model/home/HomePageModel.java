package org.ionchain.wallet.mvp.model.home;

import android.util.Log;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.ionchain.wallet.bean.DeviceBean;
import org.ionchain.wallet.bean.DeviceListBean;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.mvp.callback.OnBindDeviceCallback;
import org.ionchain.wallet.mvp.callback.OnDeviceDetailCallback;
import org.ionchain.wallet.mvp.callback.OnDeviceListCallback;
import org.ionchain.wallet.mvp.callback.OnUnbindDeviceCallback;
import org.ionchain.wallet.utils.NetUtils;
import org.ionchain.wallet.utils.ToastUtil;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ionchain.wallet.constant.ConstantUrl.DEVICES_BIND_POST;
import static org.ionchain.wallet.constant.ConstantUrl.DEVICES_GET;
import static org.ionchain.wallet.constant.ConstantUrl.DEVICES_UNBIND_POST;

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述: 首页数据的业务类
 */
public class HomePageModel implements IHomePageModel {
    private String TAG = "HomePageModel";
    private HttpParams params;

    public HomePageModel() {
        params = new HttpParams();
    }

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
                Log.i(TAG, "onCreateSuccess: " + json);
                DeviceListBean bean = NetUtils.gsonToBean(json, DeviceListBean.class);
                if (bean == null || bean.getData() == null) {
                    return;
                }
                callback.onDeviceListSuccess(bean.getData());

            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onDeviceListFailure(response.body());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                callback.onLoadFinish();
            }
        }, callback);

    }

    @Override
    public void getAllWalletDeviceList(List<WalletBean> walletBeans, OnDeviceListCallback listData) {

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
                    ToastUtil.showToastLonger("绑定失败");
                    return;
                }

                String json = response.body();
                DeviceBean bindBean = NetUtils.gsonToBean(json, DeviceBean.class);
                if (bindBean == null || bindBean.getData() == null) {
                    callback.onBindFailure("绑定失败");
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
                callback.onBindFailure(response.body());
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
        Map<String, String> params = new HashMap<>();
        params.put("eth_address", address);
        params.put("cksn", cksn);
        JSONObject jsonObject = new JSONObject(params);
        NetUtils.post(DEVICES_UNBIND_POST, jsonObject, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                if (response == null) {
                    ToastUtil.showToastLonger("绑定失败");
                    return;
                }

                String json = response.body();
                DeviceBean bindBean = NetUtils.gsonToBean(json, DeviceBean.class);
                if (bindBean == null || bindBean.getData() == null) {
                    callback.onUnbindFailure("解绑定失败");
                    return;
                }
                if (bindBean.getSuccess() != 0) {
                    ToastUtil.showToastLonger(bindBean.getMessage());
                } else {
                    callback.onUnbindSuccess(bindBean.getData());
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
                callback.onUnbindFailure(response.body());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                callback.onLoadFinish();
            }
        }, callback);
    }

    @Override
    public void getDeviceDetail(String cksn, OnDeviceDetailCallback deviceBean) {

    }
}
