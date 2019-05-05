package org.ionc.dialog.version;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ionc.dialog.R;

import org.ionc.dialog.base.AbsBaseDialog;

public class VersionInfoDialog extends AbsBaseDialog implements View.OnClickListener {
    private TextView versionInfoTitle;
    private TextView versionInfoContent;
    private Button versionInfoBtnCancel;
    private Button versionInfoBtnUpdate;

    private String title_name;
    private String version_info_content;

    public VersionInfoDialog setTitleName(String name) {
        title_name = name;
        return this;
    }

    public VersionInfoDialog setVersionInfo(String info) {
        version_info_content = info;
        return this;
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-05-05 11:48:20 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        versionInfoTitle = (TextView) findViewById(R.id.version_info_title);
        versionInfoContent = (TextView) findViewById(R.id.version_info_content);
        versionInfoBtnCancel = (Button) findViewById(R.id.version_info_btn_cancel);
        versionInfoBtnUpdate = (Button) findViewById(R.id.version_info_btn_update);

        versionInfoBtnCancel.setOnClickListener(this);
        versionInfoBtnUpdate.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2019-05-05 11:48:20 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == versionInfoBtnCancel) {
            dismiss();
        } else if (v == versionInfoBtnUpdate) {
            // Handle clicks for versionInfoBtnUpdate
        }
    }

    public VersionInfoDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_version_info;
    }

    @Override
    protected void initDialog() {

    }

    @Override
    protected void initView() {
        findViews();
    }

    @Override
    protected void initData() {
        versionInfoTitle.setText(title_name);
        versionInfoContent.setText(version_info_content);
    }
}
