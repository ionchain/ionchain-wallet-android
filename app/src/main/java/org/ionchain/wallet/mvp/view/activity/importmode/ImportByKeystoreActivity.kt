package org.ionchain.wallet.mvp.view.activity.importmode

import android.support.v7.widget.AppCompatEditText
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import org.ionchain.wallet.R
import org.ionchain.wallet.mvp.view.activity.ScanActivity
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity

class ImportByKeystoreActivity : AbsBaseActivity() {

    private var mKeystore: AppCompatEditText? = null
    private var pwdEt: AppCompatEditText? = null
    private var importBtn: Button? = null
    private var isWelcome: Boolean = false
    private var checkbox: CheckBox? = null
    private var linkUrlTv: TextView? = null
    override val layoutId: Int
        get() = R.layout.activity_import_by_keystore //To change initializer of created properties use File | Settings | File Templates.

    override fun initData() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initView() {
        mImmersionBar!!.titleBar(R.id.import_header)
                .statusBarDarkFont(true)
                .execute()
        val back = findViewById<View>(R.id.back) as ImageView
        back.setOnClickListener {
            finish()
        }

        val scan = findViewById<View>(R.id.scan) as ImageView
        scan.setOnClickListener {
            skip(ScanActivity::class.java, 999)
        }
        mKeystore = findViewById<View>(R.id.mnemonic) as AppCompatEditText
        pwdEt = findViewById<View>(R.id.pwdEt) as AppCompatEditText
        checkbox = findViewById<View>(R.id.checkbox) as CheckBox
        linkUrlTv = findViewById<View>(R.id.linkUrlTv) as TextView
        importBtn = findViewById<View>(R.id.importBtn) as Button
    }

}
