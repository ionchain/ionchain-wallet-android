package org.ionchain.wallet.ui.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.smart.holder.CommonAdapter;

import org.ionchain.wallet.R;
import org.ionchain.wallet.adapterhelper.device.DeviceViewHelper;
import org.ionchain.wallet.bean.DeviceListBean;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.ui.base.AbsBaseFragment;
import org.ionchain.wallet.utils.NetUtils;
import org.ionchain.wallet.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.ionchain.wallet.constant.ConstantUrl.DEVICES_GET;

/**
 * 我的设备
 */
public class DevicesFragment extends AbsBaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private ListView mListView;
    private CommonAdapter mAdapter;

    private List<DeviceListBean.DataBean> mDevicelistbeans = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_devices;
    }

    @Override
    protected void initView(View view) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh_asset);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mListView = view.findViewById(R.id.devices_lv);

        mAdapter = new CommonAdapter(getActivity(), mDevicelistbeans, R.layout.item_devices_layout, new DeviceViewHelper());
        mListView.setAdapter(mAdapter);


    }

    @Override
    protected void initData() {

        getDeviceList();
    }

    @Override
    protected void setImmersionBar() {
        super.setImmersionBar();
        mImmersionBar
                .statusBarColor(R.color.blue_top)
                .execute();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mImmersionBar
                    .statusBarColor(R.color.blue_top)
                    .execute();
        }
    }

    private void getDeviceList() {
        List<WalletBean> list = WalletDaoTools.getAllWallet();
        if (list == null || list.size() == 0) {
            return;
        }
        int size = list.size();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append(list.get(i).getAddress()+",");
        }
        String address = builder.toString();
        if (StringUtils.isEmpty(address)) {
            return;
        }
        OkGo.<String>get(DEVICES_GET)                            // 请求方式和请求url
                .tag(this)// 请求的 tag, 主要用于取消对应的请求
                .params("eth_address", address)
                .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        isRefreshing = false;

                        String json = response.body();
                        DeviceListBean bean = NetUtils.gsonToBean(json, DeviceListBean.class);
                        if (bean == null || bean.getData() == null) {
                            return;
                        }
                        mDevicelistbeans.clear();
                        mDevicelistbeans.addAll(bean.getData());
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onRefresh() {
        isRefreshing = true;
        getDeviceList();
    }
}
