package org.ionchain.wallet.view.activity.create;

import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.ionchain.wallet.R;
import org.ionchain.wallet.view.activity.imports.SelectImportModeActivity;
import org.ionchain.wallet.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.ToastUtil;

import static org.ionchain.wallet.constant.ConstantActivitySkipTag.INTENT_FROM_MAIN_ACTIVITY;
import static org.ionchain.wallet.constant.ConstantActivitySkipTag.INTENT_FROM_WHERE_TAG;

/**
 * 创建钱包。导入钱包，第一次安装时，由启动页跳转过来
 */
public class CreateWalletSelectActivity extends AbsBaseActivity {


    private CheckBox checkBox;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-19 19:22:58 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        Button createBtn = findViewById(R.id.createBtn);
        Button importBtn = findViewById(R.id.importBtn);
        checkBox = findViewById(R.id.checkbox);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
                    ToastUtil.showLong(getResources().getString(R.string.protocol_please_read));
                    return;
                }
                Intent intent = new Intent(mActivity, CreateWalletActivity.class);
                intent.putExtra(INTENT_FROM_WHERE_TAG, INTENT_FROM_MAIN_ACTIVITY);
                startActivity(intent);
            }
        });
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
                    ToastUtil.showLong(getResources().getString(R.string.protocol_please_read));
                    return;
                }
                Intent intent = new Intent(mActivity, SelectImportModeActivity.class);//
                startActivity(intent);
            }
        });
    }


    @Override
    protected void initData() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String s1 = getResources().getString(R.string.protocol_first);
        SpannableString sp1 = new SpannableString(s1);
        sp1.setSpan(null, 9, sp1.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append(sp1);


        String s2 = getResources().getString(R.string.protocol_service_agreement);
        SpannableString sp2 = new SpannableString(s2);
        sp2.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                skipWebProtocol();
            }
        }, 0, sp2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan fcs2 = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.bga_pp_btn_confirm_pressed));
        sp2.setSpan(fcs2, 0, sp2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//设置字体的颜色
        builder.append(sp2);

        checkBox.setText(builder);
        checkBox.setMovementMethod(LinkMovementMethod.getInstance());//加上这句话才有效果
        checkBox.setHighlightColor(ContextCompat.getColor(this, R.color.transparent));//去掉点击后的背景颜色为透明

    }

    @Override
    protected void initView() {
        findViews();

    }

    @Override
    protected void setImmersionBar() {
        super.setImmersionBar();
        mImmersionBar
                .statusBarDarkFont(false)
                .transparentStatusBar()
                .navigationBarColor(R.color.black, 0.5f)
                .fitsSystemWindows(false)
                .execute();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_wallet_select;
    }

    @Override
    protected void handleIntent(@NonNull Intent intent) {
        requestCameraPermissions();
        super.handleIntent(intent);
    }


}