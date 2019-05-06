package org.ionchain.wallet.mvp.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import org.ionchain.wallet.R;
import org.ionchain.wallet.constant.ConstantParams;
import org.ionchain.wallet.mvp.model.update.OnCheckUpdateInfoCallback;
import org.ionchain.wallet.mvp.presenter.Presenter;
import org.ionchain.wallet.mvp.view.activity.ManageWalletActivity;
import org.ionchain.wallet.mvp.view.activity.SettingLanguageActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseFragment;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.widget.dialog.download.DownloadDialog;
import org.ionchain.wallet.widget.dialog.version.VersionInfoDialog;

import java.util.Locale;

public class MineFragment extends AbsBaseFragment implements VersionInfoDialog.OnVersionDialogBtnClickedListener, OnCheckUpdateInfoCallback {

    /**
     * 钱包管理
     */
    private RelativeLayout walletManageRLayout;
    /**
     * 语言设置
     */
    private RelativeLayout language_setting;
    /**
     * 版本信息
     */
    private RelativeLayout version_info;
    /**
     * 关于我们
     */
    private RelativeLayout about_us;
    /**
     * 下载对话框
     */
    private DownloadDialog mDownloadDialog;
    /**
     * 版本信息对话框
     */
    private VersionInfoDialog mVersionInfoDialog;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-12 16:34:06 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @SuppressLint("CutPasteId")
    private void findViews(View rootView) {
        walletManageRLayout = rootView.findViewById(R.id.walletManageRLayout);
        version_info = rootView.findViewById(R.id.version_info);
        about_us = rootView.findViewById(R.id.about_us);
        language_setting = rootView.findViewById(R.id.language_setting);
    }

    /**
     * 设置相关的点击事件处理函数
     */
    @Override
    protected void setListener() {

        walletManageRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(ManageWalletActivity.class);
            }
        });
        language_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, SettingLanguageActivity.class));
            }
        });
        /*
         * 展示版本信息
         * */
        version_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = "";
                String info = "";
                String btn_tx = "";

                if ("zh_CN".equals(Locale.getDefault().toString())) {
                    title = "版本信息";
                    info = "中文信息";
                    btn_tx = "检查更细";
                } else {
                    title = "Version Info";
                    info = "英文信息";
                    btn_tx = "update";
                }
                mVersionInfoDialog = new VersionInfoDialog(mActivity, "", MineFragment.this)
                        .setTitleName(title)
                        .setSureBtnName(btn_tx)
                        .setVersionInfo(info);
                mVersionInfoDialog.show();
            }
        });
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                NetUtils.downloadShowDialog(mActivity, "sasasasa", "dsadasadasd", NetUtils.downloadTask("http://192.168.1.3:8009/quzhizuo.apk", MineFragment.this));
            }
        });
    }


    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(View view) {
        findViews(view);
    }

    @Override
    protected void initData() {

    }

    /**
     * 检查更新
     *
     * @param dialog 对话框
     * @param type   事件类型
     * @param url    下载地址
     */
    @Override
    public void onVersionDialogRightBtnClicked(VersionInfoDialog dialog, char type, String url) {
        dialog.dismiss();
        if (type == ConstantParams.VERSION_TAG_CHECK_FOR_UPDATE) {
            if (requestStoragePermissions()) {
                new Presenter().checkForUpdate(this);
            }
        } else if (type == ConstantParams.VERSION_TAG_DOWNLOAD) {
            //下载对话框
            new DownloadDialog(mActivity, url).show();
        }
    }

    @Override
    public void onVersionDialogLeftBtnClicked(VersionInfoDialog dialog) {
        dialog.dismiss();
    }

    @Override
    public void onCheckForUpdateStart() {
        showProgress(getString(R.string.updating));
    }

    @Override
    public void onCheckForUpdateSuccess() {
        hideProgress();
        mVersionInfoDialog.dismiss();
    }

    @Override
    public void onCheckForUpdateError() {
        hideProgress();
        mVersionInfoDialog.dismiss();
        ToastUtil.showShortToast(getAppString(R.string.app_update_error));
    }

    @Override
    public void onCheckForUpdateNeedUpdate(String url, String update_info, String v_code) {
        /*
         * 更新当前版本信息对话框的展示内容
         *
         * */

        mVersionInfoDialog = new VersionInfoDialog(mActivity, url, MineFragment.this)
                .setTitleName(getResources().getString(R.string.v_info, v_code))
                .setSureBtnName(getString(R.string.dialog_btn_download))
                .setVersionInfo(update_info);
        mVersionInfoDialog.setType(ConstantParams.VERSION_TAG_DOWNLOAD);
        mVersionInfoDialog.show();
//        NetUtils.downloadShowDialog(mActivity, url, update_info, v_code, this);
    }

    @Override
    public void onCheckForUpdateNoNewVersion() {
        ToastUtil.showLong(getString(R.string.app_update_no));
    }

}
