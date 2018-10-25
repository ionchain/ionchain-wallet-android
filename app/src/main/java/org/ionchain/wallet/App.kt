package org.ionchain.wallet

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper

import com.facebook.stetho.Stetho
import com.lzy.okgo.OkGo
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import org.ionchain.wallet.myweb3j.Web3jHelper


class App : Application() {

    internal var TAG = "App"
    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
        Stetho.initializeWithDefaults(this)
        OkGo.getInstance().init(this)
        Web3jHelper.getInstance().initWeb3j(this)
        Logger.addLogAdapter(AndroidLogAdapter())
    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    companion object {


        var mHandler = Handler(Looper.getMainLooper())

        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
        var sRandomHeader = intArrayOf(R.mipmap.random_header_more_1, R.mipmap.random_header_more_2, R.mipmap.random_header_more_3, R.mipmap.random_header_more_4, R.mipmap.random_header_more_5, R.mipmap.random_header_more_6, R.mipmap.random_header_more_7, R.mipmap.random_header_more_8)
        var sRandomHeaderMore = intArrayOf(R.mipmap.random_header_1, R.mipmap.random_header_2, R.mipmap.random_header_3, R.mipmap.random_header_4, R.mipmap.random_header_5, R.mipmap.random_header_6, R.mipmap.random_header_7, R.mipmap.random_header_8)
    }

}
