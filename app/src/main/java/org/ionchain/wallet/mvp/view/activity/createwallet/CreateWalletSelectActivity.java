package org.ionchain.wallet.mvp.view.activity.createwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.activity.importmode.SelectImportModeActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.ToastUtil;

import static org.ionchain.wallet.constant.ConstantParams.FROM_WELCOME;
import static org.ionchain.wallet.constant.ConstantParams.SERVER_PROTOCOL_VALUE;

/**
 * 创建钱包。导入钱包，第一次安装时，由启动页跳转过来
 */
public class CreateWalletSelectActivity extends AbsBaseActivity {



    private Button createBtn;
    private Button importBtn;
    private boolean isWelcome;

    private CheckBox checkBox;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-19 19:22:58 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        createBtn = findViewById( R.id.createBtn );
        importBtn = findViewById( R.id.importBtn );
        checkBox = findViewById(R.id.checkbox);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
                    ToastUtil.showLong("请先阅读并同意服务及协议");
                    return;
                }
                Intent intent = new Intent(getMActivity(),CreateWalletActivity.class);
                intent.putExtra(FROM_WELCOME,isWelcome);
                startActivity(intent);
            }
        });
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
                    ToastUtil.showLong("请先阅读并同意服务及协议");
                    return;
                }
                Intent intent = new Intent(getMActivity(),SelectImportModeActivity.class);
                intent.putExtra(FROM_WELCOME,isWelcome);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getMImmersionBar()
                .statusBarDarkFont(false)
                .transparentStatusBar()
                .navigationBarColor(R.color.black,0.5f)
                .fitsSystemWindows(false)
                .execute();
    }

    @Override
    protected void initData() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String s1 = "开始使用本产品及表示同意";
        SpannableString sp1 = new SpannableString(s1);
        sp1.setSpan(null, 9, sp1.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append(sp1);



        String s2 = "服务及协议";
        SpannableString sp2 = new SpannableString(s2);
        sp2.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                skipWeb(SERVER_PROTOCOL_VALUE);
            }
        }, 0, sp2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan fcs2 = new ForegroundColorSpan(ContextCompat.getColor(this,R.color.bga_pp_btn_confirm_pressed));
        sp2.setSpan(fcs2,0,sp2.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//设置字体的颜色
        builder.append(sp2);

        checkBox.setText(builder);
        checkBox.setMovementMethod(LinkMovementMethod.getInstance());//加上这句话才有效果
        checkBox.setHighlightColor(ContextCompat.getColor(this,R.color.transparent));//去掉点击后的背景颜色为透明
    }



    @Override
    protected void initView() {
        findViews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_wallet_select;
    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        isWelcome = intent.getBooleanExtra(FROM_WELCOME,false);
        requestCodeQRCodePermissions();
    }

}
