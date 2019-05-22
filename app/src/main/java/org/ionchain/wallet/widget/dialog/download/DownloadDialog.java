package org.ionchain.wallet.widget.dialog.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;

import org.ionc.wallet.utils.Logger;
import org.ionchain.wallet.App;
import org.ionchain.wallet.BuildConfig;
import org.ionchain.wallet.R;
import org.ionchain.wallet.widget.dialog.base.AbsBaseDialog;

import java.io.File;

import static org.ionchain.wallet.constant.ConstantParams.DOWNLOAD_MUST_UPDATE_YES;

public class DownloadDialog extends AbsBaseDialog implements View.OnClickListener {
    private TextView download_progress_tv;
    private ProgressBar mProgress;
    private Button mBtnCacncel;
    private String url;

    private DL mDL;

    private DownloadTask task;

    private DownloadCallback mDownloadCallback;
    private String mMustUpdate;
    private boolean cancelable;

    private void findViews() {
        download_progress_tv = findViewById(R.id.download_progress_tv);
        mProgress = findViewById(R.id.download_progress_schedule);
        mBtnCacncel = findViewById(R.id.download_progress_btn_cancel);
        if (DOWNLOAD_MUST_UPDATE_YES.equals(mMustUpdate)) {
            mBtnCacncel.setVisibility(View.GONE);
        }
        mBtnCacncel.setOnClickListener(this);
    }

    /**
     * @param progress 下载进度进度条
     */
    public void updateProgress(int progress) {
        mProgress.setProgress(progress);
    }

    /**
     * @param progress 下载进度值
     */
    public void updateProgressTv(String progress) {
        download_progress_tv.setText(mActivity.getString(R.string.download_progress_tv, progress));
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnCacncel) {
            dismiss();
            task.remove(true);
            mDownloadCallback.onDownloadCancel();
        }
    }

    /**
     * @param context
     * @param url
     * @param callback
     * @param must_update   用于隐藏取消下载按钮   在强制更新的时候用到
     */
    public DownloadDialog(@NonNull Context context, String url, DownloadCallback callback, String must_update) {
        super(context);
        this.url = url;
        mDownloadCallback = callback;
        mMustUpdate = must_update;
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_download;
    }

    @Override
    protected void initDialog() {
        initDialogDefault();
        setCancelable(cancelable);
    }

    @Override
    protected void initView() {
        findViews();
    }

    @Override
    protected void initData() {
        mDL = new DL("apk_download");
        GetRequest<File> request = OkGo.<File>get(url);
        task = OkDownload.request("task_download_apk", request)
                .save()
                .register(mDL);
    }

    public DownloadDialog setCancelableBydBackKey(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }


    class DL extends DownloadListener {

        DL(Object tag) {
            super(tag);
        }

        @Override
        public void onStart(Progress progress) {
            mDownloadCallback.onDownloadStart(progress);
        }

        @Override
        public void onProgress(Progress progress) {
            int p = (int) App.scale(progress.fraction);
            updateProgress(p);
            updateProgressTv(p + "%");
            Logger.i("onProgress = " + p + "%");
        }

        @Override
        public void onError(Progress progress) {
            dismiss();
            mDownloadCallback.onDownloadError(progress);
        }

        @Override
        public void onFinish(File file, Progress progress) {
            dismiss();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                /* Android N 写法*/
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Logger.i("appid ", BuildConfig.APPLICATION_ID);
                Uri contentUri = FileProvider.getUriForFile(App.mContext, BuildConfig.APPLICATION_ID + ".provider", new File(progress.filePath));
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                /* Android N之前的老版本写法*/
                intent.setDataAndType(Uri.fromFile(new File(progress.filePath)), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            mActivity.startActivity(intent);
        }

        @Override
        public void onRemove(Progress progress) {
            dismiss();
        }
    }

    public interface DownloadCallback {
        /**
         * 开始下载
         *
         * @param progress
         */
        void onDownloadStart(Progress progress);

        /**
         * 下载出错
         *
         * @param progress
         */
        void onDownloadError(Progress progress);

        /**
         * 取消下载
         */
        void onDownloadCancel();
    }

    @Override
    public void show() {
        super.show();
        task.start();
    }
}
