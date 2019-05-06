package org.ionchain.wallet.mvp.model.update;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.ionc.wallet.utils.Logger;
import org.ionchain.wallet.App;
import org.ionchain.wallet.bean.UpdateBean;
import org.ionchain.wallet.utils.AppUtil;
import org.ionchain.wallet.utils.NetUtils;

import static org.ionchain.wallet.constant.ConstantUrl.UPDATE_APK;

public class UpdateModel implements IUpdate {
    @Override
    public void update(final OnCheckUpdateInfoCallback callback) {
        NetUtils.get(UPDATE_APK, new StringCallback() {

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                Logger.i("onCheckForUpdateStart");
                callback.onCheckForUpdateStart();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onCheckForUpdateError();
            }

            @Override
            public void onSuccess(Response<String> response) {
                callback.onCheckForUpdateSuccess();
                String json = response.body();
                UpdateBean updateBean = NetUtils.gsonToBean(json, UpdateBean.class);
                if (updateBean != null && updateBean.getData() != null && updateBean.getData().get(0) != null) {

                    int new_code = updateBean.getData().get(0).getVersion_code();
                    int old_code = AppUtil.getVersionCode(App.mContext);
                    if (new_code > old_code) {
                        //询问用户是否下载？

                        callback.onCheckForUpdateNeedUpdate(updateBean.getData().get(0).getUrl(), updateBean.getData().get(0).getUpdate_info(), String.valueOf(updateBean.getData().get(0).getVersion_code()));
                    } else {
                        callback.onCheckForUpdateNoNewVersion();
                    }
                } else {
                    callback.onCheckForUpdateNoNewVersion();
                }
            }
        }, "checkForUpdate");
    }
}
