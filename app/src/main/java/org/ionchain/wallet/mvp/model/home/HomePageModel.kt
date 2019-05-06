package org.ionchain.wallet.mvp.model.home

import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import org.ionc.wallet.bean.WalletBean
import org.ionc.wallet.utils.Logger
import org.ionchain.wallet.App
import org.ionchain.wallet.R
import org.ionchain.wallet.bean.DeviceBean
import org.ionchain.wallet.bean.DeviceListBean
import org.ionchain.wallet.constant.ConstantErrorCode.*
import org.ionchain.wallet.constant.ConstantUrl.*
import org.ionchain.wallet.mvp.callback.OnBindDeviceCallback
import org.ionchain.wallet.mvp.callback.OnDeviceDetailCallback
import org.ionchain.wallet.mvp.callback.OnDeviceListCallback
import org.ionchain.wallet.mvp.callback.OnUnbindDeviceCallback
import org.ionchain.wallet.utils.NetUtils
import org.ionchain.wallet.utils.ToastUtil
import org.json.JSONObject
import java.util.*

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述: 首页数据的业务类
 */
class HomePageModel : IHomePageModel {
    private val TAG = "HomePageModel"
    private val params: HttpParams = HttpParams()

    override fun getCurrentWalletDevicesList(walletBean: WalletBean, callback: OnDeviceListCallback) {
        params.clear()
        params.put("eth_address", walletBean.address)
        NetUtils.get(DEVICES_GET, params, object : StringCallback() {

            override fun onStart(request: Request<String, out Request<*, *>>?) {
                super.onStart(request)
                callback.onLoadStart()
            }

            override fun onSuccess(response: Response<String>) {
                val json = response.body()
                Logger.j(TAG, "onCreateSuccess: $json")
                val bean = NetUtils.gsonToBean(json, DeviceListBean::class.java)
                if (bean?.data == null) {
                    callback.onLoadFinish()
                    return
                }
                callback.onDeviceListSuccess(bean.data!!)

            }

            override fun onError(response: Response<String>) {
                super.onError(response)
                if (response.body() == null) {
                    callback.onDeviceListFailure(App.mContext.resources.getString(R.string.device_service_error))
                } else {
                    callback.onDeviceListFailure(response.body())
                }
            }

            override fun onFinish() {
                super.onFinish()
                callback.onLoadFinish()
            }
        }, callback)

    }

    override fun getAllWalletDeviceList(walletBeans: List<WalletBean>, listData: OnDeviceListCallback) {

    }

    override fun bindDeviceToWallet(address: String, cksn: String, callback: OnBindDeviceCallback) {
        params.clear()
        params.put("eth_address", address)
        params.put("cksn", cksn)
        NetUtils.post(DEVICES_BIND_POST, params, object : StringCallback() {
            override fun onSuccess(response: Response<String>?) {
                if (response == null) {
                    ToastUtil.showToastLonger(ERROR_CODE_NET_WORK_ERROR_DEVICE_BIND_NULL)
                    return
                }

                val json = response.body()
                val bindBean = NetUtils.gsonToBean(json, DeviceBean::class.java)
                if (bindBean == null || bindBean.data == null) {
                    callback.onBindFailure(ERROR_CODE_NET_WORK_ERROR_DEVICE_BIND_NULL)
                    return
                }
                if (bindBean.success != 0) {
                    ToastUtil.showToastLonger(bindBean.message)
                } else {
                    callback.onBindSuccess(bindBean.data!!)
                }
            }

            override fun onStart(request: Request<String, out Request<*, *>>?) {
                super.onStart(request)
                callback.onLoadStart()
            }

            override fun onError(response: Response<String>) {
                super.onError(response)
                Logger.i(response.exception.message.orEmpty())
                callback.onBindFailure( ERROR_CODE_NET_WORK_ERROR_DEVICE_BIND)
            }

            override fun onFinish() {
                super.onFinish()
                callback.onLoadFinish()
            }
        }, callback)

    }

    override fun unbindDeviceToWallet(address: String, cksn: String, callback: OnUnbindDeviceCallback) {
        val params = HashMap<String, String>()
        params["eth_address"] = address
        params["cksn"] = cksn
        val jsonObject = JSONObject(params)
        NetUtils.post(DEVICES_UNBIND_POST, jsonObject, object : StringCallback() {
            override fun onSuccess(response: Response<String>?) {
                if (response == null) {
                    ToastUtil.showToastLonger(ERROR_CODE_NET_WORK_ERROR_DEVICE_UNBIND_NULL)
                    return
                }

                val json = response.body()
                val bindBean = NetUtils.gsonToBean(json, DeviceBean::class.java)
                if (bindBean?.data == null) {
                    callback.onUnbindFailure(ERROR_CODE_NET_WORK_ERROR_DEVICE_UNBIND_NULL)
                    return
                }
                if (bindBean.success != 0) {
                    ToastUtil.showToastLonger(bindBean.message)
                } else {
                    callback.onUnbindSuccess(bindBean.data!!)
                }
            }

            override fun onStart(request: Request<String, out Request<*, *>>?) {
                super.onStart(request)
                callback.onLoadStart()
            }

            override fun onError(response: Response<String>) {
                super.onError(response)
                callback.onUnbindFailure(ERROR_CODE_NET_WORK_ERROR_DEVICE_UNBIND)
            }

            override fun onFinish() {
                super.onFinish()
                callback.onLoadFinish()
            }
        }, callback)
    }

    override fun getDeviceDetail(cksn: String, deviceBean: OnDeviceDetailCallback) {

    }
}
