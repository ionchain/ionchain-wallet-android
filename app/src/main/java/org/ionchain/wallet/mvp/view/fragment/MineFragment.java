package org.ionchain.wallet.mvp.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ionc.wallet.sdk.utils.Logger;
import com.lzy.okgo.model.Progress;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;

import org.ionchain.wallet.BuildConfig;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.activity.ManageWalletActivity;
import org.ionchain.wallet.mvp.view.activity.MessageCenterActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseFragment;
import org.ionchain.wallet.utils.NetUtils;
import org.ionchain.wallet.utils.ToastUtil;

import java.io.File;

public class MineFragment extends AbsBaseFragment {

    private TextView loginRegTv;
    private RelativeLayout walletManageRLayout;
    private RelativeLayout messageCenterRLayout;
    private ImageView arrowIv;
    private TextView hintMessageNum;
    private ImageView arrowIv1;
    DownloadTask task;
    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-12 16:34:06 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews(View rootView) {
        loginRegTv = rootView.findViewById(R.id.loginRegTv);
        walletManageRLayout = rootView.findViewById(R.id.walletManageRLayout);
        messageCenterRLayout = rootView.findViewById(R.id.messageCenterRLayout);
        arrowIv = rootView.findViewById(R.id.arrowIv);
        hintMessageNum = rootView.findViewById(R.id.hint_message_num);
        arrowIv1 = rootView.findViewById(R.id.arrowIv1);
    }

    @Override
    protected void setListener() {
        loginRegTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToastLonger("暂不需要登录，不影响钱包的使用");
                task.start();
            }
        });
        walletManageRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(ManageWalletActivity.class);
            }
        });
        messageCenterRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(MessageCenterActivity.class);
            }
        });

        hintMessageNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(View view) {
        findViews(view);
    }

    @Override
    protected void initData() {
        task = NetUtils.downloader("http://169.254.51.93:8080/WebTest/apk/app.apk", new DownloadListener("") {
            @Override
            public void onStart(Progress progress) {
                Logger.i(" onStart = "+progress.fraction);
            }

            @Override
            public void onProgress(Progress progress) {
                Logger.i(" onProgress = "+progress.fraction);
            }

            @Override
            public void onError(Progress progress) {
                Logger.i(" onError = "+progress.filePath);
            }

            @Override
            public void onFinish(File file, Progress progress) {
                Logger.i(" onFinish = "+progress.filePath);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    /* Android N 写法*/
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileProvider", new File(progress.filePath));
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {
                    /* Android N之前的老版本写法*/
                    intent.setDataAndType(Uri.fromFile(new File(progress.filePath)), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);
            }

            @Override
            public void onRemove(Progress progress) {

            }
        });
    }
}
