package org.ionchain.wallet.model.update;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.UpdateBean;
import org.ionchain.wallet.constant.ConstantNetCancelTag;
import org.ionchain.wallet.utils.AppUtil;
import org.ionchain.wallet.utils.NetUtils;
import org.ionchain.wallet.utils.UrlUtils;

public class UpdateModelModel implements IUpdateModel {
    @Override
    public void update(final OnCheckUpdateInfoCallback callback) {
        NetUtils.get("update", UrlUtils.getUpdateApkUrl(), new StringCallback() {

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                callback.onCheckForUpdateStart();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                LoggerUtils.e("检查更新失败:" + response.getException().getMessage());
                callback.onCheckForUpdateError(App.mAppInstance.getResources().getString(R.string.error_net_request_update));
            }

            @Override
            public void onSuccess(Response<String> response) {
                callback.onCheckForUpdateSuccess();
                String json = response.body();
                LoggerUtils.j("update", json);
                UpdateBean updateBean = NetUtils.gsonToBean(json, UpdateBean.class);
                if (updateBean != null && updateBean.getData() != null && updateBean.getData().get(0) != null) {
                    //询问用户是否下载？i
                    if (AppUtil.getVersionCode(App.mAppInstance) < updateBean.getData().get(0).getVersion_code()) {
                        callback.onCheckForUpdateNeedUpdate(updateBean, updateBean.getData().get(0).getMust_update());
                    } else {
                        LoggerUtils.i("update: 没有新版本");
                        callback.onCheckForUpdateNoNewVersion();
                    }

                } else {
                    LoggerUtils.e("update: 数据解析失败");
                    callback.onCheckForUpdateError(App.mAppInstance.getResources().getString(R.string.error_data_parse));
                }
            }
        }, ConstantNetCancelTag.NET_CANCEL_TAG_UPDATE);
    }
}
