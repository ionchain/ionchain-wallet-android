package org.ionchain.wallet.ui.fragment;

import android.view.View;
import android.widget.ListView;

import org.ionchain.wallet.R;
import org.ionchain.wallet.ui.base.AbsBaseFragment;
import org.ionchain.wallet.widget.IONCTitleBar;

/**
 * 我的设备
 */
public class DevicesFragment extends AbsBaseFragment {
    private ListView mListView;
    private IONCTitleBar mIONCTitleBar;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_devices;
    }

    @Override
    protected void initView(View view) {
        mIONCTitleBar = view.findViewById(R.id.ionc_title_bar);
        mIONCTitleBar.setTitle("我的设备").setTitleTextColor(R.color.white);
        mIONCTitleBar.setBackgroundColor(getResources().getColor(R.color.blue_top));
        mIONCTitleBar.getLeftImg().setVisibility(View.GONE);
        mIONCTitleBar.getRightImg().setVisibility(View.GONE);
        mIONCTitleBar.getRightText().setVisibility(View.GONE);
    }

    @Override
    protected void getData() {

    }

    @Override
    protected void setImmersionBar() {
        super.setImmersionBar();
        mImmersionBar
                .statusBarColor(R.color.blue_top)
                .execute();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mImmersionBar
                    .statusBarColor(R.color.blue_top)
                    .execute();
        }
    }

}
