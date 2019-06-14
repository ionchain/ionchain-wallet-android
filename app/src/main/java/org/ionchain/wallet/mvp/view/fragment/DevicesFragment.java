package org.ionchain.wallet.mvp.view.fragment;

import android.view.View;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.ToastUtil;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.device.DeviceViewHelper;
import org.ionchain.wallet.bean.DeviceBean;
import org.ionchain.wallet.mvp.callback.OnDeviceListCallback;
import org.ionchain.wallet.mvp.callback.OnUnbindDeviceButtonClickedListener;
import org.ionchain.wallet.mvp.presenter.device.DevicePresenter;
import org.ionchain.wallet.mvp.view.base.AbsBaseFragment;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的设备
 */
public class DevicesFragment extends AbsBaseFragment implements OnUnbindDeviceButtonClickedListener, OnRefreshListener, OnDeviceListCallback {
    private ListView mListView;
    private CommonAdapter mAdapter;

    private List<DeviceBean.DataBean> mDataBeanList = new ArrayList<>();
    private SmartRefreshLayout mSwipeRefreshLayout;
    private DevicePresenter mDevicePresenter;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_devices;
    }

    @Override
    protected void initView(View view) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh_asset);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.setEnableRefresh(false);
        mListView = view.findViewById(R.id.devices_lv);

        mAdapter = new CommonAdapter(getActivity(), mDataBeanList, R.layout.item_devices_layout, new DeviceViewHelper(this));
        mListView.setAdapter(mAdapter);


    }

    @Override
    protected void initData() {
        mDevicePresenter = new DevicePresenter();
//        getDeviceList();
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

    @Override
    protected void handleShow() {

    }

    @Override
    protected void handleHidden() {

    }

    private void getDeviceList() {
//        List<WalletBeanNew> list = IONCWalletSDK.getInstance().getAllWalletNew();
//        if (list == null || list.size() == 0) {
//            return;
//        }
//        int size = list.size();
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < size; i++) {
//            builder.append(list.get(i).getAddress() + ",");
//        }
//        String address = builder.toString();
//        if (StringUtils.isEmpty(address)) {
//            return;
//        }
//        mDevicePresenter.getAllWalletDevicesList(address, this);

    }


    @Override
    public void onUnbindButtonClick(String cksn, int position) {
        LoggerUtils.i(TAG, "onUnbindButtonClick: " + cksn);

    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        isRefreshing = true;
        getDeviceList();
    }

    @Override
    public void onDeviceListSuccess(@NotNull List<DeviceBean.DataBean> list) {
//        mSwipeRefreshLayout.finishRefresh();
        isRefreshing = false;
        mDataBeanList.clear();
        mDataBeanList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeviceListFailure(@NotNull String errorMessage) {
        ToastUtil.showToastLonger(errorMessage);
    }

    @Override
    public void onLoadStart() {
//        showProgress(getAppString(R.string.being_loaded));
    }

    @Override
    public void onLoadFinish() {
        mSwipeRefreshLayout.finishRefresh();
    }

}
