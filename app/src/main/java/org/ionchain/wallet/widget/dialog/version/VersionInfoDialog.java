package org.ionchain.wallet.widget.dialog.version;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.ionchain.wallet.R;
import org.ionchain.wallet.widget.dialog.base.AbsBaseDialog;

/**
 * 展示 版本信息  检查版本更新
 */
public class VersionInfoDialog extends AbsBaseDialog implements View.OnClickListener {
    private TextView versionInfoTitle;
    private TextView versionInfoContent;
    private Button versionInfoBtnCancel;
    private Button versionInfoBtnUpdate;

    private String title_name;
    private String btn_name;
    private String version_info_content;


    private char type;
    private String url;

    public char getType() {
        return type;
    }

    public VersionInfoDialog setType(char type) {
        this.type = type;
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
     * @param name 确定按钮的文字
     * @return 对话框
     */
    public VersionInfoDialog setSureBtnName(String name) {
        btn_name = name;
        return this;
    }

    /**
     * @param name 确定按钮的文字
     * @return 对话框
     */
    public VersionInfoDialog updateSureBtnName(String name) {
        versionInfoBtnUpdate.setText(name);
        return this;
    }

    /**
     * @param name 确定按钮的文字
     * @return 对话框
     */
    public VersionInfoDialog updateTitle(String title) {
        versionInfoTitle.setText(title);
        return this;
    }

    /**
     * @param name 确定按钮的文字
     * @return 对话框
     */
    public VersionInfoDialog updateVersionInfoContent(String info) {
        versionInfoContent.setText(info);
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
            callback.onVersionDialogLeftBtnClicked(this);
        } else if (v == versionInfoBtnUpdate) {
            callback.onVersionDialogRightBtnClicked(this, type, url);
        }
    }


    @Override
    protected int getLayout() {
        return R.layout.dialog_version_info;
    }

    @Override
    protected void initDialog() {

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

    /**
     * 更新按钮点击事件监听函数
     */
    public interface OnVersionDialogBtnClickedListener {
        /**
         * 右侧按钮---确定,更新,下载
         *
         * @param dialog
         * @param type
         * @param url
         */
        void onVersionDialogRightBtnClicked(VersionInfoDialog dialog, char type, String url);

        /**
         * 左侧按钮---取消
         *
         * @param dialog 对话框
         */
        void onVersionDialogLeftBtnClicked(VersionInfoDialog dialog);
    }
}
