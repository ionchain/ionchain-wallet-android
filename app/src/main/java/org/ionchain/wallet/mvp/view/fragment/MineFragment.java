package org.ionchain.wallet.mvp.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import org.ionc.dialog.version.VersionInfoDialog;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.activity.ManageWalletActivity;
import org.ionchain.wallet.mvp.view.activity.SettingLanguageActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseFragment;

import java.util.Locale;

public class MineFragment extends AbsBaseFragment {

    private RelativeLayout walletManageRLayout;
    private RelativeLayout language_setting;
    private RelativeLayout version_info;
    private RelativeLayout about_us;
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
                String title ="";
                String info = "";

               if ("zh_CN".equals(Locale.getDefault().toString())) {
                     title = "版本信息";
                     info = "中文信息";
               } else {
                   title = "Version Info";
                   info = "英文信息";
               }
                new VersionInfoDialog(mActivity).setTitleName(title).setVersionInfo(info).show();
            }
        });
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
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
}
