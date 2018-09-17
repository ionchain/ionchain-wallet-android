package org.ionchain.wallet.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ionchain.wallet.R;
import org.ionchain.wallet.utils.StringUtils;

/*
 * 导出私钥对话框
 * 、*/
public class DialogPasswordCheck extends BaseDialog {

    private View.OnClickListener leftBtnClickedListener;
    private View.OnClickListener rightBtnClickedListener;


    public DialogPasswordCheck(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        findViews();
        setCanceledOnTouchOutside(false);
        initListener();
    }

    private void initListener() {
        leftBtn.setOnClickListener(leftBtnClickedListener);
        rightBtn.setOnClickListener(rightBtnClickedListener);
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_layout_check_pwd;
    }

    private TextView title;
    private EditText passwordEt;
    private Button leftBtn;
    private Button rightBtn;

    private String leftBtnText;
    private String rightBtnText;
    private String titleText;

    public String getLeftBtnText() {
        return leftBtnText;
    }

    public DialogPasswordCheck setLeftBtnText(String leftBtnText) {
        this.leftBtnText = leftBtnText;
        return this;
    }

    public String getRightBtnText() {
        return rightBtnText;
    }

    public DialogPasswordCheck setRightBtnText(String rightBtnText) {
        this.rightBtnText = rightBtnText;
        return this;
    }

    public String getTitleText() {
        return titleText;
    }

    public DialogPasswordCheck setTitleText(String titleText) {
        this.titleText = titleText;
        return this;
    }

    public TextView getTitle() {
        return title;
    }

    public DialogPasswordCheck setTitle(TextView title) {
        this.title = title;
        return this;
    }

    public EditText getPasswordEt() {
        return passwordEt;
    }

    public DialogPasswordCheck setPasswordEt(EditText passwordEt) {
        this.passwordEt = passwordEt;
        return this;
    }

    public Button getLeftBtn() {
        return leftBtn;
    }

    public DialogPasswordCheck setLeftBtn(Button leftBtn) {
        this.leftBtn = leftBtn;
        return this;
    }

    public Button getRightBtn() {
        return rightBtn;
    }

    public DialogPasswordCheck setRightBtn(Button rightBtn) {
        this.rightBtn = rightBtn;
        return this;
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-03 11:23:09 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        title = (TextView) findViewById(R.id.title);
        passwordEt = (EditText) findViewById(R.id.password_et);
        leftBtn = (Button) findViewById(R.id.left_btn);
        rightBtn = (Button) findViewById(R.id.right_btn);
        if (!StringUtils.isEmpty(titleText)) {
            title.setText(titleText);
        }
        if (!StringUtils.isEmpty(leftBtnText)) {
            leftBtn.setText(leftBtnText);
        }
        if (!StringUtils.isEmpty(rightBtnText)) {
            rightBtn.setText(rightBtnText);
        }
    }

    /**
     * 设置左侧按钮点击事件
     */
    public DialogPasswordCheck setLeftBtnClickedListener(View.OnClickListener leftBtnClickedListener) {
        this.leftBtnClickedListener = leftBtnClickedListener;
        return this;
    }

    /**
     * 设置右侧按钮点击事件
     */
    public DialogPasswordCheck setRightBtnClickedListener(View.OnClickListener rightBtnClickedListener) {
        this.rightBtnClickedListener = rightBtnClickedListener;
        return this;
    }

    public DialogPasswordCheck setBtnClickedListener(View.OnClickListener leftBtnClickedListener,View.OnClickListener rightBtnClickedListener) {
        this.leftBtnClickedListener = leftBtnClickedListener;
        this.rightBtnClickedListener = rightBtnClickedListener;
        return this;
    }
}
