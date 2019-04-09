package org.ionc.dialog.mnemonic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ionc.dialog.R;

import org.ionc.dialog.base.AbsBaseDialog;

/**
 * describe:
 * 助记词弹窗
 * <p>
 * 用户在未备份助记词时,当用户切换到该钱包时,强制用户备份助记词,未备份的钱包,将不能使用该钱包
 * 备份前,需要用户输入该钱包的密码,如果密码错误,则不能备份助记词
 *
 * @author 596928539@qq.com
 * @date 2019/04/04
 */
public class MnemonicDialog extends AbsBaseDialog {
    private TextView mmnemonic1;
    private TextView mmnemonic2;
    private TextView mmnemonic3;
    private TextView mmnemonic4;
    private TextView mmnemonic5;
    private TextView mmnemonic6;
    private TextView mmnemonic7;
    private TextView mmnemonic8;
    private TextView mmnemonic9;
    private TextView mmnemonic10;
    private TextView mmnemonic11;
    private TextView mmnemonic12;
    private String[] mmnemonicArray;
    private Button mClose;
    private OnSavedMnemonicCallback callback;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-04-04 23:47:15 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        mmnemonic1 = findViewById(R.id.mmnemonic1);
        mmnemonic2 = findViewById(R.id.mmnemonic2);
        mmnemonic3 = findViewById(R.id.mmnemonic3);
        mmnemonic4 = findViewById(R.id.mmnemonic4);
        mmnemonic5 = findViewById(R.id.mmnemonic5);
        mmnemonic6 = findViewById(R.id.mmnemonic6);
        mmnemonic7 = findViewById(R.id.mmnemonic7);
        mmnemonic8 = findViewById(R.id.mmnemonic8);
        mmnemonic9 = findViewById(R.id.mmnemonic9);
        mmnemonic10 = findViewById(R.id.mmnemonic10);
        mmnemonic11 = findViewById(R.id.mmnemonic11);
        mmnemonic12 = findViewById(R.id.mmnemonic12);
        mClose = findViewById(R.id.close);
    }

    public MnemonicDialog(@NonNull Context context, String[] mmnemonicList,OnSavedMnemonicCallback callback) {
        super(context);
        this.mmnemonicArray = mmnemonicList;
        this.callback = callback;
    }

    @Override
    protected void initData() {
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onToSaved();
            }
        });
    }

    @Override
    protected void initDialog() {
        setCancelable(false);
    }

    @Override
    protected void initView() {
        findViews();
        mmnemonic1.setText(mmnemonicArray[0]);
        mmnemonic2.setText(mmnemonicArray[1]);
        mmnemonic3.setText(mmnemonicArray[2]);
        mmnemonic4.setText(mmnemonicArray[3]);
        mmnemonic5.setText(mmnemonicArray[4]);
        mmnemonic6.setText(mmnemonicArray[5]);
        mmnemonic7.setText(mmnemonicArray[6]);
        mmnemonic8.setText(mmnemonicArray[7]);
        mmnemonic9.setText(mmnemonicArray[8]);
        mmnemonic10.setText(mmnemonicArray[9]);
        mmnemonic11.setText(mmnemonicArray[10]);
        mmnemonic12.setText(mmnemonicArray[11]);
    }

    @Override
    protected int getLayout() {
        return R.layout.mnemonic_dialog_layout;
    }

    public interface OnSavedMnemonicCallback {
        void onToSaved();
    }
}
