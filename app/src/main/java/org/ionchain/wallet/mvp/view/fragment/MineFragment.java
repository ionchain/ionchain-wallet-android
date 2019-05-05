package org.ionchain.wallet.mvp.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.lzy.okgo.model.Progress;

import org.ionc.dialog.download.DownloadDialog;
import org.ionc.dialog.version.VersionInfoDialog;
import org.ionc.wallet.utils.Logger;
import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.model.update.OnUpdateInfoCallback;
import org.ionchain.wallet.mvp.presenter.Presenter;
import org.ionchain.wallet.mvp.view.activity.ManageWalletActivity;
import org.ionchain.wallet.mvp.view.activity.SettingLanguageActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseFragment;
import org.ionchain.wallet.utils.NetUtils;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.Locale;

public class MineFragment extends AbsBaseFragment implements VersionInfoDialog.OnVersionDialogBtnClickedListener, OnUpdateInfoCallback, NetUtils.DownloadCallback {

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
        version_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = "";
                String info = "";

                if ("zh_CN".equals(Locale.getDefault().toString())) {
                    title = "版本信息";
                    info = "中文信息";
                } else {
                    title = "Version Info";
                    info = "英文信息";
                }
                mVersionInfoDialog = new VersionInfoDialog(mActivity, MineFragment.this)
                        .setTitleName(title)
                        .setVersionInfo(info);
                mVersionInfoDialog.show();
            }
        });
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetUtils.downloadShowDialog(mActivity, "sasasasa", "dsadasadasd", NetUtils.downloadTask("http://192.168.1.3:8009/quzhizuo.apk", MineFragment.this));
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
     */
    @Override
    public void onCheckUpdateBtnClicked() {
        if (requestStoragePermissions()) {
            new Presenter().update(this);
        }
    }

    @Override
    public void onCancel(VersionInfoDialog dialog) {
        dialog.dismiss();
    }

    @Override
    public void onStartCheckUpdate() {
        showProgress(getString(R.string.updating));
    }

    @Override
    public void onRequestSuccess() {
        hideProgress();
    }

    @Override
    public void onErrorCheckUpdate() {
        hideProgress();
    }

    @Override
    public void needUpdate(String url, String update_info, String v_code) {
        NetUtils.downloadShowDialog(mActivity, update_info, v_code, NetUtils.downloadTask(url, this));
    }

    @Override
    public void noNewVersion() {
        ToastUtil.showLong(getString(R.string.app_update_no));
    }

    @Override
    public void onDownloadStart(Progress progress) {
        //开始
        Logger.i("开始下载......");

        mDownloadDialog = new DownloadDialog(mActivity);
        mDownloadDialog.show();
    }

    @Override
    public void onDownloadProgress(Progress progress) {
        int p = (int) App.scale(progress.fraction);
        mDownloadDialog.updateProgress(p);
        mDownloadDialog.updateProgresTvs(p + "%");
    }

    @Override
    public void onDownloadError(Progress progress) {

    }

    @Override
    public void onDownloadFinish(Progress progress) {
        mDownloadDialog.dismiss();
        NetUtils.downloader(mActivity, progress.filePath);
    }
}
