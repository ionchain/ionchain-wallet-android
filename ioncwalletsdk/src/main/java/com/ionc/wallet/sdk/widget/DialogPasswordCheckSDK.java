package com.ionc.wallet.sdk.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ionc.wallet.sdk.R;
import com.ionc.wallet.sdk.utils.StringUtils;


/*
 * 支付对话框
 * 、*/
public class DialogPasswordCheckSDK extends BaseDialog {

    private View.OnClickListener leftBtnClickedListener;
    private View.OnClickListener rightBtnClickedListener;


    public DialogPasswordCheckSDK(@NonNull Context context) {
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
        return R.layout.sdk_dialog_layout_check_pwd;
    }

    private TextView title;
    private EditText account_money_tx;
    private TextView from;
    private TextView to;
    private EditText passwordEt;
    private Button leftBtn;
    private Button rightBtn;

    private String leftBtnText;
    private String rightBtnText;
    private String titleText;

    private String name;

    public DialogPasswordCheckSDK setName(String name) {
        this.name = name;
        return this;
    }

    public String getLeftBtnText() {
        return leftBtnText;
    }

    public DialogPasswordCheckSDK setLeftBtnText(String leftBtnText) {
        this.leftBtnText = leftBtnText;
        return this;
    }

    public String getRightBtnText() {
        return rightBtnText;
    }

    public DialogPasswordCheckSDK setRightBtnText(String rightBtnText) {
        this.rightBtnText = rightBtnText;
        return this;
    }

    public String getTitleText() {
        return titleText;
    }

    public DialogPasswordCheckSDK setTitleText(String titleText) {
        this.titleText = titleText;
        return this;
    }

    public TextView getTitle() {
        return title;
    }

    public DialogPasswordCheckSDK setTitle(TextView title) {
        this.title = title;
        return this;
    }

    public EditText getPasswordEt() {
        return passwordEt;
    }

    public DialogPasswordCheckSDK setPasswordEt(EditText passwordEt) {
        this.passwordEt = passwordEt;
        return this;
    }

    public Button getLeftBtn() {
        return leftBtn;
    }

    public DialogPasswordCheckSDK setLeftBtn(Button leftBtn) {
        this.leftBtn = leftBtn;
        return this;
    }

    public Button getRightBtn() {
        return rightBtn;
    }

    public DialogPasswordCheckSDK setRightBtn(Button rightBtn) {
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
        account_money_tx = (EditText) findViewById(R.id.account_money_tx);
        from = (TextView) findViewById(R.id.from_account);
        to = (TextView) findViewById(R.id.to_account);
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
        if (!StringUtils.isEmpty(name)) {
            from.setText("转出：" + name);
        }
        if (!StringUtils.isEmpty(name)) {
//            to.setText("转入：0x10cDB745720e2A1A60dB812183AbC901975d101c");
            to.setText("转入：旺链");
        }

    }

    public Double getAccountMoney() {
        String value = account_money_tx.getText().toString();
        if (TextUtils.isEmpty(value)) {
            return Double.valueOf(100);
        }
        return Double.valueOf(value);
    }

    /**
     * 设置左侧按钮点击事件
     */
    public DialogPasswordCheckSDK setLeftBtnClickedListener(View.OnClickListener leftBtnClickedListener) {
        this.leftBtnClickedListener = leftBtnClickedListener;
        return this;
    }

    /**
     * 设置右侧按钮点击事件
     */
    public DialogPasswordCheckSDK setRightBtnClickedListener(View.OnClickListener rightBtnClickedListener) {
        this.rightBtnClickedListener = rightBtnClickedListener;
        return this;
    }

    public DialogPasswordCheckSDK setBtnClickedListener(View.OnClickListener leftBtnClickedListener, View.OnClickListener rightBtnClickedListener) {
        this.leftBtnClickedListener = leftBtnClickedListener;
        this.rightBtnClickedListener = rightBtnClickedListener;
        return this;
    }
}
