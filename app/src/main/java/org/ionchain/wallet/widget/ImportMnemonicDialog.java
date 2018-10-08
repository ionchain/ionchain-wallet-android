package org.ionchain.wallet.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import org.ionchain.wallet.R;

/**
 * 导出助记词
 */
public class ImportMnemonicDialog extends BaseDialog {
    private String mnemonic;

    public ImportMnemonicDialog setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
        return this;
    }

    public ImportMnemonicDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        setCanceledOnTouchOutside(false);
        initDialog();
        TextView tv = findViewById(R.id.import_mnemonic_tv);
        tv.setText(mnemonic);
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_import_mnemonic_layout;
    }
}
