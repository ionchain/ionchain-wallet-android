package org.ionchain.wallet.mvp.view.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ionchain.wallet.R;

public abstract class AbsBaseCommonTitleTwoActivity extends AbsBaseActivity {

    protected RelativeLayout mTitleRl;
    protected ImageView mTitleLeftImage;
    protected ImageView mTitleRightImage;
    protected TextView mTitleNameTv;

    protected View mTitleHeader;

    @Override
    protected void initCommonTitle() {
        super.initCommonTitle();
        mTitleRl = findViewById(R.id.common_title_rl);
        mTitleHeader = findViewById(R.id.title_header);
        mTitleLeftImage = findViewById(R.id.common_image_back);
        mTitleLeftImage.setImageResource(getLeftArrow());
        mTitleNameTv = findViewById(R.id.common_title);
        mTitleRl.setBackgroundColor(getCommonTitleBackgroundColor());
        mTitleHeader.setBackgroundColor(getTitleHeaderBackgroundColor());
        mTitleNameTv.setText(getTitleName());
        mTitleNameTv.setTextColor(getTitleNameColor());
    }

    protected int getTitleHeaderBackgroundColor() {
        return getColorBlue();
    }
    protected int getCommonTitleBackgroundColor() {
        return getColorBlue();
    }

    protected int getLeftArrow() {
        return R.mipmap.arrow_back_white;
    }

    @Override
    protected void initData() {

    }

    /**
     * @param titleName 设置标题
     */
    protected void setActivityTitle(String titleName){
        mTitleNameTv.setText(titleName);
    }
    protected int getTitleNameColor() {
        return getColorWhite();
    }

    protected abstract String getTitleName();


    @Override
    protected void setImmersionBar() {
        super.setImmersionBar();
        mImmersionBar.titleView(R.id.title_header).statusBarDarkFont(false).execute();
    }

    protected void setListener() {
        super.setListener();
        mTitleLeftImage.setOnClickListener(v -> finish());
    }
}
