package org.ionc.dialog.download;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ionc.dialog.R;

import org.ionc.dialog.base.AbsBaseDialog;

public class DownloadDialog extends AbsBaseDialog implements View.OnClickListener {
    private TextView download_progress_tv;
    private ProgressBar mProgress;
    private Button mProgressBtn;


    private void findViews() {
        download_progress_tv = findViewById(R.id.download_progress_tv);
        mProgress = findViewById(R.id.download_progress_schedule);
        mProgressBtn = findViewById(R.id.download_progress_btn_cancel);
        mProgressBtn.setOnClickListener(this);
    }

    public void updateProgress(int progress) {
        mProgress.setProgress(progress);
    }

    public void updateProgresTvs(String progress) {
        download_progress_tv.setText(mActivity.getString(R.string.download_progress_tv,progress));
    }

    @Override
    public void onClick(View v) {
        if (v == mProgressBtn) {
            dismiss();
        }
    }

    public DownloadDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_download;
    }

    @Override
    protected void initDialog() {
        initDialogDefault();
    }

    @Override
    protected void initView() {
        findViews();
    }

    @Override
    protected void initData() {

    }
}
