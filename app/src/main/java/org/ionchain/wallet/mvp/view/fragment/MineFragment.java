package org.ionchain.wallet.mvp.view.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.activity.ManageWalletActivity;
import org.ionchain.wallet.mvp.view.activity.SettingLanguageActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseFragment;

public class MineFragment extends AbsBaseFragment {

    private RelativeLayout walletManageRLayout;
    private RelativeLayout language_setting;
    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-12 16:34:06 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews(View rootView) {
        walletManageRLayout = rootView.findViewById(R.id.walletManageRLayout);
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
