package org.ionchain.wallet.mvp.view.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import org.ionchain.wallet.R
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity

class TxRecoderActivity : AbsBaseActivity() {
    override val layoutId: Int
        get() = R.layout.activity_tx_recoder

    lateinit var tx_recoder_lv:ListView
    override fun initData() {

    }

    override fun initView() {
        mImmersionBar!!.titleBar(R.id.toolbarlayout).statusBarDarkFont(true).execute()
        tx_recoder_lv = findViewById(R.id.tx_recoder_lv)

    }


}
