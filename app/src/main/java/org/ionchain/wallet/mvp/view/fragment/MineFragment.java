package org.ionchain.wallet.mvp.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.lzy.okgo.model.Progress;

import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.UpdateBean;
import org.ionchain.wallet.constant.ConstantParams;
import org.ionchain.wallet.mvp.model.update.OnCheckUpdateInfoCallback;
import org.ionchain.wallet.mvp.presenter.update.UpdatePresenter;
import org.ionchain.wallet.mvp.view.activity.manager.ManageWalletActivity;
import org.ionchain.wallet.mvp.view.activity.setting.SettingLanguageActivity;
import org.ionchain.wallet.mvp.view.activity.webview.WebActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseFragment;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.widget.dialog.download.DownloadDialog;
import org.ionchain.wallet.widget.dialog.version.VersionInfoDialog;

import java.util.Objects;

import static org.ionchain.wallet.App.isCurrentLanguageZN;
import static org.ionchain.wallet.constant.ConstantParams.URL_REQUEST_TYPE;
import static org.ionchain.wallet.constant.ConstantParams.URL_TAG_ABOUT_US;

public class MineFragment extends AbsBaseFragment implements VersionInfoDialog.OnVersionDialogBtnClickedListener, OnCheckUpdateInfoCallback, DownloadDialog.DownloadCallback {

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
    private VersionInfoDialog mCurrentVersionInfoDialog;
    /**
     * 版本信息对话框
     */
    private VersionInfoDialog mLastedVersionInfoDialog;
    private UpdatePresenter mUpdatePresenter;
    private String must_update = "0";


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

                if (isCurrentLanguageZN()) {
                    title = "版本信息";
                    info = "中文信息";
                    btn_tx = "检查更细";
                } else {
                    title = "Version Info";
                    info = "英文信息";
                    btn_tx = "update";
                }
                mCurrentVersionInfoDialog = new VersionInfoDialog(mActivity, "", MineFragment.this)
                        .setTitleName(getAppString(R.string.version_info_title))
                        .setSureBtnName(getAppString(R.string.version_info_right_btn_text))
                        .setVersionInfo(getAppString(R.string.version_info_massage));
                mCurrentVersionInfoDialog.show();
            }
        });
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, WebActivity.class);
                intent.putExtra(URL_REQUEST_TYPE, URL_TAG_ABOUT_US);
                startActivity(intent);
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
        mUpdatePresenter = new UpdatePresenter();
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
                mUpdatePresenter.checkForUpdate(this);
            }
        } else if (type == ConstantParams.VERSION_TAG_DOWNLOAD) {
            //下载对话框
            mLastedVersionInfoDialog.dismiss();
            new DownloadDialog(mActivity, url, this, must_update).setCancelableBydBackKey(false).show();
        }
    }

    @Override
    public void onVersionDialogLeftBtnClicked(VersionInfoDialog dialog) {
        if (must_update.equals("1")) {
            ToastUtil.showShort(getAppString(R.string.must_update));
        } else {
            dialog.dismiss();
        }
    }

    @Override
    public void onCheckForUpdateStart() {
//        showProgress(getString(R.string.updating));
    }

    @Override
    public void onCheckForUpdateSuccess() {
//        hideProgress();
        mCurrentVersionInfoDialog.dismiss();
    }

    @Override
    public void onCheckForUpdateError(String error) {
        hideProgress();
        mCurrentVersionInfoDialog.dismiss();
        ToastUtil.showShortToast(getAppString(R.string.app_update_error));
    }

    @Override
    public void onCheckForUpdateNeedUpdate(UpdateBean updateBean, String must_update) {
        try {
            String update_info;
            this.must_update = must_update;
            int v_code;
            UpdateBean.DataBean dataBean_CN = null, dataBean_EN = null;
            for (int i = 0; i < 2; i++) {
                if (updateBean.getData().get(i).getLanguage().equals("0")) {
                    //中文
                    dataBean_CN = updateBean.getData().get(i);
                } else {
                    dataBean_EN = updateBean.getData().get(i);
                }
            }
            if (isCurrentLanguageZN()) {
                v_code = Objects.requireNonNull(dataBean_CN).getVersion_code();
                update_info = dataBean_CN.getUpdate_info();
            } else {
                v_code = Objects.requireNonNull(dataBean_EN).getVersion_code();
                update_info = dataBean_EN.getUpdate_info();
            }
            /*
             * 更新当前版本信息对话框的展示内容
             *
             * */
            mCurrentVersionInfoDialog.dismiss();

            mLastedVersionInfoDialog = new VersionInfoDialog(mActivity, updateBean.getData().get(0).getUrl(), MineFragment.this)
                    .setTitleName(mActivity.getAppString(R.string.version_info_title))
                    .setSureBtnName(getString(R.string.dialog_btn_download))
                    .setVersionInfo(update_info);
            mLastedVersionInfoDialog.setType(ConstantParams.VERSION_TAG_DOWNLOAD);
            mLastedVersionInfoDialog.show();
        } catch (NullPointerException e) {
            ToastUtil.showToastLonger(mActivity.getAppString(R.string.data_parase_error));
        }

//        NetUtils.downloadShowDialog(mActivity, url, update_info, v_code, this);
    }

    @Override
    public void onCheckForUpdateNoNewVersion() {
        ToastUtil.showLong(getString(R.string.app_update_no));
    }

    /**
     * 开始下载
     *
     * @param progress 下载状态
     */
    @Override
    public void onDownloadStart(Progress progress) {

    }

    /**
     * 下载出错
     *
     * @param progress 下载状态
     */
    @Override
    public void onDownloadError(Progress progress) {
        ToastUtil.showToastLonger(getAppString(R.string.download_error));
    }

    @Override
    public void onDownloadCancel() {
        ToastUtil.showToastLonger(getAppString(R.string.download_cancel));
    }
}
