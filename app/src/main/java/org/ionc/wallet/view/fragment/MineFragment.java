package org.ionc.wallet.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.lzy.okgo.model.Progress;

import org.ionc.ionclib.utils.ToastUtil;
import org.ionc.wallet.App;
import org.ionc.wallet.bean.UpdateBean;
import org.ionc.wallet.model.update.OnCheckUpdateInfoCallback;
import org.ionc.wallet.presenter.update.UpdatePresenter;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.view.activity.exchange.ExChangeActivity;
import org.ionc.wallet.view.activity.manager.ManageWalletActivity;
import org.ionc.wallet.view.activity.setting.coin.SelectCoinActivity;
import org.ionc.wallet.view.activity.setting.language.SettingLanguageActivity;
import org.ionc.wallet.view.activity.webview.AboutUsWebActivity;
import org.ionc.wallet.view.activity.webview.UseHelpActivity;
import org.ionc.wallet.view.base.AbsBaseFragment;
import org.ionc.wallet.view.widget.dialog.download.DownloadDialog;
import org.ionc.wallet.view.widget.dialog.version.VersionInfoDialog;
import org.ionchain.wallet.R;

import java.util.Objects;

import static org.ionc.wallet.App.isCurrentLanguageZN;

public class MineFragment extends AbsBaseFragment implements VersionInfoDialog.OnVersionDialogBtnClickedListener, OnCheckUpdateInfoCallback, DownloadDialog.OnDownloadCallback {

    /**
     * 钱包管理
     */
    private RelativeLayout mMineChangeMainChain;
    /**
     * 钱包管理
     */
    private RelativeLayout mWalletManageRL;
    /**
     * 语言设置
     */
    private RelativeLayout mLanguageSettingRL;
    /**
     * 版本信息
     */
    private RelativeLayout mVersionInfoRL;
    /**
     * 关于我们
     */
    private RelativeLayout mAboutUsRL;
    /**
     * 使用帮助
     */
    private RelativeLayout mUseHelp;
    /**
     * 币种选择
     */
    private RelativeLayout mCoinTypeRL;

    /**
     * 版本信息对话框 ,带有的按钮类型为检查更新
     */
    private VersionInfoDialog mVersionInfoDialogWithUpdate;
    /**
     * 新版本信息对话框 ,带有的按钮类型为下载
     */
    private VersionInfoDialog mVersionInfoDialog;
    private UpdatePresenter mUpdatePresenter;

    @Override
    public void onResume() {
        super.onResume();
        if (App.isCurrentLanguageEN()) {
            mUseHelp.setVisibility(View.GONE);
        }
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-12 16:34:06 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @SuppressLint("CutPasteId")
    private void findViews(View rootView) {
        mWalletManageRL = rootView.findViewById(R.id.mine_manager_rl);
        mMineChangeMainChain = rootView.findViewById(R.id.mine_change_main_chain);
        mVersionInfoRL = rootView.findViewById(R.id.version_info);
        mAboutUsRL = rootView.findViewById(R.id.about_us);
        mUseHelp = rootView.findViewById(R.id.using_help);
        mCoinTypeRL = rootView.findViewById(R.id.coin_type);
        mLanguageSettingRL = rootView.findViewById(R.id.language_setting);
    }

    /**
     * 设置相关的点击事件处理函数
     */
    @Override
    protected void setListener() {

        mWalletManageRL.setOnClickListener(v -> skip(ManageWalletActivity.class));
        mMineChangeMainChain.setOnClickListener(v -> skip(ExChangeActivity.class));
        mCoinTypeRL.setOnClickListener(v -> skip(SelectCoinActivity.class));
        mLanguageSettingRL.setOnClickListener(v -> startActivity(new Intent(mActivity, SettingLanguageActivity.class)));
        /*
         * 展示版本信息
         * */
        mVersionInfoRL.setOnClickListener(v -> {
            String info;

            if (isCurrentLanguageZN()) {
                info = getAppString(R.string.version_info_zn);
            } else {
                info = getAppString(R.string.version_info_en);
            }
            mVersionInfoDialogWithUpdate = new VersionInfoDialog(mActivity, "", MineFragment.this)
                    .setTitleName(getAppString(R.string.version_info_title))
                    .setSureBtnName(getAppString(R.string.version_info_right_btn_text))
                    .setVersionInfo(info);
            mVersionInfoDialogWithUpdate.show();
        });
        mAboutUsRL.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, AboutUsWebActivity.class);
            startActivity(intent);
        });
        mUseHelp.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, UseHelpActivity.class);
            startActivity(intent);
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

    @Override
    protected void handleShow() {

    }

    @Override
    protected void handleHidden() {

    }

    /**
     * 检查更新
     * 先做一个存储权限检查
     *
     * @param dialog 对话框
     */
    @Override
    public void onDialogVersionUpdateBtnClicked(VersionInfoDialog dialog) {
        dialog.dismiss();
        if (requestStoragePermissions()) {
            mUpdatePresenter.update(this);
        }
    }

    @Override
    public void onDialogVersionCancelBtnClicked(VersionInfoDialog dialog) {

    }


    /**
     * 开始检查更新
     */
    @Override
    public void onCheckForUpdateStart() {
//        showProgress(getString(R.string.updating));
    }

    /**
     * 检查更新成功
     */
    @Override
    public void onCheckForUpdateSuccess() {
//        hideProgress();
        mVersionInfoDialogWithUpdate.dismiss();
    }

    /**
     * @param error 检查更新失败
     */
    @Override
    public void onCheckForUpdateError(String error) {
        this.hideProgress();
        mVersionInfoDialogWithUpdate.dismiss();
        ToastUtil.showShortToast(this.getAppString(R.string.error_app_update));
    }

    /**
     * 检查结果，需要更新
     *
     * @param updateBean
     * @param must_update
     */
    @Override
    public void onCheckForUpdateNeedUpdate(UpdateBean updateBean, String must_update) {
        try {
            String update_info;
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
            mVersionInfoDialogWithUpdate.dismiss();

            mVersionInfoDialog = new VersionInfoDialog(mActivity, updateBean.getData().get(0).getUrl(), MineFragment.this)
                    .setTitleName(mActivity.getAppString(R.string.version_info_title))
                    .setMustUpdate(must_update)
                    .setSureBtnName(getString(R.string.dialog_btn_download))
                    .setVersionInfo(update_info)
                    .setRightBtnType(VersionInfoDialog.VERSION_RIGHT_BTN_TYPE_DOWNLOAD);
            mVersionInfoDialog.show();
        } catch (NullPointerException e) {
            LoggerUtils.e("" + e.getMessage());
            ToastUtil.showToastLonger(mActivity.getAppString(R.string.error_data_parse));
        }

    }

    /**
     * 检查结果，是最新版本，不需更新
     */
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
        ToastUtil.showToastLonger(getAppString(R.string.error_net_download));
    }

    /**
     * 下载取消
     */
    @Override
    public void onDownloadCancel() {
        ToastUtil.showToastLonger(getAppString(R.string.download_cancel));
    }

    /**
     * 下对话框隐藏
     */
    @Override
    public void onDownloadHide() {

    }

}
