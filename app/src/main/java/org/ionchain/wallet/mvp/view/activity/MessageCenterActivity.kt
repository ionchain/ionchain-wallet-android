package org.ionchain.wallet.mvp.view.activity

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ListView

import org.ionchain.wallet.R
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity

import java.util.ArrayList

class MessageCenterActivity : AbsBaseActivity() {


    private var back: ImageView? = null
    private var listview: ListView? = null

    override val layoutId: Int
        get() = R.layout.activity_message_center

    /**
     * Find the Views in the layout<br></br>
     * <br></br>
     * Auto-created on 2018-09-26 17:05:28 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private fun findViews() {
        back = findViewById<View>(R.id.back) as ImageView
        listview = findViewById<View>(R.id.listview) as ListView
    }

    private fun loadData(): List<String> {
        val stringList = ArrayList<String>()
        stringList.add("消息中心数据")
        stringList.add("消息中心数据")
        stringList.add("消息中心数据")
        stringList.add("消息中心数据")
        stringList.add("消息中心数据")
        stringList.add("消息中心数据")
        stringList.add("消息中心数据")
        stringList.add("消息中心数据")
        stringList.add("消息中心数据")
        return stringList
    }

    override fun initData() {

    }


    override fun initView() {
        findViews()
        mImmersionBar!!.titleBar(R.id.header).statusBarDarkFont(true).execute()
        val layoutManager = LinearLayoutManager(this)

        back!!.setOnClickListener { finish() }

    }

}
