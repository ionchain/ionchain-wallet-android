package org.ionchain.wallet.mvp.view.activity.transaction

import android.content.Intent
import android.view.View
import android.widget.ListView
import org.ionc.wallet.adapter.CommonAdapter
import org.ionc.wallet.bean.TxRecoderBean
import org.ionc.wallet.callback.OnTxRecoderCallback
import org.ionchain.wallet.R
import org.ionchain.wallet.adapter.txrecoder.TxRecoderViewHelper
import org.ionchain.wallet.mvp.callback.OnLoadingView
import org.ionchain.wallet.mvp.presenter.Presenter
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity
import org.ionchain.wallet.utils.ToastUtil

class TxRecoderActivity : AbsBaseActivity(), OnTxRecoderCallback,OnLoadingView {
    override fun onTxRecoderSuccess(beans: ArrayList<TxRecoderBean.DataBean.ItemBean>) {
        itemBeans.addAll(beans)
        adapterLv!!.notifyDataSetChanged()
    }

    override fun onTxRecoderFailure(error: String) {
        ToastUtil.showShortToast(error)
    }

    override fun onLoadStart() {
        showProgress("正在加载...")
    }

    override fun onLoadFinish() {
        hideProgress()
    }

    override val layoutId: Int
        get() = R.layout.activity_tx_recoder

    lateinit var tx_recoder_lv: ListView
    var adapterLv: CommonAdapter<*>? = null
    var itemBeans = ArrayList<TxRecoderBean.DataBean.ItemBean>()
    lateinit var presenter: Presenter
    lateinit var address: String

    override fun initData() {
        presenter = Presenter()
        presenter.getTxRedocer("3", address, "1", "10", this)
    }

    override fun handleIntent(intent: Intent) {
        super.handleIntent(intent)
        address = intent.getStringExtra("address")
    }

    override fun initView() {
        mImmersionBar!!.titleView(R.id.toolbarlayout).statusBarDarkFont(true).execute()
        tx_recoder_lv = findViewById(R.id.tx_recoder_lv)
        adapterLv = CommonAdapter(this, itemBeans, R.layout.item_txrecoder, TxRecoderViewHelper())
        tx_recoder_lv.adapter = adapterLv
        findViewById<View>(R.id.back).setOnClickListener {
            finish()
        }
    }

}
