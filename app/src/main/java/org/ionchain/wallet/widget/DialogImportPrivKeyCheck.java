package org.ionchain.wallet.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ionchain.wallet.R;

/*
 * 导出私钥对话框
 * 、*/
public class DialogImportPrivKeyCheck extends BaseDialog {

    private View.OnClickListener leftBtnClickedListener;
    private View.OnClickListener rightBtnClickedListener;


    public DialogImportPrivKeyCheck(@NonNull Context context) {
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
        return R.layout.layout_base_dialog;
    }

    private TextView title;
    private EditText passwordEt;
    private Button leftBtn;
    private Button rightBtn;

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public EditText getPasswordEt() {
        return passwordEt;
    }

    public void setPasswordEt(EditText passwordEt) {
        this.passwordEt = passwordEt;
    }

    public Button getLeftBtn() {
        return leftBtn;
    }

    public void setLeftBtn(Button leftBtn) {
        this.leftBtn = leftBtn;
    }

    public Button getRightBtn() {
        return rightBtn;
    }

    public void setRightBtn(Button rightBtn) {
        this.rightBtn = rightBtn;
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
    }

    /**
     * 设置左侧按钮点击事件
     */
    public DialogImportPrivKeyCheck setLeftBtnClickedListener(View.OnClickListener leftBtnClickedListener) {
        this.leftBtnClickedListener = leftBtnClickedListener;
        return this;
    }

    /**
     * 设置右侧按钮点击事件
     */
    public DialogImportPrivKeyCheck setRightBtnClickedListener(View.OnClickListener rightBtnClickedListener) {
        this.rightBtnClickedListener = rightBtnClickedListener;
        return this;
    }
}
