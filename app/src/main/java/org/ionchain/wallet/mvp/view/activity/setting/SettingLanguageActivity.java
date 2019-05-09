package org.ionchain.wallet.mvp.view.activity.setting;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.activity.MainActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.LocalManageUtil;

public class SettingLanguageActivity extends AbsBaseActivity implements View.OnClickListener {
    private TextView mUserSelect;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mUserSelect = findViewById(R.id.tv_user_select);
        mUserSelect.setText(getString(R.string.language_select,LocalManageUtil.getSelectLanguage(this)));
        findViewById(R.id.btn_auto).setOnClickListener(this);
        findViewById(R.id.btn_cn).setOnClickListener(this);
        findViewById(R.id.btn_en).setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_language;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_auto:
                LocalManageUtil.saveSelectLanguage(this, 0);
                break;
            case R.id.btn_cn:
                LocalManageUtil.saveSelectLanguage(this, 1);
                break;
            case R.id.btn_en:
                LocalManageUtil.saveSelectLanguage(this, 2);
                break;
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
