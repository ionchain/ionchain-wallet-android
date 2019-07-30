package org.ionchain.wallet.view.widget.dialog.modify;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.view.widget.dialog.base.AbsBaseDialog;

/**
 * describe: 修改密码
 *
 * @author xubinbin
 * @date 2019/03/12
 */
public class ModifyPasswordDialog extends AbsBaseDialog implements View.OnClickListener {
    private AppCompatEditText modifyDialogCurrentPasswordEt;
    private AppCompatEditText newPasswordEt;
    private ImageView showNewPasswordImg;
    private AppCompatEditText newPasswordAgainEt;
    private ImageView showNewPasswordAgainImg;
    private AppCompatButton modifyBtnCancel;
    private AppCompatButton modifyBtnSure;

    private String currentPassword;//当前密码
    private String newPassword;//新密码
    private String newPasswordAgain;//重复新密码


    private boolean newPasswordIsShow = false;
    private boolean newPasswordAgainIsShow = false;

    private OnModifyPasswordDialogCallback modifyPasswordCallback;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-03-12 13:40:56 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        //输入框
        modifyDialogCurrentPasswordEt = findViewById(R.id.modify_dialog_current_password_et);
        newPasswordEt = findViewById(R.id.modify_dialog_new_password_et);
        newPasswordAgainEt = findViewById(R.id.modify_dialog_new_password_again_et);

        //小眼睛
        showNewPasswordImg = findViewById(R.id.show_new_password);
        showNewPasswordAgainImg = findViewById(R.id.show_new_password_again);
        showNewPasswordImg.setOnClickListener(this);
        showNewPasswordAgainImg.setOnClickListener(this);
        //按钮
        modifyBtnCancel = findViewById(R.id.modify_btn_cancel);
        modifyBtnSure = findViewById(R.id.modify_btn_sure);
        modifyBtnCancel.setOnClickListener(this);
        modifyBtnSure.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2019-03-12 13:40:56 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == showNewPasswordImg) {
            //新密码小眼睛
            if (newPasswordIsShow) {
                newPasswordIsShow = false;
                newPasswordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showNewPasswordImg.setImageResource(R.mipmap.hide_password);
            } else {
                newPasswordIsShow = true;
                showNewPasswordImg.setImageResource(R.mipmap.show_password);
                newPasswordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            if (!TextUtils.isEmpty(newPassword)) {
                newPasswordEt.setSelection(newPassword.length());
            }

        } else if (v == showNewPasswordAgainImg) {
            //重复新密码小眼睛
            if (newPasswordAgainIsShow) {
                newPasswordAgainIsShow = false;
                newPasswordAgainEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showNewPasswordAgainImg.setImageResource(R.mipmap.hide_password);
            } else {
                newPasswordAgainIsShow = true;
                newPasswordAgainEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showNewPasswordAgainImg.setImageResource(R.mipmap.show_password);
            }
            if (!TextUtils.isEmpty(newPasswordAgain)) {
                newPasswordAgainEt.setSelection(newPasswordAgain.length());
            }
        } else if (v == modifyBtnCancel) {
            dismiss();
        } else if (v == modifyBtnSure) {
            //回调给宿主
            if (modifyDialogCurrentPasswordEt.getText() != null) {
                currentPassword = modifyDialogCurrentPasswordEt.getText().toString();
                if (TextUtils.isEmpty(currentPassword)) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.please_input_current_password), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (newPasswordEt.getText() != null) {
                newPassword = newPasswordEt.getText().toString();
                if (TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.please_input_new_password), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (newPasswordAgainEt.getText() != null) {
                newPasswordAgain = newPasswordAgainEt.getText().toString();
                if (TextUtils.isEmpty(newPasswordAgain)) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.please_input_new_password_2), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            modifyPasswordCallback.onModifyPasswordDialogParam(currentPassword, newPassword, newPasswordAgain);
        }
    }

    public ModifyPasswordDialog(@NonNull Context context, OnModifyPasswordDialogCallback callback) {
        super(context);
        modifyPasswordCallback = callback;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initDialog() {
        super.initDialog();
    }

    @Override
    protected void initView() {
        findViews();
        modifyDialogCurrentPasswordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //新密码
        newPasswordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                newPassword = s.toString();
                LoggerUtils.i(newPassword);
            }
        });
        //重复新密码
        newPasswordAgainEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                newPasswordAgain = s.toString();
            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.modify_password_dialog;
    }

}
