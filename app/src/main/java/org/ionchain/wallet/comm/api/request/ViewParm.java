package org.ionchain.wallet.comm.api.request;

import com.fast.lib.logger.Logger;

import org.ionchain.wallet.comm.constants.Global;
import org.ionchain.wallet.comm.utils.ParamsUtils;
import org.ionchain.wallet.ui.comm.BaseActivity;
import org.ionchain.wallet.ui.comm.BaseFragment;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.reflect.Type;
import java.util.HashMap;


public class ViewParm {
    private SoftReference<BaseActivity> activitySoftReference;
    private SoftReference<BaseFragment> fragmentSoftReference;
    public Type type;
    public int refreshType;

    public ViewParm(BaseActivity activity, BaseFragment fragment, Type type, int refreshType) {
        if (null != activity) activitySoftReference = new SoftReference<BaseActivity>(activity);
        if (null != fragment) fragmentSoftReference = new SoftReference<BaseFragment>(fragment);
        this.type = type;
        this.refreshType = refreshType;
    }

    public BaseActivity getBaseActivity() {
        BaseActivity activity = null;
        if (null != activitySoftReference) {
            activity = activitySoftReference.get();
        }
        return activity;
    }

    public BaseFragment getBaseFragment() {
        BaseFragment fragment = null;
        if (null != fragmentSoftReference) {
            fragment = fragmentSoftReference.get();
        }
        return fragment;
    }
}
