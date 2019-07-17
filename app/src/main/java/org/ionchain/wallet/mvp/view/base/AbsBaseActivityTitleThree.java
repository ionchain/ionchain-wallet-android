package org.ionchain.wallet.mvp.view.base;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ionchain.wallet.R;

public abstract class AbsBaseActivityTitleThree extends AbsBaseActivity {

    protected RelativeLayout mTitleRl;
    protected ImageView mTitleLeftImage;
    protected ImageView mTitleRightImage;
    protected TextView mTitleNameTv;

    @Override
    protected void initCommonTitle() {
        super.initCommonTitle();
        mTitleRl = findViewById(R.id.common_title_rl);
        mTitleLeftImage = findViewById(R.id.common_image_back);
        mTitleRightImage = findViewById(R.id.common_title_scan);
        mTitleNameTv = findViewById(R.id.common_title);
        mTitleRl.setBackgroundColor(getCommonTitleBackgroundColor());
        mTitleNameTv.setText(getTitleName());
    }

    protected abstract String getTitleName();

    private int getCommonTitleBackgroundColor() {
        return getResources().getColor(R.color.blue_top);
    }
    @Override
    protected void setImmersionBar() {
        super.setImmersionBar();
        mImmersionBar.titleView(R.id.title_header).statusBarDarkFont(false).execute();
    }
    protected void setListener() {
        super.setListener();
        mTitleLeftImage.setOnClickListener(v -> finish());
    }

    @Override
    protected void initData() {

    }
}
