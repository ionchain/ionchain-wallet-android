package org.ionchain.wallet.helper;

import com.lzy.okgo.OkGo;

import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.mvp.callback.OnIONCNodeCallback;
import org.ionchain.wallet.mvp.presenter.node.IONCNodePresenter;

public class NodeHelper {
    private static final NodeHelper ourInstance = new NodeHelper();
    private IONCNodePresenter mIONCNodePresenter;
    private int mTryCount = 0;

    public static NodeHelper getInstance() {
        return ourInstance;
    }

    private NodeHelper() {
        if (ourInstance == null) {
            mIONCNodePresenter = new IONCNodePresenter();
        }
    }

    public void tryGetNode(OnIONCNodeCallback callback, OnTryTimesCallback tryTimesCallback) {

        //尝试几次
        LoggerUtils.e("mTryCount" + mTryCount);
        if (mTryCount < 3) {
            mTryCount++;
            tryTimesCallback.onTryTimes(mTryCount);
            mIONCNodePresenter.getNodes(callback, String.valueOf(mTryCount));
        } else {
            tryTimesCallback.onTryFinish(mTryCount);
            LoggerUtils.e("mTryCount" + mTryCount + " 次后失败,结束尝试");
            mTryCount = 0;
        }
    }

    public void reset() {
        mTryCount = 0;
    }

    public void cancelAndReset() {
        reset();
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), "1");
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), "2");
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), "3");
    }

    public interface OnTryTimesCallback {
        /**
         * @param count 尝试次数
         */
        void onTryTimes(int count);

        /**
         * @param count 尝试次数
         */
        void onTryFinish(int count);
    }
}
