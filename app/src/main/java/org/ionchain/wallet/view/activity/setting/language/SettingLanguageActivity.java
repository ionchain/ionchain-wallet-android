package org.ionchain.wallet.view.activity.setting.language;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.utils.LocalManageUtil;
import org.ionchain.wallet.utils.SPUtils;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.utils.ViewUtils;
import org.ionchain.wallet.view.base.AbsBaseActivityTitleTwo;

import static org.ionchain.wallet.App.skipToMain;

public class SettingLanguageActivity extends AbsBaseActivityTitleTwo implements View.OnClickListener {
    private TextView mUserSelect;
    private Button auto;
    private Button chinese;
    private Button english;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mUserSelect = findViewById(R.id.tv_user_select);
        mUserSelect.setText(getString(R.string.setting_select_language, LocalManageUtil.getSelectLanguage(this)));

        auto = findViewById(R.id.btn_auto);
        auto.setOnClickListener(this);
        chinese = findViewById(R.id.btn_cn);
        chinese.setOnClickListener(this);
        english = findViewById(R.id.btn_en);
        english.setOnClickListener(this);
        setBtnBgColor();


    }

    private void setBtnBgColor() {
        switch (SPUtils.getInstance().getSelectLanguage()) {
            case 0:
                ViewUtils.setBtnSelectedColor(this, auto);
                ViewUtils.setBtnUnSelectedColor(this, chinese);
                ViewUtils.setBtnUnSelectedColor(this, english);
                break;
            case 1:
                ViewUtils.setBtnSelectedColor(this, chinese);
                ViewUtils.setBtnUnSelectedColor(this, english);
                ViewUtils.setBtnUnSelectedColor(this, auto);
                break;
            case 2:
            default:
                ViewUtils.setBtnSelectedColor(this, english);
                ViewUtils.setBtnUnSelectedColor(this, chinese);
                ViewUtils.setBtnUnSelectedColor(this, auto);
                break;
        }

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
        setBtnBgColor();
        skipToMain();
    }
}
