package org.ionc.wallet.view.widget.dialog.export;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.ionchain.wallet.R;
import org.ionc.wallet.view.widget.dialog.base.AbsBaseDialog;

/**
 * 导出私钥和KS的弹窗
 */
public class DialogTextMessage extends AbsBaseDialog {

    private String message;
    private String title;
    private String btn_text;
    private boolean cancelByBackKey = false;

    public DialogTextMessage setHintMsg(String hint_msg_txt) {
        this.hint_msg_txt = hint_msg_txt;
        return this;
    }

    private String hint_msg_txt;

    private TextView messageTV;
    private Button copyBtn;
    private TextView titleTv;
    private TextView hint_msg;
    private String tag;//用来划分处理逻辑

    private OnBtnClickedListener onBtnClickedListener;

    public DialogTextMessage setTitle(String title) {
        this.title = title;
        return this;

    }

    public String getTag() {
        return tag;
    }

    public DialogTextMessage setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public DialogTextMessage setBtnText(String btn_text) {
        this.btn_text = btn_text;
        return this;
    }

    public DialogTextMessage setCopyBtnClickedListener(OnBtnClickedListener onBtnClickedListener) {
        this.onBtnClickedListener = onBtnClickedListener;
        return this;
    }

    public DialogTextMessage setCancelByBackKey(boolean cancelByBackKey){
        this.cancelByBackKey = cancelByBackKey;
        return this;
    }

    public DialogTextMessage(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void initData() {

    }


    @Override
    protected void initView() {
        findViews();
        messageTV.setText(message);
        if (!TextUtils.isEmpty(title)) {
            titleTv.setText(title);
        }
        if (!TextUtils.isEmpty(btn_text)) {
            copyBtn.setText(btn_text);
        }
        if (hint_msg_txt != null) {
            hint_msg.setText(hint_msg_txt);
        }
        if (TextUtils.isEmpty(hint_msg_txt)) {
            hint_msg.setVisibility(View.GONE);
        }
        setCancelable(cancelByBackKey);
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
        hint_msg = (TextView) findViewById(R.id.hint_msg);
        copyBtn = (Button) findViewById(R.id.copyBtn);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnClickedListener.onDialogTextMessageBtnClicked(DialogTextMessage.this);
            }
        });
    }

    public DialogTextMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    public interface OnBtnClickedListener {
        void onDialogTextMessageBtnClicked(DialogTextMessage dialogTextMessage);
    }
}
