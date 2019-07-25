package org.ionchain.wallet.view.widget.dialog.mnemonic;

import android.app.Activity;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

import org.ionchain.wallet.R;
import org.ionchain.wallet.view.widget.dialog.base.AbsBaseDialog;
import org.ionchain.wallet.view.widget.dialog.callback.OnDialogCheck12MnemonicCallbcak;

import java.util.ArrayList;
import java.util.List;

/**
 * describe:
 *
 * @author 596928539@qq.com
 * @date 2019/04/09
 */
public class DialogCheckMnemonic extends AbsBaseDialog implements View.OnClickListener, TextWatcher {
    private Button rightBtn;
    private Button leftBtn;

    private OnDialogCheck12MnemonicCallbcak stringCallbcak;

    public DialogCheckMnemonic(@NonNull Activity context, OnDialogCheck12MnemonicCallbcak stringCallbcak) {
        super(context);
        this.stringCallbcak = stringCallbcak;
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_layout_check_mnemonic;
    }

    @Override
    protected void initDialog() {
        super.initDialog();
        setCancelable(false);
    }

    @Override
    protected void initView() {
        findViews();
    }

    @Override
    protected void initData() {

    }


    private TextView title;
    private AppCompatEditText mnemonic1;
    private AppCompatEditText mnemonic2;
    private AppCompatEditText mnemonic3;
    private AppCompatEditText mnemonic4;
    private AppCompatEditText mnemonic5;
    private AppCompatEditText mnemonic6;
    private AppCompatEditText mnemonic7;
    private AppCompatEditText mnemonic8;
    private AppCompatEditText mnemonic9;
    private AppCompatEditText mnemonic10;
    private AppCompatEditText mnemonic11;
    private AppCompatEditText mnemonic12;

    List<AppCompatEditText> editTextList = new ArrayList<>();

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-04-09 11:42:51 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        title = findViewById(R.id.title);
        mnemonic1 = findViewById(R.id.mnemonic1);
        mnemonic2 = findViewById(R.id.mnemonic2);
        mnemonic3 = findViewById(R.id.mnemonic3);
        mnemonic4 = findViewById(R.id.mnemonic4);
        mnemonic5 = findViewById(R.id.mnemonic5);
        mnemonic6 = findViewById(R.id.mnemonic6);
        mnemonic7 = findViewById(R.id.mnemonic7);
        mnemonic8 = findViewById(R.id.mnemonic8);
        mnemonic9 = findViewById(R.id.mnemonic9);
        mnemonic10 = findViewById(R.id.mnemonic10);
        mnemonic11 = findViewById(R.id.mnemonic11);
        mnemonic12 = findViewById(R.id.mnemonic12);
        rightBtn = findViewById(R.id.right_btn);
        leftBtn = findViewById(R.id.left_btn);
        editTextList.add(mnemonic1);
        editTextList.add(mnemonic2);

        editTextList.add(mnemonic3);
        editTextList.add(mnemonic4);
        editTextList.add(mnemonic5);
        editTextList.add(mnemonic6);
        editTextList.add(mnemonic7);
        editTextList.add(mnemonic8);
        editTextList.add(mnemonic9);
        editTextList.add(mnemonic10);
        editTextList.add(mnemonic11);
        editTextList.add(mnemonic12);

        mnemonic1.addTextChangedListener(this);
        mnemonic2.addTextChangedListener(this);
        mnemonic3.addTextChangedListener(this);
        mnemonic4.addTextChangedListener(this);
        mnemonic5.addTextChangedListener(this);
        mnemonic6.addTextChangedListener(this);
        mnemonic7.addTextChangedListener(this);
        mnemonic8.addTextChangedListener(this);
        mnemonic9.addTextChangedListener(this);
        mnemonic10.addTextChangedListener(this);
        mnemonic11.addTextChangedListener(this);
        mnemonic12.addTextChangedListener(this);

        rightBtn.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2019-04-09 11:42:51 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */

    @Override
    public void onClick(View v) {
        if (v == rightBtn) {
            // Handle clicks for rightBtn
            String m1 = mnemonic1.getText().toString().trim().toLowerCase();
            String m2 = mnemonic2.getText().toString().trim().toLowerCase();
            String m3 = mnemonic3.getText().toString().trim().toLowerCase();
            String m4 = mnemonic4.getText().toString().trim().toLowerCase();
            String m5 = mnemonic5.getText().toString().trim().toLowerCase();
            String m6 = mnemonic6.getText().toString().trim().toLowerCase();
            String m7 = mnemonic7.getText().toString().trim().toLowerCase();
            String m8 = mnemonic8.getText().toString().trim().toLowerCase();
            String m9 = mnemonic9.getText().toString().trim().toLowerCase();
            String m10 = mnemonic10.getText().toString().trim().toLowerCase();
            String m11 = mnemonic11.getText().toString().trim().toLowerCase();
            String m12 = mnemonic12.getText().toString().trim().toLowerCase();
            if (TextUtils.isEmpty(m1)
                    || TextUtils.isEmpty(m2)
                    || TextUtils.isEmpty(m3)
                    || TextUtils.isEmpty(m4)
                    || TextUtils.isEmpty(m5)
                    || TextUtils.isEmpty(m6)
                    || TextUtils.isEmpty(m7)
                    || TextUtils.isEmpty(m8)
                    || TextUtils.isEmpty(m9)
                    || TextUtils.isEmpty(m10)
                    || TextUtils.isEmpty(m11)
                    || TextUtils.isEmpty(m12)
            ) {
                Toast.makeText(mActivity, mActivity.getResources().getString(R.string.please_check_input), Toast.LENGTH_SHORT).show();
                return;
            }
            String[] ms = {
                    m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12
            };

            stringCallbcak.onDialogCheckMnemonics12(ms, editTextList, this);
        } else if (v == leftBtn) {
            dismiss();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        for (int i = 0; i < 12; i++) {
            editTextList.get(i).setTextColor(Color.BLACK);
        }
    }
}
