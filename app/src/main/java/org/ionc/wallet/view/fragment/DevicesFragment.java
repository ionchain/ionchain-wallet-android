package org.ionc.wallet.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.ionclib.bean.WalletBeanNew;
import org.ionc.ionclib.utils.ToastUtil;
import org.ionc.ionclib.web3j.IONCSDKWallet;
import org.ionc.wallet.App;
import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.adapter.device.DeviceViewHelper;
import org.ionc.wallet.bean.DeviceBean;
import org.ionc.wallet.callback.OnBindDeviceCallback;
import org.ionc.wallet.callback.OnDeviceListCallback;
import org.ionc.wallet.callback.OnUnbindDeviceCallback;
import org.ionc.wallet.presenter.device.DevicePresenter;
import org.ionc.wallet.qrcode.activity.CaptureActivity;
import org.ionc.wallet.qrcode.activity.CodeUtils;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.view.base.AbsBaseFragment;
import org.ionc.wallet.view.widget.DialogBindDevice;
import org.ionc.wallet.view.widget.dialog.callback.OnDialogCheck12MnemonicCallbcak;
import org.ionc.wallet.view.widget.dialog.export.DialogTextMessage;
import org.ionc.wallet.view.widget.dialog.mnemonic.DialogCheckMnemonic;
import org.ionc.wallet.view.widget.dialog.mnemonic.DialogMnemonicShow;
import org.ionchain.wallet.R;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的设备
 */
public class DevicesFragment extends AbsBaseFragment implements
        OnRefreshListener,
        OnDeviceListCallback,
        DialogMnemonicShow.OnSavedMnemonicCallback,
        DialogTextMessage.OnBtnClickedListener,
        OnDialogCheck12MnemonicCallbcak,
        OnBindDeviceCallback, OnUnbindDeviceCallback {
    private static final int QRCODE_BIND_DEVICE = 10;
    private ListView mListView;
    private CommonAdapter mAdapter;

    private List<DeviceBean.DataBean> mDataBeanList = new ArrayList<>();
    private SmartRefreshLayout mSwipeRefreshLayout;
    private DevicePresenter mDevicePresenter;
    private ImageView addDevice;
    private ImageView wallet_img_device;
    private ImageView no_device_img;
    private TextView wallet_name_devices;

    private WalletBeanNew mCurrentWallet;
    private DialogMnemonicShow dialogMnemonic;

    private DialogBindDevice mDialogBindCardWithWallet;//绑定设备的弹窗

    private int mPosToBeRemove;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_devices;
    }

    @Override
    protected void initView(View view) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh_asset);
        mSwipeRefreshLayout.setOnRefreshListener(this);

//        mSwipeRefreshLayout.setEnableRefresh(false);
        mListView = view.findViewById(R.id.devices_lv);
        addDevice = view.findViewById(R.id.add_devices);
        wallet_img_device = view.findViewById(R.id.wallet_img_device);
        no_device_img = view.findViewById(R.id.no_device_img);
        wallet_name_devices = view.findViewById(R.id.wallet_name_devices);

        mAdapter = new CommonAdapter(mActivity, mDataBeanList, R.layout.item_devices_layout, new DeviceViewHelper());
        mListView.setAdapter(mAdapter);


    }

    @Override
    protected void setListener() {
        super.setListener();
        /*
         * 绑定设备
         * */
        addDevice.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(mCurrentWallet.getMnemonic())) {
               ToastUtil.showToastLonger(getResources().getString(R.string.toast_please_backup_wallet));
                String[] mnemonics = mCurrentWallet.getMnemonic().split(" ");
                dialogMnemonic = new DialogMnemonicShow(mActivity, mnemonics, DevicesFragment.this);
                dialogMnemonic.show();
                return;
            }
            Intent intent = new Intent(mActivity, CaptureActivity.class);
            startActivityForResult(intent, 10);
        });
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            mPosToBeRemove = position;
            mDevicePresenter.unbindDeviceToWallet(mCurrentWallet.getAddress(), mDataBeanList.get(position).getCksn(), DevicesFragment.this);
        });

    }

    @Override
    protected void initData() {
        mDevicePresenter = new DevicePresenter();
        mCurrentWallet = IONCSDKWallet.getMainWallet();
        wallet_img_device.setImageResource(App.sRandomHeader[mCurrentWallet.getMIconIndex()]);
        wallet_name_devices.setText(mCurrentWallet.getName());
        showProgress();
        no_device_img.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
        getDeviceList();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mImmersionBar
                    .statusBarColor(R.color.main_color)
                    .execute();
        }
    }

    @Override
    protected void handleShow() {
        if (mCurrentWallet != null) {
            WalletBeanNew walletBeanNew = IONCSDKWallet.getMainWallet();
            if (!mCurrentWallet.getName().equals(walletBeanNew.getName())) {
                mDataBeanList.clear();
                mAdapter.notifyDataSetChanged();
                mCurrentWallet = walletBeanNew;
                wallet_img_device.setImageResource(App.sRandomHeader[mCurrentWallet.getMIconIndex()]);
                wallet_name_devices.setText(mCurrentWallet.getName());
                getDeviceList();
            }
        } else {
            mCurrentWallet = IONCSDKWallet.getMainWallet();
            wallet_img_device.setImageResource(App.sRandomHeader[mCurrentWallet.getMIconIndex()]);
            wallet_name_devices.setText(mCurrentWallet.getName());
        }
    }

    @Override
    protected void handleHidden() {

    }

    private void getDeviceList() {
//        List<WalletBeanNew> list = IONCSDKWallet.getAllWalletNew();
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

        //从网络获取数据
        mDevicePresenter.getCurrentWalletDevicesList(mCurrentWallet, this);

    }


    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        no_device_img.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
        getDeviceList();
    }

    @Override
    public void onDeviceListSuccess(@NotNull List<DeviceBean.DataBean> list) {
//        mSwipeRefreshLayout.finishRefresh();
        hideProgress();
        if (list.size() == 0) {
            no_device_img.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            no_device_img.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }

        mDataBeanList.clear();
        mDataBeanList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeviceListFailure(@NotNull String errorMessage) {
        hideProgress();
        no_device_img.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
        ToastUtil.showToastLonger(errorMessage);
    }

    @Override
    public void onDataNull() {
        hideProgress();
        no_device_img.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
    }

    @Override
    public void onLoadStart() {
//        showProgress(getAppString(R.string.being_loaded));
    }

    @Override
    public void onLoadFinish() {
        mSwipeRefreshLayout.finishRefresh();
    }

    @Override
    public void onSaveMnemonicSure() {
        new DialogTextMessage(mActivity).setTitle(getResources().getString(R.string.attention))
                .setMessage(getResources().getString(R.string.key_store_to_save))
                .setBtnText(getResources().getString(R.string.i_know))
                .setHintMsg("")
                .setCancelByBackKey(true)
                .setTag("")
                .setCopyBtnClickedListener(this).show();
    }

    @Override
    public void onSaveMnemonicCancel(DialogMnemonicShow dialogMnemonic) {
        dialogMnemonic.dismiss();
    }

    @Override
    public void onDialogTextMessageBtnClicked(DialogTextMessage dialogTextMessage) {
        dialogMnemonic.dismiss();
        dialogTextMessage.dismiss();
        //保存准状态
        //去测试一下助记词
        new DialogCheckMnemonic(mActivity, this).show();
    }

    @Override
    public void onDialogCheckMnemonics12(String[] s, List<AppCompatEditText> editTextList, DialogCheckMnemonic dialogCheckMnemonic) {
        String[] mnemonics = mCurrentWallet.getMnemonic().split(" ");
        if (s.length != mnemonics.length) {
           ToastUtil.showToastLonger(getResources().getString(R.string.error_mnemonics));
            return;
        }
        int count = mnemonics.length;
        for (int i = 0; i < count; i++) {
            if (!mnemonics[i].equals(s[i])) {
                String index = String.valueOf((i + 1));
                ToastUtil.showToastLonger(getResources().getString(R.string.error_index_mnemonics, index));
                editTextList.get(i).setTextColor(Color.RED);
                return;
            }
        }
        //更新
        mCurrentWallet.setMnemonic("");
        IONCSDKWallet.updateWallet(mCurrentWallet);
        ToastUtil.showToastLonger(getResources().getString(R.string.authentication_successful));
        dialogCheckMnemonic.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * 处理二维码扫描结果
         */
        if (requestCode == QRCODE_BIND_DEVICE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    ToastUtil.showLong(getResources().getString(R.string.error_parase_toast_qr_code));
                    return;
                }
                final String result = bundle.getString(CodeUtils.RESULT_STRING);
                if (TextUtils.isEmpty(result)) {
                    ToastUtil.showLong(getResources().getString(R.string.error_parase_toast_qr_code));
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {

                    mDialogBindCardWithWallet = new DialogBindDevice(mActivity);
                    mDialogBindCardWithWallet.setMessageText(result);
                    mDialogBindCardWithWallet.setLeftBtnClickedListener(v -> mDialogBindCardWithWallet.dismiss());
                    mDialogBindCardWithWallet.setRightBtnClickedListener(v -> {
                        mDialogBindCardWithWallet.dismiss();
                        String address = mCurrentWallet.getAddress();
                        mDevicePresenter.bindDeviceToWallet(address, result, DevicesFragment.this);
                    });
                    mDialogBindCardWithWallet.show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtil.showLong(getResources().getString(R.string.error_parase_toast_qr_code));
                }
            }
        }
    }

    @Override
    public void onBindSuccess(DeviceBean.DataBean dataBean) {
        DeviceBean.DataBean bean = new DeviceBean.DataBean();
        bean.setCksn(dataBean.getCksn());
        bean.setCreated_at(dataBean.getCreated_at());
        bean.setId(dataBean.getId());
        bean.setImage_url(dataBean.getImage_url());
        bean.setName(dataBean.getName());
        bean.setSystem(dataBean.getSystem());
        mDataBeanList.add(bean);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBindFailure(String result) {
        LoggerUtils.e(result);
        ToastUtil.showShortToast(result);
    }

    @Override
    public void onUnbindSuccess(DeviceBean.DataBean dataBean) {
        mDataBeanList.remove(mPosToBeRemove);
        mAdapter.notifyDataSetChanged();
        ToastUtil.showShort(getAppString(R.string.device_unbind_success));
    }

    @Override
    public void onUnbindFailure(String result) {
        ToastUtil.showShort(result);
    }
}
