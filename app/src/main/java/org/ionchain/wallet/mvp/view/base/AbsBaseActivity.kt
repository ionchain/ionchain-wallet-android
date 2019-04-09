package org.ionchain.wallet.mvp.view.base

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.ionchain.wallet.constant.ConstantParams.SERVER_PROTOCOL_KEY
import org.ionchain.wallet.helper.ActivityHelper
import org.ionchain.wallet.immersionbar.ImmersionBar
import org.ionchain.wallet.mvp.view.activity.WebActivity
import org.ionchain.wallet.utils.SoftKeyboardUtil
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.Serializable

/**
 * 作者: binny
 * 时间: 5/24
 * 描述:
 */
abstract class AbsBaseActivity : AppCompatActivity() {
    protected lateinit var mActivity: AbsBaseActivity
    protected var mImmersionBar: ImmersionBar? = null
    protected val TAG = this.javaClass.simpleName

    protected abstract val layoutId: Int

    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
        ActivityHelper.getHelper().addActivity(this)
        setContentView(layoutId)

        mImmersionBar = ImmersionBar.with(this)

        mActivity = this
        val intent = intent
        if (intent != null) {
            handleIntent(intent)
        }
        initView()
        initData()
        setListener()
    }

    protected abstract fun initData()


    protected open fun setListener() {

    }

    protected open fun handleIntent(intent: Intent) {

    }

    protected abstract fun initView()


    override fun onDestroy() {
        super.onDestroy()

        if (mImmersionBar != null)
            mImmersionBar!!.destroy()  //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
    }

    protected fun skip(clazz: Class<*>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }

    /*
    * 跳转到 web 页面
    * */
    protected fun skipWeb(value: String) {
        val intent = Intent(mActivity, WebActivity::class.java)
        intent.putExtra(SERVER_PROTOCOL_KEY, value)
        startActivity(intent)
    }

    protected fun skip(clazz: Class<*>, requestCode: Int) {
        val intent = Intent(this, clazz)
        startActivityForResult(intent, requestCode)
    }


    fun skip(clazz: Class<*>, params: String, obj: Serializable) {
        val intent = Intent(this, clazz)
        intent.putExtra(params, obj)
        startActivity(intent)
    }

    fun skip(clazz: Class<*>, params: String, obj: Serializable, requestCode: Int) {
        val intent = Intent(this, clazz)
        intent.putExtra(params, obj)
        startActivityForResult(intent, requestCode)
    }

    fun skip(clazz: Class<*>, params: String, obj: Serializable, params1: String, obj1: Serializable) {
        val intent = Intent(this, clazz)
        intent.putExtra(params, obj)
        intent.putExtra(params1, obj1)
        startActivityForResult(intent, 0)
    }

    fun skip(clazz: Class<*>, params: String, obj: String, params1: String, obj1: Serializable, requestCode: Int) {
        val intent = Intent(this, clazz)
        intent.putExtra(params, obj)
        intent.putExtra(params1, obj1)
        startActivityForResult(intent, requestCode)
    }


    fun skip(clazz: Class<*>, params: String, obj: Serializable, params1: String, obj1: Serializable, params2: String, obj2: Serializable) {
        val intent = Intent(this, clazz)
        intent.putExtra(params, obj)
        intent.putExtra(params1, obj1)
        intent.putExtra(params2, obj2)
        startActivityForResult(intent, 0)
    }

    fun skip(clazz: Class<*>, params: String, obj: Serializable, params1: String, obj1: Serializable, params2: String, obj2: Serializable, requestCode: Int) {
        val intent = Intent(this, clazz)
        intent.putExtra(params, obj)
        intent.putExtra(params1, obj1)
        intent.putExtra(params2, obj2)
        startActivityForResult(intent, requestCode)
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    protected fun requestCodeQRCodePermissions() {
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.VIBRATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和闪光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, *perms)
        }
    }


    protected fun hideKeyboard() {
        SoftKeyboardUtil.hideSoftKeyboard(this)
    }

    /**
     * 显示进度提示窗
     *
     * @param msg 显示信息
     */
    protected fun showProgress(msg: String) {
        dialog = ProgressDialog(this)
        dialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)// 设置进度条的形式为圆形转动的进度条
        dialog!!.setCancelable(false)// 设置是否可以通过点击Back键取消
        dialog!!.setCanceledOnTouchOutside(false)// 设置在点击Dialog外是否取消Dialog进度条
        dialog!!.setMessage(msg)
        dialog!!.show()

    }

    /**
     * 隐藏进度弹窗
     */
    protected fun hideProgress() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    companion object {

        private const val REQUEST_CODE_QRCODE_PERMISSIONS = 1
    }
}
