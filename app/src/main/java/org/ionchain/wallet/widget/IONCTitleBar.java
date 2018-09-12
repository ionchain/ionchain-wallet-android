package org.ionchain.wallet.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ionchain.wallet.R;

/**
 * 自定义标题栏
 */
public class IONCTitleBar extends RelativeLayout {
    private ImageView mLeftImg;
    private TextView mLeftText;
    private TextView mRightText;
    private TextView mTitle;
    private ImageView mRightImg;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-08-30 22:31:56 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        mLeftImg = (ImageView) findViewById(R.id.left_img);
        mLeftText = (TextView) findViewById(R.id.left_text);
        mRightText = (TextView) findViewById(R.id.right_text);
        mTitle = (TextView) findViewById(R.id.title);
        mRightImg = (ImageView) findViewById(R.id.right_img);
    }

    public IONCTitleBar(Context context) {
        super(context);
    }

    public IONCTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViews();
    }

    /**
     * 设置 标题
     *
     * @param title 标题
     */
    public IONCTitleBar setTitle(String title) {
        mTitle.setText(title);
        return this;
    }

    /**
     * 设置左侧文字
     *
     * @param leftText 左边的文字
     */
    public IONCTitleBar setLeftText(String leftText) {
        mLeftText.setText(leftText);
        return this;
    }

    /**
     * 设置右侧文字
     *
     * @param rightText ➡you边的文字
     */
    public IONCTitleBar setRightText(String rightText) {
        mRightText.setText(rightText);
        return this;
    }

    /**
     * 设置左侧图标
     *
     * @param resId 资源id
     */
    public IONCTitleBar setLeftImgRes(int resId) {
        mLeftImg.setBackgroundResource(resId);
        return this;
    }

    /**
     * 设置右侧图标
     *
     * @param resId 资源id
     */
    public IONCTitleBar setRightImgRes(int resId) {
        mRightImg.setBackgroundResource(resId);
        return this;
    }

    /**
     * @return 左侧图标控件
     */
    public ImageView getLeftImg() {
        return mLeftImg;
    }

    /**
     * @return 左侧文字控件
     */
    public TextView getLeftText() {
        return mLeftText;
    }

    /**
     * @return 右侧文字控件
     */
    public TextView getRightText() {
        return mRightText;
    }

    /**
     * @return 标题控件
     */
    public TextView getTitle() {
        return mTitle;
    }

    /**
     * @return 右侧图标控件
     */
    public ImageView getRightImg() {
        return mRightImg;
    }

    /**
     * 设置左侧按钮点击事件
     */
    public IONCTitleBar setLeftBtnCLickedListener(View.OnClickListener listener) {
        mLeftImg.setOnClickListener(listener);
        return this;
    }

    /**
     * 设置右侧按钮点击事件
     */
    public IONCTitleBar setRightBtnCLickedListener(View.OnClickListener listener) {
        mRightImg.setOnClickListener(listener);
        return this;
    }

    /**
     * 设置右侧文字点击事件
     */
    public IONCTitleBar setRightTextCLickedListener(View.OnClickListener listener) {
        mRightText.setOnClickListener(listener);
        return this;
    }

    /**
     * 设置左侧文字点击事件
     */
    public IONCTitleBar setLeftTextCLickedListener(View.OnClickListener listener) {
        mRightText.setOnClickListener(listener);
        return this;
    }

    public IONCTitleBar setTitleTextColor(int color) {
        mTitle.setTextColor(getResources().getColor(color));
        return this;
    }
}
