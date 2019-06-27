package org.ionchain.wallet.mvp.view.activity.setting.language;

import android.view.View;
import android.widget.TextView;

import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseCommonTitleTwoActivity;
import org.ionchain.wallet.utils.LocalManageUtil;
import org.ionchain.wallet.utils.ToastUtil;

import static org.ionchain.wallet.App.skipToMain;

public class SettingLanguageActivity extends AbsBaseCommonTitleTwoActivity implements View.OnClickListener {
    private TextView mUserSelect;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mUserSelect = findViewById(R.id.tv_user_select);
        mUserSelect.setText(getString(R.string.setting_select_language, LocalManageUtil.getSelectLanguage(this)));
        findViewById(R.id.btn_auto).setOnClickListener(this);
        findViewById(R.id.btn_cn).setOnClickListener(this);
        findViewById(R.id.btn_en).setOnClickListener(this);
    }

    @Override
    protected String getTitleName() {
        return getAppString(R.string.language_setting);
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
                if (App.isCurrentLanguageZN()) {
                    ToastUtil.showToastLonger(getAppString(R.string.current_language_zn));
                    return;
                }
                LocalManageUtil.saveSelectLanguage(this, 1);
                break;
            case R.id.btn_en:
                if (App.isCurrentLanguageEN()) {
                    ToastUtil.showToastLonger(getAppString(R.string.current_language_en));
                    return;
                }
                LocalManageUtil.saveSelectLanguage(this, 2);
                App.currentLanguage = "en";
                break;
        }
        skipToMain();
    }
}
