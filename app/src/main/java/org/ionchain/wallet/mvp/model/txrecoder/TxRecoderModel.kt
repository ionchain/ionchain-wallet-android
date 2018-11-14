package org.ionchain.wallet.mvp.model.txrecoder

import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.orhanobut.logger.Logger
import org.ionchain.wallet.bean.DeviceListBean
import org.ionchain.wallet.bean.TxRecoderBean
import org.ionchain.wallet.constant.ConstantUrl.TX_RECODER_URL_GET
import org.ionchain.wallet.mvp.callback.OnTxRecoderCallback
import org.ionchain.wallet.utils.NetUtils

/**
 * AUTHOR binny
 *
 *
 * TIME 2018/11/13 16:15
 */
class TxRecoderModel : ITxRecoderModel {
    val params: HttpParams = HttpParams()
    override fun getTxRecoder(type: String, key: String, pageNumber: String, pageSize: String, callback: OnTxRecoderCallback) {
        params.clear()
        params.put("type", type)
        params.put("key", key)
        params.put("pageNumber", pageNumber)
        params.put("pageSize", pageSize)
        NetUtils.get(TX_RECODER_URL_GET, params, object : StringCallback() {
            override fun onSuccess(response: Response<String>?) {
                val json = response!!.body()
                Logger.i(json)
                val txRecoderBean = NetUtils.gsonToBean(json, TxRecoderBean::class.java)
                if (txRecoderBean == null || txRecoderBean.data == null||txRecoderBean.data.data==null) {
                    callback.onTxRecoderFailure("解析数据失败！")
                    return
                }
                val beans = txRecoderBean.data.data
                callback.onTxRecoderSuccess(beans as ArrayList<TxRecoderBean.DataBean.ItemBean>)
            }

            override fun onFinish() {
                super.onFinish()
                callback.onLoadFinish()
            }

            override fun downloadProgress(progress: Progress?) {
                super.downloadProgress(progress)
            }

            override fun uploadProgress(progress: Progress?) {
                super.uploadProgress(progress)
            }

            override fun onError(response: Response<String>?) {
                super.onError(response)
                if (response != null) {
                    callback.onTxRecoderFailure(response.body().toString())
                }
            }

            override fun onCacheSuccess(response: Response<String>?) {
                super.onCacheSuccess(response)
            }

            override fun onStart(request: Request<String, out Request<Any, Request<*, *>>>?) {
                super.onStart(request)
                callback.onLoadStart()
            }
        }, "recoder_cancel");

    }
}
