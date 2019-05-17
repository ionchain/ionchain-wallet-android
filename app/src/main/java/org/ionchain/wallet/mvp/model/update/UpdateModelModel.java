package org.ionchain.wallet.mvp.model.update;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.ionc.wallet.utils.Logger;
import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.UpdateBean;
import org.ionchain.wallet.utils.AppUtil;
import org.ionchain.wallet.utils.NetUtils;

import static org.ionchain.wallet.constant.ConstantUrl.URL_UPDATE_APK;

public class UpdateModelModel implements IUpdateModel {
    @Override
    public void update(final OnCheckUpdateInfoCallback callback) {
        NetUtils.get(URL_UPDATE_APK, new StringCallback() {

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                Logger.i("onCheckForUpdateStart");
                callback.onCheckForUpdateStart();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onCheckForUpdateError(App.mContext.getResources().getString(R.string.data_parase_error));
            }

            @Override
            public void onSuccess(Response<String> response) {
                callback.onCheckForUpdateSuccess();
                String json = response.body();
                UpdateBean updateBean = NetUtils.gsonToBean(json, UpdateBean.class);
                if (updateBean != null && updateBean.getData() != null && updateBean.getData().get(0) != null) {
                    //询问用户是否下载？
                    if (AppUtil.getVersionCode(App.mContext) < updateBean.getData().get(0).getVersion_code()) {
                        callback.onCheckForUpdateNeedUpdate(updateBean);
                    }else {
                        callback.onCheckForUpdateNoNewVersion();
                    }

                } else {
                    callback.onCheckForUpdateError(App.mContext.getResources().getString(R.string.data_parase_error));
                }
            }
        }, "checkForUpdate");
    }
}
