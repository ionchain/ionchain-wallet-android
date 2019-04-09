package org.ionchain.wallet.mvp.view.activity;import android.content.DialogInterface;import android.content.Intent;import android.net.Uri;import android.os.Build;import android.support.v4.content.FileProvider;import android.support.v7.app.AlertDialog;import android.view.View;import android.view.animation.AlphaAnimation;import android.view.animation.Animation;import android.widget.ProgressBar;import android.widget.RelativeLayout;import android.widget.TextView;import com.lzy.okgo.callback.StringCallback;import com.lzy.okgo.model.Progress;import com.lzy.okgo.model.Response;import com.lzy.okgo.request.base.Request;import com.lzy.okserver.download.DownloadListener;import com.lzy.okserver.download.DownloadTask;import org.ionc.wallet.sdk.IONCWalletSDK;import org.ionc.wallet.utils.Logger;import org.ionchain.wallet.BuildConfig;import org.ionchain.wallet.R;import org.ionchain.wallet.bean.UpdateBean;import org.ionchain.wallet.mvp.view.activity.createwallet.CreateWalletSelectActivity;import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;import org.ionchain.wallet.utils.AppUtil;import org.ionchain.wallet.utils.NetUtils;import org.ionchain.wallet.utils.ToastUtil;import java.io.File;import java.math.BigDecimal;import static org.ionchain.wallet.constant.ConstantUrl.UPDATE_APK;/** * Created by siberiawolf on 17/4/28. */public class WelcomeActivity extends AbsBaseActivity implements Animation.AnimationListener {    RelativeLayout welcome_layout;    DownloadTask task;    ProgressBar progressBar;    TextView progressValueTv;    RelativeLayout progressRl;    private AlphaAnimation aAnimation;    @Override    protected void initData() {        String version_name = AppUtil.getVersionName(this);        TextView v = findViewById(R.id.version_name);        v.setText("当前版本：" + version_name);        progressValueTv = findViewById(R.id.download_progress_value);        progressBar = findViewById(R.id.download_progress);        progressRl = findViewById(R.id.progressRL);        aAnimation = new AlphaAnimation(0.0f, 1.0f);        aAnimation.setDuration(2000);        aAnimation.setAnimationListener(this);        welcome_layout.startAnimation(aAnimation);    }    @Override    protected void setListener() {    }    @Override    protected void handleIntent(Intent intent) {    }    @Override    protected void initView() {        welcome_layout = findViewById(R.id.welcome_layout);    }    @Override    protected int getLayoutId() {        return R.layout.activity_welcome;    }    @Override    public void onAnimationStart(Animation animation) {    }    @Override    public void onAnimationEnd(Animation animation) {//        skipToNext();//        检查是否有更新        checkForUpdate();    }    private void checkForUpdate() {        NetUtils.get(UPDATE_APK,  new StringCallback() {            @Override            public void onStart(Request<String, ? extends Request> request) {                super.onStart(request);                Logger.i("onStart");                showProgress("正在检查更新,请稍后……");            }            @Override            public void onCacheSuccess(Response<String> response) {                super.onCacheSuccess(response);                Logger.i("onCacheSuccess");                hideProgress();            }            @Override            public void onFinish() {                super.onFinish();                Logger.i("onFinish");                hideProgress();            }            @Override            public void uploadProgress(Progress progress) {                super.uploadProgress(progress);                Logger.i("uploadProgress");                hideProgress();            }            @Override            public void downloadProgress(Progress progress) {                super.downloadProgress(progress);                Logger.i("downloadProgress");                hideProgress();            }            @Override            public void onError(Response<String> response) {                super.onError(response);                hideProgress();                ToastUtil.showToastLonger("检查失败!"+response.getException().getMessage());                skipToNext();            }            @Override            public void onSuccess(Response<String> response) {                hideProgress();                String json = response.body();                UpdateBean updateBean = NetUtils.gsonToBean(json, UpdateBean.class);                if (updateBean != null && updateBean.getData() != null && updateBean.getData().get(0) != null) {                    int new_code = updateBean.getData().get(0).getVersion_code();                    int old_code = AppUtil.getVersionCode(mActivity);                    if (new_code > old_code) {                        //询问用户是否下载？                        showDialog(updateBean.getData().get(0).getUrl(), updateBean.getData().get(0).getUpdate_info(), String.valueOf(updateBean.getData().get(0).getVersion_code()));                    } else {                        skipToNext();                    }                } else {                    skipToNext();                }            }        }, "update");    }    @Override    public void onAnimationRepeat(Animation animation) {    }    public void showDialog(final String downloadUrl, String msg, String new_code) {        AlertDialog.Builder builder = new AlertDialog.Builder(this);        builder.setTitle("新版本 ：" + new_code + " ，请更新后使用！")                .setMessage(msg)                .setCancelable(false)                .setPositiveButton("下载", new DialogInterface.OnClickListener() {                    @Override                    public void onClick(DialogInterface dialog, int which) {                        task = NetUtils.downloader(downloadUrl, new AppDownloadListener("apk_download"));                        task.start();                    }                })                .setNegativeButton("取消", new DialogInterface.OnClickListener() {                    @Override                    public void onClick(DialogInterface dialog, int which) {                        skipToNext();                    }                })                .show();    }    private void skipToNext() {//        IONCWalletSDK.getInstance().initIONCWalletSDK(mActivity.getApplicationContext());        if (IONCWalletSDK.getInstance().getAllWallet() != null && IONCWalletSDK.getInstance().getAllWallet().size() > 0) {            skip(MainActivity.class);        } else {            Intent intent1 = new Intent(getMActivity(), CreateWalletSelectActivity.class);            startActivity(intent1);            finish();        }    }    class AppDownloadListener extends DownloadListener {        public AppDownloadListener(Object tag) {            super(tag);        }        @Override        public void onStart(Progress progress) {            //开始            Logger.i("开始下载......");        }        @Override        public void onProgress(Progress progress) {            //下载进度            int p = (int) scale(progress.fraction);            Logger.i("当前进度  " + p + "%");            progressValueTv.setText(p + "%");            progressRl.setVisibility(View.VISIBLE);            progressBar.setProgress(p);        }        @Override        public void onError(Progress progress) {            progressRl.setVisibility(View.GONE);            progressBar.setProgress(0);            ToastUtil.showLong("下载出错");            skipToNext();        }        @Override        public void onFinish(File file, Progress progress) {            progressRl.setVisibility(View.GONE);            progressBar.setProgress(0);            finish();            Intent intent = new Intent(Intent.ACTION_VIEW);            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {                /* Android N 写法*/                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);                Uri contentUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".fileProvider", new File(progress.filePath));                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");            } else {                /* Android N之前的老版本写法*/                intent.setDataAndType(Uri.fromFile(new File(progress.filePath)), "application/vnd.android.package-archive");                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);            }            startActivity(intent);        }        @Override        public void onRemove(Progress progress) {        }    }    /**     * 保留两位小数 四舍五入     *     * @param f 原始浮点型数     * @return 两位小数     */    private float scale(float f) {        BigDecimal b = new BigDecimal(f);        return b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() * 100;    }}