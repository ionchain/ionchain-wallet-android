package org.ionc.dialog.check;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ionc.dialog.R;

import org.ionc.dialog.base.AbsBaseDialog;
import org.ionc.dialog.callback.OnStringCallbcak;

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

    private OnStringCallbcak stringCallbcak;

    public DialogCheckMnemonic(@NonNull Activity context, OnStringCallbcak stringCallbcak) {
        super(context);
        this.stringCallbcak = stringCallbcak;
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_layout_check_mnemonic;
    }

    @Override
    protected void initDialog() {
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
    private android.support.v7.widget.AppCompatEditText mnemonic1;
    private android.support.v7.widget.AppCompatEditText mnemonic2;
    private android.support.v7.widget.AppCompatEditText mnemonic3;
    private android.support.v7.widget.AppCompatEditText mnemonic4;
    private android.support.v7.widget.AppCompatEditText mnemonic5;
    private android.support.v7.widget.AppCompatEditText mnemonic6;
    private android.support.v7.widget.AppCompatEditText mnemonic7;
    private android.support.v7.widget.AppCompatEditText mnemonic8;
    private android.support.v7.widget.AppCompatEditText mnemonic9;
    private android.support.v7.widget.AppCompatEditText mnemonic10;
    private android.support.v7.widget.AppCompatEditText mnemonic11;
    private android.support.v7.widget.AppCompatEditText mnemonic12;

    List<android.support.v7.widget.AppCompatEditText> editTextList = new ArrayList<>();

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-04-09 11:42:51 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        title = (TextView) findViewById(R.id.title);
        mnemonic1 = (android.support.v7.widget.AppCompatEditText) findViewById(R.id.mnemonic1);
        mnemonic2 = (android.support.v7.widget.AppCompatEditText) findViewById(R.id.mnemonic2);
        mnemonic3 = (android.support.v7.widget.AppCompatEditText) findViewById(R.id.mnemonic3);
        mnemonic4 = (android.support.v7.widget.AppCompatEditText) findViewById(R.id.mnemonic4);
        mnemonic5 = (android.support.v7.widget.AppCompatEditText) findViewById(R.id.mnemonic5);
        mnemonic6 = (android.support.v7.widget.AppCompatEditText) findViewById(R.id.mnemonic6);
        mnemonic7 = (android.support.v7.widget.AppCompatEditText) findViewById(R.id.mnemonic7);
        mnemonic8 = (android.support.v7.widget.AppCompatEditText) findViewById(R.id.mnemonic8);
        mnemonic9 = (android.support.v7.widget.AppCompatEditText) findViewById(R.id.mnemonic9);
        mnemonic10 = (android.support.v7.widget.AppCompatEditText) findViewById(R.id.mnemonic10);
        mnemonic11 = (android.support.v7.widget.AppCompatEditText) findViewById(R.id.mnemonic11);
        mnemonic12 = (android.support.v7.widget.AppCompatEditText) findViewById(R.id.mnemonic12);
        rightBtn = (Button) findViewById(R.id.right_btn);
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
            String m1 = mnemonic1.getText().toString().trim();
            String m2 = mnemonic2.getText().toString().trim();
            String m3 = mnemonic3.getText().toString().trim();
            String m4 = mnemonic4.getText().toString().trim();
            String m5 = mnemonic5.getText().toString().trim();
            String m6 = mnemonic6.getText().toString().trim();
            String m7 = mnemonic7.getText().toString().trim();
            String m8 = mnemonic8.getText().toString().trim();
            String m9 = mnemonic9.getText().toString().trim();
            String m10 = mnemonic10.getText().toString().trim();
            String m11 = mnemonic11.getText().toString().trim();
            String m12 = mnemonic12.getText().toString().trim();
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
                Toast.makeText(mActivity, "请检查您的是否已全部输入", Toast.LENGTH_SHORT).show();
                return;
            }
            String[] ms = {
                    m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12
            };

            stringCallbcak.onString(ms, editTextList,this);
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
