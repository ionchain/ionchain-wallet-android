package org.ionchain.wallet.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.ionc.wallet.widget.BaseDialog;

import org.ionchain.wallet.R;

public class DialogImport extends BaseDialog {

    private String message;
    private String title;

    private TextView messageTV;
    private Button copyBtn;
    private TextView titleTv;

    public DialogImport setTitle(String title) {

        this.title = title;
        return this;

    }

    public DialogImport setCopyBtnClickedListener(View.OnClickListener copyBtnClickedListener) {
        this.copyBtnClickedListener = copyBtnClickedListener;
        return this;
    }

    private View.OnClickListener copyBtnClickedListener;

    public DialogImport(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        findViews();
        messageTV.setText(message);
        titleTv.setText(title);
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_import_layout;
    }


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-03 14:48:34 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        messageTV = (TextView) findViewById(R.id.message_tv);
        titleTv = (TextView) findViewById(R.id.title_tv);
        copyBtn = (Button) findViewById(R.id.copyBtn);
        copyBtn.setOnClickListener(copyBtnClickedListener);
    }

    public DialogImport setMessage(String message) {
        this.message = message;
        return this;
    }
}
