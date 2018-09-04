package org.ionchain.wallet.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.ionchain.wallet.R;

public class DialogImportPrivKey extends BaseDialog {

    private String preivateKeyText;

    public DialogImportPrivKey setCopyBtnClickedListener(View.OnClickListener copyBtnClickedListener) {
        this.copyBtnClickedListener = copyBtnClickedListener;
        return this;
    }

    private View.OnClickListener copyBtnClickedListener;

    public DialogImportPrivKey(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        findViews();
        privateKeyTv.setText(preivateKeyText);
    }

    @Override
    protected int getLayout() {
        return R.layout.layout_import_private_key;
    }
    private TextView privateKeyTv;
    private Button copyBtn;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-03 14:48:34 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        privateKeyTv = (TextView)findViewById( R.id.privateKeyTv);
        copyBtn = (Button)findViewById( R.id.copyBtn );
        copyBtn.setOnClickListener(copyBtnClickedListener);
    }
    public DialogImportPrivKey setPrivateKeyText(String preivateKeyTv) {
        this.preivateKeyText = preivateKeyTv;
        return this;
    }
}
