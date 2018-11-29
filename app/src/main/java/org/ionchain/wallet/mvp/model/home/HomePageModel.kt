package org.ionchain.wallet.mvp.model.home

import android.util.Log
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.orhanobut.logger.Logger
import org.ionchain.wallet.bean.DeviceBean
import org.ionchain.wallet.bean.DeviceListBean
import org.ionchain.wallet.bean.WalletBean
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
                Logger.i(TAG, "onCreateSuccess: $json")
                val bean = NetUtils.gsonToBean(json, DeviceListBean::class.java)
                if (bean?.data == null) {
                    callback.onLoadFinish()
                    return
                }
                callback.onDeviceListSuccess(bean.data!!)

            }

            override fun onError(response: Response<String>) {
                super.onError(response)
                callback.onDeviceListFailure(response.body())
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
                    ToastUtil.showToastLonger("绑定失败")
                    return
                }

                val json = response.body()
                val bindBean = NetUtils.gsonToBean(json, DeviceBean::class.java)
                if (bindBean == null || bindBean.data == null) {
                    callback.onBindFailure("绑定失败")
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
                callback.onBindFailure(response.body())
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
                    ToastUtil.showToastLonger("绑定失败")
                    return
                }

                val json = response.body()
                val bindBean = NetUtils.gsonToBean(json, DeviceBean::class.java)
                if (bindBean == null || bindBean.data == null) {
                    callback.onUnbindFailure("解绑定失败")
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
                callback.onUnbindFailure(response.body())
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
