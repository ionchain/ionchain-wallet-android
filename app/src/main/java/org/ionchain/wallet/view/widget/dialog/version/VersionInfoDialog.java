package org.ionchain.wallet.view.widget.dialog.version;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lzy.okgo.model.Progress;

import org.ionchain.wallet.R;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.view.widget.dialog.base.AbsBaseDialog;
import org.ionchain.wallet.view.widget.dialog.download.DownloadDialog;

/**
 * 展示 版本信息  检查版本更新
 */
public class VersionInfoDialog extends AbsBaseDialog implements View.OnClickListener, DownloadDialog.OnDownloadCallback {
    private TextView versionInfoTitle;
    private TextView versionInfoContent;
    private Button versionInfoBtnCancel;
    private Button versionInfoBtnUpdate;

    private String title_name;
    private String btn_name;
    private String version_info_content;


    /*
     * 右侧按钮为更新
     * */
    public static final char VERSION_RIGHT_BTN_TYPE_DOWNLOAD_UPDATE = 0;
    /**
     * 右侧按钮为下载
     */
    public static final char VERSION_RIGHT_BTN_TYPE_DOWNLOAD = 1;

    private static final String DOWNLOAD_MUST_UPDATE_NO = "0";//  不是必须更新，可以取消对话框
    private static final String DOWNLOAD_MUST_UPDATE_YES = "1";//   必须更新，不可取消

    /**
     * 是不是必须更新版本
     */
    private String mustUpdate = DOWNLOAD_MUST_UPDATE_NO;

    /**
     * 右侧按钮的类型 ,默认是检查更新
     * 1、检查更新
     * 2、下载
     */
    private char rightBtnType = VERSION_RIGHT_BTN_TYPE_DOWNLOAD_UPDATE;
    private String url;
    private boolean cancelable;

    /**
     * @param rightBtnType
     * @return
     */
    public VersionInfoDialog setRightBtnType(char rightBtnType) {
        this.rightBtnType = rightBtnType;
        return this;
    }

    private OnVersionDialogBtnClickedListener callback;

    /**
     * @param name 对话框的名字
     * @return 对话框
     */
    public VersionInfoDialog setTitleName(String name) {
        title_name = name;
        return this;
    }

    /**
     * @param mustUpdate 是否必须更新
     * @return
     */
    public VersionInfoDialog setMustUpdate(String mustUpdate) {
        this.mustUpdate = mustUpdate;
        return this;
    }

    /**
     * @param name 确定按钮的文字
     * @return 对话框
     */
    public VersionInfoDialog setSureBtnName(String name) {
        btn_name = name;
        return this;
    }

    /**
     * @param context  环境
     * @param url
     * @param listener 检查更新的按钮点击事件
     */
    public VersionInfoDialog(@NonNull Context context, String url, OnVersionDialogBtnClickedListener listener) {
        super(context);
        this.url = url;
        this.callback = listener;
    }

    /**
     * @param info 版本信息
     * @return 对话框
     * <p>
     * 使用 & 表示换行
     */
    public VersionInfoDialog setVersionInfo(String info) {
        version_info_content = info.replace("&", "\n");
        return this;
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-05-05 11:48:20 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        versionInfoTitle = (TextView) findViewById(R.id.version_info_title);
        versionInfoContent = (TextView) findViewById(R.id.version_info_content);
        versionInfoBtnCancel = (Button) findViewById(R.id.version_info_btn_cancel);
        versionInfoBtnUpdate = (Button) findViewById(R.id.version_info_btn_update);

        versionInfoBtnCancel.setOnClickListener(this);
        versionInfoBtnUpdate.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2019-05-05 11:48:20 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == versionInfoBtnCancel) {
            if (mustUpdate.equals(DOWNLOAD_MUST_UPDATE_YES)) {
                ToastUtil.showShort(mActivity.getString(R.string.must_update));
            } else {
                dismiss();
                callback.onDialogVersionCancelBtnClicked(this);
            }

        } else if (v == versionInfoBtnUpdate) {
            if (VERSION_RIGHT_BTN_TYPE_DOWNLOAD == rightBtnType) {
                dismiss();
                new DownloadDialog(mActivity, url, this, "").setCancelableBydBackKey(false).show();
            } else {
                callback.onDialogVersionUpdateBtnClicked(this);
            }
        }
    }


    @Override
    protected int getLayout() {
        return R.layout.dialog_version_info;
    }

    @Override
    protected void initDialog() {
        super.initDialog();
        setCancelable(cancelable);
    }

    @Override
    protected void initView() {
        findViews();
    }

    @Override
    protected void initData() {
        versionInfoTitle.setText(title_name);
        versionInfoContent.setText(version_info_content);
        versionInfoBtnUpdate.setText(btn_name);
    }

    public VersionInfoDialog setCancelableBydBackKey(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    @Override
    public void onDownloadStart(Progress progress) {
        callback.onDownloadStart(progress);
    }

    @Override
    public void onDownloadError(Progress progress) {
        callback.onDownloadError(progress);
    }

    @Override
    public void onDownloadCancel() {
        callback.onDownloadCancel();
    }

    @Override
    public void onDownloadHide() {
        callback.onDownloadHide();
    }

    /**
     * 更新按钮点击事件监听函数
     */
    public interface OnVersionDialogBtnClickedListener extends DownloadDialog.OnDownloadCallback {
        /**
         * 版本对话框。点击检查更新
         *
         * @param dialog 信息展示对话框
         */
        void onDialogVersionUpdateBtnClicked(VersionInfoDialog dialog);

        /**
         * 左侧按钮---取消
         *
         * @param dialog 对话框
         */
        void onDialogVersionCancelBtnClicked(VersionInfoDialog dialog);

    }
}
