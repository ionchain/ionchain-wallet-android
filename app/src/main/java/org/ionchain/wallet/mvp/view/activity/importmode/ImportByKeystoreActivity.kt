package org.ionchain.wallet.mvp.view.activity.importmode

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.ionc.wallet.sdk.IONCWalletSDK
import com.ionc.wallet.sdk.bean.WalletBean
import com.ionc.wallet.sdk.callback.OnCreateWalletCallback
import com.ionc.wallet.sdk.utils.Logger
import com.ionc.wallet.sdk.utils.RandomUntil.getNum
import org.ionchain.wallet.R
import org.ionchain.wallet.constant.ConstantParams.FROM_SCAN
import org.ionchain.wallet.constant.ConstantParams.SERVER_PROTOCOL_VALUE
import org.ionchain.wallet.mvp.view.activity.MainActivity
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity
import org.ionchain.wallet.qrcode.android.CaptureActivity
import org.ionchain.wallet.utils.ToastUtil


class ImportByKeystoreActivity : AbsBaseActivity(), OnCreateWalletCallback, TextWatcher {
    private var mKeystore: AppCompatEditText? = null
    private var pwdEt: AppCompatEditText? = null
    private var importBtn: Button? = null
    private var isWelcome: Boolean = false
    private var checkbox: CheckBox? = null
    private var linkUrlTv: TextView? = null
    private var keystoreStr: String? = null
    override val layoutId: Int
        get() = R.layout.activity_import_by_keystore //To change initializer of created properties use File | Settings | File Templates.

    override fun afterTextChanged(s: Editable?) {
        if (mKeystore!!.getText() != null && pwdEt!!.getText() != null && pwdEt!!.getText() != null) {
            val content = mKeystore!!.getText()!!.toString().trim { it <= ' ' }
            val pwdstr = pwdEt!!.getText()!!.toString().trim { it <= ' ' }

            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(pwdstr) && checkbox!!.isChecked()) {
                importBtn!!.setEnabled(true)
                importBtn!!.setBackgroundColor(resources.getColor(R.color.blue_top))
            } else {
                importBtn!!.setEnabled(false)
                importBtn!!.setBackgroundColor(resources.getColor(R.color.grey))
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
       Logger.i(s.toString())
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        Logger.i(s.toString())
    }

    override fun onCreateSuccess(walletBean: WalletBean) {
        Logger.i(walletBean.toString())
        hideProgress()
        walletBean.mIconIdex = getNum(7)
        walletBean.isShowWallet = isWelcome
        IONCWalletSDK.getInstance().saveWallet(walletBean)
        ToastUtil.showToastLonger("导入成功啦!")
        skip(MainActivity::class.java)
    }

    override fun onCreateFailure(result: String) {
        hideProgress()
        ToastUtil.showToastLonger("导入成失败!\n$result")
        Logger.e(TAG, "onCreateFailure: $result")
    }


    override fun initData() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initView() {
        mImmersionBar!!.titleView(R.id.import_header)
                .statusBarDarkFont(true)
                .execute()
        val back = findViewById<View>(R.id.back) as ImageView
        back.setOnClickListener {
            finish()
        }

        val scan = findViewById<View>(R.id.scan) as ImageView
        scan.setOnClickListener {
            skip(CaptureActivity::class.java, FROM_SCAN)
        }
        mKeystore = findViewById<View>(R.id.mnemonic) as AppCompatEditText

        pwdEt = findViewById<View>(R.id.pwdEt) as AppCompatEditText
        mKeystore!!.addTextChangedListener(this)
        pwdEt!!.addTextChangedListener(this)
        checkbox = findViewById<View>(R.id.checkbox) as CheckBox
        checkbox!!.setOnClickListener{
            if (mKeystore!!.text!!.isNotEmpty()&&pwdEt!!.text!!.isNotEmpty()&&checkbox!!.isChecked) {
                importBtn!!.setEnabled(true)
                importBtn!!.setBackgroundColor(resources.getColor(R.color.blue_top))
            }else{
                importBtn!!.setEnabled(false)
                importBtn!!.setBackgroundColor(resources.getColor(R.color.grey))
            }
        }

        linkUrlTv = findViewById<View>(R.id.linkUrlTv) as TextView
        importBtn = findViewById<View>(R.id.importBtn) as Button
        importBtn!!.setOnClickListener{
            keystoreStr = mKeystore!!.text.toString()
            Logger.i(keystoreStr!!)
            //读取keystore密码
            var  pass = pwdEt!!.text.toString()
           //生成keystory文件
            showProgress("正在导入钱包请稍候")
            IONCWalletSDK.getInstance().importWalletByKeyStore(pass, keystoreStr, this)
        }
        linkUrlTv!!.setOnClickListener {
            skipWeb(SERVER_PROTOCOL_VALUE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == FROM_SCAN) {
            val result = data!!.getStringExtra("result")
            mKeystore!!.setText(result)
            keystoreStr = result
            Logger.i(result)
        }
    }
}
