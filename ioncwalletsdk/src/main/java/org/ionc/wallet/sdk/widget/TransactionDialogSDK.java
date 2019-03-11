package org.ionc.wallet.sdk.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ionc.wallet.sdk.R;
import org.ionc.wallet.sdk.utils.StringUtils;
import org.ionc.wallet.sdk.utils.ToastUtil;


/*
 * 支付对话框
 * 、*/
public class TransactionDialogSDK extends BaseDialog {

    private OnTransactionBtnClickedListener txBtnClickedListener;


    public TransactionDialogSDK(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        findViews();
        setCanceledOnTouchOutside(false);
    }


    @Override
    protected int getLayout() {
        return R.layout.sdk_dialog_layout_check_pwd;
    }

    private TextView title;
    private EditText account_money_tx;
    private TextView from;
    private EditText to;
    private EditText passwordEt;
    private Button leftBtn;
    private Button rightBtn;

    private String leftBtnText;
    private String rightBtnText;
    private String titleText;

    private String name;

    public TransactionDialogSDK setName(String name) {
        this.name = name;
        return this;
    }

    public String getLeftBtnText() {
        return leftBtnText;
    }

    public TransactionDialogSDK setLeftBtnText(String leftBtnText) {
        this.leftBtnText = leftBtnText;
        return this;
    }

    public String getRightBtnText() {
        return rightBtnText;
    }

    public TransactionDialogSDK setRightBtnText(String rightBtnText) {
        this.rightBtnText = rightBtnText;
        return this;
    }

    public String getTitleText() {
        return titleText;
    }

    public TransactionDialogSDK setTitleText(String titleText) {
        this.titleText = titleText;
        return this;
    }

    public TextView getTitle() {
        return title;
    }

    public TransactionDialogSDK setTitle(TextView title) {
        this.title = title;
        return this;
    }

    public EditText getPasswordEt() {
        return passwordEt;
    }

    public TransactionDialogSDK setPasswordEt(EditText passwordEt) {
        this.passwordEt = passwordEt;
        return this;
    }

    public Button getLeftBtn() {
        return leftBtn;
    }

    public TransactionDialogSDK setLeftBtn(Button leftBtn) {
        this.leftBtn = leftBtn;
        return this;
    }

    public Button getRightBtn() {
        return rightBtn;
    }

    public TransactionDialogSDK setRightBtn(Button rightBtn) {
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
        to = (EditText) findViewById(R.id.to_account);
        passwordEt = (EditText) findViewById(R.id.password_et);
        leftBtn = (Button) findViewById(R.id.left_btn);
        rightBtn = (Button) findViewById(R.id.right_btn);

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                txBtnClickedListener.onCancel();
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断转出地址是否为空
                if (to.getText() != null && !StringUtils.isEmpty(to.getText().toString())) {
                    dismiss();
                    txBtnClickedListener.onSure(to.getText().toString());
                } else {
                    ToastUtil.showLong("请输入转出地址！");
                }
            }
        });
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

    }

    public Double getAccountMoney() {
        String value = account_money_tx.getText().toString();
        if (TextUtils.isEmpty(value)) {
            return Double.valueOf(100);
        }
        return Double.valueOf(value);
    }


    public TransactionDialogSDK setBtnClickedListener(OnTransactionBtnClickedListener onTransactionBtnClickedListener) {
        this.txBtnClickedListener = onTransactionBtnClickedListener;
        return this;
    }

    public interface OnTransactionBtnClickedListener {
        void onSure(String toAddress);

        void onCancel();
    }
}
