package org.ionchain.wallet.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ionc.wallet.sdk.utils.StringUtils;
import com.ionc.wallet.sdk.widget.BaseDialog;

import org.ionchain.wallet.R;

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述:
 */
public class DialogBindDevice extends BaseDialog {
    public DialogBindDevice(@NonNull Context context) {
        super(context);
    }

    private View.OnClickListener leftBtnClickedListener;
    private View.OnClickListener rightBtnClickedListener;



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
        return R.layout.dialog_layout_bind_card;
    }

    private TextView title;

    public TextView getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(TextView deviceSN) {
        this.deviceSN = deviceSN;
    }

    private TextView deviceSN;
    private Button leftBtn;
    private Button rightBtn;

    private String leftBtnText;
    private String rightBtnText;
    private String titleText;

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    private String messageText;

    public String getLeftBtnText() {
        return leftBtnText;
    }

    public DialogBindDevice setLeftBtnText(String leftBtnText) {
        this.leftBtnText = leftBtnText;
        return this;
    }

    public String getRightBtnText() {
        return rightBtnText;
    }

    public DialogBindDevice setRightBtnText(String rightBtnText) {
        this.rightBtnText = rightBtnText;
        return this;
    }

    public String getTitleText() {
        return titleText;
    }

    public DialogBindDevice setTitleText(String titleText) {
        this.titleText = titleText;
        return this;
    }

    public TextView getTitle() {
        return title;
    }

    public DialogBindDevice setTitle(TextView title) {
        this.title = title;
        return this;
    }


    public Button getLeftBtn() {
        return leftBtn;
    }

    public DialogBindDevice setLeftBtn(Button leftBtn) {
        this.leftBtn = leftBtn;
        return this;
    }

    public Button getRightBtn() {
        return rightBtn;
    }

    public DialogBindDevice setRightBtn(Button rightBtn) {
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
        deviceSN = findViewById(R.id.device_sn);
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
        if (!StringUtils.isEmpty(messageText)) {
            deviceSN.setText(messageText);
        }
    }

    /**
     * 设置左侧按钮点击事件
     */
    public DialogBindDevice setLeftBtnClickedListener(View.OnClickListener leftBtnClickedListener) {
        this.leftBtnClickedListener = leftBtnClickedListener;
        return this;
    }

    /**
     * 设置右侧按钮点击事件
     */
    public DialogBindDevice setRightBtnClickedListener(View.OnClickListener rightBtnClickedListener) {
        this.rightBtnClickedListener = rightBtnClickedListener;
        return this;
    }
}
